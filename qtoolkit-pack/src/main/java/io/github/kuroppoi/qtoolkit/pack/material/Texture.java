package io.github.kuroppoi.qtoolkit.pack.material;

public class Texture {
    
    private String name;
    private int coord;
    
    public Texture() {
        this("null");
    }
    
    public Texture(String name) {
        this(name, 0);
    }
    
    public Texture(String name, int coord) {
        this.name = name;
        this.coord = coord;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setCoord(int coord) {
        this.coord = coord;
    }
    
    public int getCoord() {
        return coord;
    }
}
