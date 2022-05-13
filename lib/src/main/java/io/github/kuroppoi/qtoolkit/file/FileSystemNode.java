package io.github.kuroppoi.qtoolkit.file;

import java.io.File;

public abstract class FileSystemNode {
    
    protected String name;
    protected DirectoryNode parent;
    
    public FileSystemNode(String name) {
        setName(name);
    }
    
    public static boolean isNameValid(String name) {
        return name != null && !name.isEmpty() && name.equals(new File(name).getName()); // Cheesy, but eh, it works.
    }
    
    public abstract FileSystemNode copy();
    public abstract boolean isDirectory();
    
    @Override
    public String toString() {
        return name;
    }
    
    public DirectoryNode getRoot() {
        if(!hasParent()) {
            return null;
        }
        
        DirectoryNode parent = getParent();
        
        while(parent.hasParent()) {
            parent = parent.getParent();
        }
        
        return parent;
    }
    
    public String getPath() {
        return getPath(File.separator);
    }
    
    public String getPath(String separator) {
        String path = name;
        FileSystemNode node = this;
        
        while((node = node.getParent()) != null) {
            path = node.getName() + separator + path;
        }
        
        return path;
    }
    
    public void setName(String name) {
        if(isNameValid(name)) {
            this.name = name;
        }
    }
    
    public String getName() {
        return name;
    }
    
    protected void setParent(DirectoryNode parent) {
        this.parent = parent;
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    public DirectoryNode getParent() {
        return parent;
    }
}
