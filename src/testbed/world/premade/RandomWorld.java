package testbed.world.premade;

import org.joml.Vector3f;

import autopilot.airports.AirportManager;
import testbed.entities.ground.Ground;
import testbed.entities.packages.PackageGenerators;
import testbed.world.World;
import utils.FloatMath;

@SuppressWarnings("unused")
public class RandomWorld extends World {

    public RandomWorld() {
        super(1, true, 18);
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

        addAirport(new Vector3f(-200, 0, -1000), 0);
        addAirport(new Vector3f(1015, 0, -500), FloatMath.toRadians(45));
        addAirport(new Vector3f(-1000, 0, -500), FloatMath.toRadians(-45));

        addAirport(new Vector3f(-200, 0, 1000), 0);
        addAirport(new Vector3f(1015, 0, 500), FloatMath.toRadians(45));
        addAirport(new Vector3f(-1000, 0, 500), FloatMath.toRadians(-45));
    }

    @Override
    public void setupDrones() {
        addDrone("drone0", 0, 0, 1);
        addDrone("drone1", 1, 0, 1);
        addDrone("drone2", 2, 0, 1);
        addDrone("drone3", 3, 0, 1);
        addDrone("drone4", 4, 0, 1);
        addDrone("drone5", 5, 0, 1);
        addDrone("drone6", 6, 0, 1);
        addDrone("drone7", 7, 0, 1);
        addDrone("drone8", 8, 0, 1);

//		addDrone("drone0d", 0, 1, 1);
//		addDrone("drone1d", 1, 1, 1);
//		addDrone("drone2d", 2, 1, 1);
//		addDrone("drone3d", 3, 1, 1);
//		addDrone("droned4", 4, 1, 1);
//		addDrone("dronde5", 5, 1, 1);
//		addDrone("drode6", 6, 1, 1);
//		addDrone("drodne7", 7, 1, 1);
//		addDrone("dronde8", 8, 1, 1);	
    }

    @Override
    public void setupWorld() {
        this.ground = new Ground(50);

        this.generator = PackageGenerators.random(0.005f, 9, 1337);
    }

    @Override
    public String getDescription() {
        return "Testing the autopilot module";
    }

}
