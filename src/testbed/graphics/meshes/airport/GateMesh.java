package testbed.graphics.meshes.airport;

import testbed.graphics.meshes.AbstractMesh;
import utils.graphics.RGBTuple;

public class GateMesh extends AbstractMesh {

    private float width, height;
    RGBTuple gate;

    public GateMesh(float width, float height, RGBTuple colorGate) {
        this.width = width;
        this.height = height;

        this.gate = colorGate;

        finalizer();
    }

    @Override
    protected void setPositions() {
        this.positions = new float[]{
                0, height, 0,
                width / 2, height, 0,
                -width / 2, height, 0,
                width / 2, height, -width,
                -width / 2, height, -width,
        };
    }

    @Override
    protected void setColours() {
        this.colours = new float[]{
                gate.getRed(), gate.getGreen(), gate.getBlue(),
                gate.getRed(), gate.getGreen(), gate.getBlue(),
                gate.getRed(), gate.getGreen(), gate.getBlue(),
                gate.getRed(), gate.getGreen(), gate.getBlue(),
                gate.getRed(), gate.getGreen(), gate.getBlue(),
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
