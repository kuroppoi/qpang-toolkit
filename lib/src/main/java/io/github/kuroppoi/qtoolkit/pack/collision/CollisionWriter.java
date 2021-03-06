package io.github.kuroppoi.qtoolkit.pack.collision;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import io.github.kuroppoi.qtoolkit.DataBuffer;
import io.github.kuroppoi.qtoolkit.file.FileNode;
import io.github.kuroppoi.qtoolkit.pack.math.AxisAlignedBox;

public class CollisionWriter {
    
    public static void writeCollisionFile(CollisionFile collisionFile, OutputStream outputStream) throws IOException {
        outputStream.write(writeCollisionFile(collisionFile));
    }
    
    public static void writeCollisionFile(CollisionFile collisionFile, File output) throws IOException {
        Files.write(output.toPath(), writeCollisionFile(collisionFile));
    }
    
    public static void writeCollisionFile(CollisionFile collisionFile, FileNode output) {
        output.setBytes(writeCollisionFile(collisionFile));
    }
    
    public static byte[] writeCollisionFile(CollisionFile collisionFile) {
        return writeCollisionFile(generateBinarySpacePartitionTree(collisionFile));
    }
    
    private static byte[] writeCollisionFile(BinarySpacePartition root) {
        DataBuffer buffer = new DataBuffer(20 + calculateSize(root));
        writeCollisionFile(buffer, root);
        return buffer.readBytes(0, buffer.position());
    }
    
    private static void writeCollisionFile(DataBuffer buffer, BinarySpacePartition root) {
        buffer.writeInt(0);
        buffer.writeInt(1); // version, 1
        buffer.writeInt(4);
        buffer.writeInt(256);
        buffer.writeInt(0); // size in bytes minus header
        writeBinarySpacePartition(buffer, root);
        buffer.writeInt(16, buffer.position() - 20);
    }
    
    private static void writeBinarySpacePartition(DataBuffer buffer, BinarySpacePartition bsp) {
        buffer.writeShort(bsp.isLeaf() ? 1 : 0);
        buffer.writeShort(bsp.getCollisionCount());
        AxisAlignedBox bounds = bsp.getBounds();
        buffer.writeFloat(bounds.getMin().x);
        buffer.writeFloat(bounds.getMin().y);
        buffer.writeFloat(bounds.getMin().z);
        buffer.writeFloat(bounds.getMax().x);
        buffer.writeFloat(bounds.getMax().y);
        buffer.writeFloat(bounds.getMax().z);
        
        if(bsp.isLeaf()) {
            for(Collision collision : bsp.getCollisions()) {
                buffer.writeString(collision.getSceneName(), 64);
                buffer.writeInt(collision.getPolygonCount());
                
                for(Polygon polygon : collision.getPolygons()) {
                    buffer.writeInt(0);
                    writePolygon(buffer, polygon);
                }
            }
        } else {
            buffer.writeInt(bsp.getAxis().ordinal());
            Vector3f max = bsp.getLeftChild().getBounds().getMax();
            
            switch(bsp.getAxis()) {
                case X:
                    buffer.writeFloat(max.x);
                    break;
                case Y:
                    buffer.writeFloat(max.y);
                    break;
                case Z:
                    buffer.writeFloat(max.z);
                    break;
            }
            
            writeBinarySpacePartition(buffer, bsp.getLeftChild());
            writeBinarySpacePartition(buffer, bsp.getRightChild());
        }
    }
    
    private static void writePolygon(DataBuffer buffer, Polygon polygon) {
        buffer.writeFloat(polygon.getPointA().x);
        buffer.writeFloat(polygon.getPointA().y);
        buffer.writeFloat(polygon.getPointA().z);
        buffer.writeFloat(polygon.getPointB().x);
        buffer.writeFloat(polygon.getPointB().y);
        buffer.writeFloat(polygon.getPointB().z);
        buffer.writeFloat(polygon.getPointC().x);
        buffer.writeFloat(polygon.getPointC().y);
        buffer.writeFloat(polygon.getPointC().z);
        buffer.writeFloat(polygon.getNormal().x);
        buffer.writeFloat(polygon.getNormal().y);
        buffer.writeFloat(polygon.getNormal().z);
    }
    
    private static BinarySpacePartition generateBinarySpacePartitionTree(CollisionFile collisionFile) {
        return generateBinarySpacePartitionTree(collisionFile, 8);
    }
    
    private static BinarySpacePartition generateBinarySpacePartitionTree(CollisionFile collisionFile, int descendantCount) {
        AxisAlignedBox bounds = new AxisAlignedBox();
        
        for(Collision collision : collisionFile.getCollisions()) {
            for(Polygon polygon : collision.getPolygons()) {
                bounds.grow(polygon.getPointA());
                bounds.grow(polygon.getPointB());
                bounds.grow(polygon.getPointC());
            }
        }
        
        BinarySpacePartition root = new BinarySpacePartition(bounds);
        root.generateDescendants(descendantCount);
        List<BinarySpacePartition> leaves = root.getLeaves();
        
        for(Collision collision : collisionFile.getCollisions()) {
            for(BinarySpacePartition leaf : leaves) {
                List<Polygon> intersectingPolygons = new ArrayList<>();
                
                for(Polygon polygon : collision.getPolygons()) {
                    if(polygon.calculateBounds().collidesWith(leaf.getBounds())) {
                        intersectingPolygons.add(polygon);
                    }
                }
                
                if(!intersectingPolygons.isEmpty()) {
                    Collision copy = new Collision(collision.getSceneName());
                    leaf.addCollision(copy);
                    
                    for(Polygon polygon : intersectingPolygons) {
                        copy.addPolygon(polygon);
                    }
                }
            }
        }
        
        return root;
    }
    
    private static int calculateSize(BinarySpacePartition bsp) {
        int size = 28;
        
        if(bsp.isLeaf()) {
            for(Collision collision : bsp.getCollisions()) {
                size += 68 + (collision.getPolygonCount() * 52);
            }
        } else {
            size += 8 + calculateSize(bsp.getLeftChild()) + calculateSize(bsp.getRightChild());
        }
        
        return size;
    }
}
