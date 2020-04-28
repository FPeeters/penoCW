package testbed.graphics;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int programId;

    private int vertexShaderId;

    private int fragmentShaderId;

    private final Map<String, Integer> uniforms;

    /**
     * Create an openGL shader program
     *
     * @throws Exception If something goes wrong with the shader creation
     */
    public ShaderProgram() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        uniforms = new HashMap<>();
    }

    /**
     * Add Uniforms to the vertexShader
     *
     * @param uniformName Name param added to the shader
     * @throws Exception If we can't find the name of the uniform
     */
    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    /**
     * Set the uniforms of the vertex shaders
     *
     * @param uniformName Name param added to the shader
     * @param value       The value to be set to the uniform
     */
    public void setUniform(String uniformName, Matrix4f value) {
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    /**
     * Create the vertexShader using the a vertex file
     *
     * @param shaderCode What can be found in the shader file (mainly vertex.fs)
     * @throws Exception If something goes wrong
     */
    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    /**
     * Create the fragmentShader using a shader file
     *
     * @param shaderCode The content of the fragmentShader file
     * @throws Exception If something goes wrong
     */
    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    /**
     * Base code for any shader creation
     *
     * @param shaderCode The code to be passed to the shader
     * @param shaderType What kind of shader (GL_FRAGMENT_SHADER, GL_VERTEX_SHADER, ...)
     * @return The ID of the shader
     * @throws Exception If something goes wrong
     */
    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        //The shader is added to the program
        glAttachShader(programId, shaderId);

        return shaderId;
    }

    /**
     * The attached shaders can be linked to the program and detached
     *
     * @throws Exception If something goes wrong
     */
    public void link() throws Exception {
        //attached shaders will be made in to executables
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        //executables have already been made so we can detach
        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        //executables have already been made so we can detach
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        //check if the program can run, will supply additional information about the program
        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    /**
     * Installs the program object specified by programId as part of current rendering state (-docs)
     */
    public void bind() {
        glUseProgram(programId);
    }

    /**
     * Stop using a certain program and use the dummy program with id 0
     */
    public void unbind() {
        glUseProgram(0);
    }

    /**
     * Stop using the program and delete the program that was in place
     */
    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
