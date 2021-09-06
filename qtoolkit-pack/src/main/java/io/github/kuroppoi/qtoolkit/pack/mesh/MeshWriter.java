package io.github.kuroppoi.qtoolkit.pack.mesh;

import static io.github.kuroppoi.qtoolkit.pack.mesh.VertexElementSemantic.VES_DIFFUSE;
import static io.github.kuroppoi.qtoolkit.pack.mesh.VertexElementSemantic.VES_NORMAL;
import static io.github.kuroppoi.qtoolkit.pack.mesh.VertexElementSemantic.VES_POSITION;
import static io.github.kuroppoi.qtoolkit.pack.mesh.VertexElementSemantic.VES_TEXTURE_COORDINATES;
import static io.github.kuroppoi.qtoolkit.pack.mesh.VertexElementType.VET_COLOUR;
import static io.github.kuroppoi.qtoolkit.pack.mesh.VertexElementType.VET_FLOAT2;
import static io.github.kuroppoi.qtoolkit.pack.mesh.VertexElementType.VET_FLOAT3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.joml.Vector2f;
import org.joml.Vector3f;

import io.github.kuroppoi.qtoolkit.pack.math.AxisAlignedBox;
import io.github.kuroppoi.qtoolkit.shared.DataBuffer;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;

public class MeshWriter {
    
    public static void writeMeshFile(MeshFile meshFile, OutputStream outputStream) throws IOException {
        outputStream.write(writeMeshFile(meshFile));
    }
    
    public static void writeMeshFile(MeshFile meshFile, File output) throws IOException {
        writeMeshFile(meshFile, new FileOutputStream(output));
    }
    
    public static void writeMeshFile(MeshFile meshFile, FileNode output) {
        output.setBytes(writeMeshFile(meshFile));
    }
    
    public static byte[] writeMeshFile(MeshFile meshFile) {
        DataBuffer buffer = new DataBuffer();
        writeMeshFile(buffer, meshFile);
        return buffer.readBytes(0, buffer.position());
    }
    
    private static void writeMeshFile(DataBuffer buffer, MeshFile meshFile) {
        buffer.writeInt(0);
        buffer.writeInt(0x20000); // version
        buffer.writeInt(4);
        buffer.writeInt(meshFile.getMeshCount());
        buffer.writeInt(0); // length in bytes minus header
        
        for(Mesh mesh : meshFile.getMeshes()) {
            writeMesh(buffer, mesh);
        }
        
        buffer.writeInt(16, buffer.position() - 20);
    }
    
    private static void writeMesh(DataBuffer buffer, Mesh mesh) {
        int keyFrameCount = mesh.getKeyFrameCount();
        buffer.writeString(mesh.getName(), 128);
        buffer.writeInt(4); // 1, 2, or 4
        buffer.writeInt(1);
        int lengthOffset = buffer.position();
        buffer.writeInt(0); // length in bytes
        buffer.writeInt(1);
        // Seems to determine how faces are formed.
        // 0 = typical; every 3 indices is a face.
        // 1 = qpang default, I still do not understand how it handles the index data.
        // 2 through 5 = I do not even know. Doesn't even appear to use provided index data.
        // TODO switch to 0 because it is so much more convenient.
        buffer.writeShort(1);
        buffer.writeShort(mesh.getSubMeshCount());
        AxisAlignedBox bounds = mesh.calculateBounds();
        buffer.writeFloat(bounds.getMin().x);
        buffer.writeFloat(bounds.getMin().y);
        buffer.writeFloat(bounds.getMin().z);
        buffer.writeFloat(bounds.getMax().x);
        buffer.writeFloat(bounds.getMax().y);
        buffer.writeFloat(bounds.getMax().z);
        buffer.writeInt(keyFrameCount > 0 ? 64 : 0);
        
        if(keyFrameCount > 0) {
            buffer.writeInt(0);
            buffer.writeFloat(mesh.getAnimationLength());
            buffer.writeFloat(mesh.getAnimationLength());
            buffer.writeFloat(mesh.getAnimationSpeed());
            buffer.writeInt(keyFrameCount + 1);
        }
        
        for(SubMesh subMesh : mesh.getSubMeshes()) {
            writeSubMesh(buffer, subMesh);
        }
        
        buffer.writeInt(lengthOffset, buffer.position() - lengthOffset - 8);
    }
    
