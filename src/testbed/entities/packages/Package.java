package testbed.entities.packages;

import org.joml.Vector3f;

import testbed.entities.WorldObject;
import utils.Cubes;

public class Package {

    public static final int WAITING = 0,
            IN_PROGRESS = 1,
            DELIVERED = 2,
            CRASHED = 3;

    private final int from, dest;

    private final int fromGate, destGate;

    private int status;

    private WorldObject cube;

    public Package(int from, int dest, int fromGate, int destGate) {
        this.from = from;
        this.dest = dest;

        this.fromGate = fromGate;
        this.destGate = destGate;

        this.status = WAITING;

        this.cube = new WorldObject(Cubes.getPinkCube().getMesh());
        this.cube.setScale(1);
    }

    public Package(int[] details) {
        this(details[0], details[2], details[1], details[3]);
    }


    public int getFromAirport() {
        return this.from;
    }

    public int getDestAirport() {
        return this.dest;
    }

    public int getFromGate() {
        return this.fromGate;
    }

    public int getDestGate() {
        return this.destGate;
    }

    public void pickUp() {
        this.status = IN_PROGRESS;
    }

    public void deliver() {
        this.status = DELIVERED;
    }

    public void crashed() {
        this.status = CRASHED;
    }

    public String getStatusDesc() {
        switch (this.status) {
            case WAITING:
                return "Waiting";

            case IN_PROGRESS:
                return "In progress";

            case DELIVERED:
                return "Delivered";

            case CRASHED:
                return "Crashed";

            default:
                return "";
        }
    }

    public boolean isAlive() {
        return this.status == WAITING || this.status == IN_PROGRESS;
    }


    public void setPosition(Vector3f pos) {
        this.cube.setPosition(pos);
    }

    public WorldObject getCube() {
        return this.cube;
    }


    public void cleanup() {
        this.cube.getMesh().cleanUp();
    }


}
