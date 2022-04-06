package io.github.kuroppoi.qtoolkit.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import io.github.kuroppoi.qtoolkit.RenameFailReason;

public abstract class AbstractTreeModel implements TreeModel {
    
    protected final List<TreeModelListener> listeners = new ArrayList<>();
    protected Consumer<RenameFailReason> renameFailListener;
    
    public abstract void add(Object parent, Object node);
    public abstract void insert(Object parent, int index, Object node);
    public abstract void remove(Object node);
    public abstract Object getParent(Object node);
    
    @Override
    public void addTreeModelListener(TreeModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener) {
        listeners.remove(listener);
    }
    
    public void setRenameFailListener(Consumer<RenameFailReason> renameFailListener) {
        this.renameFailListener = renameFailListener;
    }
    
    public void add(Object node) {
        add(getRoot(), node);
    }
    
    public void insert(int index, Object node) {
        insert(getRoot(), index, node);
    }
    
    public void nodeInserted(Object parent, int childIndex) {
        nodesInserted(parent, new int[] {childIndex});
    }
    
    public void nodesInserted(Object parent, int[] childIndices) {
        int childCount = childIndices.length;
        
        if(childCount > 0) {
            Object[] children = new Object[childCount];
            
            for(int i = 0; i < childCount; i++) {
                children[i] = getChild(parent, childIndices[i]);
            }
            
            postTreeNodesInserted(getPathToRoot(parent), childIndices, children);
        }
    }
    
    public void nodeRemoved(Object parent, int childIndex, Object child) {
        nodesRemoved(parent, new int[] {childIndex}, new Object[] {child});
    }
    
    public void nodesRemoved(Object parent, int[] childIndices, Object[] children) {
        postTreeNodesRemoved(getPathToRoot(parent), childIndices, children);
    }
    
    public void nodeChanged(Object node) {
        Object parent = getParent(node);
        
        if(parent != null) {
            int index = getIndexOfChild(parent, node);
            
            if(index != -1) {
                nodesChanged(parent, new int[] {index});
            }
        } else if(node == getRoot()) {
            postTreeNodesChanged(getPathToRoot(node), null, null);
        }
    }
    
    public void nodesChanged(Object parent, int[] childIndices) {
        int childCount = childIndices.length;
        
        if(childCount > 0) {
            Object[] children = new Object[childCount];
            
            for(int i = 0; i < childCount; i++) {
                children[i] = getChild(parent, childIndices[i]);
            }
            
            postTreeNodesChanged(getPathToRoot(parent), childIndices, children);
        }
    }
    
    protected void postTreeNodesInserted(Object[] path, int[] childIndices, Object[] children) {
        TreeModelEvent event = new TreeModelEvent(this, path, childIndices, children);
        
        for(TreeModelListener listener : listeners) {
            listener.treeNodesInserted(event);
        }
    }
    
    protected void postTreeNodesRemoved(Object[] path, int[] childIndices, Object[] children) {
        TreeModelEvent event = new TreeModelEvent(this, path, childIndices, children);
        
        for(TreeModelListener listener : listeners) {
            listener.treeNodesRemoved(event);
        }
    }
    
    protected void postTreeNodesChanged(Object[] path, int[] childIndices, Object[] children) {
        TreeModelEvent event = new TreeModelEvent(this, path, childIndices, children);
        
        for(TreeModelListener listener : listeners) {
            listener.treeNodesChanged(event);
        }
    }
    
    protected void postRenameFailed(RenameFailReason reason) {
        if(renameFailListener != null) {
            renameFailListener.accept(reason);
        }
    }
    
    public TreePath getPath(Object node) {
        return new TreePath(getPathToRoot(node));
    }
    
    protected Object[] getPathToRoot(Object node) {
        return getPathToRoot(node, 0);
    }
    
    protected Object[] getPathToRoot(Object node, int depth) {
        if(node == null) {
            if(depth == 0) {
                return null;
            }
            
            return new Object[depth];
        }
        
        Object[] path = getPathToRoot(getParent(node), depth + 1);
        path[path.length - depth - 1] = node;
        return path;
    }
}
