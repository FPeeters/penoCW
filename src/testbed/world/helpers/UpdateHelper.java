package testbed.world.helpers;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import interfaces.AutopilotModule;
import interfaces.AutopilotOutputs;
import testbed.Physics;
import testbed.entities.packages.PackageGenerator;
import testbed.entities.airport.Airport;
import testbed.entities.packages.Package;
import testbed.gui.TestbedGui;

import org.joml.Vector2f;
import org.joml.Vector3f;

import utils.Constants;
import utils.FloatMath;
import utils.PhysicsException;
import utils.IO.MouseInput;
import utils.Utils;

import javax.swing.JOptionPane;

public class UpdateHelper {

    /**
     * All round attributes
     */
    private int TIME_SLOWDOWN_MULTIPLIER;

    /**
     * Camera update cycle
     */
    private CameraHelper cameraHelper;
    private int followDrone;

    /**
     * Airports
     */
    private List<Airport> airports;

    /**
     * Autopilot update
     */
    private AutopilotModule autopilotModule;

    /**
     * TestbedGUI update
     */
    private TestbedGui testbedGui;

    /**
     * The time passed since start of the simulation
     */
    private float time;

    /**
     * Drone update
     */
    private DroneHelper droneHelper;

    /**
     * Package generation
     */
    private Set<Package> packages;
    private PackageGenerator generator;
    private Map<Gate, Package> fromPackages;

    public UpdateHelper(DroneHelper droneHelper, int TIME_SLOWDOWN_MULTIPLIER, CameraHelper cameraHelper, List<Airport> airports,
                        AutopilotModule module, TestbedGui testbedGui, Set<Package> packages, PackageGenerator generator) {
        this.TIME_SLOWDOWN_MULTIPLIER = TIME_SLOWDOWN_MULTIPLIER;
        this.cameraHelper = cameraHelper;
        this.airports = airports;
        this.followDrone = 0;
        this.autopilotModule = module;
        this.testbedGui = testbedGui;
        this.testbedGui.setActiveDrone(followDrone);
        this.time = 0;
        this.droneHelper = droneHelper;
        this.droneHelper.setRootFrame(testbedGui);
        this.packages = packages;
        this.generator = generator;
        this.fromPackages = new HashMap<>();
    }

    public int getFollowDrone() {
        return this.followDrone;
    }

    public void nextFollowDrone() {
        boolean found = false;
        for (Entry<String, Integer> a : droneHelper.droneIds.entrySet()) {
            if (found) {
                this.followDrone = a.getValue();
                this.testbedGui.setActiveDrone(followDrone);
                return;
            }
            if (a.getValue() == followDrone) found = true;
        }

        this.followDrone = droneHelper.droneIds.entrySet().iterator().next().getValue();
        this.testbedGui.setActiveDrone(followDrone);
    }

    public void setFollowDrone(int droneId) {
        if (this.droneHelper.droneIds.containsValue(droneId))
            this.followDrone = droneId;
    }

    /**
     * This function will cycle through all the to update variables
     *
     * @param interval   The passed time (delta time)
     * @param mouseInput This is an artifact of how we set up the update classes at the
     *                   start
     */
    public void updateCycle(float interval, MouseInput mouseInput) {
        this.time += interval / TIME_SLOWDOWN_MULTIPLIER;

        droneHelper.update(interval / TIME_SLOWDOWN_MULTIPLIER, this);

        if (droneHelper.droneIds.isEmpty()) return;

        updatePackages();

        Vector3f newDronePos = droneHelper.getDronePhysics(followDrone).getPosition();

        updateCameraPositions(mouseInput, newDronePos, followDrone);

        updateModule();

        testbedGui.repaint();
    }


    private void updateCameraPositions(MouseInput mouseInput, Vector3f newDronePos, int followDrone) {

        Physics physics = droneHelper.getDronePhysics(followDrone);

        // Update camera based on mouse
        cameraHelper.freeCamera.movePosition(cameraHelper.getCameraInc().x * Constants.CAMERA_POS_STEP,
                cameraHelper.getCameraInc().y * Constants.CAMERA_POS_STEP,
                cameraHelper.getCameraInc().z * Constants.CAMERA_POS_STEP
        );
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            cameraHelper.freeCamera.moveRotation(FloatMath.toRadians(rotVec.x * Constants.MOUSE_SENSITIVITY),
                    FloatMath.toRadians(rotVec.y * Constants.MOUSE_SENSITIVITY), 0);
        }


        cameraHelper.droneCamera.setPosition(newDronePos.x, newDronePos.y, newDronePos.z);
        cameraHelper.droneCamera.setRotation(-physics.getPitch(), -physics.getHeading(), -physics.getRoll());

        float offset = 17.5f;
        cameraHelper.chaseCamera.setPosition(newDronePos.x + offset * (float) Math.sin(physics.getHeading()),
                newDronePos.y, newDronePos.z + offset * (float) Math.cos(physics.getHeading()));
        cameraHelper.chaseCamera.setRotation(0, -physics.getHeading(), 0);

