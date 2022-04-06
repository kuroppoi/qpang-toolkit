package io.github.kuroppoi.qtoolkit.icons;

import java.awt.Component;
import java.awt.Graphics2D;

import com.formdev.flatlaf.ui.FlatUIUtils;

public class CurvedArrowIcon extends AbstractIcon {
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        super.paintIcon(component, g2d);
        
        if(component.getComponentOrientation().isLeftToRight()) {
            g2d.translate(width, 0);
            g2d.scale(-1, 1);
        }
        
        // Obligatory stupidity.
        g2d.drawArc(2, 2, 12, 11, -90, 230);
        g2d.drawArc(3, 2, 12, 11, -90, 230);
        g2d.drawArc(2, 3, 12, 11, -90, 230);
        g2d.fill(FlatUIUtils.createPath(0, 2, 7, 7, 0, 10));
    }
}
