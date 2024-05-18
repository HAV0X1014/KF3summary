import java.io.*;

public class FileHandler {
    public static void write(String filename, String content) {
        try (FileWriter fw = new FileWriter(filename)) {
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String read(String filepath) {
        StringBuilder fullText = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                fullText.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fullText.toString();
    }
}
