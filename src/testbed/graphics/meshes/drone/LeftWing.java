package testbed.graphics.meshes.drone;

import interfaces.AutopilotConfig;

public class LeftWing extends DroneComponent {

    private float thickness;

    public LeftWing(AutopilotConfig config, float thickness) {
        this.thickness = thickness;
        finalizer(config);
    }

    @Override
    protected void setPositions() {
        this.positions = new float[]{
                //left wing top
                -config.getTailSize() / 8, this.thickness, -config.getWingX() / 4, //#0
                config.getWingX() * -2f, this.thickness, -config.getWingX() / 4, //#1
                config.getWingX() * -2f, this.thickness, config.getWingX() / 4, //#2
                -config.getTailSize() / 8, this.thickness, config.getWingX() / 4, //#3

                //left wing bot
                -config.getTailSize() / 8, -this.thickness, -config.getWingX() / 4, //#0
                config.getWingX() * -2f, -this.thickness, -config.getWingX() / 4, //#1
                config.getWingX() * -2f, -this.thickness, config.getWingX() / 4, //#2
                -config.getTailSize() / 8, -this.thickness, config.getWingX() / 4, //#3
        };
    }

    @Override
    protected void setColours() {
        this.colours = new float[]{
                //Face 1 (front)
                1f, 0f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0f, 0.0f,
                //bot
                1f, 0f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0.5f, 0.0f,
                1f, 0f, 0.0f,
        };
    }

    @Override
    protected void setIndices() {
        this.indices = new int[]{
                //Top wing plane
                1, 2, 0, 3, 2, 0,
                //Bot wing plane
                5, 6, 4, 7, 6, 4,
                //Back
                5, 0, 4, 5, 1, 0,
                //Font 
                6, 2, 3, 6, 3, 7,
                //left side
                1, 2, 5, 5, 2, 6,
                //right side
                7, 0, 3, 7, 3, 4,
        };
    }

}
