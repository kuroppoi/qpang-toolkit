package io.github.kuroppoi.qtoolkit.pack.scene;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.utils.ListUtils;

public class MeshObject extends Transformable {
    
    private final List<String> meshes = new ArrayList<>();
    private final List<MeshObject> meshObjects = new ArrayList<>();
    private String name;
    
    public MeshObject(String name) {
        this.name = name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void addMesh(String mesh) {
        meshes.add(mesh);
    }
    
    public void removeMesh(String mesh) {
        meshes.remove(mesh);
    }
    
    public void removeMesh(int index) {
        ListUtils.remove(meshes, index);
    }
    
    public String getMesh(int index) {
        return ListUtils.get(meshes, index);
    }
    
    public int getMeshCount() {
        return meshes.size();
    }
    
    public List<String> getMeshes() {
        return meshes;
    }
    
    public void addMeshObject(MeshObject meshObject) {
        meshObjects.add(meshObject);
    }
    
    public void removeMeshObject(MeshObject meshObject) {
        meshObjects.remove(meshObject);
    }
    
    public void removeMeshObject(int index) {
        ListUtils.remove(meshObjects, index);
    }
    
    public void removeMeshObject(String name) {
        ListUtils.remove(meshObjects, meshObject -> name.equals(meshObject.getName()));
    }
    
    public MeshObject getMeshObject(int index) {
        return ListUtils.get(meshObjects, index);
    }
    
    public MeshObject getMeshObject(String name) {
        return ListUtils.get(meshObjects, meshObject -> name.equals(meshObject.getName()));
    }
    
    public int getMeshObjectCount() {
        return meshObjects.size();
    }
    
    public List<MeshObject> getMeshObjects() {
        return meshObjects;
    }
}
