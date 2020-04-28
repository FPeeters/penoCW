package autopilot.pilots;

import autopilot.PilotPart;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;
import utils.FloatMath;
import utils.Utils;

public class HandbrakePilot extends PilotPart {
    private float maxR;
    private float time;
    private Vector3f oldPos = new Vector3f(0, 0, 0);
    private float oldspeed = 0;
    private float previousspeed = 0;
    private boolean ended;

    @Override
    public AutopilotOutputs timePassed(AutopilotInputs input) {
        Vector3f pos = new Vector3f(input.getX(), 0, input.getZ());
        float dt = input.getElapsedTime() - this.time;

        Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1 / dt);
        float speed = FloatMath.norm(vel);
        if (Float.isNaN(speed)) {
            speed = 0;
        }

        if (speed == Math.min(previousspeed, oldspeed)) {
            this.ended = true;
            return Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0);
        }

        this.oldPos = pos;
        this.oldspeed = this.previousspeed;
        this.previousspeed = speed;
        this.time = input.getElapsedTime();

        return Utils.buildOutputs(0, 0, 0, 0, 0, maxR, maxR, maxR);
    }

    @Override
    public String taskName() {
        return "Handbrake";
    }

    @Override
    public void initialize(AutopilotConfig config) {
        this.maxR = config.getRMax();
        this.ended = false;
        this.time = -1f;
    }

    @Override
    public boolean ended() {
        return ended;
    }

    @Override
    public void close() {
    }
}