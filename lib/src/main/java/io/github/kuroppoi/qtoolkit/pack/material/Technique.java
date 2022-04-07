package io.github.kuroppoi.qtoolkit.pack.material;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.utils.ListUtils;

public class Technique {
    
    private final List<Pass> passes = new ArrayList<>();
    
    public void addPass(Pass pass) {
        passes.add(pass);
    }
    
    public void removePass(Pass pass) {
        passes.remove(pass);
    }
    
    public void removePass(int index) {
        ListUtils.remove(passes, index);
    }
    
    public Pass getPass(int index) {
        return ListUtils.get(passes, index);
    }
    
    public int getPassCount() {
        return passes.size();
    }
    
    public List<Pass> getPasses() {
        return passes;
    }
}
