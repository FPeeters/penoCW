package utils;

import testbed.graphics.meshes.cube.BufferedCube;
import testbed.graphics.meshes.cube.Cube;

public class Cubes {

    public static Cube getRedCube() {
        return new Cube(0, 1f);
    }

    public static Cube getYellowCube() {
        return new Cube(60, 1f);
    }

    public static Cube getGreenCube() {
        return new Cube(120, 1f);
    }

    public static Cube getCyanCube() {
        return new Cube(180, 1f);
    }

    public static Cube getBlueCube() {
        return new Cube(240, 1f);
    }

    public static Cube getPinkCube() {
        return new Cube(300, 1f);
    }

    public static final BufferedCube redBuffCube = new BufferedCube(0, 1f);
    public static final BufferedCube yellowBuffCube = new BufferedCube(60, 1f);
    public static final BufferedCube greenBuffCube = new BufferedCube(120, 1f);
    public static final BufferedCube cyanBuffCube = new BufferedCube(180, 1f);
    public static final BufferedCube blueBuffCube = new BufferedCube(240, 1f);
    public static final BufferedCube pinkBuffCube = new BufferedCube(300, 1f);

    public static BufferedCube[] getBufferedCubes() {
        return new BufferedCube[]{redBuffCube, greenBuffCube, blueBuffCube, yellowBuffCube, cyanBuffCube, pinkBuffCube};
    }

}
