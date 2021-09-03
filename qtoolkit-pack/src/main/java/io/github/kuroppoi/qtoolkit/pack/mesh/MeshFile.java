package io.github.kuroppoi.qtoolkit.pack.mesh;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class MeshFile {
    
    private List<Mesh> meshes;
    
    public MeshFile() {
        this.meshes = new ArrayList<>();
    }
    
    public void addMesh(Mesh mesh) {
        meshes.add(mesh);
    }
    
    public void removeMesh(Mesh mesh) {
        meshes.remove(mesh);
    }
    
    public void removeMesh(int index) {
        ListUtils.remove(meshes, index);
    }
    
    public void removeMesh(String name) {
        ListUtils.remove(meshes, mesh -> mesh.getName().equals(name));
    }
    
    public Mesh getMesh(int index) {
        return ListUtils.get(meshes, index);
    }
    
    public Mesh getMesh(String name) {
        return ListUtils.get(meshes, mesh -> mesh.getName().equals(name));
    }
    
    public int getMeshCount() {
        return meshes.size();
    }
    
    public List<Mesh> getMeshes() {
        return meshes;
    }
}
