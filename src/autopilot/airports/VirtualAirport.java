package autopilot.airports;

import org.joml.Vector3f;
import utils.FloatMath;

public class VirtualAirport {

    public VirtualAirport(int id, Vector3f position, float heading, float width, float length) {
        this.id = id;
        this.position = position;
        this.heading = heading;
        this.width = width;
        this.length = length;

        Vector3f directionPerp = new Vector3f(-FloatMath.cos(heading), 0, FloatMath.sin(heading));

        this.gates = new Vector3f[]{
                position.add(directionPerp.mul(width / 2f, new Vector3f()), new Vector3f()),
                position.sub(directionPerp.mul(width / 2f, new Vector3f()), new Vector3f())};
    }

    private Vector3f position;
    private Vector3f[] gates;
    private float heading;
    private float width, length;
    private int id;

    public int getId() {
        return this.id;
    }

    public Vector3f getGate(int i) {
        return gates[i];
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getHeading() {
        return heading;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

}
