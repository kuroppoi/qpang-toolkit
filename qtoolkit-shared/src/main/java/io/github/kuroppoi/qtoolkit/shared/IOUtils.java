package io.github.kuroppoi.qtoolkit.shared;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {
    
    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = -1;
        
        while((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        
        return outputStream.toByteArray();
    }
    
    public static List<String> readAllLines(Reader reader) throws IOException {
        BufferedReader bufferedReader = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
        List<String> lines = new ArrayList<>();
        String line = null;
        
        while((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        
        bufferedReader.close();
        return lines;
    }
}
