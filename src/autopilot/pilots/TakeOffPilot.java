package autopilot.pilots;

import org.joml.Vector3f;

import com.stormbots.MiniPID;

import autopilot.PilotPart;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import utils.FloatMath;
import utils.Utils;

public class TakeOffPilot extends PilotPart {

    private final float targetHeight;

    private float maxThrust;

    private float time;

    private boolean takeoff, ended;

    private Vector3f oldPos = new Vector3f(0, 0, 0);

    private MiniPID pitchPID;

    public TakeOffPilot(float targetHeight) {
        this.targetHeight = targetHeight;

        this.takeoff = false;
        this.ended = false;

        this.time = 0;

        this.pitchPID = new MiniPID(1.1, 0, 0.3);
        float climbAngle = FloatMath.toRadians(20);
        this.pitchPID.setSetpoint(climbAngle);
        this.pitchPID.setOutputLimits(FloatMath.toRadians(10));
    }


    @Override
    public void initialize(AutopilotConfig config) {
        this.maxThrust = config.getMaxThrust();
    }


    @Override
    public AutopilotOutputs timePassed(AutopilotInputs input) {
        Vector3f pos = new Vector3f(input.getX(), input.getY(), input.getZ());

        float dt = input.getElapsedTime() - this.time;
        this.time = input.getElapsedTime();

        Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1 / dt);
        this.oldPos = pos;

        float lwIncl, rwIncl, horStabIncl, thrust;

        if (!takeoff) {
            lwIncl = 0;
            rwIncl = 0;
            horStabIncl = 0;
            thrust = this.maxThrust;
            float takeOffSpeed = 120 / 3.6f;
            if (FloatMath.norm(vel) >= takeOffSpeed)
                takeoff = true;
        } else {
            lwIncl = input.getPitch() < FloatMath.toRadians(15) ? 4 : 6;
            rwIncl = input.getPitch() < FloatMath.toRadians(15) ? 4 : 6;
            horStabIncl = (float) -pitchPID.getOutput(input.getPitch());
            thrust = this.maxThrust;
            if (pos.y > targetHeight)
                ended = true;
        }

        return Utils.buildOutputs(FloatMath.toRadians(lwIncl), FloatMath.toRadians(rwIncl), 0, horStabIncl, thrust, 0, 0, 0);
    }


    @Override
    public boolean ended() {
        return ended;
    }


    @Override
    public void close() {
    }


    @Override
    public String taskName() {
        return "Takeoff";
    }
}
