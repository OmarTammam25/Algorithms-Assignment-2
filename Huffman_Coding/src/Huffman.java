import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Huffman {
    public void encode(String path, int bytesPerGroup) {
//        byte[] fileBytes = getFileBytes(path);

        // Create a frequency map of the bytes in the file

        EfficientFileReader fr = new EfficientFileReader(path);
        long begin = System.currentTimeMillis();
        Map<ByteGroup, Integer> frequencyMap = new HashMap<>();
        populateFrequencyMap(fr, bytesPerGroup, frequencyMap);
        long end = System.currentTimeMillis();
        System.out.println("File read in " + (end - begin) + " ms");

        // Prepare huffman data
        HuffmanTree hTree = new HuffmanTree(frequencyMap);
        HuffmanNode root = hTree.buildTree();
//        System.out.println("encoding tree: ");
//        try {
//            FileOutputStream w = new FileOutputStream("encodingtree.txt");
//            dfs(root, w);
//        } catch (IOException e){
//
//        }
//        try{
//            w.writer.close();
//        } catch(IOException e) {
//
//        }
        // TODO change this to bitset or smth not a string
        Map<ByteGroup, String> encodingMap = hTree.buildEncodingMap();

        // Encode the file in bitset
        BitSetImpl bitSet = new BitSetImpl(encodingMap.size() * 2 * bytesPerGroup + 16);
        prepareHeader(hTree, bytesPerGroup, getFileSize(path), bitSet);
        EfficientFileWriter outReader = new EfficientFileWriter(path + ".hc");
        outReader.writeToFile(bitSet);

        fr = new EfficientFileReader(path);
        writeDataToFile(encodingMap, bytesPerGroup, fr, outReader);
        try{
            outReader.writer.close();
        } catch (IOException e){

        }
//        prepareFile(fileBytes, encodingMap, bytesPerGroup, bitSet);

//        int lstByte = getLastBytePosition(bitSet);
//        byte[] scaledBytes = new byte[lstByte + 1];
//        System.arraycopy(bitSet.getByteArray(), 0, scaledBytes, 0, lstByte + 1);
//        bitSet = new BitSetImpl(scaledBytes);
//
//        // Write the encoded file
//        Path p = Paths.get(System.getProperty("user.dir"), path + ".hc");
//        try {
//            Files.write(p, bitSet.getByteArray());
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
//        }

    }

    public long getFileSize(String path) {
        File file = new File(path);
        return file.length();
    }

    public void decode(String path, String fileName) {
        EfficientFileReader fr = new EfficientFileReader(path);

//        byte[] fileBytes = getFileBytes(path);
//        BitSetImpl bitSet = new BitSetImpl(fileBytes);
        int bytesPerGroup = fr.getNextInt();
        long fileSize = fr.getNextLong();
        int treeSize = fr.getNextInt();
        BufferReader br = fr.getNext(treeSize);
        BitSetImpl bitSet = new BitSetImpl(br.buffer, br.size);
        HuffmanNode root = bitSet.getDecodedTree(bytesPerGroup);

//        System.out.println("decoded tree: ");
//        try {
//            FileOutputStream w = new FileOutputStream("decodedTree.txt");
//            dfs(root, w);
//        } catch (IOException e){
//
//        }
//        try{
//            w.writer.flush();
//            w.writer.close();
//        } catch(IOException e) {
//
//        }

        EfficientFileWriter outputReader = new EfficientFileWriter(fileName);

        BufferReader data = fr.getNextChunk();
        bitSet = new BitSetImpl(data.buffer, data.size);
        int i = 0;
        int j = 7;
        Iterators it = new Iterators(i, j);
        while (it.totalSize < fileSize) {
//            decodeFile(data, fileSize, it, outputReader, fr, root);
//            System.out.println("alo");

            HuffmanNode currentNode = root;
            while(true) {
                if(currentNode.isLeaf()){
                    if(it.totalSize + currentNode.bytes.getBytes().length > fileSize)
                    {
                        byte[] temp = new byte[(int) (fileSize - it.totalSize)];
                        System.arraycopy(currentNode.bytes.getBytes(), 0, temp, 0, temp.length);
                        outputReader.writeToFile(temp);
                        it.totalSize = fileSize;
                    } else {
                        outputReader.writeToFile(currentNode.bytes.getBytes());
                        it.totalSize += currentNode.bytes.getBytes().length;
                    }
                    break;
                }
                if (bitSet.currentReadByte == bitSet.getByteArray().length) {
                    data = fr.getNextChunk();
                    if(data.size != 4096)
                        System.out.println("damn");
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

        }
//        while(data.size > 0){
////            BitSetImpl curr = new BitSetImpl(data.buffer, data.size);
////
////            BitSetImpl decodedFileBits = new BitSetImpl(data.size * 8);
////            for (int i = 0; i < data.size; i++) {
////                outputReader.writeToFile(decodedFileBits);
////            }
//            data = fr.getNextChunk();
//        }

//        while (decodedFileBits.getCurrentByteIdx() < fileSize) {
//            decodeFile(bitSet, decodedFileBits, root);
//        }
//
//        // Write the encoded file
//        Path p = Paths.get(System.getProperty("user.dir"), fileName);
//        try {
//            Files.write(p, decodedFileBits.getByteArray());
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
//        }
    }

    private void dfs(HuffmanNode u, FileOutputStream w) {
        if(u.isLeaf()){
//            w.writeToFile(u.bytes.toString());
            try {
//                FileOutputStream os = new FileOutputStream(w.path);
                w.write(u.bytes.toString().getBytes());
            } catch (IOException e) {

            }
//            System.out.println(u.bytes);
            return;
        }
        if(u.right != null)
            dfs(u.right, w);
        if(u.left != null)
            dfs(u.left, w);
    }

    public void decodeFile(BufferReader buffer, long fileSize, Iterators it, EfficientFileWriter outputReader, EfficientFileReader fr, HuffmanNode node) {
        if(node.isLeaf()) {
            if(it.totalSize + node.bytes.getBytes().length > fileSize)
            {
                byte[] temp = new byte[(int) (fileSize - it.totalSize)];
                System.arraycopy(node.bytes.getBytes(), 0, temp, 0, temp.length);
                outputReader.writeToFile(temp);
                it.totalSize = fileSize;
            } else {
                outputReader.writeToFile(node.bytes);
                it.totalSize += node.bytes.getBytes().length;
            }
//            decodedFileBits.insertByteGroup(node.bytes);
//            decodeFile(buffer, it, outputReader, fr, node);
            return;
        }

        if(it.i >= buffer.buffer.length) {
            buffer = fr.getNextChunk();
            if(buffer.size <= 0) {
                return;
            }
//            buffer = br.buffer;
            it.i = 0;
            it.j = 7;
        }

        int bit = (((buffer.buffer[it.i] & 0xFF) >>> it.j) & 1);
        if((((buffer.buffer[it.i] & 0xFF) >>> it.j) & 0xFF & 1) == 1) {
            if(it.j == 0){
                it.i++;
                it.j = 7;
            }else{
                it.j--;
            }
            decodeFile(buffer, fileSize, it, outputReader, fr, node.left);
        }else {
            if(it.j == 0){
                it.i++;
                it.j = 7;
            }else{
                it.j--;
            }
            decodeFile(buffer, fileSize, it, outputReader, fr, node.right);
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

    private void populateFrequencyMap(EfficientFileReader fr, int bytesPerGroup, Map<ByteGroup, Integer> frequencyMap) {
        BufferReader data = fr.getNextChunk();
        while (data.size > 0) {
            populateFrequencyMap(data.buffer, bytesPerGroup, frequencyMap);
            data = fr.getNextChunk();
        }
    }


    private void populateFrequencyMap(byte[] fileBytes, int bytesPerGroup, Map<ByteGroup, Integer> frequencyMap) {
//        Map<ByteGroup, Integer> frequencyMap = new HashMap<>();
        for(int i = 0; i < fileBytes.length; i += bytesPerGroup) {
            ByteGroup currentByteGroup = convertBytesToByteGroup(i, bytesPerGroup, fileBytes);
            frequencyMap.put(currentByteGroup, frequencyMap.getOrDefault(currentByteGroup, 0) + 1);
        }
//        return frequencyMap;
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

    private void prepareHeader(HuffmanTree hTree, int bytesPerGroup, long fileSize, BitSetImpl bitSet) {
        bitSet.addInt(bytesPerGroup);
        bitSet.addLong(fileSize);
        BitSetImpl bitset2 = new BitSetImpl(bitSet.getByteArray().length);
        hTree.buildEncodedTree(bitset2);
        bitSet.addInt(bitset2.getCurrentByteIdx() + (bitset2.getCurrentBitIdx() != 7 ? 1 : 0));
        bitSet.addBitSet(bitset2);
    }

    private void writeDataToFile(Map<ByteGroup, String> encodingMap, int bytesPerGroup, EfficientFileReader inputReader, EfficientFileWriter outReader) {
        BufferReader data = inputReader.getNextChunk();
        BitSetImpl bitset = new BitSetImpl(1);
        while(data.size > 0) {
            prepareFile(data, encodingMap, bytesPerGroup, bitset, outReader);
//            outReader.writeToFile(bitset);
            data = inputReader.getNextChunk();
        }
        if(bitset.getCurrentByteIdx() != 0 || bitset.getCurrentBitIdx() != 7)
            outReader.writeToFile(bitset);
    }

    private void prepareFile(BufferReader br, Map<ByteGroup, String> encodingMap, int bytesPerGroup, BitSetImpl bitSet, EfficientFileWriter outReader) {
        byte[] fileBytes = br.buffer;

        for (int i = 0; i < br.size; i += bytesPerGroup) {
            ByteGroup currentByteGroup = convertBytesToByteGroup(i, bytesPerGroup, fileBytes);
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
//        bitSet.insertOne();
    }

    class Iterators {
        public int i;
        public int j;
        public long totalSize;
        Iterators(int i, int j){
            this.i = i;
            this.j = j;
            totalSize = 0;
        }
    }

}
