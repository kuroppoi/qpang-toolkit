package io.github.kuroppoi.qtoolkit.pack.material;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

import io.github.kuroppoi.qtoolkit.shared.IOUtils;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;

public class MaterialParser {
    
    public static MaterialFile parseMaterialFile(Reader reader) throws IOException {
        return parseMaterialFile(IOUtils.readAllLines(reader));
    }
    
    public static MaterialFile parseMaterialFile(InputStream inputStream) throws IOException {
        return parseMaterialFile(new InputStreamReader(inputStream));
    }
    
    public static MaterialFile parseMaterialFile(File file) throws IOException {
        return parseMaterialFile(Files.readAllLines(file.toPath()));
    }
    
    public static MaterialFile parseMaterialFile(FileNode file) throws IOException {
        return parseMaterialFile(file.getLines());
    }
    
    public static MaterialFile parseMaterialFile(List<String> lines) {
        MaterialFile materialFile = new MaterialFile();
        Iterator<String> iterator = lines.iterator();
        
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            String[] segments = line.split(" ", 2);
            
            if(segments.length == 2) {
                if(segments[0].equalsIgnoreCase("material")) {
                    materialFile.addMaterial(parseMaterial(segments[1], iterator));
                }
            }
        }
        
        return materialFile;
    }
    
    private static Material parseMaterial(String name, Iterator<String> iterator) {
        Material material = new Material(name);
        
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            String[] segments = line.split(" ", 2);
            
            if(line.equals("}")) {
                break;
            } else if(segments.length > 0) {
                if(segments[0].equalsIgnoreCase("technique")) {
                    material.addTechnique(parseTechnique(iterator));
                }
            }
        }
        
        return material;
    }
    
    private static Technique parseTechnique(Iterator<String> iterator) {
        Technique technique = new Technique();
        
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            String[] segments = line.split(" ", 2);
            
            if(line.equals("}")) {
                break;
            } else if(segments.length > 0) {
                if(segments[0].equalsIgnoreCase("pass")) {
                    technique.addPass(parsePass(iterator));
                }
            }
        }
        
        return technique;
    }
    
    private static Pass parsePass(Iterator<String> iterator) {
        Pass pass = new Pass();
        
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            String[] segments = line.split(" ", 2);
            
            if(line.equals("}")) {
                break;
            } else if(segments.length > 0) {
                if(segments[0].equalsIgnoreCase("texture")) {
                    pass.addTexture(parseTexture(iterator));
                } else if(segments[0].equals("vertex_shader_ref")) {
                    parseDiscardNode(iterator);
                }
            }
        }
        
        return pass;
    }
    
    private static Texture parseTexture(Iterator<String> iterator) {
        Texture texture = new Texture();
        
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            String[] segments = line.split(" ", 2);
            
            if(line.equals("}")) {
                break;
            } else if(segments.length == 2) {
                if(segments[0].equalsIgnoreCase("name")) {
                    texture.setName(segments[1]);
                } else if(segments[0].equalsIgnoreCase("coord_set")) {
                    texture.setCoord(Integer.parseInt(segments[1]));
                }
            }
        }
        
        return texture;
    }
    
    private static void parseDiscardNode(Iterator<String> iterator) {
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            
            if(line.equals("}")) {
                break;
            }
        }
    }
}
