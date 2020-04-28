package testbed.entities.drone;

import interfaces.AutopilotConfig;
import testbed.graphics.meshes.Mesh;
import testbed.graphics.meshes.drone.Body;
import testbed.graphics.meshes.drone.LeftWing;
import testbed.graphics.meshes.drone.RightWing;
import testbed.graphics.meshes.drone.Wheel;
import utils.Constants;

public class DroneSkeleton {

    private LeftWing left;
    private RightWing right;
    private Body body;
    private Wheel wheel;

    public DroneSkeleton(AutopilotConfig config) {
        left = new LeftWing(config, Constants.DRONE_THICKNESS / 2);
        right = new RightWing(config, Constants.DRONE_THICKNESS / 2);
        body = new Body(config, Constants.DRONE_THICKNESS);
        wheel = new Wheel(config, Constants.DRONE_WHEEL_THICKNESS);
    }

    public Mesh getLeft() {
        return left.getMesh();
    }

    public Mesh getRight() {
        return right.getMesh();
    }

    public Mesh getBody() {
        return body.getMesh();
    }

    public Mesh getWheel() {
        return wheel.getMesh();
    }

}
