package io.github.kuroppoi.qtoolkit.pkg;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import io.github.kuroppoi.qtoolkit.shared.DataBuffer;
import io.github.kuroppoi.qtoolkit.shared.file.DirectoryNode;
import io.github.kuroppoi.qtoolkit.shared.file.FileNode;

public class PkgWriter {
    
    public static void writePkgFile(DirectoryNode root, OutputStream outputStream) throws IOException {
        outputStream.write(writePkgFile(root));
        outputStream.flush();
    }
    
    public static void writePkgFile(DirectoryNode root, File output) throws IOException {
        Files.write(output.toPath(), writePkgFile(root));
    }
    
    public static void writePkgFile(DirectoryNode root, FileNode output) {
        output.setBytes(writePkgFile(root));
    }
    
    public static byte[] writePkgFile(DirectoryNode root) {
        DataBuffer buffer = new DataBuffer();
        writePkgFile(buffer, root);
        return buffer.readBytes(0, buffer.position());
    }
    
    private static void writePkgFile(DataBuffer buffer, DirectoryNode root) {
        PkgFileInfo info = new PkgFileInfo(root);
        int fileCount = info.getFileCount();
        buffer.writeShort(0); // ?
        buffer.writeShort(fileCount);
        int contentOffset = 4 + 136 * fileCount;
        int pathOffset = root.getPath().length() + 1;
        
        for(FileNode file : info.getFiles()) {
            int length = file.getSize();
            buffer.writeString(file.getPath("\\").substring(pathOffset), 128);
            buffer.writeInt(contentOffset);
            buffer.writeInt(length);
            encryptBytes(buffer, 136);
            buffer.mark();
            buffer.position(contentOffset);
            buffer.writeInt(length);
            buffer.writeBytes(file.getBytes());
            encryptBytes(buffer, length);
            buffer.reset();
            contentOffset += length + 4;
        }
        
        buffer.position(contentOffset);
    }
    
    private static void encryptBytes(DataBuffer buffer, int length) {
        int offset = buffer.position() - length;
        byte[] bytes = buffer.readBytes(offset, length);
        buffer.writeBytes(offset, PkgCryptography.encryptBytes(bytes));
    }
}
