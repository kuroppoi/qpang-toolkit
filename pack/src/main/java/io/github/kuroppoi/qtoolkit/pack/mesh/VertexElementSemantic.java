package io.github.kuroppoi.qtoolkit.pack.mesh;

public enum VertexElementSemantic {

    VES_POSITION,
    VES_BLEND_WEIGHTS,
    VES_BLEND_INDICES,
    VES_NORMAL,
    VES_DIFFUSE,
    VES_SPECULAR,
    VES_TEXTURE_COORDINATES,
    VES_BINORMAL,
    VES_TANGENT;
    
    public static VertexElementSemantic fromId(int id) {
        VertexElementSemantic[] values = values();
        return id > 0 && id <= values.length ? values[id - 1] : null;
    }
}
