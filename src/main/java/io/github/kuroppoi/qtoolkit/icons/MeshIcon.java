package io.github.kuroppoi.qtoolkit.icons;

import java.awt.Component;
import java.awt.Graphics2D;

import com.formdev.flatlaf.ui.FlatUIUtils;

public class MeshIcon extends AbstractIcon {
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        g2d.draw(FlatUIUtils.createPath(4, 3, 14, 7, 6, 14));
        g2d.fillOval(3, 2, 3, 3);
        g2d.fillOval(13, 6, 3, 3);
        g2d.fillOval(5, 13, 3, 3);
    }
}
