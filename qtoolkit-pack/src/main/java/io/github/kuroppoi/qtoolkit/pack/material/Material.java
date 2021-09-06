package io.github.kuroppoi.qtoolkit.pack.material;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class Material {
    
    private String name;
    private final List<Technique> techniques;
    
    public Material(String name) {
        this.name = name;
        this.techniques = new ArrayList<>();
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void addTechnique(Technique technique) {
        techniques.add(technique);
    }
    
    public void removeTechnique(Technique technique) {
        techniques.remove(technique);
    }
    
    public void removeTechnique(int index) {
        ListUtils.remove(techniques, index);
    }
    
    public Technique getTechnique(int index) {
        return ListUtils.get(techniques, index);
    }
    
    public int getTechniqueCount() {
        return techniques.size();
    }
    
    public List<Technique> getTechniques() {
        return techniques;
    }
}
