package testbed.entities.ground;

import java.util.List;

import testbed.entities.WorldObject;
import utils.Constants;

public class Ground {

    TileManager tm;

    public Ground(int x, int y, int size) {
        this.tm = new TileManager(size, Constants.TILE_SIZE);
    }

    public Ground(int size) {
        this(0, 0, size);
    }

    public List<WorldObject> getTiles() {
        return tm.getTileList();
    }

    public List<WorldObject> getCombined() {
        return tm.getCombinedList();
    }

}
