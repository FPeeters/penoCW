package testbed.graphics;

import org.joml.Vector3f;

public class Camera {

    /**
     * The position of the camera
     * The camera itself will never move so we will move the entire world infront of the camera.
     */
    private final Vector3f position;

    /**
     * Camera rotation
     */
    private final Vector3f rotation;

    /**
     * Initialize a camera with basic rotation and location
     */
    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
    }

    /**
     * Init with given pos en rotation
     *
     * @param position Given pos
     * @param rotation Given rot
     */
    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -offsetZ;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -offsetX;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }
}