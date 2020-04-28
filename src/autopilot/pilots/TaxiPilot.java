package autopilot.pilots;

import com.stormbots.MiniPID;

import autopilot.PilotPart;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;
import utils.FloatMath;
import utils.Utils;

public class TaxiPilot extends PilotPart {

    private Vector3f targetPos;

    private MiniPID thrustPID;

    private boolean firstCheck;
    private boolean goalReached;
    private boolean ended;

    private float time;
    private float maxBrakeForce;
    private float finalHeading;

    private Vector3f oldPos = new Vector3f(0, 0, 0);
    private float oldheading;

    public TaxiPilot(Vector3f targetPos, float heading) {
        thrustPID = new MiniPID(100, 0.1, 0.1);
        this.targetPos = targetPos;
        this.finalHeading = heading;

        this.firstCheck = true;
        this.goalReached = false;
        this.ended = false;
    }

    public Vector3f getTarget() {
        return targetPos;
    }


    @Override
    public void initialize(AutopilotConfig config) {
        float maxThrust = config.getMaxThrust();
        this.maxBrakeForce = config.getRMax();

        thrustPID.setOutputLimits(0, maxThrust);
    }


    @Override
    public AutopilotOutputs timePassed(AutopilotInputs input) {
        float dt = input.getElapsedTime() - this.time;

        Vector3f pos = new Vector3f(input.getX(), 0, input.getZ());
        float distance = pos.distance(getTarget());
        Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1 / dt);
        float speed = FloatMath.norm(vel);
        this.oldPos = pos;

        float heading = input.getHeading();
        float toGoal = FloatMath.atan2(pos.x() - getTarget().x(), pos.z() - getTarget().z());
        float angVel = (heading - oldheading) / dt;
        oldheading = heading;

        if (Float.isNaN(speed)) {
            speed = 0;
        }
        if (Float.isNaN(angVel)) {
            angVel = 0;
        }

        float taxispeed;
        if (distance < 12.5) {
            taxispeed = 1f;
        } else if (distance < 25f) {
            taxispeed = 3f;
        } else {
            taxispeed = 10f;
        }
        thrustPID.setSetpoint(taxispeed);

        if (firstCheck) {
            if (distance <= 12.5) {
                goalReached = true;
            }
            firstCheck = false;
        }
        if (distance < 3) {
            goalReached = true;
        }

        float targetHeading;
        if (goalReached) {
            targetHeading = finalHeading;
        } else {
            targetHeading = toGoal;
        }

        float thrust = 0, fBrake = 0, lBrake = 0, rBrake = 0;

        float headingerror = Math.abs(targetHeading - heading);
        if (headingerror > FloatMath.toRadians(0.75f)) {
            Boolean side = checkTurn(targetHeading, input);
            if (headingerror == FloatMath.PI) {
                side = false;
            }
            if (side != null) {
                thrust = 70f;
                if (side) {
                    lBrake = maxBrakeForce;
                } else {
                    rBrake = maxBrakeForce;
                }
            }

        } else if (Math.abs(angVel) > FloatMath.toRadians(0.2f)) {
            fBrake = maxBrakeForce;
            lBrake = maxBrakeForce;
            rBrake = maxBrakeForce;

        } else if (!goalReached) {
            if (speed <= taxispeed) {
                thrust = (float) thrustPID.getOutput(speed);
            } else {
                fBrake = maxBrakeForce;
                lBrake = maxBrakeForce;
                rBrake = maxBrakeForce;
            }

        } else {
            ended = true;
        }

        this.time = input.getElapsedTime();

        return Utils.buildOutputs(0, 0, 0, 0, thrust, lBrake, fBrake, rBrake);
    }

    public Boolean checkTurn(float target, AutopilotInputs input) {
        Boolean side = null;
        Vector3f result = new Vector3f(FloatMath.cos(input.getHeading()), 0, -FloatMath.sin(input.getHeading())).cross(new Vector3f(FloatMath.cos(target), 0, -FloatMath.sin(target)), new Vector3f());
        if (result.normalize().y >= 0)
            side = true;
        else if (result.normalize().y < 0) {
            side = false;
        }

        return side;
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
        return "Taxi";
    }

}
