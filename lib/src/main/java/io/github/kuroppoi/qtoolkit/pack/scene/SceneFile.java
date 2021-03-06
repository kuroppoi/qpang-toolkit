package io.github.kuroppoi.qtoolkit.pack.scene;

import java.util.ArrayList;
import java.util.List;

import io.github.kuroppoi.qtoolkit.utils.ListUtils;

public class SceneFile {
    
    private List<Scene> scenes = new ArrayList<>();
    
    public void addScene(Scene scene) {
        scenes.add(scene);
    }
    
    public void removeScene(Scene scene) {
        scenes.remove(scene);
    }
    
    public void removeScene(int index) {
        ListUtils.remove(scenes, index);
    }
    
    public Scene getScene(int index) {
        return ListUtils.get(scenes, index);
    }
    
    public Scene getScene(String name) {
        return ListUtils.get(scenes, scene -> name.equals(scene.getName()));
    }
    
    public int getSceneCount() {
        return scenes.size();
    }
    
    public List<Scene> getScenes() {
        return scenes;
    }
}
