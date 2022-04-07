package io.github.kuroppoi.qtoolkit.pack;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.file.DirectoryNode;
import io.github.kuroppoi.qtoolkit.file.FileNode;
import io.github.kuroppoi.qtoolkit.file.FileSystemNode;

public class PackFileInfo {
    
    private final List<DirectoryNode> directories = new ArrayList<>();
    private final List<FileNode> files = new ArrayList<>();
    
    protected PackFileInfo(DirectoryNode directory) {
        for(FileSystemNode node : directory.getDescendants()) {
            if(node.isDirectory()) {
                directories.add((DirectoryNode)node);
            } else {
                files.add((FileNode)node);
            }
        }
    }
    
    public int getLocation(FileSystemNode node) {
        return directories.indexOf(node.getParent());
    }
    
    public int getDirectoryCount() {
        return directories.size();
    }
    
    public List<DirectoryNode> getDirectories() {
        return directories;
    }
    
    public int getFileCount() {
        return files.size();
    }
    
    public List<FileNode> getFiles() {
        return files;
    }
}
