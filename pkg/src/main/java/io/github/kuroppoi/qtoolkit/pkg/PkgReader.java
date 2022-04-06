package io.github.kuroppoi.qtoolkit.pkg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import io.github.kuroppoi.qtoolkit.shared.DataBuffer;
import io.github.kuroppoi.qtoolkit.shared.IOUtils;
import io.github.kuroppoi.qtoolkit.shared.file.DirectoryNode;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;

public class PkgReader {
    
    public static DirectoryNode readPkgFile(InputStream inputStream) throws IOException {
        return readPkgFile(IOUtils.readAllBytes(inputStream));
    }
    
    public static DirectoryNode readPkgFile(File file) throws IOException {
        return readPkgFile(Files.readAllBytes(file.toPath()));
    }
    
    public static DirectoryNode readPkgFile(FileNode file) throws IOException {
        return readPkgFile(file.getBytes());
    }
    
    public static DirectoryNode readPkgFile(byte[] bytes) {
        return readPkgFile(new DataBuffer(bytes));
    }
    
    private static DirectoryNode readPkgFile(DataBuffer buffer) {
        DirectoryNode root = new DirectoryNode("root");
        buffer.readShort();
        int entryCount = buffer.readShort();
        
        for(int i = 0; i < entryCount; i++) {
            decryptBytes(buffer, 136);
            FileNode file = root.createFile(buffer.readString(128));
            int offset = buffer.readInt();
            int length = buffer.readInt();
            buffer.mark();
            buffer.position(offset);
            buffer.readInt(); // length, not used
            decryptBytes(buffer, length);
            file.setBytes(buffer.readBytes(length));
            buffer.reset();
        }
        
        return root;
    }
    
    private static void decryptBytes(DataBuffer buffer, int length) {
        int offset = buffer.position();
        byte[] bytes = buffer.readBytes(offset, length);
        buffer.writeBytes(offset, PkgCryptography.decryptBytes(bytes));
    }
}
