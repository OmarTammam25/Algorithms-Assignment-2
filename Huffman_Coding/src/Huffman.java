import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Huffman {
    public void encode(String path, int bytesPerGroup) {
        long begin = System.currentTimeMillis();

        byte[] fileBytes = getFileBytes(path);
        long end = System.currentTimeMillis();
        System.out.println("File read in " + (end - begin) + " ms");

        // Create a frequency map of the bytes in the file
        Map<ByteGroup, Integer> frequencyMap = populateFrequencyMap(fileBytes, bytesPerGroup);

        // Prepare huffman data
        HuffmanTree hTree = new HuffmanTree(frequencyMap);
        HuffmanNode root = hTree.buildTree();
        Map<ByteGroup, String> encodingMap = hTree.buildEncodingMap();

        // Encode the file in bitset
        BitSetImpl bitSet = new BitSetImpl(encodingMap.size() * 2 * bytesPerGroup + fileBytes.length);
        prepareHeader(hTree, bytesPerGroup, fileBytes.length, bitSet);
        prepareFile(fileBytes, encodingMap, bytesPerGroup, bitSet);

        int lstByte = getLastBytePosition(bitSet);
        byte[] scaledBytes = new byte[lstByte + 1];
        System.arraycopy(bitSet.getByteArray(), 0, scaledBytes, 0, lstByte + 1);
        bitSet = new BitSetImpl(scaledBytes);

        // Write the encoded file
        Path p = Paths.get(System.getProperty("user.dir"), path + ".hc");
        try {
            Files.write(p, bitSet.getByteArray());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public void decode(String path, String fileName) {
        byte[] fileBytes = getFileBytes(path);
        BitSetImpl bitSet = new BitSetImpl(fileBytes);
        int bytesPerGroup = bitSet.getInt();
        int fileSize = bitSet.getInt();
        HuffmanNode root = bitSet.getDecodedTree(bytesPerGroup);

        BitSetImpl decodedFileBits = new BitSetImpl(fileSize);

        while (decodedFileBits.getCurrentByteIdx() < fileSize) {
            decodeFile(bitSet, decodedFileBits, root);
        }

        // Write the encoded file
        Path p = Paths.get(System.getProperty("user.dir"), fileName);
        try {
            Files.write(p, decodedFileBits.getByteArray());
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

        if(encodedFileBits.getCurrentReadBit()) {
            decodeFile(encodedFileBits, decodedFileBits, node.left);
        }else {
            decodeFile(encodedFileBits, decodedFileBits, node.right);
        }
    }

//    private byte[] getFileBytes(String path) {
//        byte[] fileBytes;
//        try {
//            Path p = Paths.get(System.getProperty("user.dir"), path);
//            fileBytes = Files.readAllBytes(p);
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
//        }
//        return fileBytes;

    private byte[] getFileBytes(String path) {
        try (InputStream inputStream = new FileInputStream(path);
             BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
        ) {
            byte[] buffer  = new byte[4 * 1024];
            int read;
            while ((read = bufferedStream.read(buffer, 0, buffer.length)) != 0) {

            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }
    private int getLastBytePosition(BitSetImpl bitSet) {
        int lstByte = 0;
        for (int i = bitSet.getByteArray().length - 1; i >= 0; i--) {
            if (bitSet.getByteArray()[i] != 0) {
                lstByte = i;
                break;
            }
        }
        return lstByte;
    }

    private void populateFrequencyMap(byte[] fileBytes, int bytesPerGroup, Map<ByteGroup, Integer> frequencyMap) {
//        Map<ByteGroup, Integer> frequencyMap = new HashMap<>();
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

    private void prepareHeader(HuffmanTree hTree, int bytesPerGroup, int fileSize, BitSetImpl bitSet) {
        bitSet.addInt(bytesPerGroup);
        bitSet.addInt(fileSize);
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
