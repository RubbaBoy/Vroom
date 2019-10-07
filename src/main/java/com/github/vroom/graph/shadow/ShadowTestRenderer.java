package com.github.vroom.graph.shadow;

import com.github.vroom.Window;
import com.github.vroom.graph.ShaderProgram;
import com.github.vroom.graph.mesh.Mesh;
import com.github.vroom.loaders.assimp.StaticMeshesLoader;
import com.github.vroom.utility.Utility;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class ShadowTestRenderer {

    private ShaderProgram testShaderProgram;

    private Mesh quadMesh;

    public void init(Window window) {
        setupTestShader();
    }

    private void setupTestShader() {
        testShaderProgram = new ShaderProgram();
        testShaderProgram.createVertexShader(Utility.loadResource("/shaders/test_vertex.vs"));
        testShaderProgram.createFragmentShader(Utility.loadResource("/shaders/test_fragment.fs"));
        testShaderProgram.link();

        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            testShaderProgram.createUniform("texture_sampler[" + i + "]");
        }

        quadMesh = StaticMeshesLoader.load("/models/quad.obj", "", true).getFirstMesh();
    }

    public void renderTest(ShadowBuffer shadowMap) {
        testShaderProgram.bind();

        testShaderProgram.setUniform("texture_sampler[0]", 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getIds()[0]);

        quadMesh.render();

        testShaderProgram.unbind();
    }

    public void cleanup() {
        if (testShaderProgram != null) {
            testShaderProgram.cleanup();
        }
    }
}
