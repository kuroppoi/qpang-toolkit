package io.github.kuroppoi.qtoolkit.pack.mesh;

public enum VertexElementType {
    
    VET_FLOAT1(1, 4),
    VET_FLOAT2(2, 8),
    VET_FLOAT3(3, 12),
    VET_FLOAT4(4, 16),
    VET_COLOUR(1, 4),
    VET_SHORT1(1, 2),
    VET_SHORT2(2, 4),
    VET_SHORT3(3, 6),
    VET_SHORT4(4, 8),
    VET_UBYTE4(4, 4);
    
    private int count;
    private int size;
    
    private VertexElementType(int count, int size) {
        this.count = count;
        this.size = size;
    }
    
    public static VertexElementType fromId(int id) {
        VertexElementType[] values = values();
        return id >= 0 && id < values.length ? values[id] : null;
    }
    
    public int getCount() {
        return count;
    }
    
    public int getSize() {
        return size;
    }
}
