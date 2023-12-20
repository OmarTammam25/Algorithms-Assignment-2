import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {
    private HuffmanNode root;
    private Map<ByteGroup, Integer> frequencyMap;
    public HuffmanTree() {

    }
    public HuffmanTree(Map<ByteGroup, Integer> frequencyMap) {
        this.frequencyMap = frequencyMap;
    }
    public HuffmanNode getRoot() {
        return root;
    }

    public void setRoot(HuffmanNode root) { this.root = root; }

    public HuffmanNode buildTree(){
        // create a priority queue that sorts frequencies as minimums
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(new NodeComparator());
        for(Map.Entry<ByteGroup, Integer> entry : frequencyMap.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getValue(), entry.getKey());
            pq.add(node);
        }

        // Build the Huffman tree
        while(pq.size() > 1) {
            HuffmanNode right = pq.poll();
            HuffmanNode left = pq.poll();
            HuffmanNode parent = new HuffmanNode(left, right);
            pq.add(parent);
        }

        // gets the root node of the Huffman tree
        this.root = pq.poll();
        return root;
    }

    public Map<ByteGroup, String> buildEncodingMap() {
        Map<ByteGroup, String> encodingMap = new HashMap<>();
        buildEncodingMap(this.root, encodingMap, "");
        return encodingMap;
    }

    private void buildEncodingMap(HuffmanNode current, Map<ByteGroup, String> encodingMap, String encoding) {

        if (current.right != null)
             buildEncodingMap(current.right, encodingMap, encoding + '0');

        if (current.left != null)
            buildEncodingMap(current.left, encodingMap, encoding + '1');

        if (current.isLeaf()) {
            encodingMap.put(current.bytes, encoding);
        }
    }

    public void buildEncodedTree(BitSetImpl bitSet) {
        buildEncodedTree(this.root, bitSet);
    }

    private void buildEncodedTree(HuffmanNode current, BitSetImpl bitSet) {
        if(current.isLeaf()) {
            bitSet.insertOne();
            bitSet.insertByteGroup(current.bytes);
        } else {
            bitSet.insertZero();

            if (current.right != null)
                buildEncodedTree(current.right, bitSet);

            if (current.left != null)
                buildEncodedTree(current.left, bitSet);

        }
    }

    public HuffmanNode buildDecodedTree(BitSetImpl bitSet, int bytesPerGroup) {
        if (bitSet.getCurrentReadBit()) {
            ByteGroup bytes = new ByteGroup(bytesPerGroup);

            for (int i = 0; i < bytesPerGroup; i++) {
                bytes.insertByte(bitSet.getCurrentReadByte());
            }

            return new HuffmanNode(0, bytes, null, null);
        } else {
            HuffmanNode right = buildDecodedTree(bitSet, bytesPerGroup);
            HuffmanNode left = buildDecodedTree(bitSet, bytesPerGroup);

            return new HuffmanNode(0, null, left, right);
        }
    }
}
