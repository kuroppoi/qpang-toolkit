package io.github.kuroppoi.qtoolkit.pack.material;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class Technique {
    
    private final List<Pass> passes;
    
    public Technique() {
        this.passes = new ArrayList<>();
    }
    
    public void addPass(Pass pass) {
        passes.add(pass);
    }
    
    public void removePass(Pass pass) {
        passes.remove(pass);
    }
    
    public void removePass(int index) {
        ListUtils.remove(passes, index);
    }
    
    public List<Pass> getPasses() {
        return passes;
    }
}
