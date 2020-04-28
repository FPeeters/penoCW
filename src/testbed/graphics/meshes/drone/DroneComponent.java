package testbed.graphics.meshes.drone;

import interfaces.AutopilotConfig;
import testbed.graphics.meshes.AbstractMesh;

public abstract class DroneComponent extends AbstractMesh {

    protected AutopilotConfig config;

    public void finalizer(AutopilotConfig config) {
        this.config = config;

        finalizer();
    }

}
