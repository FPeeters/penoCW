package world.premade;

import java.util.Arrays;
import java.util.Random;

import org.joml.Vector3f;

import entities.WorldObject;
import entities.ground.Ground;
import entities.tarmac.Tarmac;
import pilot.Pilot;
import utils.Cubes;
import utils.FloatMath;
import utils.Utils;
import world.World;

public class TestWorld extends World {

	public TestWorld() {
		super(1, true);
	}
	
	@Override
	public void setup() {
		this.config = Utils.createDefaultConfig();
		
		addDrone(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), new Vector3f(0,0,0), FloatMath.toRadians(0));
		
		this.planner = new Pilot(new int[] {Pilot.WAIT_PATH, Pilot.TAKING_OFF, Pilot.FLYING, Pilot.LANDING, Pilot.HANDBRAKE});
		
		this.worldObjects = new WorldObject[] {new WorldObject(Cubes.getBlueCube().getMesh()),
											   new WorldObject(Cubes.getGreenCube().getMesh()),
											   new WorldObject(Cubes.getYellowCube().getMesh()),
											   new WorldObject(Cubes.getRedCube().getMesh()),
											   new WorldObject(Cubes.getCyanCube().getMesh())};
		
		Random rand = new Random();
		int[] x = rand.ints(5, -1500, 1500).toArray();
		int[] y = rand.ints(5, 0, 300).toArray();
		int[] z = rand.ints(5, -1500, 1500).toArray();
		
		this.worldObjects[0].setPosition(new Vector3f(x[0], y[0], z[0]));
		this.worldObjects[1].setPosition(new Vector3f(x[1], y[1], z[1]));
		this.worldObjects[2].setPosition(new Vector3f(x[2], y[2], z[2]));
		this.worldObjects[3].setPosition(new Vector3f(x[3], y[3], z[3]));
		this.worldObjects[4].setPosition(new Vector3f(x[4], y[4], z[4]));
		System.out.println(new Vector3f(x[0], y[0], z[0]));
		System.out.println(new Vector3f(x[1], y[1], z[1]));
		System.out.println(new Vector3f(x[2], y[2], z[2]));
		System.out.println(new Vector3f(x[3], y[3], z[3]));
		System.out.println(new Vector3f(x[4], y[4], z[4]));
		
		Arrays.asList(worldObjects).stream().forEach(c -> c.setScale(5));

		
		this.ground = new Ground(50);
		this.tarmac = new Tarmac(new Vector3f(0,0,0), 30f, 300f, FloatMath.toRadians(0));
	}

	@Override
	public String getDescription() {
		return "World for demonstrating the second task in the "
				+ "first demo in the second semester.";
	}
}