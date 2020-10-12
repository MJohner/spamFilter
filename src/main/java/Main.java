import java.io.IOException;
import java.util.TreeSet;

public class Main {

    public static void main(String args[]) throws IOException {

        TreeSet<String> mailWords = MailReader.readMail("src/resources/ham-anlern/0126.d002ec3f8a9aff31258bf03d62abdafa");
        mailWords.forEach(w->{
            System.out.println(w);
        });
    }
}
