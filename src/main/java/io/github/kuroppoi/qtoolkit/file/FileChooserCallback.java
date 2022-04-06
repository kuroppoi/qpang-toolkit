package io.github.kuroppoi.qtoolkit.file;

import java.io.File;

@FunctionalInterface
public interface FileChooserCallback {
    
    public void handle(File file) throws Exception;
}
