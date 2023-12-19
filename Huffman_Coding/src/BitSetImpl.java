public class BitSetImpl{

    private int currentByteIdx;
    private int currentBitIdx;
    private byte[] bytes;
    public int currentReadByte;
    public int currentReadBit;

    public BitSetImpl() {
        currentByteIdx = 0;
        currentBitIdx = 7;
        currentReadByte = 0;
        currentReadBit = 7;
        bytes = new byte[0];
    }

    public BitSetImpl(int size) {
        currentByteIdx = 0;
        currentBitIdx = 7;
        currentReadByte = 0;
        currentReadBit = 7;
        bytes = new byte[size];
    }


    public BitSetImpl(byte[] bytes) {
        currentReadBit = 0;
        currentBitIdx = 7;
        currentReadByte = 0;
        currentReadBit = 7;
        this.bytes = bytes;
    }
    public int getCurrentByteIdx() {
        return currentByteIdx;
    }

    public int getCurrentBitIdx() {
        return currentBitIdx;
    }

    public void insertOne() {
        setBitInByte(this.currentByteIdx, this.currentBitIdx);
        incrementWriteBitByOne();
    }

    private void setBitInByte(int byteIdx, int bitIdx) {
        this.bytes[byteIdx] = (byte) ((1 << bitIdx) | this.bytes[byteIdx]);
    }

    public void insertZero() {
        incrementWriteBitByOne();
    }

    public void incrementWriteBitByOne() {
        if(this.currentBitIdx == 0) {
            this.currentBitIdx = 7;
            this.currentByteIdx++;
        } else {
            this.currentBitIdx--;
        }
    }

    public void incrementReadBitByOne() {
        if(this.currentReadBit == 0) {
            this.currentReadBit = 7;
            this.currentReadByte++;
        } else {
            this.currentReadBit--;
        }
    }

    public byte[] getByteArray() {
        return this.bytes;
    }

    public void addInt(int num) {
        this.bytes[this.currentByteIdx] = (byte)(num >>> 24);
        this.bytes[this.currentByteIdx + 1] = (byte)(num >>> 16);
        this.bytes[this.currentByteIdx + 2] = (byte)(num >>> 8);
        this.bytes[this.currentByteIdx + 3] = (byte)(num);
        this.currentByteIdx += 4;
    }


    public void insertByteGroup(ByteGroup byteGroup) {
        for (int i = 0; i < byteGroup.getBytes().length; i++) {
            if(this.currentByteIdx == this.bytes.length)
                break;
            this.addByte(byteGroup.getBytes()[i]);
        }
    }

    public void addByte(byte b) {
        if(this.currentBitIdx == 7){
            this.bytes[this.currentByteIdx] = b;
            this.currentByteIdx++;
            return;
        }

        int shift = 7 - this.currentBitIdx;
        byte shiftedByte = (byte) (b >>> shift);
        byte restOfByte = (byte) (b << (this.currentBitIdx + 1));
        this.bytes[this.currentByteIdx] = (byte) (shiftedByte | this.bytes[this.currentByteIdx]);

        this.currentByteIdx++;
        this.bytes[this.currentByteIdx] = restOfByte;
        this.currentBitIdx = 7 - shift;
    }


    public HuffmanNode getDecodedTree(int bytesPerGroup) {
        HuffmanTree hTree = new HuffmanTree();
        HuffmanNode root = hTree.buildDecodedTree(this, bytesPerGroup);
        hTree.setRoot(root);

        return root;
    }

    public int getInt() {
        int num = 0;
        for (int i = 0; i < 4; i++) {
            num <<= 8;
            num |= (this.getCurrentReadByte() & 0xFF);
        }
        return num;
    }

    public byte getCurrentReadByte() {
        if(this.currentReadBit == 7){
            return this.bytes[this.currentReadByte++];
        }

        int shift = 7 - this.currentReadBit;
        byte shiftedByte = (byte) (this.bytes[this.currentReadByte] << shift);
        this.currentReadByte++;

        byte restOfByte = 0x0;
        if(this.currentReadByte != this.bytes.length - 1)
            restOfByte = (byte) ((this.bytes[this.currentReadByte] & 0xFF) >>> (this.currentReadBit + 1));

        return (byte) (shiftedByte | restOfByte);
    }

    public boolean getCurrentReadBit() {
        boolean curBit = ((this.bytes[this.currentReadByte] >> this.currentReadBit) & 1) == 1;
        incrementReadBitByOne();
        return curBit;
    }
}
