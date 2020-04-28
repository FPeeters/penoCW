package testbed.entities.airport;

import org.joml.Vector3f;

import testbed.entities.WorldObject;
import testbed.graphics.meshes.airport.GateMesh;
import utils.graphics.RGBTuple;

public class Gate {

    private Vector3f position;
    private float width, rotation;
    private WorldObject object;

    public Gate(Vector3f position, float width, float rotation, float[] color) {
        this.position = position;
        this.width = width;
        this.rotation = rotation;

        RGBTuple colorGate = new RGBTuple();
        colorGate.set(color);

        float UP_DELTA = 0.05f;
        this.object = new WorldObject(new GateMesh(width, UP_DELTA, colorGate).getMesh());
        this.object.setPosition(position);
        this.object.setRotation(0, -rotation, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getRotation() {
        return rotation;
    }

    public WorldObject getObject() {
        return object;
    }

}
