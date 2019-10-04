package com.github.vroom.render;

import com.github.vroom.render.camera.Camera;
import com.github.vroom.render.light.LightManager;
import com.github.vroom.render.light.directional.DirectionalLight;
import com.github.vroom.render.light.directional.OrthoCoords;
import com.github.vroom.render.mesh.Mesh;
import com.github.vroom.render.object.RenderObject;
import com.github.vroom.render.shader.ShaderProgram;
import com.github.vroom.render.transform.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public final class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0F);

    private static final float Z_NEAR = 0.01F;

    private static final float Z_FAR = 1000F;

    private final Transformation transformation;

    private boolean initialized;

    private boolean wireframeOverride;

    private ShadowMap shadowMap;

    private ShaderProgram sceneShaderProgram;

    private ShaderProgram depthShaderProgram;

    public Renderer() {
        this.transformation = new Transformation();
    }

    public void init() throws IOException {
        shadowMap = new ShadowMap();

        setupDepthShader();
        setupSceneShader();

        if (wireframeOverride) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        initialized = true;
    }

    private void setupDepthShader() throws IOException {
        depthShaderProgram = new ShaderProgram();
        depthShaderProgram.createVertexShader(loadResource("/shaders/depth_vertex.vs"));
        depthShaderProgram.createFragmentShader(loadResource("/shaders/depth_fragment.fs"));
        depthShaderProgram.link();

        depthShaderProgram.createUniform("orthoProjectionMatrix");
        depthShaderProgram.createUniform("modelLightViewMatrix");
    }

    private void setupSceneShader() throws IOException {
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(loadResource("/shaders/vertex.vs"));
        sceneShaderProgram.createFragmentShader(loadResource("/shaders/fragment.fs"));
        sceneShaderProgram.link();

        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("modelViewMatrix");
        sceneShaderProgram.createUniform("texture_sampler");
        sceneShaderProgram.createUniform("normalMap");

        sceneShaderProgram.createMaterialUniform("material");

        // Create lighting related uniforms
        sceneShaderProgram.createUniform("specularPower");
        sceneShaderProgram.createUniform("ambientLight");
        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        sceneShaderProgram.createDirectionalLightUniform("directionalLight");

        // Create uniforms for shadow mapping
        sceneShaderProgram.createUniform("shadowMap");
        sceneShaderProgram.createUniform("orthoProjectionMatrix");
        sceneShaderProgram.createUniform("modelLightViewMatrix");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, List<RenderObject> renderObjects, LightManager lightManager) {
        clear();

        // Render depth map before view ports has been set up
        renderDepthMap(window, camera, lightManager, renderObjects);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        // Update projection Matrix
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        renderScene(window, camera, lightManager, renderObjects);
    }

    private void renderDepthMap(Window window, Camera camera, LightManager lightManager,
                                List<RenderObject> renderObjects) {
        // Setup view port to match the texture size
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        DirectionalLight light = lightManager.getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;

        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection)
                .mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right,
                orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);

        for (RenderObject object : renderObjects) {
            for (Mesh mesh : object.getMultiMesh().getMeshes()) {
                mesh.renderList(renderObjects, renderObject -> {
                    Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(renderObject, lightViewMatrix);
                    depthShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
                });
            }
        }

        // Unbind
        depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void renderScene(Window window, Camera camera, LightManager lightManager,
                             List<RenderObject> renderObjects) {
        sceneShaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        sceneShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        // Update view Matrix
        Matrix4f viewMatrix = transformation.updateViewMatrix(camera);

        lightManager.render(sceneShaderProgram, viewMatrix);

        sceneShaderProgram.setUniform("texture_sampler", 0);
        sceneShaderProgram.setUniform("normalMap", 1);
        sceneShaderProgram.setUniform("shadowMap", 2);

        for (var renderObject : renderObjects) {
            var multiMesh = renderObject.getMultiMesh();

            for (Mesh mesh : multiMesh.getMeshes()) {
                if (!wireframeOverride && mesh.isWireframe()) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                }

                // Set material for this object
                sceneShaderProgram.setUniform("material", mesh.getMaterial());

                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());

                // Set model view matrix for this object
                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(renderObject, viewMatrix);
                sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

                Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(renderObject, lightViewMatrix);
                sceneShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);

                // Render the mesh for this render object
                mesh.render();

                if (!wireframeOverride && mesh.isWireframe()) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                }
            }
        }

        sceneShaderProgram.unbind();
    }

    public void cleanup() {
        sceneShaderProgram.cleanup();
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
