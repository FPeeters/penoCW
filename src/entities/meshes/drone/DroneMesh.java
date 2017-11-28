package entities.meshes.drone;

import entities.meshes.Mesh;
import interfaces.AutopilotConfig;
import physics.Drone;

public class DroneMesh {


    private LeftWing left;
    private RightWing right;
    private Body body;

    public DroneMesh(AutopilotConfig config){

        left = new LeftWing(config);
        right = new RightWing(config);
        body = new Body(config);
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
}
