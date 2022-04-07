package io.github.kuroppoi.qtoolkit.pack.mesh;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import io.github.kuroppoi.qtoolkit.pack.math.AxisAlignedBox;
import io.github.kuroppoi.qtoolkit.utils.ListUtils;

public class Mesh {
    
    private final List<SubMesh> subMeshes = new ArrayList<>();
    private String name;
    private MeshFile parent;
    private float animationLength;
    
    public Mesh(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
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
    
    protected void setParent(MeshFile parent) {
        this.parent = parent;
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    public MeshFile getParent() {
        return parent;
    }
    
    public void setAnimationLength(float animationLength) {
        this.animationLength = animationLength;
    }
    
    public float getAnimationLength() {
        return animationLength;
    }
    
    public float getAnimationSpeed() {
        return animationLength / (float)getKeyFrameCount();
    }
    
    public void addSubMesh(SubMesh subMesh) {
        addSubMesh(subMeshes.size(), subMesh);
    }
    
    public void addSubMesh(int index, SubMesh subMesh) {
        if(subMesh.hasParent()) {
            throw new IllegalArgumentException("SubMesh already has a parent");
        }
        
        ListUtils.add(subMeshes, index, subMesh);
        subMesh.setParent(this);
    }
    
    public void removeSubMesh(SubMesh subMesh) {
        if(subMesh != null && subMeshes.remove(subMesh)) {
            subMesh.setParent(null);
        }
    }
    
    public void removeSubMesh(int index) {
        removeSubMesh(getSubMesh(index));
    }
    
    public void removeSubMesh(String materialName) {
        removeSubMesh(getSubMesh(materialName));
    }
    
    public SubMesh getSubMesh(int index) {
        return ListUtils.get(subMeshes, index);
    }
    
    public SubMesh getSubMesh(String materialName) {
        return ListUtils.get(subMeshes, subMesh -> subMesh.getMaterialName().equals(materialName));
    }
    
    public boolean hasSubMesh(String materialName) {
        return getSubMesh(materialName) != null;
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
