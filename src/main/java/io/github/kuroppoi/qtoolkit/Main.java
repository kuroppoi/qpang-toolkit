package io.github.kuroppoi.qtoolkit;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;

import io.github.kuroppoi.qtoolkit.icons.CollisionIcon;
import io.github.kuroppoi.qtoolkit.icons.CurvedArrowIcon;
import io.github.kuroppoi.qtoolkit.icons.DoubleFloppyDriveIcon;
import io.github.kuroppoi.qtoolkit.icons.FloppyDriveIcon;
import io.github.kuroppoi.qtoolkit.icons.MeshIcon;
import io.github.kuroppoi.qtoolkit.icons.SubMeshIcon;

public class Main {
    
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            SwingUtilities.invokeLater(() -> {
                OptionPane.showErrorDialog("Error", "An unexpected error occured. The program will close.", throwable, true);
                System.exit(-1);
            });
        });
        
        SwingUtilities.invokeLater(() -> {
            FlatOneDarkIJTheme.setup();
            registerComponents();
            new MainView();
        });
    }
    
    private static void registerComponents() {
        UIManager.put("QToolkit.floppyDriveIcon", new FloppyDriveIcon());
        UIManager.put("QToolkit.doubleFloppyDriveIcon", new DoubleFloppyDriveIcon());
        UIManager.put("QToolkit.meshIcon", new MeshIcon());
        UIManager.put("QToolkit.subMeshIcon", new SubMeshIcon());
        UIManager.put("QToolkit.collisionIcon", new CollisionIcon());
        UIManager.put("QToolkit.curvedArrowIcon", new CurvedArrowIcon());
    }
}