    private static void writeSubMesh(DataBuffer buffer, SubMesh subMesh) {
        buffer.writeString(subMesh.getMaterialName(), 64);
        VertexData vertexData = subMesh.getVertexData();
        VertexElementList elementList = createVertexElementList(vertexData);
        buffer.writeInt(0);
        buffer.writeInt(elementList.getVertexLength());
        buffer.writeInt(elementList.getElementCount());
        
        for(VertexElement element : elementList.getElements()) {
            buffer.writeShort(0); // source
            buffer.writeShort(element.getOffset());
            buffer.writeInt(element.getType().ordinal());
            buffer.writeInt(element.getSemantic().ordinal() + 1);
            buffer.writeInt(element.getIndex());
        }
        
        writeVertexData(buffer, vertexData, elementList);
        
        for(VertexData keyFrame : subMesh.getKeyFrames()) {
            writeVertexData(buffer, keyFrame, elementList);
        }
        
        buffer.writeInt(subMesh.getIndexCount());
        
        for(int index : subMesh.getIndices()) {
            buffer.writeShort(index);
        }
    }
    
    private static void writeVertexData(DataBuffer buffer, VertexData vertexData, VertexElementList elementList) {
        VertexElement positionElement = elementList.getElement(VES_POSITION);
        VertexElement normalElement = elementList.getElement(VES_NORMAL);
        VertexElement texCoordElement = elementList.getElement(VES_TEXTURE_COORDINATES);
        VertexElement lightMapTexCoordElement = elementList.getElement(VES_TEXTURE_COORDINATES, 1);
        int vertexCount = vertexData.getVertexCount();
        int vertexLength = elementList.getVertexLength();
        buffer.writeInt(vertexCount);
        int offset = buffer.position();
        
        for(int i = 0; i < vertexCount; i++) {
            if(positionElement != null) {
                Vector3f position = vertexData.getPosition(i);
                buffer.position(offset + positionElement.getOffset());
                buffer.writeFloat(position.x);
                buffer.writeFloat(position.y);
                buffer.writeFloat(position.z);
            }
            
            if(normalElement != null) {
                Vector3f normal = vertexData.getNormal(i);
                buffer.position(offset + normalElement.getOffset());
                buffer.writeFloat(normal.x);
                buffer.writeFloat(normal.y);
                buffer.writeFloat(normal.z);
            }
            
            if(texCoordElement != null) {
                Vector2f texCoord = vertexData.getTexCoord(i);
                buffer.position(offset + texCoordElement.getOffset());
                buffer.writeFloat(texCoord.x);
                buffer.writeFloat(texCoord.y);
            }
            
            if(lightMapTexCoordElement != null) {
                Vector2f lightMapTexCoord = vertexData.getLightMapTexCoord(i);
                buffer.position(offset + lightMapTexCoordElement.getOffset());
                buffer.writeFloat(lightMapTexCoord.x);
                buffer.writeFloat(lightMapTexCoord.y);
            }
            
            // Obligatory diffuse element
            buffer.writeInt(-1);
            offset += vertexLength;
        }
        
        buffer.position(offset);
    }
    
    private static VertexElementList createVertexElementList(VertexData vertexData) {
        VertexElementList elementList = new VertexElementList();
        int offset = 0;
        
        if(vertexData.hasPositions()) {
            elementList.addElement(offset, VET_FLOAT3, VES_POSITION, 0);
            offset += VET_FLOAT3.getSize();
        }
        
        if(vertexData.hasNormals()) {
            elementList.addElement(offset, VET_FLOAT3, VES_NORMAL, 0);
            offset += VET_FLOAT3.getSize();
        }
        
        if(vertexData.hasTexCoords()) {
            elementList.addElement(offset, VET_FLOAT2, VES_TEXTURE_COORDINATES, 0);
            offset += VET_FLOAT2.getSize();
        }
        
        if(vertexData.hasLightMapTexCoords()) {
            elementList.addElement(offset, VET_FLOAT2, VES_TEXTURE_COORDINATES, 1);
            offset += VET_FLOAT2.getSize();
        }
        
        // Obligatory diffuse element
        elementList.addElement(offset, VET_COLOUR, VES_DIFFUSE, 0);
        
        return elementList;
    }
}
