package io.github.kuroppoi.qtoolkit.file.editor;

import javax.swing.JComponent;
import javax.swing.undo.UndoManager;

public abstract class FileEditor {
    
    protected UndoManager undoManager = new UndoManager();
    protected boolean hasUnsavedChanges;
    
    public abstract void save();
    public abstract JComponent getComponent();
    
    public void undo() {
        if(hasUndoableAction()) {
            undoManager.undo();
        }
    }
    
    public void redo() {
        if(hasRedoableAction()) {
            undoManager.redo();
        }
    }
    
    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }
    
    public boolean hasUndoableAction() {
        return undoManager.canUndo();
    }
    
    public boolean hasRedoableAction() {
        return undoManager.canRedo();
    }
}
