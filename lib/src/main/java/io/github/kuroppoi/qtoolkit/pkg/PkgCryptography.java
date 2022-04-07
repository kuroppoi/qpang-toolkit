package io.github.kuroppoi.qtoolkit.pkg;

import io.github.kuroppoi.qtoolkit.DataBuffer;

public class PkgCryptography {
    
    public static int decryptInt(int i) {
        return (int)(((~i & 0xFFFFFFFFL) << 13) | ((~i & 0xFFFFFFFFL) >> 19));
    }
    
    public static int encryptInt(int i) {
        return (int)~(((i & 0xFFFFFFFFL) >> 13) | ((i & 0xFFFFFFFFL) << 19));
    }
    
    public static byte[] decryptBytes(byte[] bytes) {
        return cryptBytes(bytes, false);
    }
    
    public static byte[] encryptBytes(byte[] bytes) {
        return cryptBytes(bytes, true);
    }
    
    private static byte[] cryptBytes(byte[] bytes, boolean encrypt) {
        int length = bytes.length;
        DataBuffer in = new DataBuffer(bytes);
        DataBuffer out = new DataBuffer(bytes.length);
        
        for(int i = 0; i < length / 4; i++) {
            out.writeInt(encrypt ? encryptInt(in.readInt()) : decryptInt(in.readInt()));
        }
        
        out.writeBytes(in.readBytes(in.remaining()));
        return out.getBytes();
    }
}
