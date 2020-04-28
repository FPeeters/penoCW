package autopilot.airports;


public class VirtualPackage {

    private final int fromAirport, toAirport, fromGate, toGate;

    private String status;

    private VirtualDrone assignedDrone;

    public VirtualPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
        this.fromGate = fromGate;
        this.toGate = toGate;
    }


    public int getFromAirport() {
        return fromAirport;
    }

    public int getFromGate() {
        return fromGate;
    }

    public int getToAirport() {
        return toAirport;
    }

    public int getToGate() {
        return toGate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void assignDrone(VirtualDrone drone) {
        this.assignedDrone = drone;
    }

    public VirtualDrone getAssignedDrone() {
        return assignedDrone;
    }

}
