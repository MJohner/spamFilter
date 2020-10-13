import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class MailReader {
    private static final double spamFactor = 0.7;
    // inspired by: https://gist.github.com/InfoSec812/f7b03ad627f6e194c793aa908febafdc
    public static ConcurrentHashMap<String, Integer> readMail(File file) throws IOException {
        ConcurrentHashMap<String, Integer> mailWords = new ConcurrentHashMap();
        Path filePath = file.toPath();
        Files.readAllLines(filePath, StandardCharsets.ISO_8859_1)
                .parallelStream()
                .map(line -> line.split("\\s+")) // split lines by spaces
                .flatMap(Arrays::stream)// create streams for the lines
                .parallel() // read lines parallel
                .filter(w -> w.matches("[A-Za-z]{2,}+")) // read all words containing chars with length > 1
                .map(String::toLowerCase) // convert all words to lower case
                .forEach(word -> {
                    mailWords.put(word, 1); // put the words to the word hash map
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

    public static double probabilityToBeSpam(Dictionary d, File file) throws IOException {
        MathContext mc = MathContext.DECIMAL64;
        Map<String, Integer> words = readMail(file);
        AtomicReference<BigDecimal> quotient = new AtomicReference(BigDecimal.valueOf(0.0));
        words.forEach((w,p) -> {
            quotient.updateAndGet(v -> v.add(BigDecimal.valueOf((d.probablyToBeHam(w)*(1-spamFactor)) / (d.probablyToBeSpam(w)*spamFactor)), mc));
        });
        return (1/(quotient.get().doubleValue()+1))*100;
    }


}
