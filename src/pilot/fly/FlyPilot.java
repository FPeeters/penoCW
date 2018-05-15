package pilot.fly;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import pilot.PilotPart;
import pilot.fly.pid.PitchPID;
import pilot.fly.pid.RollPID;
import pilot.fly.pid.ThrustPID;
import recognition.Cube;
import recognition.ImageProcessing;
import utils.Constants;
import utils.FloatMath;
import utils.Utils;

import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

import java.util.ArrayList;

public class FlyPilot extends PilotPart {

	private boolean ended;

	private int cubeNb;
	private Vector3f[] cubes;

	public static enum State {
		Left, Right, Stable, Up, Down, StrongUp, StrongDown, SlowDown, SoftLeft, SoftRight
	};

	private State currentState;

	private float climbAngle;
	private float rMax;
	private float maxThrust;
	private float turnRadius;

	private float leftWingInclination;
	private float rightWingInclination;
	private float horStabInclination;
	private float verStabInclination;
	private float newThrust;
	private float stableTime = 0f;

	private Vector3f timePassedOldPos = new Vector3f(0, 0, 0);
	public Vector3f approxVel = new Vector3f(0f, 0f, 0f);
	private float time = 0f;

	private PitchPID pitchPID;
	private ThrustPID thrustPID;
	private RollPID rollPID;

	private ImageProcessing recog;

	public FlyPilot(Vector3f[] cubes) {
		this.cubes = cubes;
	}

	@Override
	public void initialize(AutopilotConfig config) {
		this.climbAngle = Constants.climbAngle;
		this.rMax = config.getRMax();
		this.maxThrust = config.getMaxThrust();
		this.turnRadius = 900f;

		this.pitchPID = new PitchPID(this);
		this.thrustPID = new ThrustPID(this);
		this.rollPID = new RollPID(this);

		this.cubeNb = 0;

		this.recog = new ImageProcessing();
	}

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {

		Vector3f pos = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());

		float dt = inputs.getElapsedTime() - this.time;
		this.time = inputs.getElapsedTime();
		if (dt != 0)
			this.approxVel = pos.sub(this.timePassedOldPos, new Vector3f()).mul(1 / dt);
		this.timePassedOldPos = pos;

		if (this.stableTime > 0) {
			setCurrentState(State.Stable);
			this.stableTime -= dt;

			control(inputs, currentState);

			if (stableTime <= 0 && this.cubeNb == this.cubes.length) {
				this.ended = true;
			}

			return Utils.buildOutputs(leftWingInclination, rightWingInclination, verStabInclination, horStabInclination,
					newThrust, rMax, rMax, rMax);
		}

		// cube geraakt? zo ja, volgende selecteren
		if (getCurrentCube().distance(pos) < Constants.DRONE_PICKUP_DISTANCE){
			System.out.println("Cube miss" + pos);
			this.cubeNb++;
			this.stableTime = 1.5f;

			setCurrentState(State.Stable);
			control(inputs, currentState);

			return Utils.buildOutputs(leftWingInclination, rightWingInclination, verStabInclination, horStabInclination,
					newThrust, rMax, rMax, rMax);
		}

		// update pos met imagerecog
		 if (getCurrentCube().distance(pos) < 100) {
		    System.out.println("Recog active");
             recog.addNewImage(inputs.getImage(), inputs.getPitch(),
                     inputs.getHeading(), inputs.getRoll(), new float[] {
                             inputs.getX(), inputs.getY(), inputs.getZ() });

             ArrayList<Cube> locs = recog.generateLocations();
             if (locs.size() > 0) {
                 float[] cubePos = locs.get(0).getLocation();
                 Vector3f cubePosition = new Vector3f(cubePos[0], cubePos[1], cubePos[2]);
                 getCurrentCube().add(cubePosition).mul(0.5f);
             }
		 }

