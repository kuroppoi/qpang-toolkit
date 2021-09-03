package io.github.kuroppoi.qtoolkit.pack.scene;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class MeshObject extends Transformable {
    
    private String name;
    private final List<String> meshes;
    
    public MeshObject(String name) {
        this.name = name;
        this.meshes = new ArrayList<>();
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
    
    public List<String> getMeshes() {
        return meshes;
    }
}
