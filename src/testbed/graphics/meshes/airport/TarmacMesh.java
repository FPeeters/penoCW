package testbed.graphics.meshes.airport;

import testbed.graphics.meshes.AbstractMesh;
import utils.graphics.RGBTuple;

public class TarmacMesh extends AbstractMesh {

    private float width, length, height;
    RGBTuple tarm, stripe;

    public TarmacMesh(float width, float length, float height, RGBTuple colorTarmac, RGBTuple colorStripes) {
        this.width = width;
        this.length = length;
        this.height = height;

        this.tarm = colorTarmac;
        this.stripe = colorStripes;

        finalizer();
    }

    @Override
    protected void setPositions() {
        this.positions = new float[]{
                0, height, 0,
                width / 2, height, 0,
                -width / 2, height, 0,
                width / 2, height, -length,
                -width / 2, height, -length,
        };
    }

    @Override
    protected void setColours() {
        this.colours = new float[]{
                tarm.getRed(), tarm.getGreen(), tarm.getBlue(),
                tarm.getRed(), tarm.getGreen(), tarm.getBlue(),
                tarm.getRed(), tarm.getGreen(), tarm.getBlue(),
                tarm.getRed(), tarm.getGreen(), tarm.getBlue(),
                tarm.getRed(), tarm.getGreen(), tarm.getBlue(),
        };
    }

    @Override
    protected void setIndices() {
        this.indices = new int[]{
                1, 2, 3,
                2, 3, 4,
        };
    }
}
