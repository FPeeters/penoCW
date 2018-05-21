package testbed.world.premade;

import org.joml.Vector3f;

import autopilot.airports.AirportManager;
import testbed.entities.ground.Ground;
import testbed.entities.packages.PackageGenerators;
import testbed.world.World;
import utils.FloatMath;

public class StressTestWorld extends World{

	public StressTestWorld() {
		super(1, true, 81);
	}

	@Override
	public void setupAutopilotModule() {
		this.autopilotModule = new AirportManager();
	}

	@Override
	public void setupAirports() {
		for(int j = -4; j < 5; j++) {
			for(int i = -4; i < 5; i ++) {
				addAirport(new Vector3f(i * 550, 0, j * 550), (float) Math.random() * FloatMath.PI);
			}
		}

	}

	@Override
	public void setupDrones() {
		
		for(int i = 0; i < 81; i++) { 
			addDrone("drone"+i, i, 0, 1);
		}
	}

	@Override
	public void setupWorld() {
		this.ground = new Ground(50);
		
		this.generator = PackageGenerators.random(0.005f, 81, 1337);
	}

	@Override
	public String getDescription() {
		return "Testing the autopilot module";
	}
	
}
