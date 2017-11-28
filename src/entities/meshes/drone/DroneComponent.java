package entities.meshes.drone;

import entities.meshes.Mesh;
import interfaces.AutopilotConfig;
import physics.Drone;

public abstract class DroneComponent {

	protected AutopilotConfig config;
	protected float[] positions, colours;
    protected int[] indices;
    private Mesh mesh;

    public DroneComponent(AutopilotConfig config){
        this.config = config;
        setPositions();
        setColours();
        setIndices();

        this.mesh = new Mesh(this.getPositions(), this.getColours(), this.getIndices());
    }

    abstract protected void setPositions();
    abstract protected void setColours();
    abstract protected void setIndices();

    private float[] getPositions() {
        return positions;
    }

    private float[] getColours() {
        return colours;
    }

    public int[] getIndices() {
        return indices;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
