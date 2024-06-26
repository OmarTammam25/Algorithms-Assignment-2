import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
//        String f = "gbbct10.seq";
        Huffman h = new Huffman();

        String inputPath = "C:\\Omar\\Projects\\Algorithms\\Assignmnet 2\\Huffman_Coding\\non_encoded_files\\gbbct10.seq";
        Path path = Paths.get(inputPath);
        int n = 1;

        long begin = System.currentTimeMillis();
        String encodedFile = h.encode(path, n);
//        h.encode("non_encoded_files/" + f, 5);
//        h.encode("non_encoded_files/osos.jpg", 3);
//        h.encode("non_encoded_files/lec.pdf", 1);
//        h.encode("non_encoded_files/gbbct10.seq", 1);
//        h.encode("non_encoded_files/test.txt",2);
//        h.encode("non_encoded_files/micheal.txt", 2);
//        h.encode("non_encoded_files/osos.jpg", 3);
        long end = System.currentTimeMillis();
        System.out.println("encoding time taken = " + (end - begin) + " ms");


        begin = System.currentTimeMillis();
        String inputDecompPath = encodedFile;
        Path decompPath = Paths.get(inputDecompPath);

//        Path compressedFileName = path.getFileName();
//        String[] s = compressedFileName.toString().split("\\.");
        String fileName = "extracted." + path.getFileName();
        h.decode(decompPath, fileName);
//        h.decode("non_encoded_files/osos.jpg.hc", "encoded_files/osos.jpg");
//        h.decode("non_encoded_files/lec.pdf.hc", "encoded_files/lec.pdf");
//        h.decode("non_encoded_files/gbbct10.seq.hc", "encoded_files/gbbct10.seq");
//        h.decode("non_encoded_files/test.txt.hc", "encoded_files/test.txt");
//        h.decode("non_encoded_files/micheal.txt.hc", "encoded_files/micheal.txt");
//        h.decode("non_encoded_files/vid.mkv.hc", "encoded_files/vid.mkv");
//        h.decode("non_encoded_files/osos.jpg.hc", "encoded_files/osos.jpg");
        end = System.currentTimeMillis();
        System.out.println("decoding time taken = " + (end - begin) + " ms");

        FileTester fileTester = new FileTester();
//        boolean f = fileTester.test(System.getProperty("user.dir") + "/non_encoded_files/lec.pdf",
//                System.getProperty("user.dir") + "/encoded_files/lec.pdf");

        double compressionRatio = (double)getFileSize(encodedFile) / h.getFileSize(inputPath);
        System.out.println("Compression Ratio: " + Double.toString(compressionRatio));
//        boolean ret = fileTester.test(System.getProperty("user.dir") + "/non_encoded_files/" + f,
//                System.getProperty("user.dir") + "/encoded_files/" + f);

//        System.out.println(ret);
    }

    public static long getFileSize(String path) {
        File file = new File(path);
        return file.length();
    }
}