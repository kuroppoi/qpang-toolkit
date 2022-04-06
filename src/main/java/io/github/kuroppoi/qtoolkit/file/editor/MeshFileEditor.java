package io.github.kuroppoi.qtoolkit.file.editor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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
import javax.swing.tree.TreePath;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjWriter;
import io.github.kuroppoi.qtoolkit.OptionPane;
import io.github.kuroppoi.qtoolkit.event.SimpleRenameFailListener;
import io.github.kuroppoi.qtoolkit.event.TreeRemoveUndo;
import io.github.kuroppoi.qtoolkit.file.FileChooser;
import io.github.kuroppoi.qtoolkit.pack.mesh.Mesh;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshConverter;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshFile;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshWriter;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;
import io.github.kuroppoi.qtoolkit.tree.MeshTreeCellRenderer;
import io.github.kuroppoi.qtoolkit.tree.MeshTreeModel;
import io.github.kuroppoi.qtoolkit.utils.ActionHelper;

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
            Mesh mesh = MeshConverter.convertObjToMesh(obj, file.getName());
            treeModel.add(mesh);
            TreePath path = treeModel.getPath(mesh);
            tree.scrollPathToVisible(path);
            tree.setSelectionPath(path);
        });
    }
    
    private void showExportObjDialog(Mesh mesh) {
        FileChooser.showFileExportDialog(JFileChooser.FILES_ONLY, file -> {
            FileOutputStream outputStream = new FileOutputStream(file);
            ObjWriter.write(MeshConverter.convertMeshToObj(mesh), outputStream);
            outputStream.close();
        });
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
