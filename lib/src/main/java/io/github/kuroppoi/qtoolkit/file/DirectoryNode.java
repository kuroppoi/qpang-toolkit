package io.github.kuroppoi.qtoolkit.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.kuroppoi.qtoolkit.utils.ListUtils;

public class DirectoryNode extends FileSystemNode {
    
    private static final String pathSeparatorRegex = "\\\\|/";
    private final List<FileSystemNode> children = new ArrayList<>();
    
    public DirectoryNode(String name) {
        super(name);
    }
    
    @Override
    public DirectoryNode copy() {
        DirectoryNode copy = new DirectoryNode(name);
        
        for(FileSystemNode child : children) {
            copy.addChild(child.copy());
        }
        
        return copy;
    }
    
    @Override
    public boolean isDirectory() {
        return true;
    }
    
    public FileNode createFile(String path) {
        String[] segments = path.split(pathSeparatorRegex);
        DirectoryNode directory = this;
        
        if(segments.length > 1) {
            directory = createDirectory(String.join("/", Arrays.copyOf(segments, segments.length - 1)));
        }
        
        FileNode file = new FileNode(segments[segments.length - 1]);
        directory.addChild(file);
        return file;
    }
    
    // TODO fix atrocious code
    public DirectoryNode createDirectory(String path) {
        DirectoryNode current = this;
        String[] names = path.split(pathSeparatorRegex);
        
        for(int i = 0; i < names.length; i++) {
            FileSystemNode node = current.getChild(names[i]);
            
            if(node == null) {
                node = new DirectoryNode(names[i]);
                current.addChild(node);
                current = (DirectoryNode)node;
            } else if(node.isDirectory()) {
                current = (DirectoryNode)node;
            } else {
                return null;
            }
        }
        
        return current;
    }
    
    public FileNode getFile(String path) {
        FileSystemNode node = getNode(path);
        return node == null || node.isDirectory() ? null : (FileNode)node;
    }
    
    public DirectoryNode getDirectory(String path) {
        FileSystemNode node = getNode(path);
        return node == null || !node.isDirectory() ? null : (DirectoryNode)node;
    }
    
    public FileSystemNode getNode(String path) {
        DirectoryNode current = this;
        String[] names = path.split(pathSeparatorRegex);
        
        for(int i = 0; i < names.length; i++) {
            FileSystemNode node = current.getChild(names[i]);
            
            if(node == null || (!node.isDirectory() && i + 1 < names.length)) {
                return null;
            } else if(node.isDirectory()) {
                current = ((DirectoryNode)node);
            } else {
                return node;
            }
        }
        
        return current;
    }
    
    public List<FileSystemNode> getDescendants() {
        List<FileSystemNode> descendants = new ArrayList<>();
        
        for(FileSystemNode child : children) {
            descendants.add(child);
            
            if(child.isDirectory()) {
                descendants.addAll(((DirectoryNode)child).getDescendants());
            }
        }
        
        return descendants;
    }
    
    public void addChild(FileSystemNode node) {
        if(node.hasParent()) {
            throw new IllegalArgumentException("Node already has a parent");
        } else if(node == this) {
            throw new IllegalArgumentException("Node cannot be added to itself");
        } else if(hasChild(node.getName())) {
            throw new IllegalArgumentException("A node with this name already exists");
        }
        
        children.add(node);
        node.setParent(this);
    }
    
    public void removeChild(FileSystemNode node) {
        if(children.remove(node)) {
            node.setParent(null);
        }
    }
    
    public void removeChild(int index) {
        FileSystemNode node = ListUtils.remove(children, index);
        
        if(node != null) {
            node.setParent(null);
        }
    }
    
    public FileSystemNode getChild(int index) {
        return ListUtils.get(children, index);
    }
    
    public FileSystemNode getChild(String name) {
        return ListUtils.get(children, node -> node.getName().equalsIgnoreCase(name));
    }
    
    public boolean hasChild(String name) {
        return getChild(name) != null;
    }
    
    public int getChildCount() {
        return children.size();
    }
    
    public List<FileSystemNode> getChildren() {
        return children;
    }
}
