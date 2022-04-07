package io.github.kuroppoi.qtoolkit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import com.formdev.flatlaf.extras.components.FlatTabbedPane;

import io.github.kuroppoi.qtoolkit.event.SimpleRenameFailListener;
import io.github.kuroppoi.qtoolkit.event.TreeModelAdapter;
import io.github.kuroppoi.qtoolkit.file.FileChooser;
import io.github.kuroppoi.qtoolkit.file.FileType;
import io.github.kuroppoi.qtoolkit.file.editor.CollisionFileEditor;
import io.github.kuroppoi.qtoolkit.file.editor.FileEditor;
import io.github.kuroppoi.qtoolkit.file.editor.MeshFileEditor;
import io.github.kuroppoi.qtoolkit.file.editor.TextFileEditor;
import io.github.kuroppoi.qtoolkit.file.editor.UnknownFileEditor;
import io.github.kuroppoi.qtoolkit.pack.PackReader;
import io.github.kuroppoi.qtoolkit.pack.PackWriter;
import io.github.kuroppoi.qtoolkit.pack.collision.CollisionReader;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshReader;
import io.github.kuroppoi.qtoolkit.pkg.PkgReader;
import io.github.kuroppoi.qtoolkit.pkg.PkgWriter;
import io.github.kuroppoi.qtoolkit.shared.file.DirectoryNode;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;
import io.github.kuroppoi.qtoolkit.shared.file.FileSystemNode;
import io.github.kuroppoi.qtoolkit.shared.file.FileSystems;
import io.github.kuroppoi.qtoolkit.tree.FileSystemTreeModel;
import io.github.kuroppoi.qtoolkit.utils.ActionHelper;

public class MainView {
    
    private static final Icon floppyDriveIcon = UIManager.getIcon("QToolkit.floppyDriveIcon");
    private static final Icon doubleFloppyDriveIcon = UIManager.getIcon("QToolkit.doubleFloppyDriveIcon");
    private static final Icon curvedArrowIcon = UIManager.getIcon("QToolkit.curvedArrowIcon");
    private static final Icon fileIcon = UIManager.getIcon("FileView.fileIcon");
    private static final Icon directoryIcon = UIManager.getIcon("FileView.directoryIcon");
    private final Action saveAction = ActionHelper.createAction("Save (Ctrl + S)", floppyDriveIcon, this::saveCurrentFile);
    private final Action saveAllAction = ActionHelper.createAction("Save All (Ctrl + Shift + S)", doubleFloppyDriveIcon, this::saveAllFiles);
    private final Action undoAction = ActionHelper.createAction("Undo (Ctrl + Z)", curvedArrowIcon, this::undo);
    private final Action redoAction = ActionHelper.createAction("Redo (Ctrl + Y)", curvedArrowIcon, this::redo);
    private final Map<FileNode, FileEditor> fileEditors = new HashMap<>();
    private final DirectoryNode rootNode = new DirectoryNode("root");
    private final FileSystemTreeModel fileTreeModel = new FileSystemTreeModel(rootNode);
    private final JFrame frame;
    private JTree fileTree;
    private FlatTabbedPane fileTabs;
    
