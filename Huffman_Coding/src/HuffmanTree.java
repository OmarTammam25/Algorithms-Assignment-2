import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {
    private HuffmanNode root;
    private Map<ByteGroup, Integer> frequencyMap;
    public HuffmanTree(Map<ByteGroup, Integer> frequencyMap) {
        this.frequencyMap = frequencyMap;
    }
    public HuffmanNode getRoot() {
        return root;
    }

    public HuffmanNode buildTree(){
        // create a priority queue that sorts frequencies as minimums
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(new NodeComparator());
        for(Map.Entry<ByteGroup, Integer> entry : frequencyMap.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getValue(), entry.getKey());
            pq.add(node);
        }

        // Build the Huffman tree
        while(pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode(left, right);
            pq.add(parent);
        }

        // gets the root node of the Huffman tree
        this.root = pq.poll();
        return root;
    }

    public Map<ByteGroup, String> buildEncoding() {
        Map<ByteGroup, String> encodingMap = new HashMap<>();
        buildEncoding(this.root, encodingMap, "");
        return encodingMap;
    }

    private void buildEncoding(HuffmanNode current, Map<ByteGroup, String> encodingMap, String encoding) {

        if (current.right != null)
             buildEncoding(current.right, encodingMap, encoding + '0');

        if (current.left != null)
            buildEncoding(current.left, encodingMap, encoding + '1');

        if (current.left == null && current.right == null) {
            encodingMap.put(current.bytes, encoding);
        }
    }
}
