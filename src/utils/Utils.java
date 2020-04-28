package utils;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.BufferUtils.*;

import interfaces.*;

public class Utils {

    /**
     * Read a file given by filename. It will start searching in the folder marked a resource location!
     *
     * @param fileName The name of the file (with path of necessary)
     * @return The content of the file as a String
     * @throws Exception If something goes wrong
     */
    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    /**
     * Converts a hsv color to rgb
     *
     * @param h hue value, range: 0-360
     * @param s saturation value, range: 0-1
     * @param v value value ???, range: 0-1
     * @return {r, g, b}
     * range 0-1
     */
    public static float[] toRGB(int h, float s, float v) {
        Color rgb = new Color(Color.HSBtoRGB(h / 360f, s, v));
        return new float[]{rgb.getRed() / 255f, rgb.getGreen() / 255f, rgb.getBlue() / 255f};
    }

    public static AutopilotOutputs buildOutputs(float lwIncl, float rwIncl, float verStabIncl, float horStabIncl,
                                                float thrust, float lBrake, float fBrake, float rBrake) {
        return new AutopilotOutputs() {
            public float getVerStabInclination() {
                return verStabIncl;
            }

            public float getThrust() {
                return thrust;
            }

            public float getRightWingInclination() {
                return rwIncl;
            }

            public float getLeftWingInclination() {
                return lwIncl;
            }

            public float getHorStabInclination() {
                return horStabIncl;
            }

            public float getFrontBrakeForce() {
                return fBrake;
            }

            public float getLeftBrakeForce() {
                return lBrake;
            }

            public float getRightBrakeForce() {
                return rBrake;
            }
        };
    }


    public static AutopilotInputs buildInputs(byte[] image, float x, float y, float z, float heading, float pitch, float roll, float dt) {
        return new AutopilotInputs() {
            public float getZ() {
                return z;
            }

            public float getY() {
                return y;
            }

            public float getX() {
                return x;
            }

            public float getRoll() {
                return roll;
            }

            public float getPitch() {
                return pitch;
            }

            public byte[] getImage() {
                return image;
            }

            public float getHeading() {
                return heading;
            }

            public float getElapsedTime() {
                return dt;
            }
        };
    }


    public static AutopilotInputs buildInputs(byte[] image, Vector3f pos, float heading, float pitch, float roll, float dt) {
        return buildInputs(image, pos.x, pos.y, pos.z, heading, pitch, roll, dt);
    }

    public static int[] buildIntArr(int... args) {
        return args;
    }

    public static float getEngineZ(AutopilotConfig config) {
        return config.getTailMass() / config.getEngineMass() * config.getTailSize();
    }

    public static AutopilotConfig createDefaultConfig(String id) {
        return new AutopilotConfig() {
            public float getGravity() {
                return Constants.DEFAULT_GRAVITY;
            }

            public float getWingX() {
                return Constants.DEFAULT_WINGX;
            }

            public float getTailSize() {
                return Constants.DEFAULT_TAILSIZE;
            }

            public float getEngineMass() {
                return Constants.DEFAULT_ENGINE_MASS;
            }

            public float getWingMass() {
                return Constants.DEFAULT_WING_MASS;
            }

            public float getTailMass() {
                return Constants.DEFAULT_TAIL_MASS;
            }

            public float getMaxThrust() {
                return Constants.DEFAULT_MAX_THRUST;
            }

            public float getMaxAOA() {
                return FloatMath.toRadians(Constants.DEFAULT_MAX_AOA);
            }

            public float getWingLiftSlope() {
                return Constants.DEFAULT_WING_LIFTSLOPE;
            }

            public float getHorStabLiftSlope() {
                return Constants.DEFAULT_HOR_STAB_LIFTSLOPE;
            }

            public float getVerStabLiftSlope() {
                return Constants.DEFAULT_VER_STAB_LIFTSLOPE;
            }

            public float getHorizontalAngleOfView() {
                return FloatMath.toRadians(Constants.DEFAULT_HOR_FOV);
            }

            public float getVerticalAngleOfView() {
                return FloatMath.toRadians(Constants.DEFAULT_VER_FOV);
            }

            public int getNbColumns() {
                return Constants.DEFAULT_NB_COLS;
            }

            public int getNbRows() {
                return Constants.DEFAULT_NB_ROWS;
            }

            public String getDroneID() {
                return id;
            }

            public float getWheelY() {
                return -1.37f;
            }

            public float getFrontWheelZ() {
                return -2.1f;
            }

            public float getRearWheelZ() {
                return 1f;
            }

            public float getRearWheelX() {
                return 1.39f;
            }

            public float getTyreSlope() {
                return 50000f;
            }

            public float getDampSlope() {
                return 5000f;
            }

            public float getTyreRadius() {
                return 0.2f;
            }

            public float getRMax() {
                return 2000;
            }

            public float getFcMax() {
                return 0.7f;
            }
        };
    }

    public static boolean euclDistance(Vector3f start, Vector3f end, float distance) {
        return Math.abs(start.distance(end)) >= distance;
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1);
            }
        } else {
            try (
                    InputStream source = Utils.class.getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }


}