    public MainView() {
        frame = new JFrame("QPang Toolkit :: by Kuroppoi");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setJMenuBar(createMenuBar());
        frame.add(createEditorPanel());
        frame.add(createToolBar(), BorderLayout.PAGE_START);
        frame.setIconImages(Arrays.asList(
                new ImageIcon(getClass().getResource("/icons/icon16x.png")).getImage(), 
                new ImageIcon(getClass().getResource("/icons/icon32x.png")).getImage(), 
                new ImageIcon(getClass().getResource("/icons/icon64x.png")).getImage()));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if(OptionPane.showYesNoDialog("Are you sure you want to exit? Unsaved changes will be lost!\nNote that saved changes aren't saved to disk until you export them.")) {
                    System.exit(0);
                }
            }
        });
        FileChooser.setDefaultComponent(frame);
        OptionPane.setDefaultComponent(frame);
        frame.setVisible(true);
    }
    
    private JMenuBar createMenuBar() {
        // File menu
        JMenu fileOpenMenu = new JMenu("Open");
        fileOpenMenu.add(ActionHelper.createAction("Raw", this::openRawFile));
        fileOpenMenu.add(ActionHelper.createAction("Pack File (.pack)", this::openPackFile));
        fileOpenMenu.add(ActionHelper.createAction("Pkg File (.pkg)", this::openPkgFile));
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(fileOpenMenu);
        fileMenu.addSeparator();
        fileMenu.add(ActionHelper.createAction("Exit", () -> System.exit(0)));
        
        // TODO other menus
        
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        return menuBar;
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar("Toolbar");
        toolBar.setFloatable(true);
        toolBar.add(saveAction);
        toolBar.add(saveAllAction);
        toolBar.add(undoAction).setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        toolBar.add(redoAction);
        refreshToolbar();
        return toolBar;
    }
    
    private JPanel createEditorPanel() {
        // File structure tree
        fileTreeModel.addTreeModelListener(new TreeModelAdapter() {
            @Override
            public void treeNodesRemoved(TreeModelEvent event) {
                Object[] children = event.getChildren();
                
                if(children != null) {
                    for(Object child : event.getChildren()) {
                        treeNodeRemoved((FileSystemNode)child);
                    }
                } else {
                    treeNodeRemoved((FileSystemNode)event.getTreePath().getLastPathComponent());
                }
            }
            
            @Override
            public void treeNodesChanged(TreeModelEvent event) {
                Object[] children = event.getChildren();
                
                if(children != null) {
                    for(Object child : event.getChildren()) {
                        treeNodeChanged((FileSystemNode)child);
                    }
                } else {
                    treeNodeChanged((FileSystemNode)event.getTreePath().getLastPathComponent());
                }
            }
            
            private void treeNodeRemoved(FileSystemNode node) {
                if(node.isDirectory()) {
                    List<FileSystemNode> descendants = ((DirectoryNode)node).getDescendants();
                    
                    for(FileSystemNode descendant : descendants) {
                        if(!descendant.isDirectory()) {
                            closeFileEditor((FileNode)descendant);
                        }
                    }
                } else {
                    closeFileEditor((FileNode)node);
                }
            }
            
            private void treeNodeChanged(FileSystemNode node) {
                if(node.isDirectory()) {
                    List<FileSystemNode> descendants = ((DirectoryNode)node).getDescendants();
                    
                    for(FileSystemNode descendant : descendants) {
                        if(!descendant.isDirectory()) {
                            refreshFileEditor((FileNode)descendant);
                        }
                    }
                } else {
                    refreshFileEditor((FileNode)node);
                }
            }
        });
        fileTreeModel.setRenameFailListener(new SimpleRenameFailListener());
        fileTree = new JTree(fileTreeModel);
        fileTree.setRootVisible(false);
        fileTree.setShowsRootHandles(true);
        fileTree.setEditable(true);
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                int x = event.getX();
                int y = event.getY();
                TreePath path = fileTree.getPathForLocation(x, y);
                fileTree.setSelectionPath(path);
                
                if(path == null) {
                    if(SwingUtilities.isRightMouseButton(event)) {
                        JPopupMenu menu = new JPopupMenu();
                        menu.add(createAddMenu(rootNode));
                        menu.show(fileTree, x, y);
                    }
                    
                    return;
                }
                
                FileSystemNode node = (FileSystemNode)path.getLastPathComponent();
                
                if(SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2 && !node.isDirectory()) {
                    openFileEditor((FileNode)node);
                } else if(SwingUtilities.isRightMouseButton(event)) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenu exportMenu = new JMenu("Export");
                    exportMenu.setIcon(floppyDriveIcon);
                    exportMenu.add(ActionHelper.createAction("Raw", () -> exportRawFile(node)));
                    
                    if(node.isDirectory()) {
                        DirectoryNode directory = (DirectoryNode)node;
                        exportMenu.add(ActionHelper.createAction("Pack File (.pack)", () -> exportPackFile(directory)));
                        exportMenu.add(ActionHelper.createAction("Pkg File (.pkg)", () -> exportPkgFile(directory)));
                        menu.add(createAddMenu((DirectoryNode)node));
                    } else {
                        menu.add(ActionHelper.createAction("Open", () -> openFileEditor((FileNode)node)));
                    }
                    
                    menu.add(ActionHelper.createAction("Remove", () -> showRemoveDialog(node)));
                    menu.add(ActionHelper.createAction("Rename", () -> fileTree.startEditingAtPath(path)));
                    menu.addSeparator();
                    menu.add(exportMenu);
                    menu.show(fileTree, x, y);
                }
            }
            
            private JMenu createAddMenu(DirectoryNode directory) {
                JMenu menu = new JMenu("Add");
                menu.add(ActionHelper.createAction("File", fileIcon, () -> {
                    FileNode file = fileTreeModel.createFile(directory);
                    TreePath path = fileTreeModel.getPath(file);
                    fileTree.scrollPathToVisible(path);
                    fileTree.setSelectionPath(path);
                    openFileEditor(file);
                }));
                
                menu.add(ActionHelper.createAction("Folder", directoryIcon, () -> {
                    TreePath path = fileTreeModel.getPath(fileTreeModel.createDirectory(directory));
                    fileTree.scrollPathToVisible(path);
                    fileTree.setSelectionPath(path);
                }));
                return menu;
            }
        });
        
        // Tree scroll pane
        JScrollPane treePane = new JScrollPane(fileTree);
        treePane.setBorder(BorderFactory.createTitledBorder("File Structure"));
        
        // Open files tabs
        fileTabs = new FlatTabbedPane();
        fileTabs.setBorder(BorderFactory.createTitledBorder("Content"));
        fileTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        fileTabs.setTabsClosable(true);
        fileTabs.setTabCloseCallback((pane, index) -> {
            FileEditor editor = getFileEditor(fileTabs.getComponentAt(index));
            
            if(!editor.hasUnsavedChanges() || OptionPane.showYesNoDialog("This file has unsaved changes.\nAre you sure you want to close it?")) {
                // This is a very ugly solution to a fairly simple problem, but eh whatever.
                Iterator<Entry<FileNode, FileEditor>> iterator = fileEditors.entrySet().iterator();
                
                while(iterator.hasNext()) {
                    if(iterator.next().getValue() == editor) {
                        iterator.remove();
                    }
                }
                
                fileTabs.removeTabAt(index);
            }
        });
        fileTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                refreshToolbar();
            }
        });
        
        // Handy dandy shortcuts
        InputMap inputMap = fileTabs.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = fileTabs.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), "Save");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK), "Save All");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK), "Undo");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK), "Redo");
        actionMap.put("Save", saveAction);
        actionMap.put("Save All", saveAllAction);
        actionMap.put("Undo", undoAction);
        actionMap.put("Redo", redoAction);
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, fileTabs);
        splitPane.setResizeWeight(0.25);
        
        // Panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(splitPane);
        return panel;
    }
    
    public void refreshToolbar() {
        boolean anyUnsavedChanges = false;
        
        for(FileEditor editor : fileEditors.values()) {
            if(editor.hasUnsavedChanges()) {
                anyUnsavedChanges = true;
                break;
            }
        }
        
        saveAllAction.setEnabled(anyUnsavedChanges);
        FileEditor editor = getCurrentFileEditor();
        
        if(editor == null) {
            saveAction.setEnabled(false);
            undoAction.setEnabled(false);
            redoAction.setEnabled(false);
            return;
        }
        
        saveAction.setEnabled(editor.hasUnsavedChanges());
        undoAction.setEnabled(editor.hasUndoableAction());
        redoAction.setEnabled(editor.hasRedoableAction());
    }
    
    private void openFileEditor(FileNode file) {
        int index = fileTabs.indexOfComponent(getFileEditorComponent(file));
        
        if(index == -1) {
            FileEditor editor = null;
            
            try {
                switch(FileType.determineFileType(file)) {
                    case TEXT: editor = new TextFileEditor(file, this::refreshToolbar); break; 
                    case MESH: editor = new MeshFileEditor(file, MeshReader.readMeshFile(file), this::refreshToolbar); break;
                    case COLLISION: editor = new CollisionFileEditor(file, CollisionReader.readCollisionFile(file), this::refreshToolbar); break;
                    default: editor = new UnknownFileEditor(); break;
                }
            } catch(IOException e) {
                OptionPane.showErrorDialog("Error", "Could not open file.", e);
                return;
            }
            
            if(editor != null) {
                JComponent component = editor.getComponent();
                fileEditors.put(file, editor);
                fileTabs.addTab(file.getName(), null, component, getPathExcludingRoot(file));
                fileTabs.setSelectedComponent(component);
                
                if(component instanceof JScrollPane) {
                    component = (JComponent)((JScrollPane)component).getViewport().getView();
                }
            }
        } else {
            fileTabs.setSelectedIndex(index);
        }
    }
    
    private void closeFileEditor(FileNode file) {
        FileEditor editor = fileEditors.remove(file);
        
        if(editor != null) {
            fileTabs.remove(editor.getComponent());
        }
    }
    
    private void refreshFileEditor(FileNode file) {
        int index = fileTabs.indexOfComponent(getFileEditorComponent(file));
        
        if(index != -1) {
            fileTabs.setTitleAt(index, file.getName());
            fileTabs.setToolTipTextAt(index, getPathExcludingRoot(file));
        }
    }
    
    private FileEditor getCurrentFileEditor() {
        return getFileEditor(fileTabs.getSelectedComponent());
    }
        
    private FileEditor getFileEditor(Component component) {
        for(FileEditor editor : fileEditors.values()) {
            if(editor.getComponent() == component) {
                return editor;
            }
        }
        
        return null;
    }
    
    private JComponent getFileEditorComponent(FileNode file) {
        return fileEditors.containsKey(file) ? fileEditors.get(file).getComponent() : null;
    }
    
    private void showRemoveDialog(FileSystemNode node) {
        if(OptionPane.showYesNoDialog("Are you sure you want to remove this file?\nIt will *not* be removed from disk. This cannot be undone.")) {
            fileTreeModel.remove(node);
        }
    }
    
    private void openRawFile() {
        FileChooser.showFileOpenDialog(JFileChooser.FILES_AND_DIRECTORIES, file -> fileTreeModel.add(FileSystems.readFileSystem(file)));
    }
    
    private void openPackFile() {
        FileChooser.showFileOpenDialog(JFileChooser.FILES_ONLY, file -> {
            DirectoryNode node = PackReader.readPackFile(file);
            node.setName(file.getName());
            fileTreeModel.add(node);
        });
    }
    
    private void openPkgFile() {
        FileChooser.showFileOpenDialog(JFileChooser.FILES_ONLY, file -> {
            DirectoryNode node = PkgReader.readPkgFile(file);
            node.setName(file.getName());
            fileTreeModel.add(node);
        });
    }
    
    private void exportRawFile(FileSystemNode node) {
        FileChooser.showFileExportDialog(node.isDirectory() ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY, file -> FileSystems.writeFileSystem(node, file));
    }
    
    private void exportPackFile(DirectoryNode node) {
        FileChooser.showFileExportDialog(JFileChooser.FILES_ONLY, file -> PackWriter.writePackFile(node, file));
    }
    
    private void exportPkgFile(DirectoryNode node) {
        FileChooser.showFileExportDialog(JFileChooser.FILES_ONLY, file -> PkgWriter.writePkgFile(node, file));
    }
    
    // TODO handle save errors
    private void saveAllFiles() {        
        for(FileEditor editor : fileEditors.values()) {
            editor.save();
            refreshToolbar();
        }
    }
    
    private void saveCurrentFile() {
        FileEditor editor = getCurrentFileEditor();
        
        if(editor != null) {
            editor.save();
            refreshToolbar();
        }
    }
    
    private void redo() {
        FileEditor editor = getCurrentFileEditor();
        
        if(editor != null) {
            editor.redo();
            refreshToolbar();
        }
    }
    
    private void undo() {
        FileEditor editor = getCurrentFileEditor();
        
        if(editor != null) {
            editor.undo();
            refreshToolbar();
        }
    }
    
    private String getPathExcludingRoot(FileSystemNode node) {
        String path = node.getPath("\\");
        return path.substring(path.indexOf('\\') + 1);
    }
}
