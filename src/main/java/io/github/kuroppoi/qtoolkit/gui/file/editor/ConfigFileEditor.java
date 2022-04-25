package io.github.kuroppoi.qtoolkit.gui.file.editor;

import io.github.kuroppoi.qtoolkit.conf.ConfCryptography;
import io.github.kuroppoi.qtoolkit.file.FileNode;

public class ConfigFileEditor extends TextFileEditor {
    
    public ConfigFileEditor(FileNode file, Runnable changeListener) {
        super(file, changeListener);
    }
    
    @Override
    public void save() {
        file.setBytes(ConfCryptography.encryptString(textArea.getText()).getBytes());
        hasUnsavedChanges = false;
    }
    
    @Override
    protected String loadText() {
        return ConfCryptography.decryptLinesAsString(file.getLines());
    }
}
