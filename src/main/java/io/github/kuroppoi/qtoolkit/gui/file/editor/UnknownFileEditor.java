package io.github.kuroppoi.qtoolkit.gui.file.editor;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class UnknownFileEditor extends FileEditor {
    
    private final JLabel label = new JLabel("This file cannot be viewed.", JLabel.CENTER);
    
    @Override
    public void save() {}
    
    @Override
    public JComponent getComponent() {
        return label;
    }
}
