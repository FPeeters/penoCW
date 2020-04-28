package testbed.world.premade;

import org.joml.Vector3f;

import autopilot.airports.AirportManager;
import testbed.entities.ground.Ground;
import testbed.world.World;
import utils.FloatMath;

@SuppressWarnings("unused")
public class ChainWorld extends World {

    public ChainWorld() {
        super(1, true, 2);
    }

    @Override
    public void setupAutopilotModule() {
        this.autopilotModule = new AirportManager();
    }

    @Override
    public void setupAirports() {
        addAirport(new Vector3f(-200, 0, -11), 0);
        addAirport(new Vector3f(1015, 0, 1089), FloatMath.toRadians(45));
        addAirport(new Vector3f(-1000, 0, -1000), FloatMath.toRadians(-45));
    }

    @Override
    public void setupDrones() {
        addDrone("drone0", 0, 0, 0);
        addDrone("drone1", 1, 0, 0);

    }

    @Override
    public void setupWorld() {
        this.ground = new Ground(50);

        this.generator = null;
    }

    @Override
    public String getDescription() {
        return "Testing the autopilot module";
    }

}
