package testbed.graphics.meshes.cube;

import testbed.graphics.meshes.Mesh;
import utils.Utils;

public class Cube {

    private float[] positions;
    private float[] colours;
    private int[] indices;
    private Mesh mesh;

    public Cube(int hue, float saturation) {
        setPositions();
        setColours(hue, saturation);
        setIndices();
        this.mesh = new Mesh(this.getPositions(), this.getColours(), this.getIndices());
    }

    private void setPositions() {
        this.positions = new float[]{
                //Face 1 (front)
                -0.5f, 0.5f, 0.5f, //V0 #0
                -0.5f, -0.5f, 0.5f, //V1 #1
                0.5f, -0.5f, 0.5f, //V2 #2
                0.5f, 0.5f, 0.5f, //V3 #3

                //Face 2 (top)
                -0.5f, 0.5f, 0.5f, //V0 #4
                0.5f, 0.5f, 0.5f, //V3 #5
                -0.5f, 0.5f, -0.5f, //V4 #6
                0.5f, 0.5f, -0.5f, //V5 #7

                //Face 3 (back)
                -0.5f, 0.5f, -0.5f, //V4 #8
                0.5f, 0.5f, -0.5f, //V5 #9
                -0.5f, -0.5f, -0.5f, //V6 #10
                0.5f, -0.5f, -0.5f, //V7 #11

                //Face 4 (bottom)
                -0.5f, -0.5f, 0.5f, //V1 #12
                -0.5f, -0.5f, -0.5f, //V6 #13
                0.5f, -0.5f, -0.5f, //V7 #14
                0.5f, -0.5f, 0.5f, //V2 #15

                //Face 5 (left)
                -0.5f, 0.5f, 0.5f, //V0 #16
                -0.5f, 0.5f, -0.5f, //V4 #17
                -0.5f, -0.5f, 0.5f, //V1 #18
                -0.5f, -0.5f, -0.5f, //V6 #19

                //Face 6 (right)
                0.5f, 0.5f, 0.5f, //V3 #20
                0.5f, -0.5f, 0.5f, //V2 #21
                0.5f, 0.5f, -0.5f, //V5 #22
                0.5f, -0.5f, -0.5f, //V7 #23
        };
    }

    public void setColours(int hue, float saturation) {
        float[] posY = Utils.toRGB(hue, saturation, 0.45f),
                negY = Utils.toRGB(hue, saturation, 0.20f),
                posX = Utils.toRGB(hue, saturation, 0.40f),
                negX = Utils.toRGB(hue, saturation, 0.25f),
                posZ = Utils.toRGB(hue, saturation, 0.35f),
                negZ = Utils.toRGB(hue, saturation, 0.30f);

        this.colours = new float[]{
                //Face 1 (front) pos Z
                posZ[0], posZ[1], posZ[2],
                posZ[0], posZ[1], posZ[2],
                posZ[0], posZ[1], posZ[2],
                posZ[0], posZ[1], posZ[2],

                //Face 2 (top) pos Y
                posY[0], posY[1], posY[2],
                posY[0], posY[1], posY[2],
                posY[0], posY[1], posY[2],
                posY[0], posY[1], posY[2],

                //Face 3 (back) neg Z
                negZ[0], negZ[1], negZ[2],
                negZ[0], negZ[1], negZ[2],
                negZ[0], negZ[1], negZ[2],
                negZ[0], negZ[1], negZ[2],

                //Face 4 (bottom) neg Y
                negY[0], negY[1], negY[2],
                negY[0], negY[1], negY[2],
                negY[0], negY[1], negY[2],
                negY[0], negY[1], negY[2],

                //Face 5 (left) neg X
                negX[0], negX[1], negX[2],
                negX[0], negX[1], negX[2],
                negX[0], negX[1], negX[2],
                negX[0], negX[1], negX[2],

                //Face 6 (right) pos X
                posX[0], posX[1], posX[2],
                posX[0], posX[1], posX[2],
                posX[0], posX[1], posX[2],
                posX[0], posX[1], posX[2],
        };
    }

    private void setIndices() {
        this.indices = new int[]{
                // Front face
                1, 0, 2, 3, 2, 0,
                // Top face
                6, 7, 4, 5, 4, 7,
                // Back face
                8, 9, 10, 11, 10, 9,
                // Bottom face
                12, 13, 15, 14, 15, 13,
                // Left face
                16, 17, 18, 19, 18, 17,
                // Right face
                20, 22, 21, 23, 21, 22,
        };
    }

    public Mesh getMesh() {
        return mesh;
    }

    public float[] getPositions() {
        return positions;
    }

    public float[] getColours() {
        return colours;
    }

    public int[] getIndices() {
        return indices;
    }
}

/*
// V0
-0.5f, 0.5f, 0.5f,
// V1
-0.5f, -0.5f, 0.5f,
// V2
0.5f, -0.5f, 0.5f,
// V3
0.5f, 0.5f, 0.5f,
// V4
-0.5f, 0.5f, -0.5f,
// V5
0.5f, 0.5f, -0.5f,
// V6
-0.5f, -0.5f, -0.5f,
// V7
0.5f, -0.5f, -0.5f
*/