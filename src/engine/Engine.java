package engine;

import java.awt.Dimension;
import java.awt.Toolkit;

import utils.IO.MouseInput;
import utils.Constants;
import utils.Timer;

/**
 * An engine to be added to a world
 */
public class Engine implements Runnable {

    private final Window window;

    private final Thread gameLoopThread;

    private final Timer timer;

    private final IWorldRules worldRules;

    private final MouseInput mouseInput;

	private boolean shouldExit = false;

    /**
     * Engine constructor
     * @param windowTitle
     *        The tile given to the window
     * @param width
     *        The width of the window
     * @param height
     *        The height of the window
     * @param vSync
     *        Whether vSync is enabled or not
     * @param worldRules
     *        The interface that the world has to follow
     * @throws Exception
     *         If something goes wrong
     */
    public Engine(String windowTitle, boolean vSync, IWorldRules worldRules) throws Exception {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        
        int ubuntuSiderBar = 0, windowsToolBar = 0;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (System.getProperty("os.name").equals("Linux")) {ubuntuSiderBar = Constants.UBUNTU_SIDEBAR;}
        if (System.getProperty("os.name").contains("Windows")) {windowsToolBar = 80;}
        int width = screenSize.width - Constants.AUTOPILOT_GUI_WIDTH - ubuntuSiderBar;
        int height = screenSize.height - windowsToolBar;
        
        window = new Window(windowTitle, width, height, vSync);
        
        mouseInput = new MouseInput();
        this.worldRules = worldRules;
        timer = new Timer();
    }

    /**
     * Game loop start
     */
    public void start() {
        gameLoopThread.start();    
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }
    }

    /**
     * Initialize all programs used in this world
     * @throws Exception
     *         If something goes wrong
     */
    protected void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        worldRules.init(window, this);
    }
    
    public void setLoopShouldExit() {
    	this.shouldExit = true;
    }
    
    /**
     * elapsedTime: time since last loop in seconds
     * accumulator: total elapsed time since last update
     * interval: how often we should update
     * -> when accumulator exceeds interval an update occurs
     *
     * render(): will render the objects in the world
     *
     * vsync(): depending on whether we have vsync on, sync yourself.
     */
    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / Constants.TARGET_UPS;


        while (!window.windowShouldClose() && ! shouldExit) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }
            
            if (shouldExit)
            	break;

            render();

            if (!window.isvSync()) {
                sync();
            }
        }
        worldRules.endSimulation();
    }

    protected void cleanup() {
        worldRules.cleanup();
    }

    /**
     * If vsync is disabled we'll do it ourselves
     */
    private void sync() {
        float loopSlot = 1f / Constants.TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }



    /**
     * Handle the input in the window
     */
    protected void input() {
        mouseInput.input(window);
        worldRules.input(window, mouseInput);
    }

    /**
     * Update the game objects
     * @param interval
     *        How big the delta is
     */
    protected void update(float interval) {
        worldRules.update(interval, mouseInput);
    }

    /**
     * Draw to the screen. Will also look for screen resizing
     */
    protected void render() {
        worldRules.render(window);
        window.update();
    }
}
