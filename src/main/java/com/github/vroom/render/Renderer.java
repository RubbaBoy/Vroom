package com.github.vroom.render;

import com.github.vroom.render.camera.Camera;
import com.github.vroom.render.light.LightManager;
import com.github.vroom.render.mesh.Mesh;
import com.github.vroom.render.object.RenderObject;
import com.github.vroom.render.shader.ShaderProgram;
import com.github.vroom.render.transform.Transformation;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.List;

import static com.github.vroom.render.light.LightManager.MAX_POINT_LIGHTS;
import static com.github.vroom.render.light.LightManager.MAX_SPOT_LIGHTS;
import static com.github.vroom.utility.Utility.loadResource;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glViewport;

public final class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0F);

    private static final float Z_NEAR = 0.01F;

    private static final float Z_FAR = 1000F;

    private final Transformation transformation;

    private boolean initialized;

    private boolean wireframeOverride;

    private ShaderProgram shaderProgram;

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

        shaderProgram.createMaterialUniform("material");

        // Create lighting related uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        shaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        shaderProgram.createDirectionalLightUniform("directionalLight");

        if (wireframeOverride) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        initialized = true;
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, List<RenderObject> renderObjects, LightManager lightManager) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(),
                Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        lightManager.render(shaderProgram, viewMatrix);

        shaderProgram.setUniform("texture_sampler", 0);

        for (var renderObject : renderObjects) {
            var multiMesh = renderObject.getMultiMesh();

            for (Mesh mesh : multiMesh.getMeshes()) {
                if (!wireframeOverride && mesh.isWireframe()) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                }

                // Set model view matrix for this object
                Matrix4f modelViewMatrix = transformation.getModelViewMatrix(renderObject, viewMatrix);
                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

                // Set material for this object
                shaderProgram.setUniform("material", mesh.getMaterial());

                // Render the mesh for this render object
                mesh.render();

                if (!wireframeOverride && mesh.isWireframe()) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                }
            }
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    public boolean isWireframe() {
        return wireframeOverride;
    }

    public void setWireframe(boolean wireframeOverride) {
        this.wireframeOverride = wireframeOverride;

        if (initialized) {
            if (wireframeOverride) {
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            } else {
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            }
        }
    }
}
