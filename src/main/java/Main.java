import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException {
        Dictionary d = new Dictionary();
        MailReader.readMailsToHamDirectory(d, new File("src/resources/ham-anlern"));
        MailReader.readMailsToSpamDirectory(d, new File("src/resources/spam-anlern"));
        d.calculateProbability();

        File spamFolder = new File("src/resources/spam-anlern");
        File hamFolder = new File("src/resources/ham-anlern");
        double spam = 0;
        double ham = 0;
        for(File f:spamFolder.listFiles()){
            spam += MailReader.probabilityToBeSpam(d,f);
        }

        for(File f:hamFolder.listFiles()){
            ham += MailReader.probabilityToBeSpam(d,f);
        }
        System.out.println("Ham: " + ham / hamFolder.listFiles().length);
        System.out.println("Spam: " + spam /  spamFolder.listFiles().length);
    }
}
