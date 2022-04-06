package io.github.kuroppoi.qtoolkit.event;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public abstract class TreeModelAdapter implements TreeModelListener {
    
    @Override
    public void treeNodesChanged(TreeModelEvent event) {}
    
    @Override
    public void treeNodesInserted(TreeModelEvent event) {}
    
    @Override
    public void treeNodesRemoved(TreeModelEvent event) {}
    
    @Override
    public void treeStructureChanged(TreeModelEvent event) {}
}
