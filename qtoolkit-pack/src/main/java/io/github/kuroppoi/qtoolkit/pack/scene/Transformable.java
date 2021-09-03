package io.github.kuroppoi.qtoolkit.pack.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Transformable {
    
    protected Vector3f position = new Vector3f(0, 0, 0);
    protected Quaternionf rotation = new Quaternionf(0, 0, 0, 1);
    
    public Matrix4f getTransformation() {
        Matrix4f transformation = new Matrix4f();
        transformation.translate(position);
        transformation.rotate(rotation);
        return transformation.normalize3x3();
    }
    
    public void setPosition(float x, float y, float z) {
        setPosition(new Vector3f(x, y, z));
    }
    
    public void setPosition(Vector3f position) {
        this.position = position;
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public void setRotation(float x, float y, float z, float w) {
        setRotation(new Quaternionf(x, y, z, w));
    }
    
    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }
    
    public Quaternionf getRotation() {
        return rotation;
    }
}
