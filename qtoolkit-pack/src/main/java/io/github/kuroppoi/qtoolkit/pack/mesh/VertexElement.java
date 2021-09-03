package io.github.kuroppoi.qtoolkit.pack.mesh;

public class VertexElement {
    
    private final int offset;
    private final VertexElementType type;
    private final VertexElementSemantic semantic;
    private final int index;
    
    public VertexElement(int offset,
            VertexElementType type,
            VertexElementSemantic semantic,
            int index) {
        this.offset = offset;
        this.type = type;
        this.semantic = semantic;
        this.index = index;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public VertexElementType getType() {
        return type;
    }
    
    public VertexElementSemantic getSemantic() {
        return semantic;
    }
    
    public int getIndex() {
        return index;
    }
}
