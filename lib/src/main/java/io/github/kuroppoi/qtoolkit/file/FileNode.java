package io.github.kuroppoi.qtoolkit.file;

import static java.nio.charset.StandardCharsets.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileNode extends FileSystemNode {
    
    private byte[] bytes;
    
    public FileNode(String name) {
        this(name, new byte[0]);
    }
    
    public FileNode(String name, byte[] bytes) {
        super(name);
        this.bytes = bytes;
    }
    
    @Override
    public boolean isDirectory() {
        return false;
    }
    
    public List<String> getLines() {
        return getLines(UTF_8);
    }
    
    public List<String> getLines(Charset charset) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), charset));
        List<String> lines = new ArrayList<>();
        String line = null;
        
        try {
            while((line = reader.readLine()) != null) {
                lines.add(line);
            }
            
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return lines;
    }
    
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public int getSize() {
        return bytes.length;
    }
    
    public byte[] getBytes() {
        return bytes;
    }
}
