package world;

import IO.MouseInput;
import datatypes.AutopilotConfig;
import engine.IWorldRules;
import engine.Window;
import engine.graph.Camera;
import image.ImageCreator;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.Drone;
import physics.PhysicsEngine;
import utils.Constants;
import world.drone.DroneMesh;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

/**
 * Place where all the GameItem are to be placed in
 */
public class TestWorld implements IWorldRules {

    /**
     * The angle of the camera in the current world
     */
    private final Vector3f cameraInc;

    /**
     * The renderer that is rendering this world
     */
    private final Renderer renderer;

    /**
     * A camera that defines what we see. An OpenGL camera cannot move on its own, so we move
     * the world itself around. When the camera "goes up" we just shift all world objects
     * downward. Same goes for all other translations and rotations.
     */
    private final Camera camera;

    /**
     * A list of all the GameItems.
     */
    private GameItem[] gameItems;

    /**
     * Create the renderer of this world
     */
    public TestWorld() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    private PhysicsEngine physicsEngine;
    private Drone drone;

    /**
     * Init
     */
    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        Cube redCube = new Cube();

        GameItem cube1= new GameItem(redCube.getMesh());
        cube1.setScale(0.5f);
        cube1.setPosition(0, 0, -25);

        GameItem cube2 = new GameItem(redCube.getMesh());
        cube2.setScale(0.5f);
        cube2.setPosition(0, 0, -50);

        AutopilotConfig config = new AutopilotConfig() {
            public float getGravity() {return 10f;}
            public float getWingX() {return 2.5f;}
            public float getTailSize() {return 5f;}
            public float getEngineMass() {return 70f;}
            public float getWingMass() {return 25f;}
            public float getTailMass() {return 30f;}
            public float getMaxThrust() {return 5000f;}
            public float getMaxAOA() {return -1f;}
            public float getWingLiftSlope() {return 0.11f;}
            public float getHorStabLiftSlope() {return 0f;}
            public float getVerStabLiftSlope() {return 0.11f;}
            public float getHorizontalAngleOfView() {return -1f;}
            public float getVerticalAngleOfView() {return -1f;}
            public int getNbColumns() {return -1;}
            public int getNbRows() {return -1;}};

        physicsEngine = new PhysicsEngine(config);

        drone = new Drone(config);

        DroneMesh droneMesh = new DroneMesh(drone);
        GameItem left = new GameItem(droneMesh.getLeft());
        GameItem right = new GameItem(droneMesh.getRight());
        GameItem body = new GameItem(droneMesh.getBody());

        gameItems = new GameItem[]{cube1, cube2, left, right, body};
    }

    /**
     * Handle input, should be in seperate class
     */
    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_C)) {
            ImageCreator img = new ImageCreator();
            img.screenShot();
        }
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
    }

    /**
     * Handle the game objects internally
     */
    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * Constants.CAMERA_POS_STEP, cameraInc.y * Constants.CAMERA_POS_STEP, cameraInc.z * Constants.CAMERA_POS_STEP);

        drone.setThrust(2000f);
        physicsEngine.update(interval, drone);



        for (int i = 2; i < 5; i++) {
            gameItems[i].setPosition((float)drone.getPosition().get(0),(float)drone.getPosition().get(1),(float)drone.getPosition().get(2));
        }

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
        }
    }

    /**
     * Draw to the screen
     */
    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
    }

    /**
     * Delete VBO VAO
     */
    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}