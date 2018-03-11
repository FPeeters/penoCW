package world.premade;

import org.joml.Vector3f;

import engine.IWorldRules;
import entities.WorldObject;
import pilot.Pilot;
import utils.PhysicsException;
import utils.Utils;
import world.World;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorld2 extends World implements IWorldRules {

    public TestWorld2() {
        super(1, true);
    }

    @Override
    public void setup() {
    	config = Utils.createDefaultConfig();
    	  
    	addDrone(config, new Vector3f(0, 0, 0), new Vector3f(0,0,-12));

    	planner = new Pilot(new int[] {});
    	
    	worldObjects = new WorldObject[0];

        float thrust = 20f;
        try {
        	droneHelper.getDronePhysics(config.getDroneID()).updateDrone(Utils.buildOutputs(0 ,0, 0, 0, thrust, 0, 0, 0));
		} catch (PhysicsException e) {
			e.printStackTrace();
		}
    }

	@Override
	public String getDescription() {
		return "World made for the first demonstration, serves nearly no purpose anymore.";
	}
}