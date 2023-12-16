public class HuffmanNode {

    public int frequency;
    public ByteGroup bytes;
    public HuffmanNode left;
    public HuffmanNode right;

    public HuffmanNode(int frequency, ByteGroup bytes, HuffmanNode left, HuffmanNode right) {
        this.frequency = frequency;
        this.bytes = bytes;
        this.left = left;
        this.right = right;
    }

    public HuffmanNode(int frequency, ByteGroup bytes) {
        this(frequency, bytes, null, null);
    }

    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this(left.frequency + right.frequency, new ByteGroup(0), left, right);
    }

    public String toString() {
        return "bytes: " + bytes + " Frequency: " + frequency;
    }
}
