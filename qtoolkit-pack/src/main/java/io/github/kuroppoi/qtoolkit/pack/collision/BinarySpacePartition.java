package io.github.kuroppoi.qtoolkit.pack.collision;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.pack.math.Axis;
import io.github.kuroppoi.qtoolkit.pack.math.AxisAlignedBox;

public class BinarySpacePartition {
    
    private final List<Collision> collisions;
    private AxisAlignedBox bounds;
    private Axis axis;
    private BinarySpacePartition leftChild;
    private BinarySpacePartition rightChild;
    
    public BinarySpacePartition(AxisAlignedBox bounds) {
        this(bounds, Axis.Z);
    }
    
    public BinarySpacePartition(AxisAlignedBox bounds, Axis axis) {
        this.collisions = new ArrayList<>();
        this.bounds = bounds;
        this.axis = axis;
    }
    
    public void generateDescendants(int amount) {
        if(isLeaf() && amount-- > 0) {
            Axis next = axis == Axis.X ? Axis.Y : axis == Axis.Y ? Axis.Z : Axis.X;
            AxisAlignedBox[] splitBounds = bounds.split(axis);
            setLeftChild(new BinarySpacePartition(splitBounds[0], next));
            setRightChild(new BinarySpacePartition(splitBounds[1], next));
            leftChild.generateDescendants(amount);
            rightChild.generateDescendants(amount);
        }
    }
    
    public boolean isLeaf() {
        return leftChild == null && rightChild == null;
    }
    
    public List<BinarySpacePartition> getLeaves() {
        List<BinarySpacePartition> leaves = new ArrayList<>();
        
        if(isLeaf()) {
            leaves.add(this);
        } else {
            leaves.addAll(leftChild.getLeaves());
            leaves.addAll(rightChild.getLeaves());
        }
        
        return leaves;
    }
    
    public void addCollision(Collision collision) {
        collisions.add(collision);
    }
    
    public int getCollisionCount() {
        return collisions.size();
    }
    
    public List<Collision> getCollisions() {
        return collisions;
    }
    
    public void setBounds(AxisAlignedBox bounds) {
        this.bounds = bounds;
    }
    
    public AxisAlignedBox getBounds() {
        return bounds;
    }
    
    public void setAxis(Axis axis) {
        this.axis = axis;
    }
    
    public Axis getAxis() {
        return axis;
    }
    
    public void setLeftChild(BinarySpacePartition leftChild) {
        this.leftChild = leftChild;
    }
    
    public BinarySpacePartition getLeftChild() {
        return leftChild;
    }
    
    public void setRightChild(BinarySpacePartition rightChild) {
        this.rightChild = rightChild;
    }
    
    public BinarySpacePartition getRightChild() {
        return rightChild;
    }
}
