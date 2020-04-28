package testbed.graphics;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;

import utils.*;
import testbed.engine.Window;
import testbed.entities.WorldObject;
import testbed.entities.airport.Airport;
import testbed.entities.ground.Ground;
import testbed.entities.packages.Package;
import testbed.world.helpers.CameraHelper;
import testbed.world.helpers.DroneHelper;

import java.util.List;
import java.util.Set;

public class Renderer {

    private int freeCamX, freeCamY, freeCamWidth, freeCamHeigth;
    private int chaseCamX, chaseCamY, chaseCamWidth, chaseCamHeigth;
    private int topOrthoCamX, topOrthoCamY, topOrthoCamWidth, topOrthoCamHeigth;
    private int rightOrthoCamX, rightOrthoCamY, rightOrthoCamWidth, rightOrthoCamHeigth;

    private boolean ortho = true;

    private ShaderProgram shaderProgram;


    public Renderer() {
    }


    public void toggleOrtho() {
        this.ortho = !ortho;
    }


    public void init() throws Exception {
        shaderProgram = new ShaderProgram();

        //can be used to modify properties of the vertex such as position, color, and texture coordinates.
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        //is used for calculating individual fragment colors.
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
    }


    public void clear(Window window) {
        glEnable(GL_SCISSOR_TEST);

        // clear all just to be sure
        glScissor(0, 0, window.getWidth(), window.getHeight());
        glClearColor(1f, 1f, 1f, 0f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);


        // background for chase cam
        glScissor(chaseCamX, chaseCamY, chaseCamWidth, chaseCamHeigth);
        glClearColor(.51f, .51f, .51f, 1f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        if (!ortho) {
            // background for free camera
            glScissor(freeCamX, freeCamY, freeCamWidth, freeCamHeigth);
            glClearColor(.41f, .4f, .4f, 1f);

        } else {
            // background for top ortho cam
            glScissor(topOrthoCamX, topOrthoCamY, topOrthoCamWidth, topOrthoCamHeigth);
            glClearColor(.30f, .30f, .30f, 1f);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            // background for right ortho cam
            glScissor(rightOrthoCamX, rightOrthoCamY, rightOrthoCamWidth, rightOrthoCamHeigth);
            glClearColor(0.11f, 0.65f, 0.07f, 1f);

            //end
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glDisable(GL_SCISSOR_TEST);
    }

    public void render(Window window, CameraHelper cameraHelper, DroneHelper droneHelper,
                       Set<Package> packages, Ground ground, List<Airport> airports) {
        clear(window);

        drawChaseCam(window, cameraHelper, droneHelper, packages, ground, airports);

        if (!ortho) {
            drawFreeCam(window, cameraHelper, droneHelper, packages, ground, airports);
        } else {
            int size = 160;

            drawTopOrthoCam(window, cameraHelper, droneHelper, packages, size, ground, airports);

            drawRightOrthCam(window, cameraHelper, droneHelper, packages, size, ground, airports);
        }
    }


    private void drawChaseCam(Window window, CameraHelper cameraHelper,
                              DroneHelper droneHelper, Set<Package> packages, Ground ground, List<Airport> airports) {
        Matrix4f projectionMatrix;
        Matrix4f viewMatrix;
        shaderProgram.bind();
        chaseCamWidth = (int) (window.getWidth() * 0.25);
        chaseCamHeigth = (int) (window.getHeight() * 0.5);
        chaseCamX = 0;
        chaseCamY = (int) (window.getHeight() * 0.5);
        glViewport(chaseCamX, chaseCamY, chaseCamWidth, chaseCamHeigth);

        //Update projection Matrix
        projectionMatrix = Transformation.getProjectionMatrix((float) Math.toRadians(90), chaseCamWidth, chaseCamHeigth, Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        //Update view Matrix
        viewMatrix = Transformation.getViewMatrix(cameraHelper.chaseCamera);

        renderAirports(airports, viewMatrix);
        renderGround(ground, viewMatrix, false);
        renderPackages(packages, viewMatrix);
        renderDroneItems(droneHelper, viewMatrix);
        shaderProgram.unbind();
    }


    private void drawFreeCam(Window window, CameraHelper cameraHelper,
                             DroneHelper droneHelper, Set<Package> packages, Ground ground, List<Airport> airports) {
        Matrix4f projectionMatrix;
        Matrix4f viewMatrix;
        shaderProgram.bind();
        freeCamX = chaseCamWidth;
        freeCamY = 0;
        freeCamWidth = window.getWidth() - chaseCamWidth;
        freeCamHeigth = window.getHeight();
        glViewport(freeCamX, freeCamY, freeCamWidth, freeCamHeigth);


        // Update projection Matrix
        projectionMatrix = Transformation.getProjectionMatrix(Constants.FOV, freeCamWidth, freeCamHeigth, Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        viewMatrix = Transformation.getViewMatrix(cameraHelper.freeCamera);

        renderAirports(airports, viewMatrix);
        renderGround(ground, viewMatrix, false);
        renderTrail(droneHelper, viewMatrix);
        renderPackages(packages, viewMatrix);
        renderDroneItems(droneHelper, viewMatrix);
        shaderProgram.unbind();
    }

    private void drawTopOrthoCam(Window window, CameraHelper cameraHelper,
                                 DroneHelper droneHelper, Set<Package> packages, int size, Ground ground, List<Airport> airports) {
        Matrix4f viewMatrix;
        shaderProgram.bind();
        topOrthoCamX = chaseCamWidth;
        topOrthoCamY = chaseCamY;
        topOrthoCamWidth = window.getWidth() - chaseCamWidth;
        topOrthoCamHeigth = (int) (window.getHeight() * 0.5);
        glViewport(topOrthoCamX, topOrthoCamY, topOrthoCamWidth, topOrthoCamHeigth);


        // Update projection Matrix
        Matrix4f projectionMatrix = new Matrix4f().identity().ortho((3 / 2f) * -size, (3 / 2f) * size, (4 / 3f) * -size / 2, (4 / 3f) * size / 2, Constants.Z_NEAR, Constants.Z_FAR).rotateZ(FloatMath.toRadians(-90));
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        viewMatrix = Transformation.getViewMatrix(cameraHelper.topOrthoCamera);

        renderAirports(airports, viewMatrix);
        renderGround(ground, viewMatrix, false);
        renderTrail(droneHelper, viewMatrix);
        renderPackages(packages, viewMatrix);
        renderDroneItems(droneHelper, viewMatrix);
        shaderProgram.unbind();
    }

    private void drawRightOrthCam(Window window, CameraHelper cameraHelper,
                                  DroneHelper droneHelper, Set<Package> packages, int size, Ground ground, List<Airport> airports) {
        Matrix4f viewMatrix;
        shaderProgram.bind();

        rightOrthoCamX = chaseCamWidth;
        rightOrthoCamY = 0;
        rightOrthoCamWidth = window.getWidth() - chaseCamWidth;
        rightOrthoCamHeigth = (int) (window.getHeight() * 0.5);
        glViewport(rightOrthoCamX, rightOrthoCamY, rightOrthoCamWidth, rightOrthoCamHeigth);

        // Update projection Matrix
        Matrix4f projectionMatrix = new Matrix4f().identity().ortho(-size, size, -size / 2f, size / 2f, Constants.Z_NEAR, Constants.Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        viewMatrix = Transformation.getViewMatrix(cameraHelper.rightOrthoCamera);

        renderAirports(airports, viewMatrix);
        renderGround(ground, viewMatrix, true);
        renderTrail(droneHelper, viewMatrix);
        renderPackages(packages, viewMatrix);
        renderDroneItems(droneHelper, viewMatrix);
        shaderProgram.unbind();
    }


    private void renderTrail(DroneHelper droneHelper, Matrix4f viewMatrix) {
        for (int i : droneHelper.droneIds.values()) {
            List<WorldObject> trailItems = droneHelper.getDroneTrail(i).getPathObjects();
            if (trailItems.isEmpty()) return;
            for (WorldObject gameItem : trailItems) {
                Matrix4f modelViewMatrix = Transformation.getModelViewMatrix(gameItem, viewMatrix);
                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                // Render the mesh for this game item
                gameItem.getMesh().render();
            }
        }
    }

    private void renderAirports(List<Airport> airports, Matrix4f viewMatrix) {
        for (Airport airp : airports) {
            for (WorldObject obj : airp.getObjects()) {
                if (obj == null || obj.getMesh() == null) continue;
                Matrix4f modelViewMatrix = Transformation.getModelViewMatrix(obj, viewMatrix);
                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                obj.getMesh().render();
            }
        }
    }

    private void renderGround(Ground ground, Matrix4f viewMatrix, boolean air) {
        if (ground == null || ground.getTiles().isEmpty()) return;

        if (air) {
            for (WorldObject tile : ground.getCombined()) {
                Matrix4f modelViewMatrix = Transformation.getModelViewMatrix(tile, viewMatrix);
                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                tile.getMesh().render();
            }
        } else {
            for (WorldObject tile : ground.getTiles()) {
                Matrix4f modelViewMatrix = Transformation.getModelViewMatrix(tile, viewMatrix);
                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                tile.getMesh().render();
            }
        }
    }

    private void renderPackages(Set<Package> packages, Matrix4f viewMatrix) {
        for (Package pack : packages) {
            if (pack == null) continue;
            Matrix4f modelViewMatrix = Transformation.getModelViewMatrix(pack.getCube(), viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            pack.getCube().getMesh().render();
        }
    }

    private void renderDroneItems(DroneHelper droneHelper, Matrix4f viewMatrix) {
        for (int i : droneHelper.droneIds.values()) {
            for (WorldObject droneItem : droneHelper.getDroneItems(i)) {
                droneItem.setScale(1);
                // Set model view matrix for this item
                Matrix4f modelViewMatrix = Transformation.getModelViewMatrix(droneItem, viewMatrix);
                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                // Render the mesh for this game item
                droneItem.getMesh().render();
                droneItem.setScale(1);
            }
        }
    }

    /**
     * Remove the shader program
     */
    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
