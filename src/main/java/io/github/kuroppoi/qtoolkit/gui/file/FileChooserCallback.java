package io.github.kuroppoi.qtoolkit.gui.file;

import java.io.File;

@FunctionalInterface
public interface FileChooserCallback {
    
    public void handle(File file) throws Exception;
}
