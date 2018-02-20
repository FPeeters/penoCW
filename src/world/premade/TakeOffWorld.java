package world.premade;

import org.joml.Vector3f;

import entities.WorldObject;
import physics.Physics;
import pilot.Pilot;
import utils.Cubes;
import utils.Utils;
import world.World;

public class TakeOffWorld extends World {
	
	public TakeOffWorld() {
		super(1, true);
	}
	
	@Override
	public void setup() {
		this.config = Utils.createDefaultConfig();
		
		this.physics = new Physics(false);
		this.physics.init(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), 0);
		
		this.planner = new Pilot();
		
		this.worldObjects = new WorldObject[40];
		
		for (int i = 0; i < this.worldObjects.length; i++) {
			WorldObject cube = new WorldObject(Cubes.getPinkCube().getMesh());
			cube.setScale(1);
			cube.setPosition(0, -0.5f, i*-25);
			
			this.worldObjects[i] = cube;
		}
	}

	@Override
	public String getDescription() {
		return "World to test the pilot responsible for taking off";
	}

}
