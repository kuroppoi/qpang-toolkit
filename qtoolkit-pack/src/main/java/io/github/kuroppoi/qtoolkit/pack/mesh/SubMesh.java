package io.github.kuroppoi.qtoolkit.pack.mesh;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class SubMesh {
    
    private String materialName;
    private VertexData vertexData;
    private List<VertexData> keyFrames;
    private List<Integer> indices;
    
    public SubMesh(String materialName) {
        this.materialName = materialName;
        this.vertexData = new VertexData();
        this.keyFrames = new ArrayList<>();
        this.indices = new ArrayList<>();
    }
    
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
    
    public String getMaterialName() {
        return materialName;
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