		// moeten we omhoog?
		if ((getCurrentCube().y - pos.y) > 2.5) {
			if (Math.abs(inputs.getRoll()) > FloatMath.toRadians(0.3f))
				setCurrentState(State.Stable);
			else
				setCurrentState(State.StrongUp);
		}
		// moeten we omlaag?
		else if (pos.y - getCurrentCube().y > 2.5) {
			if (inputs.getRoll() > FloatMath.toRadians(5))
				setCurrentState(State.Stable);
			else
				setCurrentState(State.StrongDown);
		}
		// iets meer stijgen
		else if (getCurrentState() == State.StrongUp && (getCurrentCube().y - pos.y) > 1) {} 
		else if (getCurrentState() == State.StrongDown && (pos.y - getCurrentCube().y) > 2) {} 
		else {

			// draaien nodig?
			Vector3f diff = getCurrentCube().sub(pos, new Vector3f());
			float targetHeading = FloatMath.atan2(-diff.x, -diff.z);
			Boolean side = null;
			// null: nee, true: links, false: rechts
			Vector3f result = new Vector3f(FloatMath.cos(inputs.getHeading()), 0, -FloatMath.sin(inputs.getHeading()))
					.cross(new Vector3f(FloatMath.cos(targetHeading), 0, -FloatMath.sin(targetHeading)),
							new Vector3f());
			if (result.normalize().y >= 0 && Math.abs(targetHeading - inputs.getHeading()) > FloatMath.toRadians(5))
				side = true;
			else if (result.normalize().y < 0 && Math.abs(targetHeading - inputs.getHeading()) > FloatMath.toRadians(5)) {
				;
				side = false;
			}

			// bocht haalbaar?
			if (side != null) {
				if (turnable(pos, inputs.getHeading(), getCurrentCube(), this.turnRadius)) {
					setCurrentState(side ? State.Left : State.Right);
				}else if (getCurrentCube().distance(pos) < 40) {
					setCurrentState(side ? State.SoftLeft : State.SoftRight);
				}else
					// bocht niet haalbaar, rechtdoor gaan.
					setCurrentState(State.Stable);
			} else {
				// moeten we omhoog?
				if ((getCurrentCube().y - pos.y) > 1.5) {
					if (inputs.getRoll() > FloatMath.toRadians(5))
						setCurrentState(State.Stable);
					else
						setCurrentState(State.Up);
				}
				// moeten we omlaag?
				else if (pos.y - getCurrentCube().y > 1.5) {
					if (inputs.getRoll() > FloatMath.toRadians(5))
						setCurrentState(State.Stable);
					else
						setCurrentState(State.Down);
				}
				else {

					if (result.normalize().y >= 0
							&& Math.abs(targetHeading - inputs.getHeading()) > FloatMath.toRadians(2))
						side = true;
					else if (result.normalize().y < 0
							&& Math.abs(targetHeading - inputs.getHeading()) > FloatMath.toRadians(2)) {
						;
						side = false;
					}
					// bocht haalbaar?
					if (side != null) {
						setCurrentState(side ? State.SoftLeft : State.SoftRight);
					} else {
						setCurrentState(State.Stable);
					}
				}
			}
		}

		control(inputs, currentState);

