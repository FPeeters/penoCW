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

public class DemoWorld3 extends World {

	public DemoWorld3() {
		super(1, true);
	}
	
	@Override
	public void setup() {
		this.config = Utils.createDefaultConfig();
		
		addDrone(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), new Vector3f(0,0,0), FloatMath.toRadians(0));
		
		this.planner = new Pilot(new int[] {Pilot.WAIT_PATH, Pilot.TAKING_OFF, Pilot.FLYING, Pilot.LANDING, Pilot.TAXIING, Pilot.HANDBRAKE});
		
		this.worldObjects = new WorldObject[] {new WorldObject(Cubes.getBlueCube().getMesh()),
											   new WorldObject(Cubes.getGreenCube().getMesh()),
											   new WorldObject(Cubes.getYellowCube().getMesh()),
											   new WorldObject(Cubes.getRedCube().getMesh()),
											   new WorldObject(Cubes.getCyanCube().getMesh())};

		this.worldObjects[0].setPosition(new Vector3f(-50, 100, -1200));
		this.worldObjects[1].setPosition(new Vector3f(-800, 100, -500));
		this.worldObjects[2].setPosition(new Vector3f(0,125,500));
		this.worldObjects[3].setPosition(new Vector3f(1400, 100, -200));
		this.worldObjects[4].setPosition(new Vector3f(100, 75, -300));
		
		Arrays.asList(worldObjects).stream().forEach(c -> c.setScale(5));

		
		this.ground = new Ground(50);
		this.tarmac = new Tarmac(new Vector3f(0,0,0), 30f, 300f, FloatMath.toRadians(0));
	}

	@Override
	public String getDescription() {
		return "World for demonstrating the third task in the "
				+ "first demo in the second semester.";
	}
}
