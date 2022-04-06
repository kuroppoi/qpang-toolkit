package io.github.kuroppoi.qtoolkit.icons;

import java.awt.Component;
import java.awt.Graphics2D;

import com.formdev.flatlaf.ui.FlatUIUtils;

public class CollisionIcon extends AbstractIcon {
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        g2d.drawOval(3, 3, 4, 4);
        g2d.draw(FlatUIUtils.createPath(false, 3, 14, 6, 10, 9, 12, 12, 6, 15, 8));
    }
}