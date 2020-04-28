package autopilot.airports;

import autopilot.Pilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;

public class VirtualDrone {

    public VirtualDrone(Vector3f position, float heading, AutopilotConfig config, AirportManager airPortManager) {
        this.position = position;
        this.heading = heading;
        this.config = config;
        this.pilot = new Pilot(this, airPortManager);
        pilot.simulationStarted(config);
    }

    private Vector3f position;
    private float heading;

    private Pilot pilot;
    private AutopilotConfig config;
    private AutopilotInputs currentInputs;
    private AutopilotOutputs currentOutputs;

    private VirtualPackage pack;
    private boolean pickedUp;
    private VirtualAirport currTarget, nextTarget;

    public Vector3f getPosition() {
        return this.position;
    }

    public float getHeading() {
        return this.heading;
    }

    public VirtualPackage getPackage() {
        return this.pack;
    }

    public boolean isActive() {
        return this.pilot != null;
    }

    public String getTask() {
        return isActive() ? this.pilot.getTask() : "Idle";
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

    public Pilot getPilot() {
        return pilot;
    }

    public AutopilotConfig getConfig() {
        return config;
    }

    public AutopilotInputs getInputs() {
        return currentInputs;
    }

    public void setInputs(AutopilotInputs inputs) {
        this.position = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());
        this.heading = inputs.getHeading();

        this.currentInputs = inputs;
    }

    public AutopilotOutputs getOutputs() {
        return currentOutputs;
    }

    public void setOutputs(AutopilotOutputs outputs) {
        this.currentOutputs = outputs;
    }

    public void calcOutputs() {
        if (pilot != null) {
            setOutputs(pilot.timePassed(getInputs()));
        }
    }

    public void setPackage(VirtualPackage vpackage) {
        this.pickedUp = false;
        this.pack = vpackage;
    }

    public void pickUp() {
        this.pickedUp = true;
        this.pack.setStatus("Picked up");
    }

    public void deliver() {
        this.pack.setStatus("Delivered");
        this.pack = null;
        this.pickedUp = false;
    }

    public boolean pickedUp() {
        return pickedUp;
    }

    public void setTargets(VirtualAirport currTarget, VirtualAirport nextTarget) {
        this.currTarget = currTarget;
        this.nextTarget = nextTarget;
    }

    public VirtualAirport getTarget() {
        return this.currTarget;
    }

    public void nextTarget() {
        this.currTarget = this.nextTarget;
        this.nextTarget = null;
    }

    public void setPilot(Pilot pilot) {
        this.pilot = pilot;
    }

    public void endSimulation() {
        if (pilot != null)
            pilot.simulationEnded();
    }
}
