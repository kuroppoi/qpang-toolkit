package io.github.kuroppoi.qtoolkit.pack.material;

import java.util.ArrayList;
import java.util.List;

import de.javagl.obj.Mtl;
import de.javagl.obj.Mtls;

public class MaterialConverter {
    
    public static List<Mtl> convertMaterialFileToMtlList(MaterialFile materialFile) {
        List<Mtl> mtlList = new ArrayList<>();
        
        for(Material material : materialFile.getMaterials()) {
            mtlList.add(convertMaterialToMtl(material));
        }
        
        return mtlList;
    }
    
    public static Mtl convertMaterialToMtl(Material material) {
        Mtl mtl = Mtls.create(material.getName());
        mtl.setKa(1, 1, 1);
        mtl.setKd(1, 1, 1);
        
        for(Technique technique : material.getTechniques()) {
            for(Pass pass : technique.getPasses()) {
                for(Texture texture : pass.getTextures()) {
                    if(texture.getCoord() == 0) {
                        mtl.setMapKd(texture.getName());
                    }
                }
            }
        }
        
        return mtl;
    }
}
