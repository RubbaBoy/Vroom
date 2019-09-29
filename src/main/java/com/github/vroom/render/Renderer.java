package com.github.vroom.render;

import com.github.vroom.render.camera.Camera;
import com.github.vroom.render.mesh.Mesh;
import com.github.vroom.render.object.RenderObject;
import com.github.vroom.render.shader.ShaderProgram;
import com.github.vroom.render.transform.Transformation;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.List;

import static com.github.vroom.utility.Utility.loadResource;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

public final class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0F);

    private static final float Z_NEAR = 0.01F;

    private static final float Z_FAR = 1000F;

    private ShaderProgram shaderProgram;

    private final Transformation transformation;

    public Renderer() {
        this.transformation = new Transformation();
    }

    public void init() throws IOException {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(loadResource("/shaders/vertex.vs"));
        shaderProgram.createFragmentShader(loadResource("/shaders/fragment.fs"));
        shaderProgram.link();

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");

        // Create uniform for default color and the flag that controls it
        shaderProgram.createUniform("color");
        shaderProgram.createUniform("useColor");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, List<RenderObject> renderObjects) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform("texture_sampler", 0);

        for (var renderObject : renderObjects) {
            Mesh mesh = renderObject.getMesh();

            // Set model view matrix for this object
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(renderObject, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

            // Render the mesh for this render object
            shaderProgram.setUniform("color", mesh.getColor());
            shaderProgram.setUniform("useColor", mesh.getTexture() == null ? 1 : 0);
            mesh.render();
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }
}
