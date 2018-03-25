package world.premade;

import java.util.Arrays;

import org.joml.Vector3f;

import entities.WorldObject;
import entities.ground.Ground;
import entities.tarmac.Tarmac;
import pilot.Pilot;
import utils.Cubes;
import utils.FloatMath;
import utils.Utils;
import world.World;

public class DemoWorld2 extends World {

	public DemoWorld2() {
		super(1, true);
	}
	
	@Override
	public void setup() {
		this.config = Utils.createDefaultConfig();
		
		addDrone(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), new Vector3f(0,0,0), FloatMath.toRadians(90));
		
		this.planner = new Pilot(new int[] {Pilot.WAIT_PATH, Pilot.TAKING_OFF, Pilot.FLYING, Pilot.LANDING});
		
		this.worldObjects = new WorldObject[] {new WorldObject(Cubes.getBlueCube().getMesh()),
											   new WorldObject(Cubes.getGreenCube().getMesh()),
											   new WorldObject(Cubes.getYellowCube().getMesh())};
		
		// TODO: realistische posities
		
		this.worldObjects[0].setPosition(new Vector3f(-200, 150, -1500));
		this.worldObjects[1].setPosition(new Vector3f(-800, 200, 0));
		this.worldObjects[2].setPosition(new Vector3f(0,100,0));
		
		Arrays.asList(worldObjects).stream().forEach(c -> c.setScale(5));
		
		this.ground = new Ground(50);
		this.tarmac = new Tarmac(new Vector3f(0,0,0), 30f, 300f, FloatMath.toRadians(90));
	}

	@Override
	public String getDescription() {
		return "World for demonstrating the second task in the "
				+ "first demo in the second semester.";
	}
}
