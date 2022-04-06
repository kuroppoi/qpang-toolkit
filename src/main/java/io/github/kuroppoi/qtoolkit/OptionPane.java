package io.github.kuroppoi.qtoolkit;

import java.awt.Component;

import javax.swing.JOptionPane;

public class OptionPane {
    
    private static final String newLine = System.lineSeparator();
    private static Component defaultComponent;
    
    public static void showErrorDialog(String title, String message, Throwable throwable) {
        showErrorDialog(title, message, throwable, false);
    }
    
    public static void showErrorDialog(String title, String message, Throwable throwable, boolean details) {
        StringBuilder builder = new StringBuilder();
        builder.append(message + newLine);
        builder.append(throwable.toString() + newLine);
        
        if(details) {
            int i = 0;
            builder.append(newLine);
            
            for(StackTraceElement element : throwable.getStackTrace()) {
                builder.append(element.toString() + newLine);
                
                if(++i > 4) {
                    break;
                }
            }
        }
        
        JOptionPane.showMessageDialog(defaultComponent, builder.toString(), title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static boolean showYesNoDialog(String message) {
        return showYesNoDialog(message, "Confirmation");
    }
    
    public static boolean showYesNoDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(defaultComponent, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    public static void showMessageDialog(String message) {
        showMessageDialog(message, "Attention");
    }
    
    public static void showMessageDialog(String message, String title) {
        JOptionPane.showMessageDialog(defaultComponent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void setDefaultComponent(Component defaultComponent) {
        OptionPane.defaultComponent = defaultComponent;
    }
}
