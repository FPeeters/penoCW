package testbed.world.premade;

import org.joml.Vector3f;

import testbed.entities.ground.Ground;
import testbed.entities.packages.PackageGenerators;
import testbed.world.World;
import utils.FloatMath;
import utils.Utils;

@SuppressWarnings("unused")
public class PackageTestWorld extends World {

    public PackageTestWorld() {
        super(1, true, 3);
    }

    @Override
    public void setupAutopilotModule() {
        this.autopilotModule = null;
    }

    @Override
    public void setupAirports() {
        addAirport(new Vector3f(0, 0, 0), 0);
        addAirport(new Vector3f(1000, 0, 1000), FloatMath.toRadians(45));
        addAirport(new Vector3f(-1000, 0, -1000), FloatMath.toRadians(-45));
    }

    @Override
    public void setupDrones() {
        addDrone("drone1", 0, 0, 0);
        addDrone("drone2", 1, 1, 1);
        addDrone("drone3", 2, 0, 1);
    }

    @Override
    public void setupWorld() {
        this.ground = new Ground(50);

        this.generator = PackageGenerators.initialSpawner(new int[][]{Utils.buildIntArr(0, 1, 1, 0),
                Utils.buildIntArr(1, 1, 2, 1),
                Utils.buildIntArr(2, 0, 2, 0)});
    }

    @Override
    public String getDescription() {
        return null;
    }

}
