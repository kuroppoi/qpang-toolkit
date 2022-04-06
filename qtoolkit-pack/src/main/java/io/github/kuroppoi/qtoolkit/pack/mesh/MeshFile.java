package io.github.kuroppoi.qtoolkit.pack.mesh;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class MeshFile {
    
    private final List<Mesh> meshes = new ArrayList<>();
    
    public void addMesh(Mesh mesh) {
        addMesh(meshes.size(), mesh);
    }
    
    public void addMesh(int index, Mesh mesh) {
        if(mesh.hasParent()) {
            throw new IllegalArgumentException("Mesh already has a parent");
        }
        
        ListUtils.add(meshes, index, mesh);
        mesh.setParent(this);
    }
    
    public void removeMesh(Mesh mesh) {
        if(mesh != null && meshes.remove(mesh)) {
            mesh.setParent(null);
        }
    }
    
    public void removeMesh(int index) {
        removeMesh(getMesh(index));
    }
    
    public void removeMesh(String name) {
        removeMesh(getMesh(name));
    }
    
    public Mesh getMesh(int index) {
        return ListUtils.get(meshes, index);
    }
    
    public Mesh getMesh(String name) {
        return ListUtils.get(meshes, mesh -> mesh.getName().equals(name));
    }
    
    public boolean hasMesh(String name) {
        return getMesh(name) != null;
    }
    
    public int getMeshCount() {
        return meshes.size();
    }
    
    public List<Mesh> getMeshes() {
        return meshes;
    }
}
