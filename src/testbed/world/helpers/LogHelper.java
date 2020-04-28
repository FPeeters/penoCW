package testbed.world.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import testbed.Physics;

public class LogHelper {

    private BufferedWriter writer;

    public LogHelper() {
        initLogging();
    }


    private void initLogging() {
        try {
            File file = new File("position.log");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write("position log, x  y  z  heading  pitch  roll lincl hincl rincl vinlc thrust\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void log(float time, Physics physics) {
        try {
            writer.write(time + ": " + physics.getPosition().x + " " + physics.getPosition().y + " "
                    + physics.getPosition().z + " " + physics.getHeading() + " " + physics.getPitch() + " "
                    + physics.getRoll() + " " + physics.getLWInclination() + " " + physics.getHSInclination() + " "
                    + physics.getRWInclination() + " " + physics.getVSInclination() + " " + physics.getThrust()
                    + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void close() {
        try {
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
