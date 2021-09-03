package io.github.kuroppoi.qtoolkit.pack.collision;

import java.util.HashSet;
import java.util.Set;

public class Collision {
    
    private String sceneName;
    private Set<Polygon> polygons;
    
    public Collision(String sceneName) {
        this.sceneName = sceneName;
        this.polygons = new HashSet<>();
    }
    
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }
    
    public String getSceneName() {
        return sceneName;
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
