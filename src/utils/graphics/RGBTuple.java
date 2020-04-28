package utils.graphics;

public class RGBTuple {

    float red, blue, green;

    public RGBTuple() {
    }

    public RGBTuple(float red, float blue, float green) {
        this.red = red;
        this.blue = blue;
        this.green = green;
    }

    public void set(float[] col) {
        this.red = col[0];
        this.blue = col[1];
        this.green = col[2];
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
    }


}
