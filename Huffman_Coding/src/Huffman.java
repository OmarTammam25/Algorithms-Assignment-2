import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Huffman {
    public void encode(String path, int bytesPerGroup) {

        // Create a frequency map of the bytes in the file
        EfficientFileReader fr = new EfficientFileReader(path);
//        long begin = System.currentTimeMillis();
        Map<ByteGroup, Integer> frequencyMap = new HashMap<>();
        populateFrequencyMap(fr, bytesPerGroup, frequencyMap);
//        long end = System.currentTimeMillis();
//        System.out.println("File read in " + (end - begin) + " ms");

        // Prepare huffman data
        HuffmanTree hTree = new HuffmanTree(frequencyMap);
        HuffmanNode root = hTree.buildTree();

        // TODO change this to bitset or smth not a string
        Map<ByteGroup, String> encodingMap = hTree.buildEncodingMap();

        // Encode the file in bitset
//        BitSetImpl bitSet = new BitSetImpl(encodingMap.size() * 2 * bytesPerGroup + 16);
        EfficientFileWriter outReader = new EfficientFileWriter(path + ".hc");
        int treeSize = (encodingMap.size()) * 2 * bytesPerGroup;
        prepareAndWriteHeaders(hTree, bytesPerGroup, getFileSize(path), treeSize, outReader);

        fr = new EfficientFileReader(path);
        writeDataToFile(encodingMap, bytesPerGroup, fr, outReader);

        try{
            outReader.writer.close();
        } catch (IOException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    public long getFileSize(String path) {
        File file = new File(path);
        return file.length();
    }

    public void decode(String path, String fileName) {
        EfficientFileReader fr = new EfficientFileReader(path);

        int bytesPerGroup = fr.getNextInt();
        long fileSize = fr.getNextLong();
        int treeSize = fr.getNextInt();
        BufferReader br = fr.getNext(treeSize);
        BitSetImpl bitSet = new BitSetImpl(br.buffer, br.size);
        HuffmanNode root = bitSet.getDecodedTree(bytesPerGroup);

        EfficientFileWriter outputReader = new EfficientFileWriter(fileName);

        BufferReader data = fr.getNextChunk();
        bitSet = new BitSetImpl(data.buffer, data.size);
        long totalSize = 0;
        while (totalSize < fileSize) {

            HuffmanNode currentNode = root;
            while(true) {
                if(currentNode.isLeaf()){
                    if(totalSize + currentNode.bytes.getBytes().length > fileSize)
                    {
                        byte[] temp = new byte[(int) (fileSize - totalSize)];
                        System.arraycopy(currentNode.bytes.getBytes(), 0, temp, 0, temp.length);
                        outputReader.writeToFile(temp);
                        totalSize = fileSize;
                    } else {
                        outputReader.writeToFile(currentNode.bytes.getBytes());
                        totalSize += currentNode.bytes.getBytes().length;
                    }
                    break;
                }
                if (bitSet.currentReadByte == bitSet.getByteArray().length) {
                    data = fr.getNextChunk();
                    bitSet = new BitSetImpl(data.buffer, data.size);
                }

                if(bitSet.getCurrentReadBit()){
                    currentNode = currentNode.left;
                } else {
                    currentNode = currentNode.right;
                }
            }
        }


        try {
            outputReader.writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    private void populateFrequencyMap(EfficientFileReader fr, int bytesPerGroup, Map<ByteGroup, Integer> frequencyMap) {
        BufferReader data = fr.getNextChunk();
        BitSetImpl bitSet = new BitSetImpl(data.buffer, data.size);
        while(data.size > 0) {
            ByteGroup currentByteGroup = new ByteGroup(bytesPerGroup);
            while(currentByteGroup.getIt() != bytesPerGroup) {
                currentByteGroup.insertByte(bitSet.getCurrentReadByte());
                if(bitSet.currentReadByte == bitSet.getByteArray().length)
                {
                    data = fr.getNextChunk();
                    if(data.size <= 0)
                        break;
                    bitSet = new BitSetImpl(data.buffer, data.size);
                }
            }
            frequencyMap.put(currentByteGroup, frequencyMap.getOrDefault(currentByteGroup, 0) + 1);

        }

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

    private void prepareAndWriteHeaders(HuffmanTree hTree, int bytesPerGroup, long fileSize, int treeSize, EfficientFileWriter fw) {
        BitSetImpl bitSet = new BitSetImpl(4);
        bitSet.addInt(bytesPerGroup);
        fw.writeToFile(bitSet);

        bitSet = new BitSetImpl(8);
        bitSet.addLong(fileSize);
        fw.writeToFile(bitSet);


        BitSetImpl bitset2 = new BitSetImpl(treeSize);
        hTree.buildEncodedTree(bitset2);

        bitSet = new BitSetImpl(4);
        bitSet.addInt(bitset2.getCurrentByteIdx() + (bitset2.getCurrentBitIdx() != 7 ? 1 : 0));
        fw.writeToFile(bitSet);

        fw.writeToFile(bitset2);
    }

    private void writeDataToFile(Map<ByteGroup, String> encodingMap, int bytesPerGroup, EfficientFileReader inputReader, EfficientFileWriter outReader) {
        BufferReader data = inputReader.getNextChunk();
        BitSetImpl bitset = new BitSetImpl(1);
        while(data.size > 0) {
            prepareFile(data, encodingMap, bytesPerGroup, bitset, outReader, inputReader);
//            outReader.writeToFile(bitset);
            data = inputReader.getNextChunk();
        }
        if(bitset.getCurrentByteIdx() != 0 || bitset.getCurrentBitIdx() != 7)
            outReader.writeToFile(bitset);
    }

    private void prepareFile(BufferReader br, Map<ByteGroup, String> encodingMap, int bytesPerGroup,
                             BitSetImpl bitSet, EfficientFileWriter outReader, EfficientFileReader fr) {
        byte[] fileBytes = br.buffer;
        BitSetImpl input = new BitSetImpl(br.buffer, br.size);
        while(br.size > 0) {
            ByteGroup currentByteGroup = new ByteGroup(bytesPerGroup);
            for (int i = 0; i < bytesPerGroup; i++) {
                currentByteGroup.insertByte(input.getCurrentReadByte());
                if(input.currentReadByte == input.getByteArray().length)
                {
                    br = fr.getNextChunk();
                    if(br.size <= 0)
                        break;
                    input = new BitSetImpl(br.buffer, br.size);
                }
            }

            String currentEncoding = encodingMap.get(currentByteGroup);
            for (int j = 0; j < currentEncoding.length(); j++) {
                if (currentEncoding.charAt(j) == '1')
                    bitSet.insertOne();
                else
                    bitSet.insertZero();

                if(bitSet.getCurrentBitIdx() == 7){
                    outReader.writeToFile(bitSet);
                    bitSet.reset();
                }
            }

        }
    }

}
