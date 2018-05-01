package testbed.world.premade;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import testbed.entities.WorldObject;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.Cubes;
import utils.Utils;

public class LandingWorld extends World{

	public LandingWorld() {
		super(1, true, 1, 20, 200);
	}

	@Override
    public void setupAutopilotModule() {
    	
    }
	
	@Override
	public void setupAirports() {
		addAirport(new Vector3f(20, 0, -500), 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setupDrones() {
		AutopilotConfig config = Utils.createDefaultConfig("drone1");
		
		addDrone(config, new Vector3f(0, 25, 0), new Vector3f(0,0,-40), 0);
	}

	@Override
	public void setupWorld() {
		
		this.worldObjects = new WorldObject[500];
		
		for (int i = 0; i < this.worldObjects.length; i++) {
			WorldObject cube = new WorldObject(Cubes.getPinkCube().getMesh());
			cube.setScale(1);
			cube.setPosition(0, -0.5f, i*-25);
			
			this.worldObjects[i] = cube;
		}
		
		this.ground = new Ground(50);		
	}
	
	@Override
	public String getDescription() {
		return "World to test the pilot responsible for landing";
	}
}
