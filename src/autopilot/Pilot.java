package autopilot;

import org.joml.Vector3f;

import autopilot.airports.AirportManager;
import autopilot.airports.VirtualAirport;
import autopilot.airports.VirtualDrone;
import autopilot.pilots.FlyPilot;
import autopilot.pilots.HandbrakePilot;
import autopilot.pilots.LandingPilot;
import autopilot.pilots.TakeOffPilot;
import autopilot.pilots.TaxiPilot;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import utils.FloatMath;
import utils.Utils;

public class Pilot {

    public static final int TAKING_OFF = 0,
            LANDING = 1,
            FLYING = 2,
            TAXIING = 3,
            HANDBRAKE = 4,
            PICKUP_TAXI = 5,

    TAKING_OFF_2 = 6,
            LANDING_2 = 7,
            FLYING_2 = 8,
            TAXIING_2 = 9,
            HANDBRAKE_2 = 10,
            PICKUP_TAXI_2 = 11;

    private int index;
    private int[] tasks;

    private PilotPart[] pilots;

    private AutopilotConfig config;

    private VirtualDrone vDrone;

    private AirportManager airportManager;

    public Pilot(VirtualDrone vDrone, AirportManager airportManager) {
        this.tasks = new int[]{};
        this.pilots = new PilotPart[13];
        this.vDrone = vDrone;
        this.airportManager = airportManager;
    }

    public void simulationStarted(AutopilotConfig config) {
        this.config = config;
    }

    private void init() {
        for (PilotPart pilot : this.pilots) {
            if (pilot != null) pilot.initialize(config);
        }
    }

    public AutopilotOutputs timePassed(AutopilotInputs inputs) {

        if (this.index >= this.tasks.length) {
            vDrone.setPilot(null);
            return Utils.buildOutputs(0, 0, 0, 0, 0, 0, 0, 0);
        }

        AutopilotOutputs output = currentPilot().timePassed(inputs);

        if (currentPilot().ended()) {
            currentPilot().close();

            if (state() == HANDBRAKE || state() == HANDBRAKE_2)
                vDrone.nextTarget();
            if (state() == TAXIING_2)
                vDrone.pickUp();
            if ((state() == PICKUP_TAXI && pilots[PICKUP_TAXI_2] == null) || state() == PICKUP_TAXI_2)
                vDrone.deliver();

            this.pilots[state()] = null;
            this.index++;
        }

        return output;
    }

    public void fly(AutopilotInputs inputs, VirtualAirport currentAirport, int currentGate,
                    VirtualAirport fromAirport, int fromGate,
                    VirtualAirport toAirport, int toGate,
                    int flyHeight) {

        this.index = 0;

        Vector3f pos = new Vector3f(inputs.getX(), inputs.getY(), inputs.getZ());

        if (onAirport(pos, fromAirport)) {
            flyLocal(fromAirport, fromGate, toAirport, toGate, flyHeight);
            vDrone.setTargets(toAirport, null);
            vDrone.pickUp();
        } else {
            flyRemote(currentAirport, currentGate, fromAirport, fromGate, toAirport, toGate, flyHeight);
            vDrone.setTargets(fromAirport, toAirport);
            vDrone.getPackage().setStatus("Awaiting pickup");
        }
    }

    private void flyRemote(VirtualAirport currentAirport, int currentGate, VirtualAirport fromAirport, int fromGate,
                           VirtualAirport toAirport, int toGate, int flyHeight) {

        //first flight
        this.pilots[TAXIING] = new TaxiPilot(currentAirport.getGate(currentGate), currentAirport.getHeading() + FloatMath.PI * (currentAirport.getHeading() < 0 ? 1 : -1));

        this.pilots[TAKING_OFF] = new TakeOffPilot(flyHeight);
        this.pilots[FLYING] = new FlyPilot(fromAirport, flyHeight, airportManager, fromGate);
        this.pilots[LANDING] = new LandingPilot(fromAirport);
        this.pilots[PICKUP_TAXI] = new TaxiPilot(fromAirport.getGate(fromGate), fromAirport.getHeading() + FloatMath.PI * (fromAirport.getHeading() < 0 ? 1 : -1));

        this.pilots[HANDBRAKE] = new HandbrakePilot();


        //second flight

        this.pilots[TAXIING_2] = new TaxiPilot(fromAirport.getGate(fromGate), fromAirport.getHeading() + FloatMath.PI * (fromAirport.getHeading() < 0 ? 1 : -1));

        this.pilots[TAKING_OFF_2] = new TakeOffPilot(flyHeight);
        this.pilots[FLYING_2] = new FlyPilot(toAirport, flyHeight, airportManager, toGate);
        this.pilots[LANDING_2] = new LandingPilot(toAirport);
        this.pilots[PICKUP_TAXI_2] = new TaxiPilot(toAirport.getGate(toGate), toAirport.getHeading() + FloatMath.PI * (toAirport.getHeading() < 0 ? 1 : -1));

        this.pilots[HANDBRAKE_2] = new HandbrakePilot();

        this.init();

        this.tasks = new int[]{TAXIING, TAKING_OFF, FLYING, LANDING, PICKUP_TAXI, HANDBRAKE, TAXIING_2, TAKING_OFF_2, FLYING_2, LANDING_2, PICKUP_TAXI_2, HANDBRAKE_2};
    }

    private void flyLocal(VirtualAirport fromAirport, int fromGate,
                          VirtualAirport toAirport, int toGate, int flyHeight) {
        this.pilots[TAXIING] = new TaxiPilot(fromAirport.getGate(fromGate), fromAirport.getHeading() + FloatMath.PI * (fromAirport.getHeading() < 0 ? 1 : -1));

        this.pilots[TAKING_OFF] = new TakeOffPilot(flyHeight);
        this.pilots[FLYING] = new FlyPilot(toAirport, flyHeight, airportManager, toGate);
        this.pilots[LANDING] = new LandingPilot(toAirport);
        this.pilots[PICKUP_TAXI] = new TaxiPilot(toAirport.getGate(toGate), toAirport.getHeading() + FloatMath.PI * (toAirport.getHeading() < 0 ? 1 : -1));

        this.pilots[HANDBRAKE] = new HandbrakePilot();

        this.init();

        this.tasks = new int[]{TAXIING, TAKING_OFF, FLYING, LANDING, PICKUP_TAXI, HANDBRAKE};
    }

    public static boolean onAirport(Vector3f pos, VirtualAirport airport) {
        Vector3f diff = pos.sub(airport.getPosition(), new Vector3f());
        float len = diff.dot(new Vector3f(-FloatMath.sin(airport.getHeading()), 0, -FloatMath.cos(airport.getHeading())));
        float wid = diff.dot(new Vector3f(-FloatMath.cos(airport.getHeading()), 0, FloatMath.sin(airport.getHeading())));

        if (Math.abs(len) > airport.getWidth() / 2) {
            return false;
        } else return !(Math.abs(wid) > airport.getWidth());

    }


	public void simulationEnded() {
        for (PilotPart pilot : this.pilots) {
            if (pilot != null) {
                pilot.close();
            }
        }
    }


    private int state() {
        return this.tasks[this.index];
    }

    public PilotPart currentPilot() {
        return this.pilots[state()];
    }

    public String getTask() {
        if (this.tasks == null || this.index >= this.tasks.length)
            return "Idle";
        return currentPilot().taskName();
    }

    public boolean getEnded() {
        return tasks.length == index;
    }
}
