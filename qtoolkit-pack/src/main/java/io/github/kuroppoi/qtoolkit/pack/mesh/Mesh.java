package io.github.kuroppoi.qtoolkit.pack.mesh;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import io.github.kuroppoi.qtoolkit.pack.math.AxisAlignedBox;
import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class Mesh {
    
    private String name;
    private float animationLength;
    private float animationSpeed;
    private List<SubMesh> subMeshes;
    
    public Mesh(String name) {
        this.name = name;
        this.subMeshes = new ArrayList<>();
    }
    
    public AxisAlignedBox calculateBounds() {
        AxisAlignedBox bounds = null;
        
        for(SubMesh subMesh : subMeshes) {
            for(Vector3f position : subMesh.getVertexData().getPositions()) {
                if(bounds == null) {
                    bounds = new AxisAlignedBox(position, position);
                } else {
                    bounds.grow(position);
                }
            }
        }
        
        return bounds;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setAnimationLength(float animationLength) {
        this.animationLength = animationLength;
    }
    
    public float getAnimationLength() {
        return animationLength;
    }
    
    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
    }
    
    public float getAnimationSpeed() {
        return animationSpeed;
    }
    
    public void addSubMesh(SubMesh subMesh) {
        subMeshes.add(subMesh);
    }
    
    public void removeSubMesh(SubMesh subMesh) {
        subMeshes.remove(subMesh);
    }
    
    public void removeSubMesh(int index) {
        ListUtils.remove(subMeshes, index);
    }
    
    public void removeSubMesh(String materialName) {
        ListUtils.remove(subMeshes, subMesh -> subMesh.getMaterialName().equals(materialName));
    }
    
    public SubMesh getSubMesh(int index) {
        return ListUtils.get(subMeshes, index);
    }
    
    public SubMesh getSubMesh(String materialName) {
        return ListUtils.get(subMeshes, subMesh -> subMesh.getMaterialName().equals(materialName));
    }
    
    public int getSubMeshCount() {
        return subMeshes.size();
    }
    
    public int getKeyFrameCount() {
        return subMeshes.isEmpty() ? 0 : subMeshes.get(0).getKeyFrameCount();
    }
    
    public List<SubMesh> getSubMeshes() {
        return subMeshes;
    }
}
