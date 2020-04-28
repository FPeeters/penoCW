package autopilot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public class LogPilot implements Autopilot {

    private BufferedWriter writer;
    private float time = 0f;

    @Override
    public AutopilotOutputs timePassed(AutopilotInputs inputs) {
        time += inputs.getElapsedTime();
        try {
            writer.write(time + ": " + inputs.getX() + " " + inputs.getY() + " " + inputs.getZ() + " " + inputs.getHeading() + " " + inputs.getPitch() + " " + inputs.getRoll() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return utils.Utils.buildOutputs(0, 0, 0, 0, 0, -1, -1, -1);
    }

    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
        time += inputs.getElapsedTime();
        try {
            File file = new File("position.log");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write("position log, x  y  z  heading  pitch  roll\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return utils.Utils.buildOutputs(0, 0, 0, 0, 0, -1, -1, -1);
    }

    @Override
    public void simulationEnded() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
