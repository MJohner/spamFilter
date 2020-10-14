import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class Main {

    public static void main(String args[]) throws IOException {
        Dictionary d = new Dictionary();
        MailReader.readMailsToHamDirectory(d, new File("src/resources/ham-anlern"));
        MailReader.readMailsToSpamDirectory(d, new File("src/resources/spam-anlern"));
        d.calculateProbability();
        // System.out.println(bestAlpha(d));
        testMails(d);
    }


    public static void testMails(Dictionary d) throws IOException {
        double alpha = 0.79;
        File spamFolder = new File("src/resources/spam-test");
        File hamFolder = new File("src/resources/ham-test");
        int spam = 0;
        int ham = 0;
        for (File f : spamFolder.listFiles()) {
            if (MailReader.probabilityToBeSpam(d, f) > alpha) {
                spam++;
            }
        }
        for (File f : hamFolder.listFiles()) {
            if (MailReader.probabilityToBeSpam(d, f) > alpha) {
                ham++;
            }
        }

        DecimalFormat df = new DecimalFormat("#.##");
        int analyzedHamMails = hamFolder.listFiles().length;
        int analyzedSpamMails = spamFolder.listFiles().length;
        double correctAnalyzedHamMails = 100 - (100.0/analyzedHamMails)*ham;
        double correctAnalyzedSpamMails = (100.0 / analyzedSpamMails)*spam;
        double averagePrecision =  (correctAnalyzedHamMails + correctAnalyzedSpamMails) / 2;

        System.out.println("----Exercise conclusion----");
        System.out.println("The highest statistic precision could be achieved with an alpha value of: " + alpha + " and an estimated spam rate of " + MailReader.spamFactor*100 +"%.");
        System.out.println("The weighting of an false positive ham and a false negative spam are set to equal.");
        System.out.println("The minimum occurrence probability of a word in a mail was set to: "+ d.minWordOccurrenceProbability);
        System.out.println();
        System.out.println("Analyzed ham mails: ");
        System.out.println(ham + " of " + analyzedHamMails + " where false classified as spam");
        System.out.println(df.format(correctAnalyzedHamMails) + "% of the mails have been correct classified.");
        System.out.println();
        System.out.println("Analyzed spam mails: ");
        System.out.println(spam + " of " + analyzedSpamMails + " where correct classified as spam");
        System.out.println(df.format(correctAnalyzedSpamMails) + "% of the mails have been correct classified.");
        System.out.println("The average precision of the statistic analysis: " + df.format(averagePrecision) + "%");

    }

    /**
     *
     * @param d
     * @return
     * @throws NullPointerException
     * @throws IOException
     *
     * bestAlpha calculates the best alpha value to determine the if a mail is spam or not
     */
    private static double bestAlpha(Dictionary d) throws NullPointerException, IOException {

        double bestAlpha = 0.0;
        double bestResult = 0.0;
        double currentResult;
        File spamFolder = new File("src/resources/spam-kallibrierung");
        File hamFolder = new File("src/resources/ham-kallibrierung");

        for (double alpha = 0.1; alpha < 1.5; alpha += 0.1) {
            int spam = 0;
            int ham = 0;
            for (File f : spamFolder.listFiles()) {
                if (MailReader.probabilityToBeSpam(d, f) > alpha) {
                    spam++;
                }
            }
            for (File f : hamFolder.listFiles()) {
                if (MailReader.probabilityToBeSpam(d, f) > alpha) {
                    ham++;
                }
            }
            // the currentResult is the average of correct determined mails
            currentResult = ((100.0 / spamFolder.listFiles().length * spam) + (100 - (100.0 / hamFolder.listFiles().length * ham)))/2;
            if (bestResult < currentResult) {
                bestResult = currentResult;
                bestAlpha = alpha;
                System.out.println("Current best: " + currentResult);
                System.out.println("Spam: " + spam + " of " + spamFolder.listFiles().length);
                System.out.println("Ham: " + ham + " of " + hamFolder.listFiles().length);
            }
        }
        return bestAlpha;
    }
}
