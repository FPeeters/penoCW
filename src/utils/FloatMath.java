package utils;

import org.joml.Matrix3f;
import org.joml.Vector3f;

public class FloatMath {

    /**
     * Calculates sin(x).
     */
    public static float sin(float x) {
        return (float) Math.sin(x);
    }

    /**
     * Calculates cos(x).
     */
    public static float cos(float x) {
        return (float) Math.cos(x);
    }

    /**
     * Calculates a x b, without changing a or b.
     */
    public static Vector3f cross(Vector3f a, Vector3f b) {
        return a.cross(b, new Vector3f());
    }

    /**
     * Calculates atan2(y,x), following Math.atan2.
     */
    public static float atan2(float y, float x) {
        return (float) Math.atan2(y, x);
    }

    /**
     * Calculates the norm of v.
     */
    public static float norm(Vector3f v) {
        return (float) Math.sqrt(FloatMath.squareNorm(v));
    }

    /**
     * Calculates the square of the norm v.
     */
    public static float squareNorm(Vector3f v) {
        return v.dot(v);
    }

    /**
     * Calculates a**2
     */
    public static float square(float a) {
        return a * a;
    }

    /**
     * Converts the given angle from degrees to radians.
     */
    public static float toRadians(float deg) {
        return (float) Math.toRadians(deg);
    }

    /**
     * Converts the given angle from radians to degrees.
     */
    public static float toDegrees(float rad) {
        return (float) Math.toDegrees(rad);
    }

    /**
     * PI in float
     */
    public static final float PI = (float) Math.PI;

    /**
     * Transforms the given vector by the given transformation matrix.
     */
    public static Vector3f transform(Matrix3f mat, Vector3f vec) {
        return mat.transform(vec, new Vector3f());
    }

    /**
     * Rounds the given value to a value with the given amount of decimal places.
     */
    public static float round(float value, int precision) {
        double base = Math.pow(10, precision);
        return (float) (Math.round(value * base) / base);
    }
}
