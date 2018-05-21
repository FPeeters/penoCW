package pilot;

import org.joml.Vector3f;

import com.stormbots.MiniPID;

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
	
	public LandingPilot() {
		this.time = 0;
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
		
		Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1/dt);
		if (firstStep) vel.z = -35;
		this.oldPos = pos;
		
		if (FloatMath.norm(vel) < 1) {
			hasEnded = true;
		}
			
		if(FloatMath.norm(vel) > 60) this.pitchPID.setSetpoint(FloatMath.toRadians(2));
		else if(FloatMath.norm(vel) > 50) this.pitchPID.setSetpoint(FloatMath.toRadians(5.5f));
		else this.pitchPID.setSetpoint(FloatMath.toRadians(8));
		
		float lwIncl = 0, rwIncl = 0, horStabIncl = 0, thrust = 0;
		
		lwIncl = input.getPitch() < FloatMath.toRadians(5) ? 2 : 3;
		rwIncl = input.getPitch() < FloatMath.toRadians(5) ? 2 : 3;
		horStabIncl = (float) -pitchPID.getOutput(input.getPitch());		
		thrust = 0;	
		if(vel.y < -3 && pos.y > 13 && pos.y < 25){
			thrust = maxThrust;
		}
		else if(vel.y < -2 && pos.y > 7 && pos.y < 13){
			thrust = maxThrust;
		}
		else if(vel.y < -1 && pos.y > 2.5 && pos.y < 7){
			thrust = maxThrust;
		}
		if(vel.y > 0 && pos.y < 2) braking = true;
		float brakes = 0f;
		if(braking)brakes = rMax;
		
		//stabelize roll
		float actual = input.getRoll();
		float output = (float) rollPID.getOutput(actual);
		
		lwIncl -= output;
		rwIncl += output;
			
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

}
