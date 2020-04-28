package autopilot.pilots;

import org.joml.Vector3f;

import com.stormbots.MiniPID;

import autopilot.PilotPart;
import autopilot.airports.VirtualAirport;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import utils.FloatMath;
import utils.Utils;

public class LandingPilot extends PilotPart {

    private float maxThrust;
    private float rMax;
    private final float dropAngle = FloatMath.toRadians(5);
    private float time;
    private MiniPID pitchPID;
    private MiniPID rollPID;
    private boolean braking = false;
    private boolean hasEnded = false;
    private Vector3f oldPos = new Vector3f(0, 0, 0);

    private VirtualAirport currentDestionationAirport;

    public LandingPilot(VirtualAirport destinationAirport) {
        this.time = 0;
        currentDestionationAirport = destinationAirport;
    }

    @Override
    public void initialize(AutopilotConfig config) {
        this.maxThrust = config.getMaxThrust();
        this.rMax = config.getRMax();

        this.pitchPID = new MiniPID(1.1, 0, 0.3);
        this.pitchPID.setSetpoint(dropAngle);
        this.pitchPID.setOutputLimits(FloatMath.toRadians(10));

        this.rollPID = new MiniPID(5.2, 0, 0);
        rollPID.setSetpoint(0);
        this.rollPID.setOutputLimits(Math.toRadians(30));
    }


    @Override
    public AutopilotOutputs timePassed(AutopilotInputs input) {

        boolean firstStep = false;
        if (this.time == 0) firstStep = true;

        Vector3f pos = new Vector3f(input.getX(), input.getY(), input.getZ());

        float dt = input.getElapsedTime() - this.time;
        this.time = input.getElapsedTime();

        Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1 / dt);
        if (firstStep) vel.z = -35;
        this.oldPos = pos;
        if (FloatMath.norm(vel) < 1) {
            hasEnded = true;
        }

        if (FloatMath.norm(vel) > 60) this.pitchPID.setSetpoint(FloatMath.toRadians(2));
        else if (FloatMath.norm(vel) > 50) this.pitchPID.setSetpoint(FloatMath.toRadians(5.5f));
        else this.pitchPID.setSetpoint(FloatMath.toRadians(8));

        float lwIncl, rwIncl, horStabIncl, thrust;

        lwIncl = input.getPitch() < FloatMath.toRadians(5) ? 2 : 3;
        rwIncl = input.getPitch() < FloatMath.toRadians(5) ? 2 : 3;
        horStabIncl = (float) -pitchPID.getOutput(input.getPitch());
        thrust = 0;
        if (vel.y < -3 && pos.y > 13 && pos.y < 25) {
            thrust = maxThrust;
        } else if (vel.y < -2 && pos.y > 7 && pos.y < 13) {
            thrust = maxThrust;
        } else if (vel.y < -1 && pos.y > 4 && pos.y < 7) {
            thrust = maxThrust;
        } else if (!onAirport(pos, currentDestionationAirport) && vel.y < 0 && pos.y < 4) {
            thrust = maxThrust;
        }
        if (vel.y > 0 && pos.y < 2) braking = true;
        float brakes = 0f;
        if (braking) brakes = rMax;

        float airportHeading = currentDestionationAirport.getHeading();

        Vector3f center = currentDestionationAirport.getPosition();
        Vector3f centerRight = auxLocPlusX(center, airportHeading, currentDestionationAirport.getWidth() * 2 / 3);
        Vector3f centerLeft = auxLocPlusX(center, airportHeading, -currentDestionationAirport.getWidth() * 2 / 3);
        Vector3f centerRightPlus = auxLocPlusMinZ(centerRight, airportHeading);
        Vector3f centerLeftPlus = auxLocPlusMinZ(centerLeft, airportHeading);

        //plane is too much left
        if (orientation(centerRight, centerRightPlus, pos) == 1) {
            rollPID.setSetpoint(Math.toRadians(-3));
            float output = (float) rollPID.getOutput(input.getRoll());

            lwIncl -= output;
            rwIncl += output;
        }
        //too much right
        else if (orientation(centerLeft, centerLeftPlus, pos) == 2) {
            rollPID.setSetpoint(Math.toRadians(3));
            float output = (float) rollPID.getOutput(input.getRoll());

            lwIncl -= output;
            rwIncl += output;
        }
        //on course
        else {
            //heading too much left
            if (makeNormal(input.getHeading() - (airportHeading + (float) Math.PI)) > Math.toRadians(1)) {
                rollPID.setSetpoint(Math.toRadians(-10));
                float output = (float) rollPID.getOutput(input.getRoll());

                lwIncl -= output;
                rwIncl += output;
            }
            //heading too much right
            else if (makeNormal(input.getHeading() - (airportHeading + (float) Math.PI)) < Math.toRadians(-1)) {
                rollPID.setSetpoint(Math.toRadians(10));
                float output = (float) rollPID.getOutput(input.getRoll());

                lwIncl -= output;
                rwIncl += output;
            } else {
                rollPID.setSetpoint(Math.toRadians(0));
                float output = (float) rollPID.getOutput(input.getRoll());

                lwIncl -= output;
                rwIncl += output;
            }
        }

        return Utils.buildOutputs(FloatMath.toRadians(lwIncl), FloatMath.toRadians(rwIncl), 0, horStabIncl, thrust, brakes, brakes, brakes);
    }

    @Override
    public boolean ended() {
        return hasEnded;
    }

    @Override
    public void close() {
    }

    @Override
    public String taskName() {
        return "Landing pilot";
    }

    //Helper functions
    private Vector3f auxLocPlusMinZ(Vector3f planePosition, float heading) {
        Vector3f newLoc = new Vector3f();
        newLoc.x = planePosition.x + (float) Math.sin(-heading) * 1f;
        newLoc.y = planePosition.y;
        newLoc.z = planePosition.z - (float) Math.cos(-heading) * 1f;
        return newLoc;
    }

    private Vector3f auxLocPlusX(Vector3f planePosition, float heading, float arg) {
        Vector3f newLoc = new Vector3f();
        newLoc.x = planePosition.x + (float) Math.cos(-heading) * arg;
        newLoc.y = planePosition.y;
        newLoc.z = planePosition.z + (float) Math.sin(-heading) * arg;
        return newLoc;
    }

    private int orientation(Vector3f p, Vector3f q, Vector3f r) {
        float val = (-q.z + p.z) * (r.x - q.x) -
                (q.x - p.x) * (-r.z + q.z);

        if (val == 0) return 0;
        return (val > 0) ? 1 : 2;    //Clock - Counterclockwise
    }

    private float makeNormal(float targetHeading) {
        float ret = targetHeading;
        while (ret > Math.PI) {
            ret -= (float) Math.PI * 2;
        }
        while (ret < -Math.PI) {
            ret += (float) Math.PI * 2;
        }
        return ret;
    }

    public VirtualAirport getCurrentDestionationAirport() {
        return currentDestionationAirport;
    }

    private boolean onAirport(Vector3f pos, VirtualAirport airport) {
        Vector3f diff = pos.sub(airport.getPosition(), new Vector3f());
        float len = diff.dot(new Vector3f(-FloatMath.sin(airport.getHeading()), 0, -FloatMath.cos(airport.getHeading())));
        float wid = diff.dot(new Vector3f(-FloatMath.cos(airport.getHeading()), 0, FloatMath.sin(airport.getHeading())));

        if (Math.abs(len) > airport.getWidth() / 2 + airport.getLength() + 125) {
            return false;
        } else return !(Math.abs(wid) > airport.getWidth());

    }
}