        cameraHelper.updateTopCam(newDronePos);
        cameraHelper.updateRightCam(newDronePos);
    }


    private void updateModule() {
        if (autopilotModule == null)
            return;

        for (int droneId : droneHelper.droneIds.values()) {
            Physics physics = droneHelper.getDronePhysics(droneId);
            autopilotModule.startTimeHasPassed(droneId, Utils.buildInputs(null, physics.getPosition(),
                    physics.getHeading(), physics.getPitch(), physics.getRoll(), this.time));
        }

        for (int droneId : droneHelper.droneIds.values()) {
            AutopilotOutputs output = autopilotModule.completeTimeHasPassed(droneId);

            try {
                droneHelper.getDronePhysics(droneId).updateDrone(output);
            } catch (PhysicsException e) {
                JOptionPane.showMessageDialog(testbedGui, "An illegal force was entered for drone " +
                                droneHelper.getDroneConfig(droneId).getDroneID() + ": " + e.getMessage(),
                        "Physics Exception", JOptionPane.ERROR_MESSAGE);
                droneHelper.removeDrone(droneId, this);
            }
        }
    }


    private void updatePackages() {
        if (generator != null) {
            int[] newDetails = generator.generatePackage(this.time);
            if (newDetails != null)
                addPackage(newDetails);
        }

        int[] newDetails = testbedGui.getNewPackage();
        testbedGui.removePackage();
        if (newDetails != null)
            addPackage(newDetails);


        for (int drone : droneHelper.droneIds.values()) {
            Physics physics = droneHelper.getDronePhysics(drone);
            if (FloatMath.norm(physics.getVelocity()) > 1)
                continue;

            int loc = physics.getAirportLocation();
            Gate gate;
            Package pack = droneHelper.getDronePackage(drone);
            switch (loc) {
                case Physics.GATE_0:
                    gate = new Gate(physics.getAirportNb(), 0);
                    if (pack == null) {
                        if (fromPackages.containsKey(gate))
                            droneHelper.collectPackage(drone, fromPackages.remove(gate));
                    } else {
                        if (new Gate(pack, false).equals(gate)) {
                            packages.remove(pack);
                            pack.cleanup();
                            droneHelper.deliverPackage(drone);
                        }
                    }
                    break;
                case Physics.GATE_1:
                    gate = new Gate(physics.getAirportNb(), 1);
                    if (pack == null) {
                        if (fromPackages.containsKey(gate))
                            droneHelper.collectPackage(drone, fromPackages.remove(gate));
                    } else {
                        if (new Gate(pack, false).equals(gate)) {
                            droneHelper.deliverPackage(drone);
                            pack.cleanup();
                            packages.remove(pack);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    boolean started = false;

    private void addPackage(int[] details) {
        Package newPackage = new Package(details);
        Gate fromGate = new Gate(newPackage, true);

        if (newPackage.getFromAirport() != newPackage.getDestAirport()
                && !fromPackages.containsKey(fromGate)) {

            if (started && droneCarryingPresent(fromGate)) return;
            started = true;

            fromPackages.put(fromGate, newPackage);
            packages.add(newPackage);

            Airport port = airports.get(fromGate.airportNb);
            Vector3f pos = new Vector3f(port.getPosition());
            pos.add(port.getPerpDirection().mul(port.getWidth() / 2f * (fromGate.gateNb == 0 ? 1 : -1), new Vector3f()));
            newPackage.setPosition(pos.add(new Vector3f(0, 1, 0)));

            testbedGui.addPackage(newPackage);

            if (autopilotModule != null)
                autopilotModule.deliverPackage(newPackage.getFromAirport(), newPackage.getFromGate(),
                        newPackage.getDestAirport(), newPackage.getDestGate());
        }
    }

    private boolean droneCarryingPresent(Gate fromGate) {
        for (int id : droneHelper.droneIds.values()) {
            if (droneHelper.getDronePhysics(id).getAirportNb() == fromGate.airportNb) {
                int gateId = droneHelper.getDronePhysics(id).getAirportLocation() - Physics.GATE_0;
                if (gateId == fromGate.gateNb) {
                    return true;
                }
            }
        }
        return false;
    }


    private static class Gate {

        public final int airportNb, gateNb;

        public Gate(int airportNb, int gateNb) {
            this.airportNb = airportNb;
            this.gateNb = gateNb;
        }

        public Gate(Package pack, boolean from) {
            if (from) {
                this.airportNb = pack.getFromAirport();
                this.gateNb = pack.getFromGate();
            } else {
                this.airportNb = pack.getDestAirport();
                this.gateNb = pack.getDestGate();
            }
        }

        @Override
        public int hashCode() {
            return 2048 * airportNb + gateNb;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null)
                return false;
            if (!(other instanceof Gate))
                return false;
            return (this.airportNb == ((Gate) other).airportNb && this.gateNb == ((Gate) other).gateNb);
        }
    }

}
