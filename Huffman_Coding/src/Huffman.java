import org.w3c.dom.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huffman {
    public void encode(String path, int bytesPerGroup) {
        byte[] fileBytes;
        try {
            Path p = Paths.get(System.getProperty("user.dir"), path);
            fileBytes = Files.readAllBytes(p);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        // Create a frequency map of the bytes in the file
        Map<String, Integer> frequencyMap = new HashMap<>();
        for(int i = 0; i < fileBytes.length; i += bytesPerGroup) {
            String chunk = new String(fileBytes, i, bytesPerGroup);
            frequencyMap.put(chunk, frequencyMap.getOrDefault(chunk, 0) + 1);
        }

        // create a priority queue that sorts frequencies as minimums
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(new NodeComparator());
        for(Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
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

        // Get the root node of the Huffman tree
        HuffmanNode root = pq.poll();


        System.out.println(root);
        System.out.println(frequencyMap);
        System.out.println("I know you want me");
    }
}
