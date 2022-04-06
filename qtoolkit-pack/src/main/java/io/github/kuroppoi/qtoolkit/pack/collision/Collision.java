package io.github.kuroppoi.qtoolkit.pack.collision;

import java.util.HashSet;
import java.util.Set;

public class Collision {
    
    private final Set<Polygon> polygons = new HashSet<>();
    private String sceneName;
    private CollisionFile parent;
    
    public Collision(String sceneName) {
        this.sceneName = sceneName;
    }
    
    @Override
    public String toString() {
        return sceneName;
    }
    
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }
    
    public String getSceneName() {
        return sceneName;
    }
    
    public void setParent(CollisionFile parent) {
        this.parent = parent;
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    public CollisionFile getParent() {
        return parent;
    }
    
    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }
    
    public void removePolygon(Polygon polygon) {
        polygons.remove(polygon);
    }
    
    public int getPolygonCount() {
        return polygons.size();
    }
    
    public Set<Polygon> getPolygons() {
        return polygons;
    }
}
