package io.github.kuroppoi.qtoolkit.pack.collision;

import java.util.Objects;

import org.joml.GeometryUtils;
import org.joml.Vector3f;

import io.github.kuroppoi.qtoolkit.pack.math.AxisAlignedBox;

public class Polygon {
    
    private Vector3f pointA;
    private Vector3f pointB;
    private Vector3f pointC;
    private Vector3f normal;
    
    public Polygon(Vector3f pointA, Vector3f pointB, Vector3f pointC) {
        this(pointA, pointB, pointC, new Vector3f());
        GeometryUtils.normal(pointC, pointB, pointA, normal);
    }
    
    public Polygon(Vector3f pointA, Vector3f pointB, Vector3f pointC, Vector3f normal) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
        this.normal = normal;
    }
    
    @Override
    public boolean equals(Object object) {
        if(object == this) {
            return true;
        } else if(!(object instanceof Polygon)) {
            return false;
        }
        
        Polygon polygon = (Polygon)object;
        return polygon.getPointA().equals(pointA) &&
               polygon.getPointB().equals(pointB) &&
               polygon.getPointC().equals(pointC) &&
               polygon.getNormal().equals(normal);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pointA, pointB, pointC, normal);
    }
    
    public AxisAlignedBox calculateBounds() {
        AxisAlignedBox bounds = new AxisAlignedBox(pointA, pointA);
        bounds.grow(pointB);
        bounds.grow(pointC);
        return bounds;
    }
    
    public void setPointA(Vector3f pointA) {
        this.pointA = pointA;
    }
    
    public Vector3f getPointA() {
        return pointA;
    }
    
    public void setPointB(Vector3f pointB) {
        this.pointB = pointB;
    }
    
    public Vector3f getPointB() {
        return pointB;
    }
    
    public void setPointC(Vector3f pointC) {
        this.pointC = pointC;
    }
    
    public Vector3f getPointC() {
        return pointC;
    }
    
    public void setNormal(Vector3f normal) {
        this.normal.set(normal);
    }
    
    public Vector3f getNormal() {
        return normal;
    }
}
