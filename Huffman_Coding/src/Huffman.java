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

        byte[] fileBytes = getFileBytes(path);

        // Create a frequency map of the bytes in the file
        Map<ByteGroup, Integer> frequencyMap = populateFrequencyMap(fileBytes, bytesPerGroup);

        HuffmanTree hTree = new HuffmanTree(frequencyMap);
        HuffmanNode root = hTree.buildTree();

        System.out.println(root);
        System.out.println(frequencyMap);
        System.out.println("I know you want me");
    }

    private byte[] getFileBytes(String path) {
        byte[] fileBytes;
        try {
            Path p = Paths.get(System.getProperty("user.dir"), path);
            fileBytes = Files.readAllBytes(p);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return fileBytes;
    }

    private Map<ByteGroup, Integer> populateFrequencyMap(byte[] fileBytes, int bytesPerGroup) {
        Map<ByteGroup, Integer> frequencyMap = new HashMap<>();
        for(int i = 0; i < fileBytes.length; i += bytesPerGroup) {
            ByteGroup currentByteGroup = convertBytesToByteGroup(i, bytesPerGroup, fileBytes);
            frequencyMap.put(currentByteGroup, frequencyMap.getOrDefault(currentByteGroup, 0) + 1);
        }
        return frequencyMap;
    }

    private ByteGroup convertBytesToByteGroup(int from, int bytesPerGroup, byte[] fileBytes) {
        ByteGroup currentByteGroup = new ByteGroup(bytesPerGroup);
        for (int j = from; j < from + bytesPerGroup; j++) {
            if (j >= fileBytes.length)
                break;
            currentByteGroup.insertByte(fileBytes[j]);
        }
        return currentByteGroup;
    }
}
