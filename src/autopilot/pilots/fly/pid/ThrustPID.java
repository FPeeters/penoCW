package autopilot.pilots.fly.pid;

import interfaces.AutopilotInputs;

import com.stormbots.MiniPID;

import autopilot.pilots.FlyPilot;

public class ThrustPID {

    MiniPID thrustUpPID, thrustDownPID, thrustTurnPID;
    FlyPilot pilot;

    public ThrustPID(FlyPilot pilot) {
        thrustUpPID = new MiniPID(8, 0, 3);
        thrustUpPID.setOutputLimits(2, pilot.getMaxThrust() / 200);
        thrustDownPID = new MiniPID(1, 0, 0);
        thrustDownPID.setOutputLimits(0, pilot.getMaxThrust() / 200);
        thrustTurnPID = new MiniPID(4, 0, 0);
        thrustTurnPID.setOutputLimits(0, pilot.getMaxThrust() / 200);

        this.pilot = pilot;
    }

    // PID sets thrust so that y component of velocity is equal to target.
    public void adjustThrustUp(AutopilotInputs inputs, float target) {
        thrustUpPID.setSetpoint(target);
        float actual = pilot.approxVel.y();
        float output = (float) thrustUpPID.getOutput(actual);

        pilot.setNewThrust(output * 200);
    }

    public void adjustThrustDown(AutopilotInputs inputs, float target) {
        thrustDownPID.setSetpoint(target);
        float actual = pilot.approxVel.y();
        float output = (float) thrustDownPID.getOutput(actual);

        pilot.setNewThrust(output * 200);
    }

    public void adjustThrustTurn(AutopilotInputs inputs, float target) {
        thrustTurnPID.setSetpoint(target);
        float actual = pilot.approxVel.y();
        float output = (float) thrustTurnPID.getOutput(actual);

        pilot.setNewThrust(output * 200);
    }

}
