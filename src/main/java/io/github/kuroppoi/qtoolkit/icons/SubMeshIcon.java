package io.github.kuroppoi.qtoolkit.icons;

import java.awt.Component;
import java.awt.Graphics2D;

import com.formdev.flatlaf.ui.FlatUIUtils;

public class SubMeshIcon extends AbstractIcon {
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        g2d.drawOval(2, 2, 12, 12);
        g2d.fill(FlatUIUtils.createPath(8.5, 9, 8, 1.5, 1.5, 8));
        g2d.fill(FlatUIUtils.createPath(8.5, 9, 16, 8.5, 8.5, 16));
    }
}
