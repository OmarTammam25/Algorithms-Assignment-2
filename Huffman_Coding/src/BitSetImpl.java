import java.util.BitSet;
import java.util.Map;

public class BitSetImpl extends BitSet {
    private int currentIdx;
    private byte[] bytes;
    public int currentReadIdx;
    public BitSetImpl() {
        super();
        currentIdx = 0;
        currentReadIdx = 0;
    }

    public BitSetImpl(int size) {
        super(size);
        currentIdx = 0;
        currentReadIdx = 0;
    }

    public BitSetImpl(byte[] bytes) {
        super(bytes.length * 8);
        currentIdx = 0;
        currentReadIdx = 0;
        fromByteArray(bytes);
    }

    public int getCurrentIdx() {
        return currentIdx;
    }

    public void insertOne() {
        super.set(this.currentIdx);
        this.currentIdx++;
    }

    private void setBitInByte(int byteIdx, int bitIdx) {
        this.bytes[byteIdx] = (byte) ((1 << bitIdx) | this.bytes[byteIdx]);
    }

    public void insertZero() {
        this.currentIdx++;
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[this.currentIdx / 8 + (this.currentIdx % 8 == 0 ? 0 : 1)];

        for (int i = 0; i < currentIdx; i++) {
            if(i < this.length() && this.get(i)) {
                int currentByte = i/8;
                int currentBit =  7 - i%8;
                bytes[currentByte] = (byte) ((1 << currentBit) | bytes[currentByte]);
            }
        }

        return bytes;
    }

    private void fromByteArray(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            this.addByte(bytes[i]);
        }
    }

    public void addIntToBitset(int num) {
        for (int i = 31; i >= 0; i--) {
            if ((num & (1 << i)) != 0)
                this.insertOne();
            else
                this.insertZero();
        }
    }

    public void addByte(Byte b) {
        for (int i = 7; i >= 0; i--) {
            if ((b & (1 << i)) != 0)
                this.insertOne();
            else
                this.insertZero();
        }
    }

    public void insertByteGroup(ByteGroup byteGroup) {
        for (int i = 0; i < byteGroup.getBytes().length; i++) {
            this.addByte(byteGroup.getBytes()[i]);
        }
    }

    public HuffmanNode getDecodedTree() {
        int bytesPerGroup = getInt(0);
        currentReadIdx = 32;

        HuffmanTree hTree = new HuffmanTree();
        HuffmanNode root = hTree.buildDecodedTree(this, bytesPerGroup);
        hTree.setRoot(root);

        return root;
    }

    public int getInt(int from) {
        int myInt = 0;
        int currIt = 31;
        for (int i = from; i < from + 32; i++) {
            if (super.get(i))
                myInt = myInt | (1 << currIt);
            currIt--;
        }
        return myInt;
    }

    public byte getByte(int from) {
        byte myByte = 0x0;
        int currIt = 7;
        for (int i = from; i < from + 8; i++) {
            if(super.get(i))
                myByte = (byte) (myByte | (1 << currIt));
            currIt--;
        }
        return myByte;
    }
}
