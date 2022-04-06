package io.github.kuroppoi.qtoolkit.tree;

import javax.swing.tree.TreePath;

import io.github.kuroppoi.qtoolkit.RenameFailReason;
import io.github.kuroppoi.qtoolkit.pack.collision.Collision;
import io.github.kuroppoi.qtoolkit.pack.collision.CollisionFile;

public class CollisionTreeModel extends AbstractTreeModel {
    
    private final CollisionFile root;
    
    public CollisionTreeModel(CollisionFile root) {
        this.root = root;
    }
    
    @Override
    public Object getRoot() {
        return root;
    }
    
    @Override
    public Object getChild(Object parent, int index) {
        if(parent instanceof CollisionFile) {
            return ((CollisionFile)parent).getCollision(index);
        }
        
        return null;
    }
    
    @Override
    public int getChildCount(Object parent) {
        if(parent instanceof CollisionFile) {
            return ((CollisionFile)parent).getCollisionCount();
        }
        
        return 0;
    }
    
    @Override
    public boolean isLeaf(Object node) {
        return node instanceof Collision;
    }
    
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        Object component = path.getLastPathComponent();
        String name = newValue.toString().trim();
        
        if(!name.matches("^[a-zA-Z0-9._]{1,64}$")) {
            postRenameFailed(RenameFailReason.INVALID_NAME);
            return;
        }
        
        if(component instanceof Collision) {
            Collision collision = (Collision)component;
            
            if(!collision.getSceneName().equals(name)) {
                if(collision.hasParent() && collision.getParent().hasCollision(name)) {
                    postRenameFailed(RenameFailReason.DUPLICATE_NAME);
                    return;
                }
                
                collision.setSceneName(name);
                nodeChanged(collision);
            }
        }
    }
    
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        int childCount = getChildCount(parent);
        
        for(int i = 0; i < childCount; i++) {
            if(getChild(parent, i) == child) {
                return i;
            }
        }
        
        return -1;
    }
    
    @Override
    public void add(Object parent, Object node) {
        if(parent == root && node instanceof Collision) {
            add((Collision)node);
        }
    }
    
    @Override
    public void insert(Object parent, int index, Object node) {
        if(parent == root && node instanceof Collision) {
            insert(index, (Collision)node);
        }
    }
    
    @Override
    public void remove(Object node) {
        if(node instanceof Collision) {
            remove((Collision)node);
        }
    }
    
    @Override
    public Object getParent(Object node) {
        if(node instanceof Collision) {
            return ((Collision)node).getParent();
        }
        
        return null;
    }
    
    private String findAvailableName(String name) {
        int tries = 0;
        String current = name;
        
        while(root.hasCollision(current)) {
            current = String.format("%s_%s", name, ++tries);
        }
        
        return current;
    }
    
    public void add(Collision collision) {
        insert(root.getCollisionCount(), collision);
    }
    
    public void insert(int index, Collision collision) {
        if(collision.hasParent()) {
            collision.getParent().removeCollision(collision);
        }
        
        String name = collision.getSceneName();
        
        if(root.hasCollision(name)) {
            collision.setSceneName(findAvailableName(name));
        }
        
        root.addCollision(index, collision);
        nodeInserted(root, index);
    }
    
    public void remove(Collision collision) {
        if(collision.hasParent()) {
            int index = getIndexOfChild(root, collision);
            root.removeCollision(collision);
            nodeRemoved(root, index, collision);
        }
    }
}
