package io.github.kuroppoi.qtoolkit.gui.component;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Utilities;

// TODO this entire class kinda sucks tbh
public class LineNumbers extends JComponent implements DocumentListener {
    
    private static final long serialVersionUID = 9113224471895839591L;
    private final JTextArea textArea;
    
    public LineNumbers(JTextArea textArea) {
        this.textArea = textArea;
        textArea.getDocument().addDocumentListener(this);
        updateDimensions();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setFont(textArea.getFont());
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics metrics = textArea.getFontMetrics(textArea.getFont());
        Insets insets = getInsets();
        int availableWidth = getWidth() - insets.left - insets.right;
        int start = textArea.viewToModel(new Point(0, -textArea.getY()));
        int end = textArea.viewToModel(new Point(0, -textArea.getY() + textArea.getHeight()));
        
        while(start <= end) {
            Element root = textArea.getDocument().getDefaultRootElement();
            int index = root.getElementIndex(start);
            
            try {
                Rectangle rectangle = textArea.modelToView(start);
                int y = rectangle.y + rectangle.height - metrics.getDescent() - textArea.getInsets().top + (textArea.getY() - getY());
                String lineNumber = "" + (index + 1);
                int x = availableWidth - 4 - metrics.stringWidth(lineNumber);
                g2d.drawString(lineNumber, x, y);
                start = Utilities.getRowEnd(textArea, start) + 1;
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        updateDimensions();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        updateDimensions();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        updateDimensions();
    }
    
    private void updateDimensions() {
        FontMetrics metrics = textArea.getFontMetrics(textArea.getFont());
        int lineCount = textArea.getLineCount();
        int width = metrics.charWidth('0') * String.valueOf(lineCount).length() + textArea.getInsets().left;
        int height = lineCount * metrics.getHeight();
        setSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));
        repaint();
    }
}
