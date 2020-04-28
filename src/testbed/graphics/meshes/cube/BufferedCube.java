package testbed.graphics.meshes.cube;

public class BufferedCube {

    private final int hue;
    private final float saturation;

    public BufferedCube(int hue, float saturation) {
        this.hue = hue;
        this.saturation = saturation;
    }

    public Cube setup() {
        return new Cube(hue, saturation);
    }
}
