package testbed.graphics.meshes.tile;

import testbed.graphics.meshes.AbstractMesh;
import utils.graphics.RGBTuple;

public class DoubleUnitTile extends AbstractMesh {

    RGBTuple color;

    public DoubleUnitTile(RGBTuple color) {
        this.color = color;
        finalizer();
    }

    @Override
    protected void setPositions() {
        this.positions = new float[]{
                0, 0, 0,          //0
                1, 0, -1,   //1
                1, 0, 1,    //2
                -1, 0, 1,   //3
                -1, 0, -1,  //4

        };
    }

    @Override
    protected void setColours() {
        this.colours = new float[]{
                color.getRed(), color.getGreen(), color.getBlue(),
                color.getRed(), color.getGreen(), color.getBlue(),
                color.getRed(), color.getGreen(), color.getBlue(),
                color.getRed(), color.getGreen(), color.getBlue(),
                color.getRed(), color.getGreen(), color.getBlue(),
        };
    }

    @Override
    protected void setIndices() {
        this.indices = new int[]{
                1, 2, 3,
                3, 4, 1,
        };
    }

    public int getSize() {
        return 2;
    }

}
