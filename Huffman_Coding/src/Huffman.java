import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Huffman {
    public void encode(String path, int bytesPerGroup) {

        byte[] fileBytes = getFileBytes(path);

        // Create a frequency map of the bytes in the file
        Map<ByteGroup, Integer> frequencyMap = populateFrequencyMap(fileBytes, bytesPerGroup);

        HuffmanTree hTree = new HuffmanTree(frequencyMap);
        HuffmanNode root = hTree.buildTree();

        Map<ByteGroup, String> encodingMap = hTree.buildEncoding();
        saveEncodedFile(fileBytes, encodingMap, bytesPerGroup);

    }

    private byte[] getFileBytes(String path) {
        byte[] fileBytes;
        try {
            Path p = Paths.get(System.getProperty("user.dir"), "non_encoded_files", path);
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

    private void saveEncodedFile(byte[] fileBytes, Map<ByteGroup, String> encodingMap, int bytesPerGroup) {
        ArrayList<Byte> encodedBytes = new ArrayList<>();
        Byte currentByte = 0x0;
        int byteIndex = 7;
        for (int i = 0; i < fileBytes.length; i += bytesPerGroup) {
            ByteGroup currentByteGroup = convertBytesToByteGroup(i, bytesPerGroup, fileBytes);
            String currentEncoding = encodingMap.get(currentByteGroup);

            for (int j = 0; j < currentEncoding.length(); j++) {
                if (currentEncoding.charAt(j) == '1') {
                    currentByte = (byte) (currentByte | (1 << byteIndex));
                }
                byteIndex--;
                if (byteIndex < 0) {
                    encodedBytes.add(currentByte);
                    currentByte = 0x0;
                    byteIndex = 7;
                }
            }
        }
        if (byteIndex != 7) {
            encodedBytes.add(currentByte);
        }

        byte[] encodedBytesArray = new byte[encodedBytes.size()];
        for (int i = 0; i < encodedBytes.size(); i++) {
            encodedBytesArray[i] = encodedBytes.get(i);
        }

        Path p = Paths.get(System.getProperty("user.dir"), "encoded_files","lec.bin");
        try {
            Files.write(p, encodedBytesArray);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
