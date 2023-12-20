import java.io.*;

public class EfficientFileWriter {

    String path;
    public OutputStream outputStream;
    public BufferedOutputStream writer;

    public EfficientFileWriter(String path) {
        this.path = path;
        try {
            outputStream = new FileOutputStream(path);
            writer = new BufferedOutputStream(outputStream);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void writeToFile(BitSetImpl bitset) {
        try {
            writer.write(bitset.getByteArray(), 0, bitset.getCurrentByteIdx() + (bitset.getCurrentBitIdx() != 7 ? 1 : 0));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void writeToFile(ByteGroup b) {
        try {
            writer.write(b.getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void writeToFile(byte[] b) {
        try {
            writer.write(b);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void writeToFile(byte b) {
        try {
            writer.write(b);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void writeToFile(String s) {
        try {
            writer.write(s.getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
