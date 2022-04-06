package io.github.kuroppoi.qtoolkit.pack.mesh;

import static io.github.kuroppoi.qtoolkit.pack.mesh.VertexElementSemantic.VES_NORMAL;
import static io.github.kuroppoi.qtoolkit.pack.mesh.VertexElementSemantic.VES_POSITION;
import static io.github.kuroppoi.qtoolkit.pack.mesh.VertexElementSemantic.VES_TEXTURE_COORDINATES;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import io.github.kuroppoi.qtoolkit.shared.DataBuffer;
import io.github.kuroppoi.qtoolkit.shared.IOUtils;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;

public class MeshReader {
    
    public static MeshFile readMeshFile(InputStream inputStream) throws IOException {
        return readMeshFile(IOUtils.readAllBytes(inputStream));
    } 
    
    public static MeshFile readMeshFile(File file) throws IOException {
        return readMeshFile(Files.readAllBytes(file.toPath()));
    }
    
    public static MeshFile readMeshFile(FileNode file) throws IOException {
        return readMeshFile(file.getBytes());
    }
    
    public static MeshFile readMeshFile(byte[] bytes) throws IOException {
        return readMeshFile(new DataBuffer(bytes));
    }
    
    private static MeshFile readMeshFile(DataBuffer buffer) throws IOException {
        buffer.readBytes(20);
        MeshFile meshFile = new MeshFile();
        
        while(buffer.remaining() > 0) {
            meshFile.addMesh(readMesh(buffer));
        }
        
        return meshFile;
    }
    
    private static Mesh readMesh(DataBuffer buffer) {
        Mesh mesh = new Mesh(buffer.readString(128));
        buffer.readBytes(18);
        int subMeshCount = buffer.readShort();
        int keyFrameCount = 0;
        
        // AABB that seems to be used for occlusion culling.
        // We will just generate a new one on write.
        buffer.readBytes(24);
        
        if(buffer.readInt() > 0) {
            // TODO not entirely sure how these values work yet
            buffer.readBytes(8);
            mesh.setAnimationLength(buffer.readFloat());
            buffer.readFloat(); // Time in milliseconds per keyframe.
            keyFrameCount = (short)buffer.readInt() - 1;
        }
        
        for(int i = 0; i < subMeshCount; i++) {
            mesh.addSubMesh(readSubMesh(buffer, keyFrameCount));
        }
        
        return mesh;
    }
    
    private static SubMesh readSubMesh(DataBuffer buffer, int keyFrameCount) {
        SubMesh subMesh = new SubMesh(buffer.readString(64));
        VertexElementList elementList = new VertexElementList();
        buffer.readInt(); // corresponds to vertex length but does nothing
        int vertexLength = buffer.readInt();
        int elementCount = buffer.readInt();
        
        for(int i = 0; i < elementCount; i++) {
            buffer.readShort(); // source
            elementList.addElement(buffer.readShort(),
                    VertexElementType.fromId(buffer.readInt()),
                    VertexElementSemantic.fromId(buffer.readInt()),
                    (short)buffer.readInt());
        }
        
        subMesh.setVertexData(readVertexData(buffer, elementList, vertexLength));
        
        for(int i = 0; i < keyFrameCount; i++) {
            subMesh.addKeyFrame(readVertexData(buffer, elementList, vertexLength));
        }
        
        int indexCount = buffer.readInt();
        
        for(int i = 0; i < indexCount; i++) {
            subMesh.addIndex(buffer.readShort());
        }
        
        return subMesh;
    }
    
    private static VertexData readVertexData(DataBuffer buffer, VertexElementList elementList, int vertexLength) {
        VertexData vertexData = new VertexData();
        VertexElement positionElement = elementList.getElement(VES_POSITION);
        VertexElement normalElement = elementList.getElement(VES_NORMAL);
        VertexElement texCoordElement = elementList.getElement(VES_TEXTURE_COORDINATES);
        VertexElement lightMapTexCoordElement = elementList.getElement(VES_TEXTURE_COORDINATES, 1);
        int vertexCount = buffer.readInt();
        int offset = buffer.position();
        
        for(int i = 0; i < vertexCount; i++) {
            if(positionElement != null) {
                buffer.position(positionElement.getOffset() + offset);
                vertexData.addPosition(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
            
            if(normalElement != null) {
                buffer.position(normalElement.getOffset() + offset);
                vertexData.addNormal(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
            
            if(texCoordElement != null) {
                buffer.position(texCoordElement.getOffset() + offset);
                vertexData.addTexCoord(buffer.readFloat(), buffer.readFloat());
            }
            
            if(lightMapTexCoordElement != null) {
                buffer.position(lightMapTexCoordElement.getOffset() + offset);
                vertexData.addLightMapTexCoord(buffer.readFloat(), buffer.readFloat());
            }
            
            offset += vertexLength;
        }
        
        buffer.position(offset);
        return vertexData;
    }
}
