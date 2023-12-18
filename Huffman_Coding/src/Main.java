public class Main {
    public static void main(String[] args) {
        Huffman h = new Huffman();
        long begin = System.currentTimeMillis();
//        h.encode("non_encoded_files/gbbct10.seq", 1);
        h.encode("non_encoded_files/test.txt", 1);
        long end = System.currentTimeMillis();
        System.out.println("encoding time taken = " + (end - begin) + " ms");


        begin = System.currentTimeMillis();
        h.decode("non_encoded_files/test.txt.hc", "encoded_files/test.txt");
        end = System.currentTimeMillis();
        System.out.println("decoding time taken = " + (end - begin) + " ms");
    }
}