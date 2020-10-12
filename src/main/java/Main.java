import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException {
        Dictionary d = new Dictionary();
        MailReader.readMailsToHamDirectory(d, new File("src/resources/ham-anlern"));
        MailReader.readMailsToSpamDirectory(d, new File("src/resources/spam-anlern"));

        d.calculateProbability();
        System.out.println("Nr of ham words: " + d.getProbabilityHamWords().size());
        System.out.println("Nr of spam words: " + d.getProbabilitySpamWords().size());
        System.out.println("Nr of ham words: " + d.getHamWords().size());
        System.out.println("Nr of spam words: " + d.getSpamWords().size());

    }
}
