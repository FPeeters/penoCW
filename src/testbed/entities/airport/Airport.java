package testbed.entities.airport;

import org.joml.Vector3f;

import testbed.entities.WorldObject;
import utils.FloatMath;
import utils.Utils;

public class Airport {

    private final Vector3f position;
    /**
     * Wijst naar baan 0.
     */
    private final Vector3f direction;
    /**
     * Wijst naar gate 0.
     * <p>
     * direction x directionPerp == (0,1,0)
     */
    private final Vector3f directionPerp;

    private final float width, length;

    private Tarmac tarmac0, tarmac1;

    private Gate gate0, gate1;


    public Airport(float width, float length, Vector3f position, float heading) {
        this.position = position;

        this.width = width;
        this.length = length;

        this.direction = new Vector3f(-FloatMath.sin(heading), 0, -FloatMath.cos(heading));
        this.directionPerp = new Vector3f(-FloatMath.cos(heading), 0, FloatMath.sin(heading));

        this.tarmac0 = new Tarmac(position.add(direction.mul(width / 2f, new Vector3f()), new Vector3f()), 2f * width, length, heading);
        this.tarmac1 = new Tarmac(position.sub(direction.mul(width / 2f, new Vector3f()), new Vector3f()), 2f * width, length,
                heading > 0 ? heading - FloatMath.PI : heading + FloatMath.PI);

        this.gate0 = new Gate(position.add(directionPerp.mul(width / 2f, new Vector3f())
                .sub(direction.mul(width / 2f, new Vector3f()), new Vector3f()), new Vector3f()), width, heading, Utils.toRGB(250, 0.1f, 0.9f));
        this.gate1 = new Gate(position.sub(directionPerp.mul(width / 2f, new Vector3f())
                .add(direction.mul(width / 2f, new Vector3f()), new Vector3f()), new Vector3f()), width, heading, Utils.toRGB(250, 0.1f, 0.6f));
    }


    public WorldObject[] getObjects() {
        return new WorldObject[]{tarmac0.getObject(), tarmac1.getObject(), gate0.getObject(), gate1.getObject()};
    }


    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public Vector3f getPerpDirection() {
        return directionPerp;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }
}
