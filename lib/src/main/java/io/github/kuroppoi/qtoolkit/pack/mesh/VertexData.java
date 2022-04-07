package io.github.kuroppoi.qtoolkit.pack.mesh;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import io.github.kuroppoi.qtoolkit.utils.ListUtils;

public class VertexData {

    private final List<Vector3f> positions = new ArrayList<>();
    private final List<Vector3f> normals = new ArrayList<>();
    private final List<Vector2f> texCoords = new ArrayList<>();
    private final List<Vector2f> lightMapTexCoords = new ArrayList<>();
    
    public void addPosition(float x, float y, float z) {
        addPosition(new Vector3f(x, y, z));
    }
    
    public void addPosition(Vector3f position) {
        positions.add(position);
    }
    
    public void removePosition(int index) {
        ListUtils.remove(positions, index);
    }
    
    public void removePosition(Vector3f position) {
        positions.remove(position);
    }
    
    public void clearPositions() {
        positions.clear();
    }
    
    public boolean hasPositions() {
        return !positions.isEmpty();
    }
    
    public int getVertexCount() {
        return positions.size();
    }
    
    public Vector3f getPosition(int index) {
        return ListUtils.get(positions, index);
    }
    
    public List<Vector3f> getPositions() {
        return positions;
    }
    
    public void addNormal(float x, float y, float z) {
        addNormal(new Vector3f(x, y, z));
    }
    
    public void addNormal(Vector3f normal) {
        normals.add(normal);
    }
    
    public void removeNormal(int index) {
        ListUtils.remove(normals, index);
    }
    
    public void removeNormal(Vector3f normal) {
        normals.remove(normal);
    }
    
    public void clearNormals() {
        normals.clear();
    }
    
    public boolean hasNormals() {
        return !normals.isEmpty();
    }
    
    public Vector3f getNormal(int index) {
        return ListUtils.get(normals, index);
    }
    
    public List<Vector3f> getNormals() {
        return normals;
    }
    
    public void addTexCoord(float x, float y) {
        addTexCoord(new Vector2f(x, y));
    }
    
    public void addTexCoord(Vector2f texCoord) {
        texCoords.add(texCoord);
    }
    
    public void removeTexCoord(int index) {
        ListUtils.remove(texCoords, index);
    }
    
    public void removeTexCoord(Vector2f texCoord) {
        texCoords.remove(texCoord);
    }
    
    public void clearTexCoords() {
        texCoords.clear();
    }
    
    public boolean hasTexCoords() {
        return !texCoords.isEmpty();
    }
    
    public Vector2f getTexCoord(int index) {
        return ListUtils.get(texCoords, index);
    }
    
    public List<Vector2f> getTexCoords() {
        return texCoords;
    }
    
    public void addLightMapTexCoord(float x, float y) {
        addLightMapTexCoord(new Vector2f(x, y));
    }
    
    public void addLightMapTexCoord(Vector2f lightMapTexCoord) {
        lightMapTexCoords.add(lightMapTexCoord);
    }
    
    public void removeLightMapTexCoord(int index) {
        ListUtils.remove(lightMapTexCoords, index);
    }
    
    public void removeLightMapTexCoord(Vector2f lightMapTexCoord) {
        lightMapTexCoords.remove(lightMapTexCoord);
    }
    
    public void clearLightMapTexCoords() {
        lightMapTexCoords.clear();
    }
    
    public boolean hasLightMapTexCoords() {
        return !lightMapTexCoords.isEmpty();
    }
    
    public Vector2f getLightMapTexCoord(int index) {
        return ListUtils.get(lightMapTexCoords, index);
    }
    
    public List<Vector2f> getLightMapTexCoords() {
        return lightMapTexCoords;
    }
}
