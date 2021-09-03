package io.github.kuroppoi.qtoolkit.shared.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileSystems {
    
    public static FileSystemNode readFileSystem(File file) throws IOException {
        FileSystemNode node = null;
        String name = file.getName();
        
        if(file.isDirectory()) {
            node = new DirectoryNode(name);
            
            for(File child : file.listFiles()) {
                ((DirectoryNode)node).addChild(readFileSystem(child));
            }
        } else {
            node = new FileNode(name, Files.readAllBytes(file.toPath()));
        }
        
        return node;
    }
    
    public static void writeFileSystem(FileSystemNode node, File output) throws IOException {
        if(node.isDirectory()) {
            for(FileSystemNode child : ((DirectoryNode)node).getChildren()) {
                writeFileSystem(child, output);
            }
        } else {
            File file = new File(output, node.getPath());
            File parentFile = file.getParentFile();
            
            if(parentFile != null) {
                parentFile.mkdirs();
            }
            
            Files.write(file.toPath(), ((FileNode)node).getBytes());
        }
    }
}
