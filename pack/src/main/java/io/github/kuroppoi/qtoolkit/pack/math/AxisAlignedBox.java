package io.github.kuroppoi.qtoolkit.pack.math;

import org.joml.Vector3f;

public class AxisAlignedBox {
    
    private Vector3f min;
    private Vector3f max;
    
    public AxisAlignedBox() {
        this(0);
    }
    
    public AxisAlignedBox(float size) {
        this(size, size, size);
    }
    
    public AxisAlignedBox(float sizeX, float sizeY, float sizeZ) {
        this(-(sizeX / 2), -(sizeY / 2), -(sizeZ / 2), sizeX / 2, sizeY / 2, sizeZ / 2);
    }
    
    public AxisAlignedBox(float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ) {
        this(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
    }
    
    public AxisAlignedBox(Vector3f min, Vector3f max) {
        this.min = new Vector3f(min);
        this.max = new Vector3f(max);
    }
    
    public boolean collidesWith(AxisAlignedBox other) {
        return max.x >= other.getMin().x && min.x <= other.getMax().x &&
               max.y >= other.getMin().y && min.y <= other.getMax().y &&
               max.z >= other.getMin().z && min.z <= other.getMax().z;
    }
    
    public void grow(Vector3f point) {
        grow(point.x(), point.y(), point.z());
    }
    
    public void grow(float x, float y, float z) {
        min.x = x < min.x ? x : min.x;
        min.y = y < min.y ? y : min.y;
        min.z = z < min.z ? z : min.z;
        max.x = x > max.x ? x : max.x;
        max.y = y > max.y ? y : max.y;
        max.z = z > max.z ? z : max.z;
    }
    
    public AxisAlignedBox[] split(Axis axis) {
        Vector3f maxA = new Vector3f(max);
        Vector3f minB = new Vector3f(min);
        
        switch(axis) {
            case X:
                maxA.x = (min.x + max.x) / 2.0F;
                minB.x = maxA.x;
                break;
            case Y:
                maxA.y = (min.y + max.y) / 2.0F;
                minB.y = maxA.y;
                break;
            case Z:
                maxA.z = (min.z + max.z) / 2.0F;
                minB.z = maxA.z;
                break;
        }
        
        return new AxisAlignedBox[] {
            new AxisAlignedBox(min, maxA),
            new AxisAlignedBox(minB, max)
        };
    }
    
    public Vector3f getMin() {
        return min;
    }
    
    public Vector3f getMax() {
        return max;
    }
}
