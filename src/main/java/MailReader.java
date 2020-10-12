import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class MailReader {

    // inspired by: https://gist.github.com/InfoSec812/f7b03ad627f6e194c793aa908febafdc
    public static TreeSet<String> readMail(String fileName) throws IOException {
        ConcurrentSkipListSet<String> mailWords = new ConcurrentSkipListSet();
        Path filePath = Paths.get(fileName);
        Files.readAllLines(filePath)
                .parallelStream()
                .map(line -> line.split("\\s+"))
                .flatMap(Arrays::stream)
                .parallel()
                .filter(w -> w.matches("\\w+"))
                .map(String::toLowerCase)
                .forEach(word -> {
                    mailWords.add(word);
                });
        return new TreeSet<>(mailWords);
    }
}
