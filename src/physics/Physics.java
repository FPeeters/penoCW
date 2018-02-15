package physics;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import interfaces.AutopilotOutputs;
import utils.FloatMath;
import utils.Utils;

public class Physics {
	
	/**
	 * pos and vel in world coordinates
	 */
	private Vector3f pos, vel, angVel, weightWorld;
	
	private float heading, pitch, roll, 
				  lwIncl, rwIncl, hsIncl, vsIncl,
				  thrust, weight, 
				  maxAOA;
	
	private Matrix3f transMat, transMatInv,
					 inertia, inertiaInv;
	
	private Vector3f[] axisVectors, wingPositions, velProj;
	private float[] liftSlopes;

	private final boolean checkAOA = false;

	
	public Physics() {
		updateDrone(Utils.buildOutputs(0, 0, 0, 0, 0,-1,-1,-1));
	}
	
	/**
	 * Initialises the drone at the given position, with the given starting velocity,
	 * and the given heading, pitch and roll.
	 */
	public void init(AutopilotConfig config, Vector3f startPos, float startVel, float startHeading, float startPitch, float startRoll) {
		setupCalculations(config);
		
		this.pos = new Vector3f(startPos);
		this.vel = new Vector3f(0, 0, -startVel);
		
		this.angVel = new Vector3f();
		
		this.transMat = new Matrix3f().identity();
		
		if (Math.abs(startHeading) > 1E-6)
			this.transMat.rotate(-startHeading, new Vector3f(0, 1, 0));
		if (Math.abs(startPitch) > 1E-6)
			this.transMat.rotate(-startPitch, new Vector3f(1, 0, 0));
		if (Math.abs(startRoll) > 1E-6)
			this.transMat.rotate(-startRoll, new Vector3f(0, 0, 1));

		this.transMatInv = this.transMat.invert(new Matrix3f());
		
		update(0);
	}
	
	/**
	 * Initialises the drone at the given position, with starting velocity of 10
	 * and no starting orientation.  
	 */
	public void init(AutopilotConfig config, Vector3f startPos) {
		init(config, startPos, 10f, 0f, 0f, 0f);
	}
	
	/**
	 * Initialises the drone at (0, 0, 0), with starting velocity of 10
	 * and no starting orientation.  
	 */
	public void init(AutopilotConfig config) {
		init(config, new Vector3f(0,0,0));
	}
	
	public void init(AutopilotConfig config, float startVelocity) {
		init(config, new Vector3f(0,0,0), startVelocity);
	}
	
	/**
	 * Initialises the drone at the given position, with the given starting velocity
	 * and no starting orientation.
	 */
	public void init(AutopilotConfig config, Vector3f startPos, float startVel) {
		init(config, startPos, startVel, 0f, 0f, 0f);
	}
	
	
	/**
	 * Initialisation constants for calculations
	 */
	private void setupCalculations(AutopilotConfig config) {
		this.axisVectors = new Vector3f[] {new Vector3f(1, 0, 0),
										   new Vector3f(1, 0, 0),
										   new Vector3f(1, 0, 0),
										   new Vector3f(0, 1, 0)};
		
		this.wingPositions = new Vector3f[] {new Vector3f(-config.getWingX(), 0, 0),
											 new Vector3f(config.getWingX(), 0, 0),
											 new Vector3f(0, 0, config.getTailSize()),
											 new Vector3f(0, 0, config.getTailSize())};
		
		this.velProj = new Vector3f[] {new Vector3f(0, 1, 1),
									   new Vector3f(0, 1, 1),
									   new Vector3f(0, 1, 1),
									   new Vector3f(1, 0, 1)};
		
		this.weight = (config.getEngineMass() + 2*config.getWingMass() + config.getTailMass());
		
		this.weightWorld = new Vector3f(0, - config.getGravity() * this.weight, 0);
		
		this.liftSlopes = new float[] {config.getWingLiftSlope(), config.getWingLiftSlope(), config.getHorStabLiftSlope(), config.getVerStabLiftSlope()};
		
		float engineZ = config.getTailMass() / config.getEngineMass() * config.getTailSize();
		float Ixx = FloatMath.square(engineZ) * config.getEngineMass() + 
					FloatMath.square(config.getTailSize()) * config.getTailMass(),
			  Izz = 2f * FloatMath.square(config.getWingX()) * config.getWingMass();
		this.inertia = new Matrix3f(Ixx, 0,         0,
									0,   Ixx + Izz, 0, 
									0,   0,         Izz);
		this.inertiaInv = new Matrix3f(1/Ixx, 0,             0,
									   0,     1/(Ixx + Izz), 0,
									   0,     0,             1/Izz);
		
		this.maxAOA = config.getMaxAOA();
	}
	
	
	// getters voor de eigenschappen van de drone
	
	public Vector3f getPosition() {
		return new Vector3f(this.pos);
	}
	
	public Vector3f getVelocity() {
		return new Vector3f(this.vel);
	}
	
	public float getHeading() {
		return this.heading;
	}
	
	public float getPitch() {
		return this.pitch;
	}
	
