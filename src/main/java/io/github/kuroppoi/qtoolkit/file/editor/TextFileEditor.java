package io.github.kuroppoi.qtoolkit.file.editor;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.StandardCharsets;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import io.github.kuroppoi.qtoolkit.component.LineNumbers;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;
import io.github.kuroppoi.qtoolkit.utils.ActionHelper;

public class TextFileEditor extends FileEditor implements DocumentListener, UndoableEditListener {
    
    private final FileNode file;
    private final Runnable changeListener;
    private final JScrollPane scrollPane;
    private final JTextArea textArea;
    
    public TextFileEditor(FileNode file, Runnable changeListener) {
        this.file = file;
        this.changeListener = changeListener;
        
        // Text area
        textArea = new JTextArea(new String(file.getBytes(), StandardCharsets.UTF_8));
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setTabSize(4);
        textArea.getDocument().addDocumentListener(this);
        textArea.getDocument().addUndoableEditListener(this);
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if(SwingUtilities.isRightMouseButton(event)) {
                    JPopupMenu menu = new JPopupMenu();
                    menu.add(ActionHelper.createAction("Copy (Ctrl + C)", textArea::copy));
                    menu.add(ActionHelper.createAction("Paste (Ctrl + V)", textArea::paste));
                    menu.show(textArea, event.getX(), event.getY());
                }
            }
        });
        
        // Scroll pane
        scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(new LineNumbers(textArea));
        scrollPane.setBorder(null);
    }
    
    @Override
    public void save() {
        file.setBytes(textArea.getText().getBytes(StandardCharsets.UTF_8));
        hasUnsavedChanges = false;
    }
    
    @Override
    public JComponent getComponent() {
        return scrollPane;
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        hasUnsavedChanges = true;
        changeListener.run();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        hasUnsavedChanges = true;
        changeListener.run();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        hasUnsavedChanges = true;
        changeListener.run();
    }
    
    @Override
    public void undoableEditHappened(UndoableEditEvent event) {
        undoManager.addEdit(event.getEdit());
        changeListener.run();
    }
}
