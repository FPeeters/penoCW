package testbed.world.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import testbed.Physics;
import testbed.entities.WorldObject;
import testbed.entities.airport.Airport;
import testbed.entities.drone.DroneSkeleton;
import testbed.entities.trail.Trail;
import testbed.entities.packages.Package;
import utils.Constants;
import utils.FloatMath;
import utils.PhysicsException;

public class DroneHelper {

    private final int nbDrones;
    private int index;
    public Map<String, Integer> droneIds;

    private WorldObject[][] droneModels;
    private Physics[] physics;
    private Trail[] trails;
    private Package[] packages;

    private JFrame rootFrame;
    private final boolean wantPhysics;

    public DroneHelper(boolean wantPhysics, int nbDrones) {
        this.nbDrones = nbDrones;
        this.index = -1;
        this.droneIds = new HashMap<>();

        this.droneModels = new WorldObject[nbDrones][];
        this.physics = new Physics[nbDrones];
        this.trails = new Trail[nbDrones];
        this.packages = new Package[nbDrones];

        this.wantPhysics = wantPhysics;
    }


    public void setRootFrame(JFrame rootFrame) {
        this.rootFrame = rootFrame;
    }


    public int getMaxNbDrones() {
        return this.nbDrones;
    }


    public Physics getDronePhysics(String droneId) {
        return droneIds.containsKey(droneId) ? physics[droneIds.get(droneId)] : null;
    }

    public Physics getDronePhysics(int droneId) {
        return droneIds.containsValue(droneId) ? physics[droneId] : null;
    }


    public Trail getDroneTrail(String droneId) {
        return droneIds.containsKey(droneId) ? trails[droneIds.get(droneId)] : null;
    }

    public Trail getDroneTrail(int droneId) {
        return droneIds.containsValue(droneId) ? trails[droneId] : null;
    }


    public Package getDronePackage(String droneId) {
        return droneIds.containsKey(droneId) ? packages[droneIds.get(droneId)] : null;
    }

    public Package getDronePackage(int droneId) {
        return droneIds.containsValue(droneId) ? packages[droneId] : null;
    }


    public WorldObject[] getDroneItems(int droneId) {
        return droneIds.containsValue(droneId) ? droneModels[droneId] : null;
    }

    public AutopilotConfig getDroneConfig(int droneId) {
        return droneIds.containsValue(droneId) ? physics[droneId].getConfig() : null;
    }


    public void addDrone(AutopilotConfig config, Vector3f startPos, Vector3f startVel, float startHeading, List<Airport> airports) {
        if (droneIds.containsKey(config.getDroneID()))
            throw new IllegalArgumentException("No duplicate drone names allowed");

        this.index++;

        if (index == nbDrones)
            throw new IllegalArgumentException("Max amount of drones reached");

        droneIds.put(config.getDroneID(), index);

        DroneSkeleton droneMesh = new DroneSkeleton(config);
        WorldObject left = new WorldObject(droneMesh.getLeft());
        WorldObject right = new WorldObject(droneMesh.getRight());
        WorldObject body = new WorldObject(droneMesh.getBody());

        WorldObject wheelFront = new WorldObject(droneMesh.getWheel());
        WorldObject wheelBackLeft = new WorldObject(droneMesh.getWheel());
        WorldObject wheelBackRight = new WorldObject(droneMesh.getWheel());

        WorldObject[] droneItems = new WorldObject[]{left, right, body,
                wheelFront, wheelBackLeft, wheelBackRight};

        droneModels[index] = droneItems;

        Physics physic = new Physics();
        physic.init(config, startPos, startVel, startHeading, airports);

        physics[index] = physic;

        trails[index] = new Trail();
    }


    public void removeDrone(String droneId, UpdateHelper updateHelper) {
        int index = droneIds.remove(droneId);

        WorldObject[] droneItems = droneModels[index];

        for (WorldObject droneItem : droneItems) {
            droneItem.getMesh().cleanUp();
        }

        droneModels[index] = null;
        physics[index] = null;
        trails[index] = null;
        packages[index] = null;

        if (index == updateHelper.getFollowDrone())
            updateHelper.nextFollowDrone();
    }

    public void removeDrone(int droneId, UpdateHelper updateHelper) {
        removeDrone(physics[droneId].getConfig().getDroneID(), updateHelper);
    }


    public void collectPackage(int droneId, Package pack) {
        pack.pickUp();
        packages[droneId] = pack;
    }

    public void deliverPackage(int droneId) {
        packages[droneId].deliver();
        packages[droneId] = null;
    }


    public void update(float interval, UpdateHelper updateHelper) {
        updatePhysics(interval, updateHelper);

        updateTrails();

        updateDroneItems();
    }


