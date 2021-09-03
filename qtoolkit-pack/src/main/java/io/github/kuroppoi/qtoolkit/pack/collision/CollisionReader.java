package io.github.kuroppoi.qtoolkit.pack.collision;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.joml.Vector3f;

import io.github.kuroppoi.qtoolkit.shared.DataBuffer;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;

public class CollisionReader {
    
    public static CollisionFile readCollisionFile(byte[] bytes) {
        return readCollisionFile(new DataBuffer(bytes));
    }
    
    public static CollisionFile readCollisionFile(FileNode file) {
        return readCollisionFile(file.getBytes());
    }
    
    public static CollisionFile readCollisionFile(File file) throws IOException {
        return readCollisionFile(Files.readAllBytes(file.toPath()));
    }
    
    private static CollisionFile readCollisionFile(DataBuffer buffer) {
        CollisionFile collisionFile = new CollisionFile();
        buffer.readInt();
        int version = buffer.readInt();
        buffer.readBytes(8);
        
        if(version == 1) { // BSP collisions
            buffer.readInt(); // size in bytes minus header
            readBinarySpacePartition(buffer, collisionFile);
        } else { // Legacy collisions (Take q05_sweety, for example)
            int collisionCount = buffer.readInt();
            
            for(int i = 0; i < collisionCount; i++) {
                collisionFile.addCollision(readCollision(buffer));
            }
        }
                
        return collisionFile;
    }
    
    private static Collision readCollision(DataBuffer buffer) {
        Collision collision = new Collision(buffer.readString(156));
        int polygonCount = buffer.readShort();
        buffer.readShort();
        
        for(int i = 0; i < polygonCount; i++) {
            collision.addPolygon(readPolygon(buffer));
        }
        
        return collision;
    }
    
    private static void readBinarySpacePartition(DataBuffer buffer, CollisionFile collisionFile) {
        boolean isLeaf = buffer.readShort() != 0;
        int collisionCount = buffer.readShort();
        buffer.readBytes(24); // Bounding box, but we only really need the actual collision data.
        
        if(isLeaf) {
            for(int i = 0; i < collisionCount; i++) {
                String sceneName = buffer.readString(64);
                
                // The same collision might be divided across multiple space partitions.
                // As such, we have to take care not to add duplicates.
                Collision collision = collisionFile.getCollision(sceneName);
                
                if(collision == null) {
                    collision = new Collision(sceneName);
                    collisionFile.addCollision(collision);
                }
                
                int polygonCount = buffer.readInt();
                
                for(int j = 0; j < polygonCount; j++) {
                    buffer.readInt(); // Seems to do nothing
                    collision.addPolygon(readPolygon(buffer));
                }
            }
        } else {
            buffer.readInt(); // axis
            
            // Float value which is equal to max [axis] of the AABB
            // of the left child of this BSP.
            buffer.readFloat();
            readBinarySpacePartition(buffer, collisionFile); // left child
            readBinarySpacePartition(buffer, collisionFile); // right child
        }
    }
    
    private static Polygon readPolygon(DataBuffer buffer) {
        return new Polygon(new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()), 
                new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()),
                new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()),
                new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()));
    }
}
