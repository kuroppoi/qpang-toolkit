package io.github.kuroppoi.qtoolkit.pack.collision;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class CollisionFile {
    
    private final List<Collision> collisions;
    
    public CollisionFile() {
        this.collisions = new ArrayList<>();
    }
    
    public void addCollision(Collision collision) {
        collisions.add(collision);
    }
    
    public void removeCollision(Collision collision) {
        collisions.remove(collision);
    }
    
    public void removeCollision(int index) {
        ListUtils.remove(collisions, index);
    }
    
    public void removeCollision(String sceneName) {
        ListUtils.remove(collisions, collision -> sceneName.equals(collision.getSceneName()));
    }
    
    public Collision getCollision(int index) {
        return ListUtils.get(collisions, index);
    }
    
    public Collision getCollision(String sceneName) {
        return ListUtils.get(collisions, collision -> sceneName.equals(collision.getSceneName()));
    }
    
    public int getCollisionCount() {
        return collisions.size();
    }
    
    public List<Collision> getCollisions() {
        return collisions;
    }
}