    private void updatePhysics(float interval, UpdateHelper updateHelper) {
        if (!wantPhysics)
            return;

        Set<Integer> dronesToRemove = new HashSet<>();
        for (int droneId : droneIds.values()) {
            try {
                getDronePhysics(droneId).update(interval);
            } catch (PhysicsException e) {
                JOptionPane.showMessageDialog(rootFrame,
                        "A physics error occured for drone " + getDronePhysics(droneId).getConfig().getDroneID()
                                + ": " + e.getMessage(),
                        "Physics Exception", JOptionPane.ERROR_MESSAGE);

                dronesToRemove.add(droneId);
            } catch (NullPointerException ignored) {
            }
        }

        for (Integer droneId : dronesToRemove) {
            if (packages[droneId] != null)
                packages[droneId].crashed();
            removeDrone(droneId, updateHelper);
        }

        checkCollision(updateHelper);
    }

    private void updateTrails() {
        for (String droneId : droneIds.keySet()) {
            getDroneTrail(droneId).leaveTrail(
                    getDronePhysics(droneId).getPosition());
        }
    }

    private void updateDroneItems() {
        for (String droneId : droneIds.keySet()) {
            Physics physics = getDronePhysics(droneId);
            Vector3f dronePos = physics.getPosition();
            // Update the position of each drone item
            for (WorldObject droneItem : droneModels[droneIds.get(droneId)]) {
                droneItem.setPosition(dronePos.x, dronePos.y, dronePos.z);
                droneItem.setRotation(-physics.getPitch(),
                        -physics.getHeading(), -physics.getRoll());
            }

            translateWheels(droneId);
            rotateWings(droneId);

            Package pack = getDronePackage(droneId);
            if (pack != null)
                pack.setPosition(dronePos.add(new Vector3f(0, -0.85f, 0), new Vector3f()));
        }
    }

    private void rotateWings(String droneId) {
        Physics physics = getDronePhysics(droneId);

        droneModels[droneIds.get(droneId)][Constants.DRONE_LEFT_WING].setRotation(-physics.getPitch() - physics.getLWInclination(), -physics.getHeading(), -physics.getRoll());
        droneModels[droneIds.get(droneId)][Constants.DRONE_RIGHT_WING].setRotation(-physics.getPitch() - physics.getRWInclination(), -physics.getHeading(), -physics.getRoll());

    }

    private void translateWheels(String droneId) {
        Physics physics = getDronePhysics(droneId);
        setWheel(droneId, Constants.DRONE_WHEEL_FRONT, 0, physics.getConfig()
                .getWheelY(), physics.getConfig().getFrontWheelZ());
        setWheel(droneId, Constants.DRONE_WHEEL_BACK_LEFT, -physics.getConfig()
                .getRearWheelX(), physics.getConfig().getWheelY(), physics
                .getConfig().getRearWheelZ());
        setWheel(droneId, Constants.DRONE_WHEEL_BACK_RIGHT, physics.getConfig()
                .getRearWheelX(), physics.getConfig().getWheelY(), physics
                .getConfig().getRearWheelZ());
    }

    private void setWheel(String droneId, int id, float x, float y, float z) {
        Vector3f wheel = droneModels[droneIds.get(droneId)][id].getPosition();
        Vector3f wheelT = FloatMath.transform(getDronePhysics(droneId)
                .getTransMat(), wheel);

        wheelT.add(new Vector3f(x, y, z));
        Physics physics = getDronePhysics(droneId);
        wheelT = FloatMath.transform(physics.getTransMatInv(), wheelT);
        droneModels[droneIds.get(droneId)][id].setPosition(wheelT.x, wheelT.y, wheelT.z);
    }


    private void checkCollision(UpdateHelper updateHelper) {
        Set<Integer> dronesToRemove = new HashSet<>();
        for (int i : droneIds.values()) {
            for (int j : droneIds.values()) {
                if (i < j)
                    if (FloatMath.norm(physics[i].getPosition().sub(
                            physics[j].getPosition())) <= Constants.COLLISION_RANGE) {
                        JOptionPane.showMessageDialog(rootFrame, "Drone "
                                        + getDroneConfig(i).getDroneID() + " and drone " + getDroneConfig(j).getDroneID()
                                        + " collided.", "Collision Exception",
                                JOptionPane.ERROR_MESSAGE);
                        dronesToRemove.add(i);
                        dronesToRemove.add(j);
                    }
            }
        }

        for (int i : dronesToRemove) {
            if (packages[i] != null)
                packages[i].crashed();
            removeDrone(i, updateHelper);
        }
    }
}
