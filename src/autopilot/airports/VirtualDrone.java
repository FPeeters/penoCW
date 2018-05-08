package autopilot.airports;

import autopilot.Pilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import org.joml.Vector3f;

public class VirtualDrone {

    public VirtualDrone(Vector3f position, float heading, AutopilotConfig config) {
        this.position = position;
        this.heading = heading;
        this.config = config;
        this.pilot = new Pilot(this);
        pilot.simulationStarted(config, null);
    }

    private Vector3f position;
    private float heading;

    private Pilot pilot;
    private AutopilotConfig config;
    private AutopilotInputs currentinputs;
    private AutopilotOutputs currentoutputs;

    private VirtualPackage pack;
    private boolean pickedUp;
    private VirtualAirport currTarget, nextTarget;
    
    public Vector3f getPosition() {
        return this.position;
    }

    public float getHeading() {
        return  this.heading;
    }

    public VirtualPackage getPackage() {
        return this.pack;
    }

    public boolean isActive() {
        return this.pilot != null;
    }

    public String getTask() {
    	return isActive() ? this.pilot.getTask(): "Idle";
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
        return currentinputs;
    }

    public void setInputs(AutopilotInputs inputs) {
        this.position = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());
        this.heading = inputs.getHeading();

        this.currentinputs = inputs;
    }

    public AutopilotOutputs getOutputs() {
        return currentoutputs;
    }

    public void setOutputs(AutopilotOutputs outputs){
        this.currentoutputs = outputs;
    }

    public void calcOutputs() {
    	if(pilot != null) {
            setOutputs(pilot.timePassed(getInputs()));
    	}
    }

    public void setPackage(VirtualPackage vpackage){
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
        pilot.simulationEnded();
    }
}
