package io.github.kuroppoi.qtoolkit.pack.material;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class MaterialFile {
    
    private final List<Material> materials;
    
    public MaterialFile() {
        this.materials = new ArrayList<>();
    }
    
    public void addMaterial(Material material) {
        materials.add(material);
    }
    
    public void removeMaterial(Material material) {
        materials.remove(material);
    }
    
    public void removeMaterial(int index) {
        ListUtils.remove(materials, index);
    }
    
    public void removeMaterial(String name) {
        ListUtils.remove(materials, material -> name.equals(material.getName()));
    }
    
    public Material getMaterial(int index) {
        return ListUtils.get(materials, index);
    }
    
    public Material getMaterial(String name) {
        return ListUtils.get(materials, material -> name.equals(material.getName()));
    }
    
    public List<Material> getMaterials() {
        return materials;
    }
}
