package autopilot;

import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public abstract class PilotPart {

    /**
     * Instellen van de config
     */
    public abstract void initialize(AutopilotConfig config);

    /**
     * Tijdsstap uitvoeren
     */
    public abstract AutopilotOutputs timePassed(AutopilotInputs input);

    /**
     * Is deze pilot klaar met zijn taak?
     */
    public abstract boolean ended();

    /**
     * Afsluiten van de pilot na dat hij klaar is
     */
    public abstract void close();


    /**
     * Taakbeschrijving (1-2 woorden)
     */
    public abstract String taskName();
}
