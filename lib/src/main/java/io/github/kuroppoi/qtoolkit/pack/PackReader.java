package io.github.kuroppoi.qtoolkit.pack;

import static java.nio.charset.StandardCharsets.UTF_16LE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import io.github.kuroppoi.qtoolkit.DataBuffer;
import io.github.kuroppoi.qtoolkit.file.DirectoryNode;
import io.github.kuroppoi.qtoolkit.file.FileNode;
import io.github.kuroppoi.qtoolkit.utils.IOUtils;

public class PackReader {
    
    public static DirectoryNode readPackFile(InputStream inputStream) throws IOException {
        return readPackFile(IOUtils.readAllBytes(inputStream));
    }
    
    public static DirectoryNode readPackFile(File file) throws IOException {
        return readPackFile(Files.readAllBytes(file.toPath()));
    }
    
    public static DirectoryNode readPackFile(byte[] bytes) {
        return readPackFile(new DataBuffer(bytes));
    }
    
    private static DirectoryNode readPackFile(DataBuffer buffer) {
        DirectoryNode root = new DirectoryNode("root");
        Map<Integer, DirectoryNode> directories = new HashMap<>();
        Map<DirectoryNode, Integer> locations = new HashMap<>();
        buffer.readBytes(8); // 0
        int directoryCount = buffer.readInt();
        int fileCount = buffer.readInt();
        int contentOffset = 16 + 132 * directoryCount + 140 * fileCount;
        
        for(int i = 0; i < directoryCount; i++) {
            DirectoryNode directory = new DirectoryNode(buffer.readString(128, UTF_16LE));
            int location = buffer.readInt();
            directories.put(i, directory);
            locations.put(directory, location);
        }
        
        for(DirectoryNode directory : directories.values()) {
            directories.getOrDefault(locations.get(directory), root).addChild(directory);
        }
        
        for(int i = 0; i < fileCount; i++) {
            FileNode file = new FileNode(buffer.readString(128, UTF_16LE));
            int location = buffer.readInt();
            int compressedSize = buffer.readInt();
            int size = buffer.readInt();
            
            if(compressedSize != size) {
                throw new UnsupportedOperationException("Compressed .pack files are currently not supported.");
            }
            
            directories.getOrDefault(location, root).addChild(file);
            buffer.mark();
            buffer.position(contentOffset);
            file.setBytes(buffer.readBytes(size));
            buffer.reset();
            contentOffset += size;
        }
        
        return root;
    }
}
