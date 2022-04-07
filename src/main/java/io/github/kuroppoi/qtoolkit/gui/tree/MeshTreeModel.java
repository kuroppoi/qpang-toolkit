package io.github.kuroppoi.qtoolkit.gui.tree;

import javax.swing.tree.TreePath;

import io.github.kuroppoi.qtoolkit.gui.RenameFailReason;
import io.github.kuroppoi.qtoolkit.pack.mesh.Mesh;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshFile;
import io.github.kuroppoi.qtoolkit.pack.mesh.SubMesh;

public class MeshTreeModel extends AbstractTreeModel {
    
    private final MeshFile root;
    
    public MeshTreeModel(MeshFile root) {
        this.root = root;
    }
    
    @Override
    public Object getRoot() {
        return root;
    }
    
    @Override
    public Object getChild(Object parent, int index) {
        if(parent instanceof MeshFile) {
            return ((MeshFile)parent).getMesh(index);
        } else if(parent instanceof Mesh) {
            return ((Mesh)parent).getSubMesh(index);
        }
        
        return null;
    }
    
    @Override
    public int getChildCount(Object parent) {
        if(parent instanceof MeshFile) {
            return ((MeshFile)parent).getMeshCount();
        } else if(parent instanceof Mesh) {
            return ((Mesh)parent).getSubMeshCount();
        }
        
        return 0;
    }
    
    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }
    
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        Object component = path.getLastPathComponent();
        String name = newValue.toString().trim();
        
        if(!name.matches("^[a-zA-Z0-9._]{1,64}$")) {
            postRenameFailed(RenameFailReason.INVALID_NAME);
            return;
        }
        
        if(component instanceof Mesh) {
            Mesh mesh = (Mesh)component;
            
            if(!mesh.getName().equals(name)) {
                if(mesh.hasParent() && mesh.getParent().hasMesh(name)) {
                    postRenameFailed(RenameFailReason.DUPLICATE_NAME);
                    return;
                }
                
                mesh.setName(name);
                nodeChanged(mesh);
            }
        } else if(component instanceof SubMesh) {
            SubMesh subMesh = (SubMesh)component;
            
            if(!subMesh.getMaterialName().equals(name)) {
                if(subMesh.hasParent() && subMesh.getParent().hasSubMesh(name)) {
                    postRenameFailed(RenameFailReason.DUPLICATE_NAME);
                    return;
                }
                
                subMesh.setMaterialName(name);
                nodeChanged(subMesh);
            }
        }
    }
    
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        int childCount = getChildCount(parent);
        
        for(int i = 0; i < childCount; i++) {
            if(getChild(parent, i) == child) {
                return i;
            }
        }
        
        return -1;
    }
    
    @Override
    public void add(Object parent, Object node) {
        if(parent == root && node instanceof Mesh) {
            add((Mesh)node);
        }
    }
    
    @Override
    public void insert(Object parent, int index, Object node) {
        if(parent == root && node instanceof Mesh) {
            insert(index, (Mesh)node);
        }
    }
    
    @Override
    public void remove(Object node) {
        if(node instanceof Mesh) {
            remove((Mesh)node);
        }
    }
    
    @Override
    public Object getParent(Object node) {
        if(node instanceof Mesh) {
            return ((Mesh)node).getParent();
        } else if(node instanceof SubMesh) {
            return ((SubMesh)node).getParent();
        }
        
        return null;
    }
    
    private String findAvailableName(String name) {
        int tries = 0;
        String current = name;
        
        while(root.hasMesh(current)) {
            current = String.format("%s_%s", name, ++tries);
        }
        
        return current;
    }
    
    public void add(Mesh mesh) {
        insert(root.getMeshCount(), mesh);
    }
    
    public void insert(int index, Mesh mesh) {
        if(mesh.hasParent()) {
            mesh.getParent().removeMesh(mesh);
        }
        
        String name = mesh.getName();
        
        if(root.hasMesh(name)) {
            mesh.setName(findAvailableName(name));
        }
        
        root.addMesh(index, mesh);
        nodeInserted(root, index);
    }
    
    public void remove(Mesh mesh) {
        if(mesh.hasParent()) {
            int index = getIndexOfChild(root, mesh);
            root.removeMesh(mesh);
            nodeRemoved(root, index, mesh);
        }
    }
}
