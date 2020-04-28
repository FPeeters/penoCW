package testbed.world;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import testbed.engine.IWorldRules;

public class WorldBuilder extends World implements IWorldRules {

    public WorldBuilder(int tSM, boolean wantPhysicsEngine) {
        super(tSM, wantPhysicsEngine, 1);
    }

    @Override
    public void setupAutopilotModule() {
    }

    @Override
    public void setupAirports() {

    }

    @Override
    public void setupDrones() {

    }

    @Override
    public void setupWorld() {

    }

    @Override
    public String getDescription() {
        return "Internal hook for GUI world creation";
    }

    @SuppressWarnings("deprecation")
    public void setupDrone(AutopilotConfig config, Vector3f startPos, Vector3f startVel) {
        addDrone(config, startPos, startVel, 0);
    }

}
