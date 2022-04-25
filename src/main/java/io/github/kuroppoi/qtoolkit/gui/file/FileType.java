package io.github.kuroppoi.qtoolkit.gui.file;

import java.util.Arrays;

import io.github.kuroppoi.qtoolkit.DataBuffer;
import io.github.kuroppoi.qtoolkit.file.FileNode;

public enum FileType {
    
    TEXT,
    MESH,
    ANIMATION,
    COLLISION,
    CONFIG,
    UNKNOWN;
    
    public static FileType determineFileType(FileNode file) {
        String name = file.getName();
        byte[] bytes = file.getBytes();
        int asciiCount = 0;
        
        for(byte b : bytes) {
            if(b >= 9) {
                asciiCount++;
            }
        }
        
        if(name.endsWith(".conf") || name.endsWith(".dat")) {
            boolean valid = true;
            
            for(String line : file.getLines()) {
                if(line.length() % 4 != 0) {
                    valid = false;
                    break;
                }
            }
            
            if(valid) {
                return CONFIG;
            }
        }
        
        float ratio = (float)asciiCount / bytes.length;
        
        // TODO maybe allow users to configure the threshold
        if(ratio >= 0.7F || bytes.length == 0) {
            return TEXT;
        }
        
        if(name.endsWith(".mesh")) {
            return MESH;
        }
        
        if(name.endsWith(".animation")) {
            return ANIMATION;
        }
        
        if(name.endsWith(".collision")) {
            return COLLISION;
        }
        
        // Not actually sure how accurate this is.
        // So far all clean installed files seem to check out.
        if(bytes.length >= 12) {
            DataBuffer buffer = new DataBuffer(Arrays.copyOfRange(bytes, 8, 12));
            int type = buffer.readInt();
            
            switch(type) {
                case 1: return MESH;
                case 2: return ANIMATION;
                case 4: return COLLISION;
            }
        }
        
        return UNKNOWN;
    }
}
