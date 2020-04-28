package testbed.entities.trail;

import testbed.entities.WorldObject;
import testbed.graphics.meshes.Mesh;
import testbed.graphics.meshes.cube.TrailCube;

import org.joml.Vector3f;

import utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class Trail {

    private Vector3f last = new Vector3f(0, 0, 0);
    private List<WorldObject> pathObjects;
    private Mesh trailCube = new TrailCube().getMesh();

    public Trail() {
        pathObjects = new LinkedList<>();
    }

    public void leaveTrail(Vector3f pos) {
        if (Utils.euclDistance(last, pos, 5)) {
            makeTrail(pos);
            int MAX_SIZE = 50;
            if (pathObjects.size() > MAX_SIZE) pathObjects.remove(0);
        }
    }

    private void makeTrail(Vector3f pos) {
        WorldObject cube = new WorldObject(trailCube);
        cube.setPosition(pos);
        cube.setScale(0.8f);
        cube.setRotation(45, 45, 0);
        pathObjects.add(cube);
        last = pos;
    }

    public List<WorldObject> getPathObjects() {
        return this.pathObjects;
    }
}
