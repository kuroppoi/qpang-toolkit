package io.github.kuroppoi.qtoolkit.conf;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ConfCryptography {
    
    public static byte[] decryptBytes(byte[] bytes) {
        int length = bytes.length;
        
        if(length % 4 != 0) {
            throw new IllegalArgumentException("input length must be multiple of 4");
        }
        
        byte[] output = new byte[length / 4];
        
        for(int i = 0; i < length; i += 4) {
            output[i / 4] = (byte)(bytes[i + 3] & 3 | 4 * (bytes[i + 2] & 3 | 4 * (bytes[i + 1] & 3 | 4 * (bytes[i] & 3))));
        }
        
        return output;
    }
    
    public static byte[] encryptBytes(byte[] bytes) {
        byte[] output = new byte[bytes.length * 4];
        
        for(int i = 0; i < bytes.length; i++) {
            for(int j = 0; j < 4; j++) {
                int mask = 192 / (int)Math.pow(4, j);
                int garbage = (int)(ThreadLocalRandom.current().nextInt(32, Byte.MAX_VALUE)) & 252;
                output[i * 4 + j] = (byte)((bytes[i] & mask) >> (6 - j * 2) | garbage);
            }
        }
        
        return output;
    }
    
    public static String decryptString(String string) {
        return new String(decryptBytes(string.getBytes(ISO_8859_1)), UTF_8);
    }
    
    public static String encryptString(String string) {
        return new String(encryptBytes(string.getBytes(UTF_8)), ISO_8859_1);
    }
    
    public static List<String> decryptLines(List<String> lines) {
        List<String> decrypted = new ArrayList<>();
        
        for(String string : lines) {
            decrypted.add(decryptString(string));
        }
        
        return decrypted;
    }
    
    public static List<String> encryptLines(List<String> lines) {
        return encryptLines(lines, true);
    }
    
    public static List<String> encryptLines(List<String> lines, boolean addLineFeed) {
        List<String> encrypted = new ArrayList<>();
        
        for(String string : lines) {
            string = addLineFeed && string.endsWith("\n") ? string + "\n" : string;
            encrypted.add(encryptString(string));
        }
        
        return encrypted;
    }
}
