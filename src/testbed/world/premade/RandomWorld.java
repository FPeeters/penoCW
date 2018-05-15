package testbed.world.premade;

import org.joml.Vector3f;

import autopilot.airports.AirportManager;
import testbed.entities.ground.Ground;
import testbed.entities.packages.PackageGenerator;
import testbed.entities.packages.PackageGenerators;
import testbed.world.World;
import utils.FloatMath;

public class RandomWorld extends World{

	public RandomWorld() {
		super(1, true, 3);
	}

	@Override
	public void setupAutopilotModule() {
		this.autopilotModule = new AirportManager();
	}

	@Override
	public void setupAirports() {
		addAirport(new Vector3f(-200, 0, -11), 0);
		addAirport(new Vector3f(1015, 0, 1089), FloatMath.toRadians(45));
		addAirport(new Vector3f(-1000, 0, -1000), FloatMath.toRadians(-45));
	}

	@Override
	public void setupDrones() {
		addDrone("drone1", 0, 0, 0);
		addDrone("drone2", 1, 1, 1);
		addDrone("drone3", 2, 0, 1);
	}

	@Override
	public void setupWorld() {
		this.ground = new Ground(50);
		
		this.generator = PackageGenerators.random(0.0005f, 3);
	}

	@Override
	public String getDescription() {
		return "Testing the autopilot module";
	}
	
}
