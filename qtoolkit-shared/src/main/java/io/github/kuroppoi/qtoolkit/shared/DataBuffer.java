package io.github.kuroppoi.qtoolkit.shared;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DataBuffer {
    
    private final ByteBuffer buffer;
    
    public DataBuffer() {
        this(16777213); // TODO bad
    }
    
    public DataBuffer(int size) {
        this(ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN));
    }
    
    public DataBuffer(byte[] bytes) {
        this(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN));
    }
    
    private DataBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }
    
    public void position(int position) {
        buffer.position(position);
    }
    
    public int remaining() {
        return buffer.remaining();
    }
    
    public int position() {
        return buffer.position();
    }
    
    public void mark() {
        buffer.mark();
    }
    
    public void reset() {
        buffer.reset();
    }
    
    public void flip() {
        buffer.flip();
    }
    
    public byte readByte() {
        return buffer.get();
    }
    
    public byte readByte(int index) {
        return buffer.get(index);
    }
    
    public void writeByte(int b) {
        buffer.put((byte)b);
    }
    
    public void writeByte(int index, int b) {
        buffer.put(index, (byte)b);
    }
    
    public short readShort() {
        return buffer.getShort();
    }
    
    public short readShort(int index) {
        return buffer.getShort(index);
    }
    
    public void writeShort(int s) {
        buffer.putShort((short)s);
    }
    
    public void writeShort(int index, int s) {
        buffer.putShort(index, (short)s);
    }
    
    public int readInt() {
        return buffer.getInt();
    }
    
    public int readInt(int index) {
        return buffer.getInt(index);
    }
    
    public void writeInt(int i) {
        buffer.putInt(i);
    }
    
    public void writeInt(int index, int i) {
        buffer.putInt(index, i);
    }
    
    public float readFloat() {
        return buffer.getFloat();
    }
    
    public float readFloat(int index) {
        return buffer.getFloat(index);
    }
    
    public void writeFloat(float f) {
        buffer.putFloat(f);
    }
    
    public void writeFloat(int index, float f) {
        buffer.putFloat(index, f);
    }
    
    public long readLong() {
        return buffer.getLong();
    }
    
    public long readLong(int index) {
        return buffer.getLong(index);
    }
    
    public void writeLong(long l) {
        buffer.putLong(l);
    }
    
    public void writeLong(int index, long l) {
        buffer.putLong(index, l);
    }
    
    public double readDouble() {
        return buffer.getDouble();
    }
    
    public double readDouble(int index) {
        return buffer.getDouble(index);
    }
    
    public void writeDouble(double d) {
        buffer.putDouble(d);
    }
    
    public void writeDouble(int index, double d) {
        buffer.putDouble(index, d);
    }
        
    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }
    
    public byte[] readBytes(int index, int length) {
        byte[] bytes = new byte[length];
        
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = readByte(index + i);
        }
        
        return bytes;
    }
    
    public void writeBytes(byte[] bytes) {
        buffer.put(bytes);
    }
    
    public void writeBytes(int index, byte[] bytes) {
        for(int i = 0; i < bytes.length; i++) {
            writeByte(index + i, bytes[i]);
        }
    }
    
    public void writeEmpty(int length) {
        writeBytes(new byte[length]);
    }
    
    public void writeEmpty(int index, int length) {
        writeBytes(index, new byte[length]);
    }
    
    public String readString(int length) {
        return readString(length, StandardCharsets.UTF_8);
    }
    
    public String readString(int length, Charset charset) {
        String string = new String(readBytes(length), charset);
        return string.substring(0, string.indexOf('\0'));
    }
    
    public String readString(int index, int length) {
        return readString(index, length);
    }
    
    public String readString(int index, int length, Charset charset) {
        String string = new String(readBytes(index, length), charset);
        return string.substring(0, string.indexOf('\0'));
    }
        
    public void writeString(String string, int length) {
        writeString(string, length, StandardCharsets.UTF_8);
    }
    
    public void writeString(String string, int length, Charset charset) {
        byte[] bytes = stringToBytes(string, length, charset);
        writeBytes(bytes);
        writeEmpty(length - bytes.length);
    }
    
    public void writeString(int index, String string, int length) {
        writeString(index, string, length, StandardCharsets.UTF_8);
    }
    
    public void writeString(int index, String string, int length, Charset charset) {
        byte[] bytes = stringToBytes(string, length, charset);
        writeBytes(index, bytes);
        writeEmpty(index, length - bytes.length);
    }
    
    private byte[] stringToBytes(String string, int length, Charset charset) {
        byte[] bytes = (string + '\0').getBytes(charset);
        
        if(bytes.length > length) {
            throw new IllegalArgumentException("String length cannot exceed length");
        }
        
        return bytes;
    }
    
    public byte[] getBytes() {
        return buffer.array();
    }
}
