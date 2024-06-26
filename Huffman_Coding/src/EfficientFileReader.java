import javax.imageio.event.IIOReadProgressListener;
import java.io.*;
import java.nio.Buffer;

public class EfficientFileReader {
    String path;
    byte[] buffer;
    InputStream inputStream;
    BufferedInputStream bufferedStream;
    public EfficientFileReader(String path) {
        this.path = path;
        this.buffer = new byte[1024 * 4];

        try {
            inputStream = new FileInputStream(this.path);
            bufferedStream = new BufferedInputStream(inputStream);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public BufferReader getNextChunk() {
        int read;
        try {
            read = bufferedStream.read(buffer, 0, buffer.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        if(read == -1)
            read = 0;
        byte[] buf = new byte[read];
        System.arraycopy(this.buffer, 0, buf, 0, read);
        return new BufferReader(buf, read);
    }

    public BufferReader getNext(int size) {
        byte[] buf = new byte[size];
        int read;
        try {
            read = bufferedStream.read(buf, 0, buf.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return new BufferReader(buf, read);
    }

    public int getNextInt() {
        int read;
        byte[] buf;
        try {
            buf = new byte[4];
            read = bufferedStream.read(buf, 0, buf.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return getInt(buf);
    }
    public int getInt(byte[] buf) {
        int num = 0;
        for (int i = 0; i < 4; i++) {
            num <<= 8;
            num |= (buf[i] & 0xFF);
        }
        return num;
    }

    public long getNextLong() {
        int read;
        byte[] buf;
        try {
            buf = new byte[8];
            read = bufferedStream.read(buf, 0, buf.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return getLong(buf);
    }

    private long getLong(byte[] buf) {
        int num = 0;
        for (int i = 0; i < 8; i++) {
            num <<= 8;
            num |= (buf[i] & 0xFF);
        }
        return num;
    }

}
