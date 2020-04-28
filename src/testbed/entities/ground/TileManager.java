package testbed.entities.ground;

import java.util.ArrayList;
import java.util.List;

import utils.FloatMath;
import utils.Utils;
import utils.graphics.RGBTuple;
import testbed.entities.WorldObject;
import testbed.graphics.meshes.tile.DoubleUnitTile;

public class TileManager {

    List<WorldObject> tileList;
    List<WorldObject> airList;

    public TileManager(int size, int tileSize) {
        tileList = new ArrayList<>();
        airList = new ArrayList<>();

        float[] col1 = Utils.toRGB(-105, 1, 0.8f);
        float[] col2 = Utils.toRGB(-105, 1, 0.7f);

        RGBTuple groundLight = new RGBTuple(col1[0], col1[1], col1[2]);
        RGBTuple groundDark = new RGBTuple(col2[0], col2[1], col2[2]);

        float[] colAir1 = Utils.toRGB(-155, 0.5f, 0.9f);
        float[] colAir2 = Utils.toRGB(-155, 0.5f, 0.8f);

        RGBTuple airLight = new RGBTuple(colAir1[0], colAir1[1], colAir1[2]);
        RGBTuple airDark = new RGBTuple(colAir2[0], colAir2[1], colAir2[2]);


        int val;
        boolean checkZero = false;
        boolean even;
        if (size % 2 == 0) {
            val = size / 2;
            even = true;
        } else {
            val = (size - 1) / 2;
            checkZero = true;
            even = false;
        }

        //oneven op de gehele getallen

        boolean flip = true;
        boolean flip2 = true;
        for (int i = -val; i <= val; i++) {
            if (i == 0 && !checkZero) {
                continue;
            }

            for (int j = -val; j <= val; j++) {
                if (j == 0 && !checkZero) {
                    continue;
                }

                // ground mesh
                DoubleUnitTile doubleUnitTile = (flip) ? new DoubleUnitTile(groundLight) : new DoubleUnitTile(groundDark);
                flip = !flip;

                // air mesh
                DoubleUnitTile doubleUnitTileAir = (flip2) ? new DoubleUnitTile(airLight) : new DoubleUnitTile(airDark);
                flip2 = !flip2;

                WorldObject tile = new WorldObject(doubleUnitTile.getMesh());
                WorldObject airTile = new WorldObject(doubleUnitTileAir.getMesh());


                float iVal = 0;
                if (even) {
                    iVal = (i < 0) ? (+0.5f) : -0.5f;
                }

                float jVal = 0;
                if (even) {
                    jVal = (j < 0) ? (+0.5f) : -0.5f;
                }

                tile.setPosition((i + iVal) * tileSize * doubleUnitTile.getSize(), 0, (j + jVal) * tileSize * doubleUnitTile.getSize());
                tile.setScale(tileSize);
                tileList.add(tile);

                airTile.setPosition(-(size * tileSize), (i + iVal) * tileSize * doubleUnitTile.getSize() + ((size * tileSize)), (j + jVal) * tileSize * doubleUnitTile.getSize());
                airTile.setRotation(0, 0, FloatMath.toRadians(90));
                airTile.setScale(tileSize);
                airList.add(airTile);

            }
            if (even) {
                flip = !flip;
                flip2 = !flip2;
            }
        }

    }

    public List<WorldObject> getCombinedList() {
        ArrayList<WorldObject> returnList = new ArrayList<>();
        returnList.addAll(airList);
        returnList.addAll(tileList);
        return returnList;
    }

    public List<WorldObject> getTileList() {
        return tileList;
    }

}
