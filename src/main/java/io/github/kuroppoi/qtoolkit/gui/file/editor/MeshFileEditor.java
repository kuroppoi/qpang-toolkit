package io.github.kuroppoi.qtoolkit.gui.file.editor;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjWriter;
import io.github.kuroppoi.qtoolkit.file.DirectoryNode;
import io.github.kuroppoi.qtoolkit.file.FileNode;
import io.github.kuroppoi.qtoolkit.gui.OptionPane;
import io.github.kuroppoi.qtoolkit.gui.SceneExporterView;
import io.github.kuroppoi.qtoolkit.gui.event.SimpleRenameFailListener;
import io.github.kuroppoi.qtoolkit.gui.event.TreeRemoveUndo;
import io.github.kuroppoi.qtoolkit.gui.file.FileChooser;
import io.github.kuroppoi.qtoolkit.gui.tree.MeshTreeCellRenderer;
import io.github.kuroppoi.qtoolkit.gui.tree.MeshTreeModel;
import io.github.kuroppoi.qtoolkit.gui.utils.ActionHelper;
import io.github.kuroppoi.qtoolkit.pack.mesh.Mesh;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshConverter;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshFile;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshWriter;

public class MeshFileEditor extends FileEditor implements TreeModelListener {
    
    private final FileNode file;
    private final MeshFile meshFile;
    private final Runnable changeListener;
    private final JTree tree;
    private final MeshTreeModel treeModel;
    private final JScrollPane scrollPane;
    
