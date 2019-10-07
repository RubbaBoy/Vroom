package com.github.vroom.loaders.assimp;

import com.github.vroom.graph.Material;
import com.github.vroom.graph.Texture;
import com.github.vroom.graph.collision.Collision;
import com.github.vroom.graph.mesh.Mesh;
import com.github.vroom.graph.mesh.MultiMesh;
import com.github.vroom.utility.Utility;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.aiGetErrorString;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;

public class StaticMeshesLoader {

    public static MultiMesh load(String resourcePath, String texturesDir, boolean createMesh) {
        return load(resourcePath, texturesDir, Mesh.GEN_COLLISIONS, createMesh);
    }

    public static MultiMesh load(String resourcePath, String texturesDir, Function<Mesh, Collision[]> collisionComputation, boolean createMesh) {
        return load(resourcePath, texturesDir,
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
                        | aiProcess_FixInfacingNormals, collisionComputation, createMesh);
    }

    public static MultiMesh load(String resourcePath, String texturesDir, int flags, Function<Mesh, Collision[]> collisionComputation, boolean createMesh) {
        AIScene aiScene = aiImportFile(resourcePath, flags);

        if (aiScene == null) {
            throw new RuntimeException("Error loading model [resourcePath: "  + resourcePath + ", texturesDir:" + texturesDir + "] " + aiGetErrorString());
        }

        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial, materials, texturesDir);
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshes = new Mesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh, materials, collisionComputation);
            meshes[i] = mesh;
        }

        var multiMesh = new MultiMesh(meshes);

        if (createMesh) {
            multiMesh.createMeshes();
        }

        return multiMesh;
    }

    protected static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }

    protected static void processMaterial(AIMaterial aiMaterial, List<Material> materials,
            String texturesDir) {
        AIColor4D color = AIColor4D.create();

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null,
                null, null, null, null, null);
        String textPath = path.dataString();
        Texture texture = null;
        if (textPath.length() > 0) {
            TextureCache textCache = TextureCache.getInstance();
            String textureFile = "";
			if ( texturesDir != null && texturesDir.length() > 0 ) {
				textureFile += texturesDir + "/";
			}
			textureFile += textPath;
            textureFile = textureFile.replace("//", "/");
            texture = textCache.getTexture(textureFile);
        }

        Vector4f diffuse = Material.DEFAULT_COLOR;
        int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0,
                color);
        if (result == 0) {
            diffuse = new Vector4f(color.r(), color.g(), color.b(), color.a());
        }

        Vector4f specular = Material.DEFAULT_COLOR;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0,
                color);
        if (result == 0) {
            specular = new Vector4f(color.r(), color.g(), color.b(), color.a());
        }

        Material material = new Material(diffuse, specular, 1.0f);
        material.setTexture(texture);
        materials.add(material);
    }

    private static Mesh processMesh(AIMesh aiMesh, List<Material> materials, Function<Mesh, Collision[]> collisionComputation) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);

        Mesh mesh = new Mesh(Utility.listToArray(vertices), Utility.listToArray(textures),
                Utility.listToArray(normals), Utility.listIntToArray(indices), false, collisionComputation);
        Material material;
        int materialIdx = aiMesh.mMaterialIndex();
        if (materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
            material = new Material();
        }
        mesh.setMaterial(material);

        return mesh;
    }

    protected static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    protected static void processTextCoords(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }

    protected static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }
}
