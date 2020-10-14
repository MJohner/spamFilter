import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException {
        Dictionary d = new Dictionary();
        MailReader.readMailsToHamDirectory(d, new File("src/resources/ham-anlern"));
        MailReader.readMailsToSpamDirectory(d, new File("src/resources/spam-anlern"));
        d.calculateProbability();
        System.out.println(bestThreshold(d));
    }

    private static double bestThreshold(Dictionary d) throws NullPointerException, IOException {

        double bestThreshold = 0.0;
        double bestResult = 0.0;
        double currentResult;
        File spamFolder = new File("src/resources/spam-kallibrierung");
        File hamFolder = new File("src/resources/ham-kallibrierung");

        for (double threshold = 0.5; threshold < 1.5; threshold += 0.1) {
            int spam = 0;
            int ham = 0;
            for (File f : spamFolder.listFiles()) {
                if (MailReader.probabilityToBeSpam(d, f) > threshold) {
                    spam++;
                }
            }
            for (File f : hamFolder.listFiles()) {
                if (MailReader.probabilityToBeSpam(d, f) > threshold) {
                    ham++;
                }
            }
            currentResult = (100.0 - (100.0 / hamFolder.listFiles().length * ham) + (100.0 / spamFolder.listFiles().length * spam));
            if (bestResult < currentResult) {
                bestResult = currentResult;
                bestThreshold = threshold;
                System.out.println("Current best: ");
                System.out.println("Spam: " + spam + " of " + spamFolder.listFiles().length);
                System.out.println("Ham: " + ham + " of " + hamFolder.listFiles().length);
            }
        }
        return bestThreshold;
    }
}
