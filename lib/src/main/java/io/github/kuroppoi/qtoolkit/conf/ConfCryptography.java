package io.github.kuroppoi.qtoolkit.conf;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ConfCryptography {
    
    private static byte[] decryptBytes(byte[] bytes) {
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
    
    private static byte[] encryptBytes(byte[] bytes) {
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
    
    private static String decryptLine(String string) {
        return new String(decryptBytes(string.getBytes(ISO_8859_1)), UTF_8).replaceAll("\n|\r\n", "");
    }
    
    public static List<String> decryptLines(List<String> lines) {
        return lines.stream().map(ConfCryptography::decryptLine).collect(Collectors.toList());
    }
        
    public static String decryptLinesAsString(List<String> lines) {
        return joinLines(decryptLines(lines));
    }
    
    public static List<String> decryptStringAsLines(String string) {
        return decryptLines(Arrays.asList(string.split("\n|\r\n")));
    }
    
    public static String decryptString(String string) {
        return joinLines(decryptStringAsLines(string));
    }
    
    private static String encryptLine(String string) {
        return new String(encryptBytes((string + "\n").getBytes(UTF_8)), ISO_8859_1);
    }
    
    public static List<String> encryptLines(List<String> lines) {
        return lines.stream().map(ConfCryptography::encryptLine).collect(Collectors.toList());
    }
    
    public static String encryptLinesAsString(List<String> lines) {
        return joinLines(encryptLines(lines));
    }
    
    public static List<String> encryptStringAsLines(String string) {
        return encryptLines(Arrays.asList(string.split("\n|\r\n")));
    }
    
    public static String encryptString(String string) {
        return joinLines(encryptStringAsLines(string));
    }
    
    private static String joinLines(List<String> lines) {
        return String.join("\r\n", lines);
    }
}