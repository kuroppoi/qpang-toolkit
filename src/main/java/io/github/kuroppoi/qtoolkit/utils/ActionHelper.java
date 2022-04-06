package io.github.kuroppoi.qtoolkit.utils;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public class ActionHelper {
    
    public static Action createAction(String name, Runnable handler) {
        return createAction(name, null, handler);
    }
    
    @SuppressWarnings("serial")
    public static Action createAction(String name, Icon icon, Runnable handler) {
        AbstractAction action = new AbstractAction(name, icon) {
            @Override
            public void actionPerformed(ActionEvent event) {
                handler.run();
            }
        };
        
        if(icon != null) {
            action.putValue(Action.SHORT_DESCRIPTION, name);
        }
        
        return action;
    }
}
