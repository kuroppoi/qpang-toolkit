package io.github.kuroppoi.qtoolkit.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.javagl.obj.MtlWriter;
import de.javagl.obj.ObjWriter;
import io.github.kuroppoi.qtoolkit.file.DirectoryNode;
import io.github.kuroppoi.qtoolkit.file.FileNode;
import io.github.kuroppoi.qtoolkit.file.FileSystems;
import io.github.kuroppoi.qtoolkit.gui.file.FileChooser;
import io.github.kuroppoi.qtoolkit.pack.material.MaterialConverter;
import io.github.kuroppoi.qtoolkit.pack.material.MaterialFile;
import io.github.kuroppoi.qtoolkit.pack.material.MaterialParser;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshConverter;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshFile;
import io.github.kuroppoi.qtoolkit.pack.mesh.MeshReader;
import io.github.kuroppoi.qtoolkit.pack.scene.SceneFile;
import io.github.kuroppoi.qtoolkit.pack.scene.SceneParser;

public class SceneExporterView {
    
    private final JDialog dialog;
    private final DirectoryNode rootNode;
    private JTextField scenePathField;
    private JTextField meshPathField;
    private JTextField materialPathField;
    private JTextField texturePathField;
    
    public SceneExporterView(Component component, DirectoryNode rootNode) {
        this(component, rootNode, null, null, null, null);
    }
    
    public SceneExporterView(Component component, DirectoryNode rootNode, 
            String scenePath, String meshPath, String materialPath, String texturePath) {
        this.rootNode = rootNode;
        
        // Silly code to get root Frame
        Frame owner = null;
        
        if(component instanceof Frame) {
            owner = (Frame)component;
        } else {
            Component root = SwingUtilities.getRoot(component);
            
            if(!(root instanceof Frame)) {
                throw new IllegalArgumentException("Component's root component must be a Frame");
            }
            
            owner = (Frame)root;
        }
        
        // Info Section
        JLabel infoLabel = new JLabel("<html>Note: Lightmaps and vertex colors are lost when converting to certain formats.<br>"
                + " If you wish to modify a scene that uses these, it is best to reimport modified objects separately.<br>"
                + "<br>"
                + "To export a scene, enter valid scene, mesh, material & texture paths and press 'Export'.<br>"
                + "Make sure the paths are in-memory!<br>"
                + "The scene exporter is still experimental. Things may not always go exactly right!</html>");
        
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        infoPanel.add(infoLabel);
        
        // Export Button
        JButton exportButton = new JButton("Export Scene");
        exportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportButton.addActionListener(event -> exportScene());
        
        // Main Panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(infoPanel);
        panel.add(createInputPanel(scenePath, meshPath, materialPath, texturePath));
        panel.add(exportButton);
        
        // Dialog
        dialog = new JDialog(owner, "Scene Exporter");
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
        
    private void exportScene() {
        String scenePath = scenePathField.getText();
        String meshPath = meshPathField.getText();
        String materialPath = materialPathField.getText();
        String texturePath = texturePathField.getText();
        
        if(scenePath.isEmpty() || meshPath.isEmpty() || materialPath.isEmpty() || texturePath.isEmpty()) {
            OptionPane.showMessageDialog("Please enter all paths.");
            return;
        }
        
        FileNode sceneFileNode = rootNode.getFile(scenePath);
        FileNode meshFileNode = rootNode.getFile(meshPath);
        FileNode materialFileNode = rootNode.getFile(materialPath);
        DirectoryNode textureNode = rootNode.getDirectory(texturePath);
        
        if(sceneFileNode == null || meshFileNode == null || materialFileNode == null || textureNode == null) {
            OptionPane.showMessageDialog(String.format("No file exists at path '%s'",
                    sceneFileNode == null ? scenePath : meshFileNode == null ? meshPath : materialFileNode == null ? materialPath : texturePath));
            return;
        }
        
        FileChooser.showFileExportDialog(JFileChooser.DIRECTORIES_ONLY, directory -> {
            directory.mkdirs();
            SceneFile sceneFile = SceneParser.parseSceneFile(sceneFileNode);
            MeshFile meshFile = MeshReader.readMeshFile(meshFileNode);
            MaterialFile materialFile = MaterialParser.parseMaterialFile(materialFileNode);
            String name = sceneFileNode.getName().replace(".scene", "");
            FileOutputStream outputStream = new FileOutputStream(new File(directory, name + ".obj"));
            ObjWriter.write(MeshConverter.convertSceneFileToObj(sceneFile, meshFile, name + ".mtl"), outputStream);
            outputStream.close();
            outputStream = new FileOutputStream(new File(directory, name + ".mtl"));
            MtlWriter.write(MaterialConverter.convertMaterialFileToMtlList(materialFile), outputStream);
            outputStream.close();
            FileSystems.writeFileSystem(textureNode, new File(directory, textureNode.getName()));
        });
    }
    
    private GridBagConstraints constraints(int row, int col, int width, int height, double weightX, double weightY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = col;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        constraints.weightx = weightX;
        constraints.weighty = weightY;
        constraints.ipady = 10;
        constraints.insets.left = 5;
        return constraints;
    }
    
    private JPanel createInputPanel(String scenePath, String meshPath, String materialPath, String texturePath) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Labels
        formPanel.add(new JLabel("Scene File", JLabel.TRAILING), constraints(0, 0, 1, 1, 0, 1));
        formPanel.add(new JLabel("Mesh File", JLabel.TRAILING), constraints(1, 0, 1, 1, 0, 1));
        formPanel.add(new JLabel("Material File", JLabel.TRAILING), constraints(2, 0, 1, 1, 0, 1));
        formPanel.add(new JLabel("Texture Folder", JLabel.TRAILING), constraints(3, 0, 1, 1, 0, 1));
        
        // TODO output format option
        
        // Fields
        formPanel.add(scenePathField = new JTextField(scenePath, 25), constraints(0, 1, 1, 1, 1, 1));
        formPanel.add(meshPathField = new JTextField(meshPath, 25), constraints(1, 1, 1, 1, 1, 1));
        formPanel.add(materialPathField = new JTextField(materialPath, 25), constraints(2, 1, 1, 1, 1, 1));
        formPanel.add(texturePathField = new JTextField(texturePath, 25), constraints(3, 1, 1, 1, 1, 1));
        return formPanel;
    }
}
