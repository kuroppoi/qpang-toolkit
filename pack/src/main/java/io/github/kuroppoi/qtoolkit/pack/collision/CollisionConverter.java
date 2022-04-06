package io.github.kuroppoi.qtoolkit.pack.collision;

import java.util.Arrays;
import java.util.Map;

import org.joml.Vector3f;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjSplitting;
import de.javagl.obj.ObjUtils;
import de.javagl.obj.Objs;

public class CollisionConverter {
    
    public static Obj convertCollisionFileToObj(CollisionFile collisionFile) {
        Obj obj = Objs.create();
        
        for(Collision collision : collisionFile.getCollisions()) {
            obj.setActiveGroupNames(Arrays.asList(collision.getSceneName()));
            convertCollisionToObj(collision, obj);
        }
        
        return obj;
    }
    
    public static Obj convertCollisionToObj(Collision collision) {
        return convertCollisionToObj(collision, Objs.create());
    }
    
    private static Obj convertCollisionToObj(Collision collision, Obj obj) {
        int indexOffset = obj.getNumVertices();
        int index = indexOffset;
        
        for(Polygon polygon : collision.getPolygons()) {
            Vector3f pointA = polygon.getPointA();
            Vector3f pointB = polygon.getPointB();
            Vector3f pointC = polygon.getPointC();
            obj.addVertex(pointA.x, pointA.y, -pointA.z);
            obj.addVertex(pointB.x, pointB.y, -pointB.z);
            obj.addVertex(pointC.x, pointC.y, -pointC.z);
            obj.addFace(index, index + 1, index + 2);
            index += 3;
        }
        
        return obj;
    }
    
    public static CollisionFile convertObjToCollisionFile(Obj obj) {
        CollisionFile collisionFile = new CollisionFile();
        Map<String, Obj> groups = ObjSplitting.splitByGroups(obj);
        
        groups.forEach((name, obj2) -> {
            collisionFile.addCollision(convertObjToCollision(obj2, name));
        });
        
        return collisionFile;
    }
    
    public static Collision convertObjToCollision(Obj obj, String sceneName) {
        Collision collision = new Collision(sceneName);
        obj = ObjUtils.convertToRenderable(obj);
        int[] indices = ObjData.getFaceVertexIndicesArray(obj, 3);
        float[] vertices = ObjData.getVerticesArray(obj);
        
        for(int i = 0; i < indices.length / 3; i++) {
            int a = indices[i * 3];
            int b = indices[i * 3 + 1];
            int c = indices[i * 3 + 2];
            Vector3f pointA = new Vector3f(vertices[a * 3], vertices[a * 3 + 1], -vertices[a * 3 + 2]);
            Vector3f pointB = new Vector3f(vertices[b * 3], vertices[b * 3 + 1], -vertices[b * 3 + 2]);
            Vector3f pointC = new Vector3f(vertices[c * 3], vertices[c * 3 + 1], -vertices[c * 3 + 2]);
            collision.addPolygon(new Polygon(pointA, pointB, pointC));
        }
        
        return collision;
    }
}
