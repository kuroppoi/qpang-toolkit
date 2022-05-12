package io.github.kuroppoi.qtoolkit.gui.file.editor;

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
import io.github.kuroppoi.qtoolkit.file.FileNode;
import io.github.kuroppoi.qtoolkit.gui.OptionPane;
import io.github.kuroppoi.qtoolkit.gui.event.SimpleRenameFailListener;
import io.github.kuroppoi.qtoolkit.gui.event.TreeRemoveUndo;
import io.github.kuroppoi.qtoolkit.gui.file.FileChooser;
import io.github.kuroppoi.qtoolkit.gui.tree.CollisionTreeCellRenderer;
import io.github.kuroppoi.qtoolkit.gui.tree.CollisionTreeModel;
import io.github.kuroppoi.qtoolkit.gui.utils.ActionHelper;
import io.github.kuroppoi.qtoolkit.pack.collision.Collision;
import io.github.kuroppoi.qtoolkit.pack.collision.CollisionConverter;
import io.github.kuroppoi.qtoolkit.pack.collision.CollisionFile;
import io.github.kuroppoi.qtoolkit.pack.collision.CollisionWriter;

public class CollisionFileEditor extends FileEditor implements TreeModelListener {
    
    private final FileNode file;
    private final CollisionFile collisionFile;
    private final Runnable changeListener;
    private final JTree tree;
    private final CollisionTreeModel treeModel;
    private final JScrollPane scrollPane;
    
    public CollisionFileEditor(FileNode file, CollisionFile collisionFile, Runnable changeListener) {
        this.file = file;
        this.collisionFile = collisionFile;
        this.changeListener = changeListener;
        tree = new JTree(treeModel = new CollisionTreeModel(collisionFile));
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setEditable(true);
        tree.setCellRenderer(new CollisionTreeCellRenderer());
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                TreePath path = tree.getPathForLocation(event.getX(), event.getY());
                tree.setSelectionPath(path);
                
                if(SwingUtilities.isRightMouseButton(event)) {
                    if(path == null) {
                        JPopupMenu menu = new JPopupMenu();
                        JMenu importMenu = new JMenu("Import");
                        importMenu.add(ActionHelper.createAction("Wavefront (.obj)", CollisionFileEditor.this::showImportObjDialog));
                        JMenu exportMenu = new JMenu("Export All");
                        exportMenu.add(ActionHelper.createAction("Wavefront (.obj)", CollisionFileEditor.this::showExportAllObjDialog));
                        menu.add(importMenu);
                        menu.add(exportMenu);
                        menu.show(tree, event.getX(), event.getY());
                        return;
                    }
                    
                    Object node = path.getLastPathComponent();
                    JPopupMenu menu = new JPopupMenu();
                    
                    if(node instanceof Collision) {
                        Collision collision = (Collision)node;
                        menu.add(ActionHelper.createAction("Remove", () -> showRemoveDialog(collision)));
                        menu.add(ActionHelper.createAction("Rename", () -> tree.startEditingAtPath(path)));
                        menu.addSeparator();
                        JMenu exportMenu = new JMenu("Export");
                        exportMenu.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
                        exportMenu.add(ActionHelper.createAction("Wavefront (.obj)", () -> showExportObjDialog(collision)));
                        menu.add(exportMenu);
                    }
                    
                    menu.show(tree, event.getX(), event.getY());
                }
            }
        });
        treeModel.setRenameFailListener(new SimpleRenameFailListener());
        treeModel.addTreeModelListener(this);
        scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(null);
    }
    
    @Override
    public void save() {
        CollisionWriter.writeCollisionFile(collisionFile, file);
        hasUnsavedChanges = false;
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
    
    @Override
    public JComponent getComponent() {
        return scrollPane;
    }
    
    private void showImportObjDialog() {
        FileChooser.showFileOpenDialog(JFileChooser.FILES_ONLY, file -> {
            FileInputStream inputStream = new FileInputStream(file);
            Obj obj = ObjReader.read(inputStream);
            inputStream.close();
            
            if(obj.getNumGroups() > 1) {
                CollisionFile collisionFile = CollisionConverter.convertObjToCollisionFile(obj);
                List<Collision> collisions = new ArrayList<>(collisionFile.getCollisions());
                List<TreePath> selectionPaths = new ArrayList<>();
                
                for(Collision collision : collisions) {
                    treeModel.add(collision);
                    selectionPaths.add(treeModel.getPath(collision));
                }
                
                tree.scrollPathToVisible(selectionPaths.get(0));
                tree.setSelectionPaths(selectionPaths.toArray(new TreePath[0]));
            } else {
                Collision collision = CollisionConverter.convertObjToCollision(obj, file.getName());
                treeModel.add(collision);
                TreePath path = treeModel.getPath(collision);
                tree.scrollPathToVisible(path);
                tree.setSelectionPath(path);
            }
        }, new FileNameExtensionFilter("Wavefront (.obj)", "obj"));
    }
    
    private void showExportAllObjDialog() {
        FileChooser.showFileExportDialog(JFileChooser.FILES_ONLY, file -> {
            FileOutputStream outputStream = new FileOutputStream(file);
            ObjWriter.write(CollisionConverter.convertCollisionFileToObj(collisionFile), outputStream);
            outputStream.close();
        }, new FileNameExtensionFilter("Wavefront (.obj)", "obj"));
    }
    
    private void showExportObjDialog(Collision collision) {
        FileChooser.showFileExportDialog(JFileChooser.FILES_ONLY, file -> {
            FileOutputStream outputStream = new FileOutputStream(file);
            ObjWriter.write(CollisionConverter.convertCollisionToObj(collision), outputStream);
            outputStream.close();
        }, new FileNameExtensionFilter("Wavefront (.obj)", "obj"));
    }
    
    private void showRemoveDialog(Collision collision) {
        if(OptionPane.showYesNoDialog("Are you sure you want to remove this collision?")) {
            int index = treeModel.getIndexOfChild(collisionFile, collision);
            treeModel.remove(collision);
            undoManager.addEdit(new TreeRemoveUndo(treeModel, collisionFile, collision, index));
            hasUnsavedChanges = true;
            changeListener.run();
        }
    }
}
