package io.github.kuroppoi.qtoolkit.pack.mesh;

import java.util.Arrays;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjSplitting;
import de.javagl.obj.ObjUtils;
import de.javagl.obj.Objs;
import io.github.kuroppoi.qtoolkit.pack.scene.CoordType;
import io.github.kuroppoi.qtoolkit.pack.scene.MeshObject;
import io.github.kuroppoi.qtoolkit.pack.scene.Scene;
import io.github.kuroppoi.qtoolkit.pack.scene.SceneFile;

public class MeshConverter {
    
    public static Obj convertSceneFileToObj(SceneFile sceneFile, MeshFile meshFile) {
        return convertSceneFileToObj(sceneFile, meshFile, null);
    }
    
    public static Obj convertSceneFileToObj(SceneFile sceneFile, MeshFile meshFile, String mtlFileName) {
        Obj obj = Objs.create();
        
        if(mtlFileName != null) {
            obj.setMtlFileNames(Arrays.asList(mtlFileName));
        }
        
        for(Scene scene : sceneFile.getScenes()) {
            Matrix4f sceneTransformation = scene.getTransformation();
            
            for(MeshObject meshObject : scene.getMeshObjects()) {
                Matrix4f transformation = meshObject.getTransformation();
                sceneTransformation.mul(transformation, transformation);
                
                for(String meshName : meshObject.getMeshes()) {
                    obj.setActiveGroupNames(Arrays.asList(meshName));
                    Mesh mesh = meshFile.getMesh(meshName);
                    
                    if(mesh != null) {
                        if(scene.getCoordType() == CoordType.LOCAL) {
                            convertMeshToObj(mesh, transformation, obj);
                        } else {
                            convertMeshToObj(mesh, null, obj);
                        }
                    }
                }
            }
        }
                
        return obj;
    }
    
    public static Obj convertMeshToObj(Mesh mesh) {
        return convertMeshToObj(mesh, null);
    }
    
    public static Obj convertMeshToObj(Mesh mesh, Matrix4f transformation) {
        return convertMeshToObj(mesh, transformation, Objs.create());
    }
    
    private static Obj convertMeshToObj(Mesh mesh, Matrix4f transformation, Obj obj) {
        for(SubMesh subMesh : mesh.getSubMeshes()) {
            int indexOffset = obj.getNumVertices();
            VertexData vertexData = subMesh.getVertexData();
            obj.setActiveMaterialGroupName(subMesh.getMaterialName());
            
            for(Vector3f position : vertexData.getPositions()) {
                if(transformation != null) {
                    position = position.mulPosition(transformation, new Vector3f());
                }
                
                obj.addVertex(position.x, position.y, position.z);
            }
            
            for(Vector3f normal : vertexData.getNormals()) {
                obj.addNormal(normal.x, normal.y, normal.z);
            }
            
            for(Vector2f texCoord : vertexData.getTexCoords()) {
                obj.addTexCoord(texCoord.x, -texCoord.y);
            }
            
            for(int i = 0; i + 2 < subMesh.getIndexCount(); i += 2) {
                int x = subMesh.getIndex(i) + indexOffset;
                int y = subMesh.getIndex(i + 1) + indexOffset;
                int z = subMesh.getIndex(i + 2) + indexOffset;

                if(x != y && x != z && y != z) {
                    int[] v = vertexData.hasPositions() ? new int[] { x, y, z } : null;
                    int[] vn = vertexData.hasNormals() ? new int[] { x, y, z } : null;
                    int[] vt = vertexData.hasTexCoords() ? new int[] { x, y, z } : null;
                    obj.addFace(v, vn, vt);
                }
                
                if(i + 3 < subMesh.getIndexCount()) {
                    x = subMesh.getIndex(i + 1) + indexOffset;
                    y = subMesh.getIndex(i + 3) + indexOffset;
                    z = subMesh.getIndex(i + 2) + indexOffset;
                    
                    if(x != y && x != z && y != z) {
                        int[] v = vertexData.hasPositions() ? new int[] { x, y, z } : null;
                        int[] vn = vertexData.hasNormals() ? new int[] { x, y, z } : null;
                        int[] vt = vertexData.hasTexCoords() ? new int[] { x, y, z } : null;
                        obj.addFace(v, vn, vt);
                    }
                }
            }
        }
                
        return obj;
    }
    
    public static MeshFile convertObjToMeshFile(Obj obj) {
        MeshFile meshFile = new MeshFile();
        Map<String, Obj> groups = ObjSplitting.splitByGroups(obj);
        
        groups.forEach((name, obj2) -> {
            meshFile.addMesh(convertObjToMesh(obj2, name));
        });
        
        return meshFile;
    }
    
    public static Mesh convertObjToMesh(Obj obj, String name) {
        Mesh mesh = new Mesh(name);
        Map<String, Obj> groups = ObjSplitting.splitByMaterialGroups(ObjUtils.convertToRenderable(obj));
        
        // Obj parser limitation? This sometimes happens, sadly
        if(groups.isEmpty()) {
            groups.put(String.format("Material #%s", System.currentTimeMillis()), obj);
        }
        
        groups.forEach((materialName, obj2) -> {
            SubMesh subMesh = new SubMesh(materialName);
            mesh.addSubMesh(subMesh);
            VertexData vertexData = subMesh.getVertexData();
            float[] vertices = ObjData.getVerticesArray(obj2);
            float[] normals = ObjData.getNormalsArray(obj2);
            float[] texCoords = ObjData.getTexCoordsArray(obj2, 2);
            int[] indices = ObjData.getFaceVertexIndicesArray(obj2, 3);
            
            for(int i = 0; i < vertices.length / 3; i++) {
                vertexData.addPosition(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]);
            }
            
            for(int i = 0; i < normals.length / 3; i++) {
                vertexData.addNormal(normals[i * 3], normals[i * 3 + 1], normals[i * 3 + 2]);
            }
            
            for(int i = 0; i < texCoords.length / 2; i++) {
                // Vertically flip the texture coordinates
                vertexData.addTexCoord(texCoords[i * 2], -texCoords[i * 2 + 1]);
            }
            
            // TODO This works, but it is not ideal.
            for(int i = 0; i < indices.length; i += 3) {
                int bOffset = i % 2 == 0 ? 2 : 1;
                int cOffset = bOffset == 2 ? 1 : 2;
                subMesh.addIndex(indices[i]);
                subMesh.addIndex(indices[i]);
                subMesh.addIndex(indices[i + bOffset]);
                subMesh.addIndex(indices[i + cOffset]);
                subMesh.addIndex(indices[i + cOffset]);
            }
        });
        
        return mesh;
    }
}
