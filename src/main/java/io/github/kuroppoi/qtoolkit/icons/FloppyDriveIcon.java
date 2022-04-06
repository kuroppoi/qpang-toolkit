package io.github.kuroppoi.qtoolkit.icons;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import com.formdev.flatlaf.ui.FlatUIUtils;

public class FloppyDriveIcon extends AbstractIcon {
    
    @Override
    protected void paintIcon(Component component, Graphics2D g2d) {
        super.paintIcon(component, g2d);
        Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        path.append(FlatUIUtils.createPath(11, 14, 11, 11, 5, 11, 5, 14, 2, 14, 2, 2, 14, 2, 14, 14, 11, 14), false);
        path.append(FlatUIUtils.createPath(4, 4, 4, 8, 12, 8, 12, 4, 4, 4), false);
        g2d.fill(path);
        g2d.fillRect(6, 12, 4, 2);
    }
}
