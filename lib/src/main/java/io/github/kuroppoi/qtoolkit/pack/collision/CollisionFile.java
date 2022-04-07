package io.github.kuroppoi.qtoolkit.pack.collision;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.utils.ListUtils;

public class CollisionFile {
    
    private final List<Collision> collisions = new ArrayList<>();
    
    public void addCollision(Collision collision) {
        addCollision(collisions.size(), collision);
    }
    
    public void addCollision(int index, Collision collision) {
        if(collision.hasParent()) {
            throw new IllegalArgumentException("Collision already has a parent");
        }
        
        ListUtils.add(collisions, index, collision);
        collision.setParent(this);
    }
    
    public void removeCollision(Collision collision) {
        if(collision != null && collisions.remove(collision)) {
            collision.setParent(null);
        }
    }
    
    public void removeCollision(int index) {
        removeCollision(getCollision(index));
    }
    
    public void removeCollision(String sceneName) {
        removeCollision(getCollision(sceneName));
    }
    
    public Collision getCollision(int index) {
        return ListUtils.get(collisions, index);
    }
    
    public Collision getCollision(String sceneName) {
        return ListUtils.get(collisions, collision -> sceneName.equals(collision.getSceneName()));
    }
    
    public boolean hasCollision(String sceneName) {
        return getCollision(sceneName) != null;
    }
    
    public int getCollisionCount() {
        return collisions.size();
    }
    
    public List<Collision> getCollisions() {
        return collisions;
    }
}
