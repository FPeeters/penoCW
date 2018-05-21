package utils;


public class Constants {

    /**
     * Window constants
     */
    public static final String TITLE = "PnO Drone Simulation";
    public static final boolean VSYNC = true;
    //only for initialization!
//    public static final int WIDTH = 1500;
    //only for initialization!
//    public static final int HEIGHT = 750;
    
    /**
     * Premade worlds that should show in the gui.
     */
	public static final String[] PREMADE_WORLDS = new String[] {"CubeWorld", "StopWorld", "TestWorld", "TestWorldFlyStraight",
																"OrthoTestWorld", "TestWorld2", "LandingWorld", 
																"TakeOffWorld", "BounceWorld", "AirportSetupWorld", 
																"DemoWorld1", "DemoWorld2", "DemoWorld3",  "TestWorldPitch", "TaxiWorld"};

	/**
	 * Pilot constants
	 */
    
	public static float climbAngle = FloatMath.toRadians(10);


    /**
     * Drone pickup accuracy + 2.5 to take sides of cubes into account
     */
    public static final float PICKUP_DISTANCE = 3f;
    public static final float DRONE_PICKUP_DISTANCE = 3f;


    /**
     * GUI
     */    
    public static final int AUTOPILOT_GUI_HEIGHT = 450;
    public static final int AUTOPILOT_GUI_WIDTH = 500;
    
    public static final float COLLISION_RANGE = 5f;
    public static final float PATH_ACCURACY = 0f;
    
    /**
     * Renderer constants
     */
    //Field of View in Radians
    public static final float FOV = (float) Math.toRadians(90f);
    public static final float Z_NEAR = 0.7f;
    public static final float Z_FAR = 10000.f;
    public static final float DRONE_THICKNESS = 0.35f;  //in meters
    public static final float DRONE_WHEEL_THICKNESS = 0.08f;
    public static final int DRONE_LEFT_WING = 0;
    public static final int DRONE_RIGHT_WING = 1;
    public static final int DRONE_BODY = 2;
    public static final int DRONE_WHEEL_FRONT = 3;
    public static final int DRONE_WHEEL_BACK_LEFT = 4;
    public static final int DRONE_WHEEL_BACK_RIGHT = 5;
    
    public static final int UBUNTU_SIDEBAR = 105;
    /**
     * Game engine
     */
    public static final int TARGET_FPS = 75;

    public static final int TARGET_UPS = 100;

    /**
     * physics.World constants
     */
    public static final float MOUSE_SENSITIVITY = 0.4f;

    public static final float CAMERA_POS_STEP = 0.5f;

    public static final int TILE_SIZE = 75;
    /**
     * default config settings
     */
    public static final float DEFAULT_GRAVITY = 9.81f;
    
    public static final float DEFAULT_WINGX = 4.2f;
    
    public static final float DEFAULT_TAILSIZE = 4.2f;
    
    public static final float DEFAULT_ENGINE_MASS = 180f;
    
    public static final float DEFAULT_WING_MASS = 100f;
    
    public static final float DEFAULT_TAIL_MASS = 100f;
    
    public static final float DEFAULT_MAX_THRUST = 2000f;
    
    public static final int DEFAULT_MAX_AOA = 15;
    
    public static final float DEFAULT_WING_LIFTSLOPE = 10f;
    
    public static final float DEFAULT_VER_STAB_LIFTSLOPE = 5f;
    
    public static final float DEFAULT_HOR_STAB_LIFTSLOPE = 5f;
    
    public static final int DEFAULT_VER_FOV = 120;
    
    public static final int DEFAULT_HOR_FOV = 120;
    
    public static final int DEFAULT_NB_COLS = 200;
    
    public static final int DEFAULT_NB_ROWS = 200;
}