package io.github.kuroppoi.qtoolkit.pack.material;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.ListUtils;

public class Pass {
    
    private final List<Texture> textures;
    
    public Pass() {
        this.textures = new ArrayList<>();
    }
    
    public void addTexture(Texture texture) {
        textures.add(texture);
    }
    
    public void removeTexture(Texture texture) {
        textures.remove(texture);
    }
    
    public void removeTexture(int index) {
        ListUtils.remove(textures, index);
    }
    
    public List<Texture> getTextures() {
        return textures;
    }
}
