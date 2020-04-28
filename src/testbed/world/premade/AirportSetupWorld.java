package testbed.world.premade;

import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.FloatMath;
import utils.PhysicsException;
import utils.Utils;

import org.joml.Vector3f;

@SuppressWarnings("unused")
public class AirportSetupWorld extends World {
	
    public AirportSetupWorld() {
        super(1, true, amount*2, 25, 250);

    }

    @Override
    public void setupAutopilotModule() {
    	
    }
    
    private static final int amount = 24;
    
    @Override
    public void setupAirports() {
    	for(int i = -amount/2; i < amount/2; i++) {
        	addAirport(new Vector3f(100 * i, 0, 0), FloatMath.toRadians(0));

    	}
    }
    
	@Override
	public void setupDrones() {		
		for(int i = 0; i < amount; i++) {
			for(int j = 0; j < 2; j++) {
				addDrone("" + i + j, i, j, 0);
			}	
		}
	}

	@Override
	public void setupWorld() {
		try {
			for(int i = 0; i < amount * 2; i++) {
				droneHelper.getDronePhysics(i).updateDrone(Utils.buildOutputs(FloatMath.toRadians(10), FloatMath.toRadians(10), 0, 0, 2000, 0, 0, 0));
			}
		} catch (PhysicsException e) {
			e.printStackTrace();
		} 

        this.ground = new Ground(50);		
	}
	
    @Override
    public String getDescription() {
        return "A test for the airport setup";
    }
}
