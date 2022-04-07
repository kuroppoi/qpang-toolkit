package io.github.kuroppoi.qtoolkit.gui.tree;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

public class CollisionTreeCellRenderer extends DefaultTreeCellRenderer {
    
    private static final long serialVersionUID = 4271144050903932490L;
    private static final Icon collisionIcon = UIManager.getIcon("QToolkit.collisionIcon");
    
    @Override
    public Icon getOpenIcon() {
        return collisionIcon;
    }
    
    @Override
    public Icon getClosedIcon() {
        return collisionIcon;
    }
    
    @Override
    public Icon getLeafIcon() {
        return collisionIcon;
    }
}
