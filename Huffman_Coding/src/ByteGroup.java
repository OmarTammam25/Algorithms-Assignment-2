import java.util.Arrays;

public class ByteGroup {
    private byte[] bytes;
    private int it;
    private final int size;
    public ByteGroup(int n) {
        this.bytes = new byte[n];
        it = 0;
        size = n;
    }

    public void insertByte(byte b) {
        if (it == size)
            throw new RuntimeException("ByteGroup is full");
        bytes[it++] = b;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getIt(){
        return this.it;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteGroup byteGroup = (ByteGroup) o;
        return Arrays.equals(bytes, byteGroup.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return Arrays.toString(bytes);
    }
}
