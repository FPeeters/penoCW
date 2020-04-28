package testbed.entities.packages;

import java.util.Random;

public class PackageGenerators {

    public static PackageGenerator initialSpawner(int[][] details) {
        return new PackageGenerator() {
            private int count = 0;

            public int[] generatePackage(float time) {
                if (count < details.length) {
                    count++;
                    return details[count - 1];
                } else
                    return null;
            }
        };
    }


    public static PackageGenerator initialSpawner(int[] fromPorts, int[] toPorts) {
        return new PackageGenerator() {
            private int count = 0;
            private Random rand = new Random();

            public int[] generatePackage(float time) {
                if (count < fromPorts.length) {
                    count++;
                    return new int[]{fromPorts[count - 1], rand.nextInt(2), toPorts[count - 1], rand.nextInt(2)};
                }
                return null;
            }
        };
    }


    public static PackageGenerator random(float spawnChance, int nbPorts, long seed) {
        return new PackageGenerator() {
            private Random rand = new Random(seed);

            public int[] generatePackage(float time) {
                if (rand.nextFloat() < spawnChance) {
                    return new int[]{rand.nextInt(nbPorts), rand.nextInt(2), rand.nextInt(nbPorts), rand.nextInt(2)};
                }
                return null;
            }
        };
    }


    public static PackageGenerator timeStamps(float[] stamps, int nbPorts, long seed) {
        return new PackageGenerator() {
            private Random rand = new Random(seed);
            private int count = 0;

            public int[] generatePackage(float time) {
                if (count < stamps.length && time > stamps[count]) {
                    count++;
                    return new int[]{rand.nextInt(nbPorts), rand.nextInt(2), rand.nextInt(nbPorts), rand.nextInt(2)};
                }
                return null;
            }
        };
    }

}
