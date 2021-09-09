package io.github.kuroppoi.qtoolkit.pack.scene;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import io.github.kuroppoi.qtoolkit.shared.IOUtils;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;

public class SceneParser {
    
    public static SceneFile parseSceneFile(Reader reader) throws IOException {
        return parseSceneFile(IOUtils.readAllLines(reader));
    }
    
    public static SceneFile parseSceneFile(InputStream inputStream) throws IOException {
        return parseSceneFile(new InputStreamReader(inputStream));
    }
    
    public static SceneFile parseSceneFile(File file) throws IOException {
        return parseSceneFile(Files.readAllLines(file.toPath()));
    }
    
    public static SceneFile parseSceneFile(FileNode file) throws IOException {
        return parseSceneFile(file.getLines());
    }
    
    public static SceneFile parseSceneFile(List<String> lines) {
        SceneFile sceneFile = new SceneFile();
        Iterator<String> iterator = lines.iterator();
        
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            String[] segments = line.split(" ", 2);
            
            if(segments.length == 2) {
                if(segments[0].equalsIgnoreCase("scene")) {
                    sceneFile.addScene(parseScene(segments[1], iterator));
                }
            }
        }
        
        return sceneFile;
    }
    
    private static Scene parseScene(String name, Iterator<String> iterator) {
        Scene scene = new Scene(name);
        
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            String[] segments = line.split(" ", 2);
            
            if(line.equals("}")) {
                break;
            } else if(segments.length > 0) {
                if(segments.length == 2) {
                    if(segments[0].equalsIgnoreCase("coord")) {
                        scene.setCoordType(CoordType.valueOf(segments[1].toUpperCase()));
                    } else if(segments[0].equalsIgnoreCase("pos")) {
                        scene.setPosition(parseVector3f(segments[1]));
                    } else if(segments[0].equalsIgnoreCase("rot")) {
                        scene.setRotation(parseQuaternionf(segments[1]).invert());
                    }
                }
                
                if(segments[0].equalsIgnoreCase("mesh_object")) {
                    scene.addMeshObject(parseMeshObject(segments.length == 2 ? segments[1] : null, iterator));
                } else if(segments[0].equals("collision_info")) {
                    parseDiscardNode(iterator);
                }
            }
        }
        
        return scene;
    }
    
    private static MeshObject parseMeshObject(String name, Iterator<String> iterator) {
        MeshObject meshObject = new MeshObject(name);
        
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            String[] segments = line.split(" ", 2);
            
            if(line.equals("}")) {
                break;
            } else if(segments.length > 0) {
                if(segments.length == 2) {
                    if(segments[0].equalsIgnoreCase("pos")) {
                        meshObject.setPosition(parseVector3f(segments[1]));
                    } else if(segments[0].equalsIgnoreCase("rot")) {
                        meshObject.setRotation(parseQuaternionf(segments[1]).invert());
                    }
                } else if(segments[0].equalsIgnoreCase("mesh")) {
                    meshObject.addMesh(parseMesh(iterator));
                } else if(segments[0].equals("animation")) {
                    parseDiscardNode(iterator);
                }
                
                if(segments[0].equalsIgnoreCase("mesh_object")) {
                    meshObject.addMeshObject(parseMeshObject(segments.length == 2 ? segments[1] : null, iterator));
                }
            }
        }
        
        return meshObject;
    }
    
    private static String parseMesh(Iterator<String> iterator) {
        String name = null;
        
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            String[] segments = line.split(" ", 2);
            
            if(line.equals("}")) {
                break;
            } else if(segments.length == 2 && segments[0].equalsIgnoreCase("name")) {
                name = segments[1];
            }
        }
        
        return name;
    }
    
    private static void parseDiscardNode(Iterator<String> iterator) {
        while(iterator.hasNext()) {
            String line = iterator.next().trim();
            
            if(line.equals("}")) {
                break;
            }
        }
    }
    
    private static Vector3f parseVector3f(String string) {
        String[] segments = string.split(" ");
        
        if(segments.length != 3) {
            return new Vector3f(0, 0, 0);
        }
        
        return new Vector3f(Float.parseFloat(segments[0]),
                Float.parseFloat(segments[1]),
                Float.parseFloat(segments[2]));
    }
    
    private static Quaternionf parseQuaternionf(String string) {
        String[] segments = string.split(" ");
        
        if(segments.length != 4) {
            return new Quaternionf(0, 0, 0, 1);
        }
        
        return new Quaternionf(Float.parseFloat(segments[0]),
                Float.parseFloat(segments[1]),
                Float.parseFloat(segments[2]),
                Float.parseFloat(segments[3]));
    }
}
