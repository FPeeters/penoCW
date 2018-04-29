package autopilot.pilots;

import com.stormbots.MiniPID;

import autopilot.PilotPart;
import interfaces.Autopilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;
import utils.FloatMath;
import utils.Utils;

import java.util.*;

public class TaxiPilot extends PilotPart {

	private Vector3f targetPos;

	private MiniPID thrustPID;

	private boolean ended;

	private float time;
	private float maxBrakeForce;
	private float finalHeading;

	private Vector3f oldPos = new Vector3f(0, 0, 0);
	private float oldheading;

	public TaxiPilot() {
		thrustPID = new MiniPID(100, 0.1, 0.1);
		targetPos = new Vector3f(0,0,0);
		finalHeading = 0;

		this.ended = false;
	}

	public TaxiPilot(Vector3f targetPos, float heading) {
		thrustPID = new MiniPID(100, 0.1, 0.1);
		this.targetPos = targetPos;
		this.finalHeading = heading;

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
		Vector3f vel = pos.sub(this.oldPos, new Vector3f()).mul(1/dt);
		this.oldPos = pos;

		float heading = input.getHeading();
		float angVel = (heading - oldheading)/dt;
		oldheading = heading;

		float fBrake = 0, lBrake = 0 , rBrake = 0, thrust, taxispeed;
		float speed = FloatMath.norm(vel);
		float distance = pos.distance(getTarget());
		float targetHeading = FloatMath.atan2(pos.x() - getTarget().x(), pos.z() - getTarget().z());

		if (Float.isNaN(speed)) {
			speed = 0;
		}
		if (Float.isNaN(angVel)) {
			angVel = 0;
		}

		if (distance < 25f) {
			taxispeed = 3f;
		} else {
			taxispeed = 10f;
		}

		thrustPID.setSetpoint(taxispeed);

		if (speed <= taxispeed) {
			thrust = (float)thrustPID.getOutput(speed);
			fBrake = 0f;
		} else {
			fBrake = maxBrakeForce;
			lBrake = maxBrakeForce;
			rBrake = maxBrakeForce;
			thrust = 0f;
		}

		System.out.println(distance);

		if (distance < 12.5f) {
			if (speed > 1f) {
				thrust = 0f;
				lBrake = maxBrakeForce;
				rBrake = maxBrakeForce;
				fBrake = maxBrakeForce;
			} else if (Math.abs(finalHeading - input.getHeading()) > FloatMath.toRadians(0.5f)) {
				Boolean side =  checkTurn(finalHeading, input);
				if (side != null){
					thrust = 50f;
					fBrake = 0;
					lBrake = 0;
					rBrake = 0;
					if (side) {
						lBrake = maxBrakeForce;
					} else {
						rBrake = maxBrakeForce;
					}
				}
			} else if (Math.abs(angVel) > FloatMath.toRadians(0.1f)) {
				return Utils.buildOutputs(0, 0, 0, 0, 0, maxBrakeForce, maxBrakeForce, maxBrakeForce);
			} else {
				System.out.println("Exiting taxi");
				this.ended = true;
				return Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0);
			}
		} else if (Math.abs(targetHeading - heading) > FloatMath.toRadians(2f)) {
			Boolean side = checkTurn(targetHeading, input);
			if (side != null) {
				thrust = 50f;
				fBrake = 0;
				lBrake = 0;
				rBrake = 0;
				if (side) {
            	    lBrake = maxBrakeForce;
            	} else {
                	rBrake = maxBrakeForce;
	            }
			}
		}
		this.time = input.getElapsedTime();

		return Utils.buildOutputs(0, 0, 0, 0, thrust, lBrake, fBrake, rBrake);
	}

	public Boolean checkTurn(float target, AutopilotInputs input) {
		Boolean side = null;
		Vector3f result = new Vector3f(FloatMath.cos(input.getHeading()),0,-FloatMath.sin(input.getHeading())).cross(new Vector3f(FloatMath.cos(target),0,-FloatMath.sin(target)), new Vector3f());
		if (result.normalize().y >= 0)
			side = true;
		else if (result.normalize().y < 0) {;
			side = false;
		}

		return side;
	}

	@Override
	public boolean ended() {
		return ended;
	}


	@Override
	public void close() { }


	@Override
	public String taskName() {
		return "Taxi";
	}

}
