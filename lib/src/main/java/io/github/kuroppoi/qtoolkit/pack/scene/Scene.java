package io.github.kuroppoi.qtoolkit.pack.scene;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.utils.ListUtils;

public class Scene extends Transformable {
    
    private final List<MeshObject> meshObjects = new ArrayList<>();
    private String name;
    private CoordType coordType;
    
    public Scene(String name) {
        this(name, CoordType.GLOBAL);
    }
        
    public Scene(String name, CoordType coordType) {
        this.name = name;
        this.coordType = coordType;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setCoordType(CoordType coordType) {
        this.coordType = coordType;
    }
    
    public CoordType getCoordType() {
        return coordType;
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
