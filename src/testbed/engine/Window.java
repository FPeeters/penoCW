package testbed.engine;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import utils.Constants;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    /**
     * Title shown on the window
     */
    private final String title;

    /**
     * Width and length of the window
     */
    private int width;
    private int height;

    /**
     * Storage for the window
     */
    private long windowHandle;

    /**
     * Are we using vSync on the GPU or do we have to use it ourselves
     */
    private boolean vSync;

    /**
     * Constructor
     */
    public Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
    }

    /**
     * Mainly initialization for the window
     * Is the same for most LWJGL projects
     */
    public void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
        });

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });


        // Center our window
        glfwSetWindowPos(
                windowHandle,
                System.getProperty("os.name").equals("Linux") ? Constants.TESTBED_GUI_WIDTH + Constants.UBUNTU_SIDEBAR : Constants.TESTBED_GUI_WIDTH,
                System.getProperty("os.name").contains("Windows") ? 40 : 0
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        if (isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(windowHandle);

        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void restoreState() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
    }

    public void update() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }
}
