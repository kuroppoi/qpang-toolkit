package io.github.kuroppoi.qtoolkit.file;

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
            output.mkdirs();
            
            for(FileSystemNode child : ((DirectoryNode)node).getChildren()) {
                writeFileSystem(child, new File(output, child.getName()));
            }
        } else {
            File parentFile = output.getParentFile();
            
            if(parentFile != null) {
                parentFile.mkdirs();
            }
            
            Files.write(output.toPath(), ((FileNode)node).getBytes());
        }
    }
}
