package io.github.kuroppoi.qtoolkit.pack.mesh;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.utils.ListUtils;

public class VertexElementList {
    
    private List<VertexElement> elements = new ArrayList<>();
    
    public void addElement(int byteOffset, 
            VertexElementType type,
            VertexElementSemantic semantic, 
            int index) {
        addElement(new VertexElement(byteOffset, type, semantic, index));
    }
    
    public void addElement(VertexElement element) {
        elements.add(element);
    }
    
    public void removeElement(VertexElement element) {
        elements.remove(element);
    }
    
    public void removeElementAt(int index) {
        ListUtils.remove(elements, index);
    }
    
    public void removeElement(VertexElementSemantic semantic) {
        removeElement(semantic, 0);
    }
    
    public void removeElement(VertexElementSemantic semantic, int index) {
        ListUtils.remove(elements, element -> element.getSemantic() == semantic && element.getIndex() == index);
    }
    
    public VertexElement getElementAt(int index) {
        return ListUtils.get(elements, index);
    }
    
    public VertexElement getElement(VertexElementSemantic semantic) {
        return getElement(semantic, 0);
    }
    
    public VertexElement getElement(VertexElementSemantic semantic, int index) {
        return ListUtils.get(elements, element -> element.getSemantic() == semantic && element.getIndex() == index);
    }
    
    public void clearElements() {
        elements.clear();
    }
    
    public int getElementCount() {
        return elements.size();
    }
    
    public List<VertexElement> getElements() {
        return elements;
    }
    
    public int getVertexLength() {
        int vertexLength = 0;
        
        for(VertexElement element : elements) {
            vertexLength += element.getType().getSize();
        }
        
        return vertexLength;
    }
}
