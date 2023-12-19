public class Main {
    public static void main(String[] args) {
        Huffman h = new Huffman();
        long begin = System.currentTimeMillis();
//        h.encode("non_encoded_files/img.png", 1);
        h.encode("non_encoded_files/gbbct10.seq", 4);
//        h.encode("non_encoded_files/test.txt", 2);
        long end = System.currentTimeMillis();
        System.out.println("encoding time taken = " + (end - begin) + " ms");


        begin = System.currentTimeMillis();
//        h.decode("non_encoded_files/img.png.hc", "encoded_files/img.png");
        h.decode("non_encoded_files/gbbct10.seq.hc", "encoded_files/gbbct10.seq");
//        h.decode("non_encoded_files/test.txt.hc", "encoded_files/test.txt");
        end = System.currentTimeMillis();
        System.out.println("decoding time taken = " + (end - begin) + " ms");
    }
}