package testbed.entities.packages;

public interface PackageGenerator {

    /**
     * Defines the behaviour of this packageGenerator.
     *
     * @return {fromAirportId, fromGate, destAirportId, destAirport, destGate}
     */
    int[] generatePackage(float time);

}