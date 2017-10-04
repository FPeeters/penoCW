import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.la4j.Vector;

public class World {
	
	private final float gravity = (float) 9.81;
	private Map<Vector,Cube> cubes;
	
	public void addCube(Cube cube) {
		cubes.put(cube.getCenter(), cube);
	}
	
	public void removeCube(Cube cube) {
		if (cubes.containsValue(cube))
			cubes.remove(cube.getCenter());
	}
	
	public Set<Cube> getCubes() {
		return new HashSet<Cube>(cubes.values());
	}
	
	public float getGravity() {
		return this.gravity;
	}
}