	public float getRoll() {
		return this.roll;
	}
	
	public float getLWInclination() {
		return this.lwIncl;
	}
	
	public float getRWInclination() {
		return this.rwIncl;
	}
	
	public float getHSInclination() {
		return this.hsIncl;
	}
	
	public float getVSInclination() {
		return this.vsIncl;
	}

	public Matrix3f getTransMat() {
		return transMat;
	}

	public Matrix3f getTransMatInv() {
		return transMatInv;
	}

	/**
	 * Updates the wing inclinations and thrust of the drone
	 */
	public void updateDrone(AutopilotOutputs data) {
		this.lwIncl = data.getLeftWingInclination();
		this.rwIncl = data.getRightWingInclination();
		this.hsIncl = data.getHorStabInclination();
		this.vsIncl = data.getVerStabInclination();
		
		this.thrust = data.getThrust();
	}
	
	
	
	
	public void update(float dt) {
		updateTransMat(dt);
		updateHPR();
		
		Vector3f[] forceTorque = calculateForce();
		
		// forward euler
		this.pos.add(vel.mul(dt, new Vector3f())); 
		this.vel.add(FloatMath.transform(transMatInv, forceTorque[0]).mul(dt / this.weight));
		this.angVel.add(calculateAlfa(forceTorque[1]).mul(dt));
	}

	
	/**
	 * Updates the transformation matrix, it rotates with the drone's angular velocity.
	 */
	private void updateTransMat(float dt) {
		Vector3f rotation = this.angVel.mul(dt, new Vector3f());
		
		float norm = FloatMath.norm(rotation);
		if (Math.abs(norm) > 1E-6) {
			this.transMat.rotate(-norm, rotation.normalize());
			this.transMatInv = this.transMat.invert(new Matrix3f());
		}
	}
	
	/**
	 * Updates heading, pitch and roll
	 */
	private void updateHPR() {
		Vector3f F = FloatMath.transform(transMatInv, new Vector3f(0, 0, -1)),
				 R = FloatMath.transform(transMatInv, new Vector3f(1, 0, 0));
		
		Vector3f H = F.normalize(new Vector3f());
		H.mul(new Vector3f(1,0,1));
		
		Vector3f R0 = FloatMath.cross(H, new Vector3f(0, 1, 0));
		Vector3f U0 = FloatMath.cross(R0, F);
		
		this.heading = FloatMath.atan2(H.dot(-1, 0, 0), H.dot(0, 0, -1));
		this.pitch = FloatMath.atan2(F.dot(0, 1, 0), F.dot(H));
		this.roll = FloatMath.atan2(R.dot(U0), R.dot(R0));
	}
	
	
	/**
	 * Calculates total force and torque, in drone coordinates
	 * 
	 * @return {force, torque}
	 */
	@SuppressWarnings("unused")
	private Vector3f[] calculateForce() {
		Vector3f[] attacks = new Vector3f[] {new Vector3f(0, FloatMath.sin(this.lwIncl), -FloatMath.cos(this.lwIncl)),
											 new Vector3f(0, FloatMath.sin(this.rwIncl), -FloatMath.cos(this.rwIncl)),
											 new Vector3f(0, FloatMath.sin(this.hsIncl), -FloatMath.cos(this.hsIncl)),
											 new Vector3f(-FloatMath.sin(this.vsIncl), 0, -FloatMath.cos(this.vsIncl))}; 
		
		Vector3f thrustV = new Vector3f(0, 0, -this.thrust);
		Vector3f weightDrone = FloatMath.transform(this.transMat, this.weightWorld);
		
		Vector3f totalForce = (new Vector3f()).add(thrustV).add(weightDrone);
		Vector3f totalTorque = new Vector3f(0, 0, 0);
		
		for (int i = 0; i < 4; i++) {
			Vector3f normal = FloatMath.cross(axisVectors[i], attacks[i]);
			
			Vector3f veli = (new Vector3f()).add(FloatMath.transform(this.transMat, this.vel)).add(FloatMath.cross(this.angVel, this.wingPositions[i]));
			veli.mul(this.velProj[i]); // projecteren op vlak loodrecht op axis
			float aoa = - FloatMath.atan2(veli.dot(normal), veli.dot(attacks[i]));
			
			if (checkAOA && Math.abs(aoa) > maxAOA)
				System.out.println("wing nb " + i + " exceeded maximum aoa");
			
			Vector3f force = normal.mul(this.liftSlopes[i] * aoa * FloatMath.squareNorm(veli));
			
			totalForce.add(force);
			totalTorque.add(FloatMath.cross(this.wingPositions[i], force));
		}
		
		return new Vector3f[] {totalForce, totalTorque};
	}
	
	/**
	 * Calulates the rotational acceleration, in drone coordinates
	 */
	private Vector3f calculateAlfa(Vector3f torque) {
		
		Vector3f part1 = FloatMath.cross(this.angVel, FloatMath.transform(this.inertia, this.angVel));
		Vector3f alfa = FloatMath.transform(this.inertiaInv, torque.add(part1, new Vector3f())); 
		return alfa;
	}
}