    public MeshFileEditor(FileNode file, MeshFile meshFile, Runnable changeListener) {
        this.file = file;
        this.meshFile = meshFile;
        this.changeListener = changeListener;
        tree = new JTree(treeModel = new MeshTreeModel(meshFile));
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setEditable(true);
        tree.setCellRenderer(new MeshTreeCellRenderer());
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                TreePath path = tree.getPathForLocation(event.getX(), event.getY());
                tree.setSelectionPath(path);
                
                if(SwingUtilities.isRightMouseButton(event)) {
                    if(path == null) {
                        JPopupMenu menu = new JPopupMenu();
                        JMenu importMenu = new JMenu("Import");
                        importMenu.add(ActionHelper.createAction("Wavefront (.obj)", MeshFileEditor.this::showImportObjDialog));
                        menu.add(importMenu);
                        menu.add(ActionHelper.createAction("Export Scene", MeshFileEditor.this::showSceneExporter));
                        menu.show(tree, event.getX(), event.getY());
                        return;
                    }
                    
                    Object node = path.getLastPathComponent();
                    JPopupMenu menu = new JPopupMenu();
                    
                    if(node instanceof Mesh) {
                        Mesh mesh = (Mesh)node;
                        menu.add(ActionHelper.createAction("Remove", () -> showRemoveDialog(mesh)));
                        menu.add(ActionHelper.createAction("Rename", () -> tree.startEditingAtPath(path)));
                        menu.addSeparator();
                        JMenu exportMenu = new JMenu("Export");
                        exportMenu.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
                        exportMenu.add(ActionHelper.createAction("Wavefront (.obj)", () -> showExportObjDialog(mesh)));
                        menu.add(exportMenu);
                    } else {
                        menu.add(ActionHelper.createAction("Rename", () -> tree.startEditingAtPath(path)));
                    }
                    
                    menu.show(tree, event.getX(), event.getY());
                }
            }
        });
        treeModel.addTreeModelListener(this);
        treeModel.setRenameFailListener(new SimpleRenameFailListener());
        scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(null);
    }
    
    @Override
    public void save() {
        MeshWriter.writeMeshFile(meshFile, file);
        hasUnsavedChanges = false;
    }
    
    @Override
    public JComponent getComponent() {
        return scrollPane;
    }
    
    @Override
    public void treeNodesChanged(TreeModelEvent event) {
        hasUnsavedChanges = true;
        changeListener.run();
    }

    @Override
    public void treeNodesInserted(TreeModelEvent event) {
        hasUnsavedChanges = true;
        changeListener.run();
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent event) {
        hasUnsavedChanges = true;
        changeListener.run();
    }

    @Override
    public void treeStructureChanged(TreeModelEvent event) {
        hasUnsavedChanges = true;
        changeListener.run();
    }
    
    private void showImportObjDialog() {
        FileChooser.showFileOpenDialog(JFileChooser.FILES_ONLY, file -> {
            FileInputStream inputStream = new FileInputStream(file);
            Obj obj = ObjReader.read(inputStream);
            inputStream.close();
            
            if(obj.getNumGroups() > 1) {
                MeshFile meshFile = MeshConverter.convertObjToMeshFile(obj);
                List<Mesh> meshes = new ArrayList<>(meshFile.getMeshes());
                List<TreePath> selectionPaths = new ArrayList<>();
                
                for(Mesh mesh : meshes) {
                    treeModel.add(mesh);
                    selectionPaths.add(treeModel.getPath(mesh));
                }
                
                tree.scrollPathToVisible(selectionPaths.get(0));
                tree.setSelectionPaths(selectionPaths.toArray(new TreePath[0]));
            } else {
                Mesh mesh = MeshConverter.convertObjToMesh(obj, file.getName());
                treeModel.add(mesh);
                TreePath path = treeModel.getPath(mesh);
                tree.scrollPathToVisible(path);
                tree.setSelectionPath(path);
            }
        }, new FileNameExtensionFilter("Wavefront (.obj)", "obj"));
    }
    
    private void showExportObjDialog(Mesh mesh) {
        FileChooser.showFileExportDialog(JFileChooser.FILES_ONLY, file -> {
            FileOutputStream outputStream = new FileOutputStream(file);
            ObjWriter.write(MeshConverter.convertMeshToObj(mesh), outputStream);
            outputStream.close();
        }, new FileNameExtensionFilter("Wavefront (.obj)", "obj"));
    }
    
    private void showSceneExporter() {
        if(hasUnsavedChanges) {
            OptionPane.showMessageDialog("This file has unsaved changes.\n"
                    + "If you want the exported scene to reflect these, please save them before using the scene exporter.");
        }
        
        if(!file.getParent().hasParent()) {
            OptionPane.showMessageDialog("Couldn't auto-detect file paths. Please enter them manually.");
        }
        
        DirectoryNode packRootNode = file.getParent().getParent();
        DirectoryNode rootNode = file.getRoot();
        String meshPath = file.getPath("/").substring(rootNode.getName().length() + 1);
        
        if(packRootNode == null) {
            new SceneExporterView((Frame)SwingUtilities.getRoot(tree), rootNode, null, meshPath, null, null);
        } else {
            String rootPath = packRootNode.getPath("/");
            
            if(packRootNode == rootNode) {
                rootPath = rootPath.substring(rootNode.getName().length());
            } else {
                rootPath = rootPath.substring(rootPath.indexOf('/') + 1) + "/";
            }
            
            String name = file.getName().replace(".mesh", "");
            String scenePath = String.format("%sscene/%s.scene", rootPath, name);
            String materialPath = String.format("%smaterial/%s.material", rootPath, name);
            String texturePath = String.format("%stexture", rootPath);
            new SceneExporterView((Frame)SwingUtilities.getRoot(tree), rootNode, scenePath, meshPath, materialPath, texturePath);
        }
    }
    
    private void showRemoveDialog(Mesh mesh) {
        if(OptionPane.showYesNoDialog("Are you sure you want to remove this mesh?")) {
            int index = treeModel.getIndexOfChild(meshFile, mesh);
            treeModel.remove(mesh);
            undoManager.addEdit(new TreeRemoveUndo(treeModel, meshFile, mesh, index));
            hasUnsavedChanges = true;
            changeListener.run();
        }
    }
}
