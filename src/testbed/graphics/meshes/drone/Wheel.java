package testbed.graphics.meshes.drone;

import interfaces.AutopilotConfig;

public class Wheel extends DroneComponent {

    private float radius, thickness;

    public Wheel(AutopilotConfig config, float thickness) {
        this.thickness = thickness;
        this.radius = config.getTyreRadius();
        finalizer(config);
    }

    @Override
    protected void setPositions() {
        this.positions = new float[]{
                this.thickness, 0, 0, //0
                this.thickness, this.radius, 0, //1
                this.thickness, 0.707f * this.radius, -0.707f * this.radius, //2
                this.thickness, 0, -this.radius, //3
                this.thickness, -0.707f * this.radius, -0.707f * this.radius, //4
                this.thickness, -this.radius, 0, //5
                this.thickness, -0.707f * this.radius, 0.707f * this.radius, //6
                this.thickness, 0, this.radius, //7
                this.thickness, 0.707f * this.radius, 0.707f * this.radius, //8

                -this.thickness, 0, 0, //9
                -this.thickness, this.radius, 0, //10
                -this.thickness, 0.707f * this.radius, -0.707f * this.radius, //11
                -this.thickness, 0, -this.radius, //12
                -this.thickness, -0.707f * this.radius, -0.707f * this.radius, //13
                -this.thickness, -this.radius, 0, //14
                -this.thickness, -0.707f * this.radius, 0.707f * this.radius, //15
                -this.thickness, 0, this.radius, //16
                -this.thickness, 0.707f * this.radius, 0.707f * this.radius, //17
        };
    }

    @Override
    protected void setColours() {
        this.colours = new float[]{
                0.2f, 0.2f, 0.2f,
                0f, 0f, 0f,
                0.8f, 0.8f, 0.8f,
                0f, 0f, 0f,
                0.8f, 0.8f, 0.8f,
                0f, 0f, 0f,
                0.8f, 0.8f, 0.8f,
                0f, 0f, 0f,
                0.8f, 0.8f, 0.8f,

                0.2f, 0.2f, 0.2f,
                0f, 0f, 0f,
                0.8f, 0.8f, 0.8f,
                0f, 0f, 0f,
                0.8f, 0.8f, 0.8f,
                0f, 0f, 0f,
                0.8f, 0.8f, 0.8f,
                0f, 0f, 0f,
                0.8f, 0.8f, 0.8f,
        };
    }

    @Override
    protected void setIndices() {
        this.indices = new int[]{
                //right face
                0, 1, 2,
                0, 2, 3,
                0, 3, 4,
                0, 4, 5,
                0, 5, 6,
                0, 6, 7,
                0, 7, 8,
                0, 8, 1,
                //left face
                9, 10, 11,
                9, 11, 12,
                9, 12, 13,
                9, 13, 14,
                9, 14, 15,
                9, 15, 16,
                9, 16, 17,
                9, 17, 10,

                //connectors also clockwise
                1, 10, 11, 1, 11, 2,
                2, 3, 11, 11, 3, 12,
                3, 4, 12, 4, 12, 13,
                4, 5, 13, 5, 14, 13,
                5, 6, 14, 6, 14, 15,
                6, 7, 16, 6, 16, 15,
                7, 8, 17, 16, 17, 7,
                1, 8, 10, 8, 17, 10,
        };
    }

}
