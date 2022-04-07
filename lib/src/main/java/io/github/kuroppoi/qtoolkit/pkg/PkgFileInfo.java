package io.github.kuroppoi.qtoolkit.pkg;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.file.DirectoryNode;
import io.github.kuroppoi.qtoolkit.file.FileNode;
import io.github.kuroppoi.qtoolkit.file.FileSystemNode;

public class PkgFileInfo {
    
    private final List<FileNode> files = new ArrayList<>();
    
    public PkgFileInfo(DirectoryNode directory) {        
        for(FileSystemNode descendant : directory.getDescendants()) {
            if(!descendant.isDirectory()) {
                files.add((FileNode)descendant);
            }
        }
    }
    
    public int getFileCount() {
        return files.size();
    }
    
    public List<FileNode> getFiles() {
        return files;
    }
}
