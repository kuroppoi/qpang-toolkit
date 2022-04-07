package io.github.kuroppoi.qtoolkit.gui.file;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

// TODO better display error info?
public class FileChooser {
    
    private static File lastDir = new File(".");
    private static Component defaultComponent = null;
    
    public static void showFileOpenDialog(int selectionMode, FileChooserCallback callback, FileFilter... filters) {
        JFileChooser fileChooser = new JFileChooser(lastDir);
        fileChooser.setFileSelectionMode(selectionMode);
        fileChooser.setMultiSelectionEnabled(true);
        
        for(FileFilter filter : filters) {
            fileChooser.setFileFilter(filter);
        }
        
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
    
    public static void showFileExportDialog(int selectionMode, FileChooserCallback callback, FileFilter... filters) {
        JFileChooser fileChooser = new JFileChooser(lastDir);
        fileChooser.setFileSelectionMode(selectionMode);
        
        for(FileFilter filter : filters) {
            fileChooser.setFileFilter(filter);
        }
        
        if(fileChooser.showSaveDialog(defaultComponent) == JFileChooser.APPROVE_OPTION) {
            lastDir = fileChooser.getCurrentDirectory();
            File file = fileChooser.getSelectedFile();
            FileFilter filter = fileChooser.getFileFilter();
            
            // Add file extension of the current filter if there is none
            if(filter instanceof FileNameExtensionFilter && file.getName().indexOf('.') == -1) {
                String[] extensions = ((FileNameExtensionFilter)filter).getExtensions();
                
                if(extensions.length == 1) {
                    file = new File(file.getAbsolutePath() + '.' + extensions[0]);
                }
            }
            
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
