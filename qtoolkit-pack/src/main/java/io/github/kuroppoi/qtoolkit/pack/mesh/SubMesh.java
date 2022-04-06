package io.github.kuroppoi.qtoolkit.pack.mesh;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class SubMesh {
    
    private final List<VertexData> keyFrames = new ArrayList<>();
    private final List<Integer> indices = new ArrayList<>();
    private VertexData vertexData = new VertexData();
    private String materialName;
    private Mesh parent;
    
    public SubMesh(String materialName) {
        this.materialName = materialName;
    }
    
    @Override
    public String toString() {
        return materialName;
    }
    
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
    
    public String getMaterialName() {
        return materialName;
    }
    
    protected void setParent(Mesh parent) {
        this.parent = parent;
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    public Mesh getParent() {
        return parent;
    }
    
    public void setVertexData(VertexData vertexData) {
        this.vertexData = vertexData;
    }
    
    public VertexData getVertexData() {
        return vertexData;
    }
    
    public void addKeyFrame(VertexData keyFrame) {
        keyFrames.add(keyFrame);
    }
    
    public int getKeyFrameCount() {
        return keyFrames.size();
    }
    
    public List<VertexData> getKeyFrames() {
        return keyFrames;
    }
    
    public void addIndex(int index) {
        indices.add(index);
    }
    
    public void removeIndex(int index) {
        indices.remove(index);
    }
    
    public int getIndex(int index) {
        return ListUtils.get(indices, index, -1);
    }
    
    public int getIndexCount() {
        return indices.size();
    }
    
    public List<Integer> getIndices() {
        return indices;
    }
}
