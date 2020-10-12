import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MailReader {
    // inspired by: https://gist.github.com/InfoSec812/f7b03ad627f6e194c793aa908febafdc
    public static Map<String, Integer> readMail(File file) throws IOException {
        ConcurrentHashMap<String, Integer> mailWords = new ConcurrentHashMap();
        Path filePath = file.toPath();
        Files.readAllLines(filePath, StandardCharsets.ISO_8859_1)
                .parallelStream()
                .map(line -> line.split("\\s+"))
                .flatMap(Arrays::stream)
                .parallel()
                .filter(w -> w.matches("\\w+"))
                .map(String::toLowerCase)
                .forEach(word -> {
                    mailWords.put(word, 1);
                });
        return mailWords;
    }

    public static void readMailsToSpamDirectory(Dictionary d, File folder) throws IOException {
        for(File f:folder.listFiles()){
            d.addSpamWords(1, readMail(f));
        }
    }

    public static void readMailsToHamDirectory(Dictionary d, File folder) throws IOException {
        for(File f:folder.listFiles()){
            d.addHamWords(1, readMail(f));
        }
    }


}
