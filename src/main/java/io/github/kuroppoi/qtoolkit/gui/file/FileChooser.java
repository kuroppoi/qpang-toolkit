package io.github.kuroppoi.qtoolkit.gui.file;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

// TODO better display error info?
public class FileChooser {
    
    private static File lastDir = new File(".");
    private static Component defaultComponent = null;
    
    public static void showFileOpenDialog(int selectionMode, FileChooserCallback callback) {
        JFileChooser fileChooser = new JFileChooser(lastDir);
        fileChooser.setFileSelectionMode(selectionMode);
        fileChooser.setMultiSelectionEnabled(true);
        List<String> errors = new ArrayList<>();
        
        if(fileChooser.showOpenDialog(defaultComponent) == JFileChooser.APPROVE_OPTION) {
            lastDir = fileChooser.getCurrentDirectory();
            File[] files = fileChooser.getSelectedFiles();
            
            for(File file : files) {
                try {
                    callback.handle(file);
                } catch(Exception e) {
                    errors.add(String.format("%s: %s", e, file.getAbsolutePath()));
                }
            }
        }
        
        if(!errors.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("One or more files could not be opened.\n\n");
            
            for(String error : errors) {
                builder.append(String.format("%s\n", error));
            }
            
            JOptionPane.showMessageDialog(defaultComponent, builder.toString(), "Something went wrong!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void showFileExportDialog(int selectionMode, FileChooserCallback callback) {
        JFileChooser fileChooser = new JFileChooser(lastDir);
        fileChooser.setFileSelectionMode(selectionMode);
        
        if(fileChooser.showSaveDialog(defaultComponent) == JFileChooser.APPROVE_OPTION) {
            lastDir = fileChooser.getCurrentDirectory();
            File file = fileChooser.getSelectedFile();
            
            try {
                callback.handle(file);
                JOptionPane.showMessageDialog(defaultComponent, String.format("Successfully exported to '%s'", file.getAbsolutePath()));
            } catch(Exception e) {
                JOptionPane.showMessageDialog(defaultComponent, e.toString(), "Export Failed.", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void setDefaultComponent(Component defaultComponent) {
        FileChooser.defaultComponent = defaultComponent;
    }
}
