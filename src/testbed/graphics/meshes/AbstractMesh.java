package testbed.graphics.meshes;

public abstract class AbstractMesh {

    protected float[] positions, colours;
    protected int[] indices;

    protected Mesh mesh;

    public void finalizer() {
        setPositions();
        setColours();
        setIndices();

        this.mesh = new Mesh(positions, colours, indices);
    }

    abstract protected void setPositions();

    abstract protected void setColours();

    abstract protected void setIndices();

    public Mesh getMesh() {
        return mesh;
    }

}
