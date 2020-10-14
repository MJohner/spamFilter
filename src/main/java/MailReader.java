import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class MailReader {
    // estimated proportion of spam mails between 1 and 0
    public static final double spamFactor = 0.6;

    /**
     *
     * @param file
     * @return HashMap of words in file
     * @throws IOException
     * inspired by: https://gist.github.com/InfoSec812/f7b03ad627f6e194c793aa908febafdc
     * takes a file and generates a HashMap of all words containing alphabetic chars with length > 1
     */
    public static HashMap<String, Integer> readMail(File file) throws IOException {
        ConcurrentHashMap<String, Integer> mailWords = new ConcurrentHashMap();
        Path filePath = file.toPath();
        Files.readAllLines(filePath, StandardCharsets.ISO_8859_1)
                .parallelStream()
                .map(line -> line.split("[\\s]+")) // split lines by spaces
                .flatMap(Arrays::stream)// create streams for the lines
                .parallel() // read lines parallel
                .filter(w -> w.matches("[A-Za-z]+")) // read all words containing chars with length > 1
                .map(String::toLowerCase) // convert all words to lower case
                .forEach(word -> {
                    mailWords.put(word, 1); // put the words to the word hash map
                });
        return new HashMap(mailWords);
    }

    /**
     *
     * @param d
     * @param folder
     * @throws IOException
     * Takes an existing directory and adds all words from all files in folder to the spam word list of directory
     */
    public static void readMailsToSpamDirectory(Dictionary d, File folder) throws IOException {
        for(File f:folder.listFiles()){
            d.addSpamWords(1, readMail(f));
        }
    }

    /**
     *
     * @param d
     * @param folder
     * @throws IOException
     * Takes an existing directory and adds all words from all files in folder to the ham word list of dictionary
     */
    public static void readMailsToHamDirectory(Dictionary d, File folder) throws IOException {
        for(File f:folder.listFiles()){
            d.addHamWords(1, readMail(f));
        }
    }

    /**
     *
     * @param d
     * @param file
     * @return
     * @throws IOException
     * Calculates the probability of a file to be spam according to its containing words and the words in the spam / ham list of the dictionary
     */
    public static double probabilityToBeSpam(Dictionary d, File file) throws IOException {
        // use a precision of 64 bits in the calculation
        MathContext mc = MathContext.DECIMAL64;
        Map<String, Integer> words = readMail(file);
        AtomicReference<BigDecimal> quotient = new AtomicReference(BigDecimal.valueOf(0.0));
        words.forEach((w,p) -> {
            quotient.updateAndGet(v -> v.add(BigDecimal.valueOf((d.probablyToBeHam(w)*(1-spamFactor)) / (d.probablyToBeSpam(w)*spamFactor)), mc));
        });
        return (1/(quotient.get().doubleValue()+1))*100;
    }


}
