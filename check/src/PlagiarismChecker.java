import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PlagiarismChecker {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PaperChecker <origFile> <copyFile> <outputFile>");
            System.exit(1);
        }

        String origFile = args[0];
        String copyFile = args[1];
        String outputFile = args[2];

        // Check if the files exist and can be read
        if (!new File(origFile).canRead()) {
            System.out.println("Cannot read file: " + origFile);
            System.exit(1);
        }
        if (!new File(copyFile).canRead()) {
            System.out.println("Cannot read file: " + copyFile);
            System.exit(1);
        }

        String origText = "";
        String copyText = "";

        try {
            origText = readFileAsString(origFile);
            copyText = readFileAsString(copyFile);
        } catch (IOException e) {
            System.out.println("Error reading files: " + e.getMessage());
            System.exit(1);
        }

        int[] origHash = simHash(origText);
        int[] copyHash = simHash(copyText);

        double similarity = 1 - (hammingDistance(origHash, copyHash) / (double) origHash.length);

        try (PrintWriter out = new PrintWriter(outputFile)) {
            out.printf("%.2f", similarity);
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to output file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static int[] simHash(String text) {
        int[] v = new int[32];
        for (int i = 0; i < text.length(); i++) {
            int hashCode = text.charAt(i) * 31;
            for (int j = 0; j < v.length; j++) {
                if (((hashCode >> j) & 1) == 1) {
                    v[j]++;
                } else {
                    v[j]--;
                }
            }
        }

        for (int i = 0; i < v.length; i++) {
            v[i] = v[i] > 0 ? 1 : 0;
        }

        return v;
    }

    private static int hammingDistance(int[] a, int[] b) {
        int distance = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                distance++;
            }
        }

        return distance;
    }

    private static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}
