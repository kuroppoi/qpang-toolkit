package io.github.kuroppoi.qtoolkit.gui.tree;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MeshTreeCellRenderer extends DefaultTreeCellRenderer {
    
    private static final long serialVersionUID = 6912247384696729439L;
    private static final Icon meshIcon = UIManager.getIcon("QToolkit.meshIcon");
    private static final Icon subMeshIcon = UIManager.getIcon("QToolkit.subMeshIcon");
    
    @Override
    public Icon getOpenIcon() {
        return meshIcon;
    }
    
    @Override
    public Icon getClosedIcon() {
        return meshIcon;
    }
    
    @Override
    public Icon getLeafIcon() {
        return subMeshIcon;
    }
}
