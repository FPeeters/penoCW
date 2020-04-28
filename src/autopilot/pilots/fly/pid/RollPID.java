package autopilot.pilots.fly.pid;

import utils.FloatMath;

import com.stormbots.MiniPID;

import autopilot.pilots.FlyPilot;
import autopilot.pilots.FlyPilot.State;
import interfaces.AutopilotInputs;

public class RollPID {

    MiniPID rollPID;

    FlyPilot pilot;

    public RollPID(FlyPilot pilot) {
        rollPID = new MiniPID(0.3, 0, 0);
        rollPID.setOutputLimits(Math.toRadians(2.5f));

        this.pilot = pilot;
    }

    public void adjustRoll(AutopilotInputs inputs, float target, State state) {
        rollPID.setSetpoint(target);
        float actual = inputs.getRoll();
        float output = (float) rollPID.getOutput(actual);
        if (state == State.StrongUp) {
            output = 0.5f * output;
        }
        pilot.setLeftWingInclination(FloatMath.toRadians(7) - output);
        pilot.setRightWingInclination(FloatMath.toRadians(7) + output);
    }

}
