package utils;

public class Timer {

    /**
     * Contains the last time (in seconds) that this function was called
     */
    private double lastLoopTime;

    /**
     * Give a start value to the timer
     * Has to be init'd for elapsedTime to work properly
     */
    public void init() {
        lastLoopTime = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1000_000_000.0;
    }

    /**
     * Measures the time between init and elapsedTime or between two consecutive elapsedTime calls
     *
     * @return elapsedTime
     * Time in seconds
     */
    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }

    /**
     * Simply return when this function was called the last time.
     *
     * @return lastTime
     * Time in seconds
     */
    public double getLastLoopTime() {
        return lastLoopTime;
    }
}