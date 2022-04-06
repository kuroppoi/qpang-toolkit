package io.github.kuroppoi.qtoolkit.pack;

import static java.nio.charset.StandardCharsets.UTF_16LE;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import io.github.kuroppoi.qtoolkit.shared.DataBuffer;
import io.github.kuroppoi.qtoolkit.shared.file.DirectoryNode;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;
import io.github.kuroppoi.qtoolkit.shared.file.FileSystemNode;

public class PackWriter {
    
    public static void writePackFile(DirectoryNode root, OutputStream outputStream) throws IOException {
        outputStream.write(writePackFile(root));
    }
    
    public static void writePackFile(DirectoryNode root, File outputFile) throws IOException {
        Files.write(outputFile.toPath(), writePackFile(root));
    }
    
    public static byte[] writePackFile(DirectoryNode root) {
        DataBuffer buffer = new DataBuffer(16 + calculateSize(root));
        writePackFile(buffer, root);
        return buffer.readBytes(0, buffer.position());
    }
    
    private static void writePackFile(DataBuffer buffer, DirectoryNode root) {
        PackFileInfo info = new PackFileInfo(root);
        int directoryCount = info.getDirectoryCount();
        int fileCount = info.getFileCount();
        int contentOffset = 16 + 132 * directoryCount + 140 * fileCount;
        buffer.writeEmpty(8);
        buffer.writeInt(directoryCount);
        buffer.writeInt(fileCount);
        
        for(DirectoryNode directory : info.getDirectories()) {
            buffer.writeString(directory.getName(), 128, UTF_16LE);
            buffer.writeInt(info.getLocation(directory));
        }
        
        for(FileNode file : info.getFiles()) {
            int size = file.getSize();
            buffer.writeString(file.getName(), 128, UTF_16LE);
            buffer.writeInt(info.getLocation(file));
            buffer.writeInt(size); // TODO compressed size
            buffer.writeInt(size);
            buffer.mark();
            buffer.position(contentOffset);
            buffer.writeBytes(file.getBytes());
            buffer.reset();
            contentOffset += size;
        }
        
        // Gotta set the position to the end or we won't know where it is
        buffer.position(contentOffset);
    }
    
    private static int calculateSize(DirectoryNode root) {
        int size = 0;
        
        for(FileSystemNode node : root.getChildren()) {
            if(node.isDirectory()) {
                size += 132 + calculateSize((DirectoryNode)node);
            } else {
                size += 140 + ((FileNode)node).getSize();
            }
        }
        
        return size;
    }
}
