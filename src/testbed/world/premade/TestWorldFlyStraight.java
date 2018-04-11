package testbed.world.premade;

import org.joml.Vector3f;

import autopilot.Pilot;
import interfaces.AutopilotConfig;
import testbed.engine.IWorldRules;
import testbed.entities.WorldObject;
import testbed.world.World;
import utils.Cubes;
import utils.Utils;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorldFlyStraight extends World implements IWorldRules {

    public TestWorldFlyStraight() {
        super(1, true, 1);
    }

    @Override
    public void setupAirports() { }

	@SuppressWarnings("deprecation")
	@Override
	public void setupDrones() {
		AutopilotConfig config = Utils.createDefaultConfig("drone1");
  	  
    	addDrone(config, new Vector3f(0,100,0), new Vector3f(0,0,-10), 0);

    	planner = new Pilot(new int[] {Pilot.FLYING});		
	}

	@Override
	public void setupWorld() {
        worldObjects = new WorldObject[1];
        worldObjects[0] = new WorldObject(Cubes.getCubes()[0].getMesh());
        worldObjects[0].setPosition(0f,0f,-400f);		
	}
    
	@Override
	public String getDescription() {
		return "Demonstrates the ability to fly straight and horizonatally of the autopilot. The drone starts with an initial velocity,"
				+ " flying towards a cube located 100m infront of it.";
	}
}
