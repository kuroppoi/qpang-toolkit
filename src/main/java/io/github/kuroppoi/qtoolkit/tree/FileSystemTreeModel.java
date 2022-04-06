package io.github.kuroppoi.qtoolkit.tree;

import javax.swing.tree.TreePath;

import io.github.kuroppoi.qtoolkit.RenameFailReason;
import io.github.kuroppoi.qtoolkit.shared.file.DirectoryNode;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;
import io.github.kuroppoi.qtoolkit.shared.file.FileSystemNode;

public class FileSystemTreeModel extends AbstractTreeModel {
    
    private final FileSystemNode root;
    
    public FileSystemTreeModel(FileSystemNode root) {
        this.root = root;
    }
    
    @Override
    public Object getRoot() {
        return root;
    }
    
    @Override
    public Object getChild(Object parent, int index) {
        return parent instanceof DirectoryNode ? ((DirectoryNode)parent).getChild(index) : null;
    }
    
    @Override
    public int getChildCount(Object parent) {
        return parent instanceof DirectoryNode ? ((DirectoryNode)parent).getChildCount() : 0;
    }
    
    @Override
    public boolean isLeaf(Object node) {
        return node instanceof FileNode;
    }
    
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        FileSystemNode node = (FileSystemNode)path.getLastPathComponent();
        String name = newValue.toString().trim();
        
        if(!node.getName().equals(name)) {
            if(!FileSystemNode.isNameValid(name)) {
                postRenameFailed(RenameFailReason.INVALID_NAME);
                return;
            }
            
            if(node.hasParent() && node.getParent().hasChild(name)) {
                postRenameFailed(RenameFailReason.DUPLICATE_NAME);
                return;
            }
            
            node.setName(name);
            nodeChanged(node);
        }
    }
    
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        for(int i = 0; i < getChildCount(parent); i++) {
            if(getChild(parent, i) == child) {
                return i;
            }
        }
        
        return -1;
    }
    
    @Override
    public void add(Object parent, Object node) {
        if(parent instanceof DirectoryNode && node instanceof FileSystemNode) {
            add((DirectoryNode)parent, (FileSystemNode)node);
        }
    }
    
    @Override
    public void insert(Object parent, int index, Object node) {
        // TODO
    }

    @Override
    public void remove(Object node) {
        if(node instanceof FileSystemNode) {
            remove((FileSystemNode)node);
        }
    }
    
    @Override
    public Object getParent(Object node) {
        return ((FileSystemNode)node).getParent();
    }
    
    public FileNode createFile(DirectoryNode parent) {
        return createFile(parent, "New File");
    }
    
    public FileNode createFile(DirectoryNode parent, String name) {
        FileNode node = new FileNode(findAvailableName(parent, name));
        add(parent, node);
        return node;
    }
    
    public DirectoryNode createDirectory(DirectoryNode parent) {
        return createDirectory(parent, "New Folder");
    }
    
    public DirectoryNode createDirectory(DirectoryNode parent, String name) {
        DirectoryNode node = new DirectoryNode(findAvailableName(parent, name));
        add(parent, node);
        return node;
    }
    
    private String findAvailableName(DirectoryNode parent, String name) {
        int tries = 0;
        String current = name;
        
        while(parent.hasChild(current)) {
            current = String.format("%s (%s)", name, ++tries);
        }
        
        return current;
    }
    
    public void add(FileSystemNode node) {
        if(root instanceof DirectoryNode) {
            add((DirectoryNode)root, node);
        }
    }
    
    public void add(DirectoryNode parent, FileSystemNode node) {
        if(node.hasParent()) {
            remove(node);
        }
        
        node.setName(findAvailableName(parent, node.getName()));
        parent.addChild(node);
        nodeInserted(parent, getIndexOfChild(parent, node));
    }
    
    public void remove(FileSystemNode node) {
        if(node.hasParent()) {
            DirectoryNode parent = node.getParent();
            int index = getIndexOfChild(parent, node);
            parent.removeChild(node);
            nodeRemoved(parent, index, node);
        }
    }
}
