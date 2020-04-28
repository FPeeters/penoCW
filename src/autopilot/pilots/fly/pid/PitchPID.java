package autopilot.pilots.fly.pid;

import interfaces.AutopilotInputs;

import com.stormbots.MiniPID;

import autopilot.pilots.FlyPilot;

public class PitchPID {

    MiniPID pitchClimbPID, pitchDownPID, pitchTurnPID;
    FlyPilot pilot;

    public PitchPID(FlyPilot pilot) {

        pitchClimbPID = new MiniPID(2, 0, 1.5);
        pitchClimbPID.setOutputLimits(Math.toRadians(10));
        pitchDownPID = new MiniPID(5, 0, 0.5);
        pitchDownPID.setOutputLimits(Math.toRadians(8));
        pitchTurnPID = new MiniPID(3, 0, 65);
        pitchTurnPID.setOutputLimits(Math.toRadians(7.5));

        this.pilot = pilot;
    }

    // PID uses horizontal stabiliser to adjust pitch.
    public void adjustPitchClimb(AutopilotInputs input, float target) {
        pitchClimbPID.setSetpoint(target);

        float actual = input.getPitch();
        float output = (float) pitchClimbPID.getOutput(actual);

        pilot.setHorStabInclination(-output);
    }

    public void adjustPitchDown(AutopilotInputs input, float target) {
        pitchDownPID.setSetpoint(target);

        float actual = input.getPitch();
        float output = (float) pitchDownPID.getOutput(actual);

        pilot.setHorStabInclination(-output);
    }

    public void adjustPitchTurn(AutopilotInputs input, float target) {
        pitchTurnPID.setSetpoint(target);

        float actual = input.getPitch();
        float output = (float) pitchTurnPID.getOutput(actual);

        pilot.setHorStabInclination(-output);
    }

}
