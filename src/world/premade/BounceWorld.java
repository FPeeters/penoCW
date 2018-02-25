package world.premade;

import org.joml.Vector3f;

import physics.Physics;
import utils.Utils;
import world.World;

public class BounceWorld extends World {
	
	public BounceWorld() {
		super(1, true);
	}
	
	@Override
	public void setup() {
		this.config = Utils.createDefaultConfig();
		
		this.physics = new Physics(false);
		this.physics.init(config, new Vector3f(0, -config.getWheelY() + config.getTyreRadius(), 0), new Vector3f(0,-1.8f,0));
		
		this.planner = null;
	}

	@Override
	public String getDescription() {
		return "Testing for maximum inpact velocity";
	}

}
