package io.github.kuroppoi.qtoolkit.gui.event;

import java.util.function.Consumer;

import io.github.kuroppoi.qtoolkit.gui.OptionPane;
import io.github.kuroppoi.qtoolkit.gui.RenameFailReason;

public class SimpleRenameFailListener implements Consumer<RenameFailReason> {
    
    @Override
    public void accept(RenameFailReason reason) {
        switch(reason) {
            case INVALID_NAME:
                OptionPane.showMessageDialog("This name is not allowed.");
                break;
            case DUPLICATE_NAME:
                OptionPane.showMessageDialog("This name is already used in this location.");
                break;
        }
    }
}
