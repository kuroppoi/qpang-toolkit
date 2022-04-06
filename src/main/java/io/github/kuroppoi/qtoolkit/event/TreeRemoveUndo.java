package io.github.kuroppoi.qtoolkit.event;

import javax.swing.undo.AbstractUndoableEdit;

import io.github.kuroppoi.qtoolkit.tree.AbstractTreeModel;

public class TreeRemoveUndo extends AbstractUndoableEdit {
    
    private static final long serialVersionUID = 8486561577199047464L;
    private final AbstractTreeModel treeModel;
    private final Object parent;
    private final Object node;
    private final int index;
    
    public TreeRemoveUndo(AbstractTreeModel treeModel, Object parent, Object node, int index) {
        this.treeModel = treeModel;
        this.parent = parent;
        this.node = node;
        this.index = index;
    }
    
    @Override
    public void undo() {
        super.undo();
        treeModel.insert(parent, index, node);
    }
    
    @Override
    public void redo() {
        super.redo();
        treeModel.remove(node);
    }
}
