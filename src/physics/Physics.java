package physics;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import interfaces.AutopilotOutputs;
import utils.FloatMath;
import utils.PhysicsException;
import utils.Utils;

public class Physics {
	
	/**
	 * pos and vel in world coordinates
	 */
	private Vector3f pos, vel, angVel, weightWorld, enginePos;
	
	private float heading, pitch, roll, 
				  lwIncl, rwIncl, hsIncl, vsIncl,
				  thrust, weight,
				  tyreRadius, tyreSlope, dampSlope,
				  maxAOA, maxR, maxFC, maxThrust;
	
	private Matrix3f transMat, transMatInv,
					 inertia, inertiaInv;
	
	private Vector3f[] axisVectors, wingPositions, velProj, wheelPositions;
	private float[] liftSlopes, dBuffer, brakeForce;

	private final boolean checkAOA;

	private AutopilotConfig config;
	
	public Physics(boolean checkAOA) {
		this.checkAOA = checkAOA;
		
		this.brakeForce = new float[] {0, 0, 0};
		try {
			updateDrone(Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0));
		} catch (PhysicsException e) {
			e.printStackTrace();
		}
	}
	
	public Physics() {
		this(true);		
	}
	
	/**
	 * Initialises the drone at the given position, with the given starting velocity,
	 * and the given heading, pitch and roll.
	 */
	public void init(AutopilotConfig config, Vector3f startPos, Vector3f startVel, float startHeading, float startPitch, float startRoll) {
		setupCalculations(config);
		
		this.config = config;
		
		this.pos = new Vector3f(startPos);
		this.vel = startVel;
		
		this.angVel = new Vector3f();
		
		this.transMat = new Matrix3f().identity();
		
		if (Math.abs(startHeading) > 1E-6)
			this.transMat.rotate(-startHeading, new Vector3f(0, 1, 0));
		if (Math.abs(startPitch) > 1E-6)
			this.transMat.rotate(-startPitch, new Vector3f(1, 0, 0));
		if (Math.abs(startRoll) > 1E-6)
			this.transMat.rotate(-startRoll, new Vector3f(0, 0, 1));

		this.transMatInv = this.transMat.invert(new Matrix3f());
				
		try {
			update(0);
		} catch (PhysicsException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void init(AutopilotConfig config, Vector3f startPos, float startVel, float startHeading, float startPitch, float startRoll) {
		init(config, startPos, new Vector3f(0,0,-startVel), startHeading, startPitch, startRoll);
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
	
	/**
	 * Initialises the drone at (0, 0, 0), with the given starting velocity
	 * and no starting orientation.
	 */
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
	
	public void init(AutopilotConfig config, Vector3f startPos, Vector3f startVel) {
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
		
		this.wheelPositions = new Vector3f[] {new Vector3f(-config.getRearWheelX(), config.getWheelY(), config.getRearWheelZ()),
											  new Vector3f(0, config.getWheelY(), config.getFrontWheelZ()),
											  new Vector3f(config.getRearWheelX(), config.getWheelY(), config.getRearWheelZ())};
		
		this.velProj = new Vector3f[] {new Vector3f(0, 1, 1),
									   new Vector3f(0, 1, 1),
									   new Vector3f(0, 1, 1),
									   new Vector3f(1, 0, 1)};
		
		this.weight = (config.getEngineMass() + 2*config.getWingMass() + config.getTailMass());
		
		this.weightWorld = new Vector3f(0, - config.getGravity() * this.weight, 0);
		
		this.liftSlopes = new float[] {config.getWingLiftSlope(), config.getWingLiftSlope(), config.getHorStabLiftSlope(), config.getVerStabLiftSlope()};
		
		this.dBuffer = new float[] {0,0,0};
		
		float engineZ = config.getTailMass() / config.getEngineMass() * config.getTailSize();
		this.enginePos = new Vector3f(0, 0, -engineZ);
		
		float Ixx = FloatMath.square(engineZ) * config.getEngineMass() + 
					FloatMath.square(config.getTailSize()) * config.getTailMass(),
			  Izz = 2f * FloatMath.square(config.getWingX()) * config.getWingMass();
		this.inertia = new Matrix3f(Ixx, 0,         0,
									0,   Ixx + Izz, 0, 
									0,   0,         Izz);
		this.inertiaInv = new Matrix3f(1/Ixx, 0,             0,
									   0,     1/(Ixx + Izz), 0,
									   0,     0,             1/Izz);
		
		this.tyreRadius = config.getTyreRadius();
		
		this.tyreSlope = config.getTyreSlope();
		
		this.dampSlope = config.getDampSlope();
		
		this.maxAOA = config.getMaxAOA();
		
		this.maxR = config.getRMax();
		
		this.maxFC = config.getFcMax();
		
		this.maxThrust = config.getMaxThrust();
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
	
	public float getThrust() {
		return this.thrust;
	}
	
	public Matrix3f getTransMat() {
		return transMat;
	}

	public Matrix3f getTransMatInv() {
		return transMatInv;
	}
	
	public AutopilotConfig getConfig() {
		return config;
	}
	
	

	/**
	 * Updates the wing inclinations and thrust of the drone
	 * @throws PhysicsException 
	 */
	public void updateDrone(AutopilotOutputs data) throws PhysicsException {
		this.lwIncl = data.getLeftWingInclination();
		this.rwIncl = data.getRightWingInclination();
		this.hsIncl = data.getHorStabInclination();
		this.vsIncl = data.getVerStabInclination();
		
		if (thrust > maxThrust)
			throw new PhysicsException("Illegal thrust force");
		this.thrust = data.getThrust();
		
		this.brakeForce[0] = data.getLeftBrakeForce();
		this.brakeForce[1] = data.getFrontBrakeForce();
		this.brakeForce[2] = data.getRightBrakeForce();
		for (int i = 0; i < 3; i++) {
			if (brakeForce[i] < 0 || brakeForce[i] > this.maxR)
				throw new PhysicsException("Illegal brake force on wheel nb " + i);
		}
	}
	
	
	/**
	 * advances the time for this drone
	 * @throws PhysicsException
	 * 		if an exception occurs (AOA, crash, tyre)
	 */
	public void update(float dt) throws PhysicsException {
		updateTransMat(dt);
		updateHPR();
		
		Vector3f[] forceTorque = calculateForce(dt);
		
		// forward euler
		this.pos.add(vel.mul(dt, new Vector3f()));
		this.vel.add(FloatMath.transform(transMatInv, forceTorque[0]).mul(dt / this.weight));
		this.angVel.add(calculateAlfa(forceTorque[1]).mul(dt));
		
		checkCrash();
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
	 * @throws PhysicsException 
	 */
	private Vector3f[] calculateForce(float dt) throws PhysicsException {
		
		// thrust & weight
		Vector3f thrustV = new Vector3f(0, 0, -this.thrust);
		Vector3f weightDrone = FloatMath.transform(this.transMat, this.weightWorld);
		
		Vector3f totalForce = (new Vector3f()).add(thrustV).add(weightDrone);
		Vector3f totalTorque = new Vector3f(0, 0, 0);
		
		
		// wings
		Vector3f[] attacks = new Vector3f[] {new Vector3f(0, FloatMath.sin(this.lwIncl), -FloatMath.cos(this.lwIncl)),
				 new Vector3f(0, FloatMath.sin(this.rwIncl), -FloatMath.cos(this.rwIncl)),
				 new Vector3f(0, FloatMath.sin(this.hsIncl), -FloatMath.cos(this.hsIncl)),
				 new Vector3f(-FloatMath.sin(this.vsIncl), 0, -FloatMath.cos(this.vsIncl))}; 
		
		float[] aoa = new float[4];
		Vector3f wingForce = new Vector3f();
		for (int i = 0; i < 4; i++) {
			Vector3f normal = FloatMath.cross(axisVectors[i], attacks[i]);
			
			Vector3f veli = (new Vector3f()).add(FloatMath.transform(this.transMat, this.vel)).add(FloatMath.cross(this.angVel, this.wingPositions[i]));
			veli.mul(this.velProj[i]); // projecteren op vlak loodrecht op axis
			aoa[i] = - FloatMath.atan2(veli.dot(normal), veli.dot(attacks[i]));
			
			Vector3f force = normal.mul(this.liftSlopes[i] * aoa[i] * FloatMath.squareNorm(veli));
			
			wingForce.add(force);
			totalTorque.add(FloatMath.cross(this.wingPositions[i], force));
		}
		
		
		if (dt != 0 && FloatMath.norm(wingForce) > 50) {
			for (int i = 0; i < 4; i++) {
				if (checkAOA && Math.abs(aoa[i]) > maxAOA)
					throw new PhysicsException("Wing nb " + i + " exceeded maximum aoa");
			}
		}
		totalForce.add(wingForce);
		
		
		// wheels
		for (int i = 0; i < 3; i++) {
			Vector3f worldWheelPos = this.pos.add(FloatMath.transform(this.transMatInv, this.wheelPositions[i]),new Vector3f());
			
			float d = this.tyreRadius - worldWheelPos.y;
			
			worldWheelPos.y = 0;
			
			Vector3f relPos = FloatMath.transform(this.transMat, worldWheelPos.sub(this.pos));
						
			if (d >= this.tyreRadius)
				throw new PhysicsException("Tyre nb " + i + " went underground");
			
			if (d > 0) { // op de grond?
				// lift
				
				float dD = (d - dBuffer[i]) / dt,
					  forceY = this.tyreSlope * d + this.dampSlope * dD;
				
				dBuffer[i] = d;
				
				if (forceY > 0) {
					Vector3f liftForce = FloatMath.transform(transMat, new Vector3f(0, forceY, 0));
					
					totalForce.add(liftForce);
					totalTorque.add(FloatMath.cross(relPos, liftForce));
				} else {
					forceY = 0;
				}
				
				// remmen
				
				Vector3f worldVel = this.vel.add(FloatMath.cross(this.angVel, this.wheelPositions[i]), new Vector3f());
				worldVel.y = 0;
				
				Vector3f brakeForce;
				if (FloatMath.norm(worldVel) > 0) {
					brakeForce = worldVel.normalize(new Vector3f()).mul(-this.brakeForce[i]);
					
				} else {
					Vector3f direction = totalForce.normalize(new Vector3f());
					float norm = FloatMath.norm(totalForce);
					
					if (norm <= this.brakeForce[i]) {
						brakeForce = direction.mul(norm);
					} else {
						brakeForce = direction.mul(this.brakeForce[i]);
					}
				}
				
				totalForce.add(brakeForce);
				totalTorque.add(FloatMath.cross(relPos, brakeForce));
				
				
				// wrijving
				if (i != 1) {
					Vector3f xDirection =  FloatMath.transform(this.transMatInv, new Vector3f(1,0,0));
					xDirection.y = 0;

					float lateralVel = worldVel.dot(xDirection);

					xDirection = FloatMath.transform(this.transMat, xDirection);
					xDirection.normalize();
					
					Vector3f frictionForce =  xDirection.mul(- this.maxFC * lateralVel * forceY);
					totalForce.add(frictionForce);
					totalTorque.add(FloatMath.cross(relPos, frictionForce));
				}
			} else {
				dBuffer[i] = 0;
			}
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
	
	/**
	 * Checks if the plane hits the ground.
	 */
	private void checkCrash() throws PhysicsException {
		// left wing
		Vector3f worldPos = this.pos.add(FloatMath.transform(this.transMatInv, this.wingPositions[0]), new Vector3f());
		if (worldPos.y <= 0)
			throw new PhysicsException("Left wing hit the ground");
		
		// right wing
		worldPos = this.pos.add(FloatMath.transform(this.transMatInv, this.wingPositions[1]), new Vector3f());
		if (worldPos.y <= 0)
			throw new PhysicsException("Right wing hit the ground");
		
		// tail
		worldPos = this.pos.add(FloatMath.transform(this.transMatInv, this.wingPositions[2]), new Vector3f());
		if (worldPos.y <= 0)
			throw new PhysicsException("Tail hit the ground");
		
		// engine
		worldPos = this.pos.add(FloatMath.transform(this.transMatInv, this.enginePos), new Vector3f());
		if (worldPos.y <= 0)
			throw new PhysicsException("Engine hit the ground");
	}
}
