import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class Huffman {
    public void encode(String path, int bytesPerGroup) {

        byte[] fileBytes = getFileBytes(path);

        // Create a frequency map of the bytes in the file
        Map<ByteGroup, Integer> frequencyMap = populateFrequencyMap(fileBytes, bytesPerGroup);

        // Prepare huffman data
        HuffmanTree hTree = new HuffmanTree(frequencyMap);
        HuffmanNode root = hTree.buildTree();
        Map<ByteGroup, String> encodingMap = hTree.buildEncodingMap();

        // Encode the file in bitset
        BitSetImpl bitSet = new BitSetImpl();
        prepareHeader(hTree, bytesPerGroup, bitSet);
        prepareFile(fileBytes, encodingMap, bytesPerGroup, bitSet);

        // Write the encoded file
        Path p = Paths.get(System.getProperty("user.dir"), path + ".hc");
        try {
            Files.write(p, bitSet.toByteArray());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public void decode(String path) {
        byte[] fileBytes = getFileBytes(path);
        BitSetImpl bitSet = new BitSetImpl(fileBytes);
        HuffmanNode root = bitSet.getDecodedTree();

        BitSetImpl decodedFileBits = new BitSetImpl();

        int lstBit = 0;
        for (int it = bitSet.getCurrentIdx()-1; it >= 0; it--) {
            if(bitSet.get(it)) {
                lstBit = it;
                break;
            }
        }

        while (bitSet.currentReadIdx < lstBit) {
            decodeFile(bitSet, decodedFileBits, root);
        }

        // Write the encoded file
        Path p = Paths.get(System.getProperty("user.dir"), "wdy.seq");
        try {
            Files.write(p, decodedFileBits.toByteArray());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public void decodeFile(BitSetImpl encodedFileBits, BitSetImpl decodedFileBits, HuffmanNode node) {
        if(node.isLeaf()) {
            decodedFileBits.insertByteGroup(node.bytes);
            return;
        }

        if(encodedFileBits.get(encodedFileBits.currentReadIdx++)) {
            decodeFile(encodedFileBits, decodedFileBits, node.left);
        }else {
            decodeFile(encodedFileBits, decodedFileBits, node.right);
        }
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

    private void prepareHeader(HuffmanTree hTree, int bytesPerGroup, BitSetImpl bitSet) {
        bitSet.addIntToBitset(bytesPerGroup);
        hTree.buildEncodedTree(bitSet);
    }



    private void prepareFile(byte[] fileBytes, Map<ByteGroup, String> encodingMap, int bytesPerGroup, BitSetImpl bitSet) {

        for (int i = 0; i < fileBytes.length; i += bytesPerGroup) {
            ByteGroup currentByteGroup = convertBytesToByteGroup(i, bytesPerGroup, fileBytes);
            String currentEncoding = encodingMap.get(currentByteGroup);

            for (int j = 0; j < currentEncoding.length(); j++) {
                if (currentEncoding.charAt(j) == '1')
                    bitSet.insertOne();
                else
                    bitSet.insertZero();
            }
        }
        bitSet.insertOne();
    }
}