		return Utils.buildOutputs(leftWingInclination, rightWingInclination, verStabInclination, horStabInclination,
				newThrust, rMax, rMax, rMax);
	}

	private void setCurrentState(State state) {
		this.currentState = state;
	}

	public State getCurrentState() {
		return currentState;
	}

	private Vector3f getCurrentCube() {
		return this.cubes[this.cubeNb];
	}

	private void control(AutopilotInputs input, State state) {
		switch (state) {
		case StrongUp:
			climbPID(input);
			break;
		case Up:
			risePID(input);
			break;
		case StrongDown:
			dropPID(input);
			break;
		case Down:
			descendPID(input);
			break;
		case Stable:
			flyStraightPID(input);
			break;
		case Left:
			turnLeft(input);
			break;
		case Right:
			turnRight(input);
			break;
		case SoftLeft:
			turnSoftLeft(input);
			break;
		case SoftRight:
			turnSoftRight(input);
			break;
		case SlowDown:
			slowDown(input);
			setLeftWingInclination(FloatMath.toRadians(2));
			setRightWingInclination(FloatMath.toRadians(2));
			break;
		default:
			break;
		}
	}

	/**
	 * PIDs set pitch and thrust to fly straight.
	 */
	private void flyStraightPID(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(0), State.Stable);
		pitchPID.adjustPitchClimb(input, 0f);
		thrustPID.adjustThrustUp(input, 0.4f);
	}

	/**
	 * Causes drone to climb by changing pitch and using thrust to increase vertical
	 * velocity
	 */
	private void climbPID(AutopilotInputs inputs) {
		rollPID.adjustRoll(inputs, FloatMath.toRadians(0), State.StrongUp);
		pitchPID.adjustPitchClimb(inputs, climbAngle);
		thrustPID.adjustThrustUp(inputs, 6f);
	}

	private void dropPID(AutopilotInputs inputs) {
		rollPID.adjustRoll(inputs, FloatMath.toRadians(0), State.StrongDown);
		pitchPID.adjustPitchDown(inputs, FloatMath.toRadians(-5f));
		thrustPID.adjustThrustDown(inputs, -4f);
	}

	/**
	 * Causes drone to rise by increasing lift through higher speed.
	 */
	private void risePID(AutopilotInputs inputs) {
		rollPID.adjustRoll(inputs, FloatMath.toRadians(0), State.StrongUp);
		// pitch op 0
		pitchPID.adjustPitchClimb(inputs, FloatMath.toRadians(4));
		// thrust bijgeven
		thrustPID.adjustThrustUp(inputs, 2f);
		
	}

	private void descendPID(AutopilotInputs inputs) {
		rollPID.adjustRoll(inputs, FloatMath.toRadians(0), State.StrongDown);
		// pitch op 0
		pitchPID.adjustPitchDown(inputs, 0);
		// val vertragen
		// thrustPID.adjustThrustDown(inputs, -1.5f);
	}

	private void slowDown(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(0), State.SlowDown);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
		setNewThrust(0);
	}

	private void turnRight(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(-20), State.Right);
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}

	private void turnLeft(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(20), State.Left);
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}
	
	private void turnSoftRight(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(-12), State.Right);
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}

	private void turnSoftLeft(AutopilotInputs input) {
		rollPID.adjustRoll(input, FloatMath.toRadians(12), State.Left);
		thrustPID.adjustThrustUp(input, 0.37f);
		pitchPID.adjustPitchTurn(input, FloatMath.toRadians(0));
	}

	public Vector3f horProjVel(AutopilotInputs inputs) {
		Vector3f relVelD = getTransMat(inputs).transform(approxVel, new Vector3f());
		return new Vector3f(0, relVelD.y, relVelD.z);
	}

	public Vector3f getRelVel(AutopilotInputs input) {
		return getTransMat(input).transform(approxVel, new Vector3f());
	}

	private Matrix3f getTransMat(AutopilotInputs inputs) {
		float heading = inputs.getHeading();
		float pitch = inputs.getPitch();
		float roll = inputs.getRoll();

		Matrix3f transMat = new Matrix3f().identity();

		if (Math.abs(heading) > 1E-6)
			transMat.rotate(heading, new Vector3f(0, 1, 0));
		if (Math.abs(pitch) > 1E-6)
			transMat.rotate(pitch, new Vector3f(1, 0, 0));
		if (Math.abs(roll) > 1E-6)
			transMat.rotate(roll, new Vector3f(0, 0, 1));

		return transMat;
	}

	private static boolean turnable(Vector3f pos, float heading, Vector3f target, float radius) {
		Vector3f perp = new Vector3f(FloatMath.cos(heading), 0,-FloatMath.sin(heading));

		Vector3f center1 = pos.add(perp.mul(radius), new Vector3f()),
				center2 = pos.add(perp.mul(-radius), new Vector3f());

		if (center1.distance(target) < radius || center2.distance(target) < radius) {
			return false;
		}
		return true;
	}

	public float getMaxThrust() {
		return maxThrust;
	}

	public float getNewThrust() {
		return newThrust;
	}

	public float getVerStabInclination() {
		return verStabInclination;
	}

	public float getLeftWingInclination() {
		return leftWingInclination;
	}

	public float getRightWingInclination() {
		return rightWingInclination;
	}

	public float getHorStabInclination() {
		return horStabInclination;
	}

	public void setLeftWingInclination(float leftWingInclination) {
		this.leftWingInclination = leftWingInclination;
	}

	public void setRightWingInclination(float rightWingInclination) {
		this.rightWingInclination = rightWingInclination;
	}

	public void setNewThrust(float newThrust) {
		this.newThrust = newThrust;
	}

	public void setHorStabInclination(float horStabInclination) {
		this.horStabInclination = horStabInclination;
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
		return "Fly: " + (this.currentState == null ? "" : this.currentState.name());
	}

}
