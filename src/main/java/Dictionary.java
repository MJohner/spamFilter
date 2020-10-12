import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dictionary {
    private int numberOfSpamMailsAnalyzed;
    private int numberOfHamMailsAnalyzed;
    private ConcurrentHashMap<String, Integer> hamWords = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Integer> spamWords = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Double> probabilityHamWords = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Double> probabilitySpamWords = new ConcurrentHashMap();

    // https://www.baeldung.com/java-merge-maps
    public void addHamWords(int analyzedMails, Map<String, Integer> mails){
        numberOfHamMailsAnalyzed += analyzedMails;
        mails.forEach((word, numberOfOccurrence) -> {
            hamWords.merge(word, numberOfOccurrence, (numberOfAnalyzedMails, numberOfNewMails)-> numberOfNewMails + numberOfAnalyzedMails);
        });
    }

    public void addSpamWords(int analyzedMails, Map<String, Integer> mails){
        numberOfSpamMailsAnalyzed += analyzedMails;
        mails.forEach((word, numberOfOccurrence) -> {
            spamWords.merge(word, numberOfOccurrence, (numberOfAnalyzedMails, numberOfNewMails)-> numberOfNewMails + numberOfAnalyzedMails);
        });
    }

    public void calculateProbability(){
        probabilityHamWords.clear();
        probabilitySpamWords.clear();
        hamWords.forEach(1000,(word, numberOfOccurrence)->{
            probabilityHamWords.put(word, (double) numberOfOccurrence / numberOfHamMailsAnalyzed);
        });
        spamWords.forEach(1000,(word, numberOfOccurrence)->{
            probabilitySpamWords.put(word, (double) numberOfOccurrence / numberOfSpamMailsAnalyzed);
        });
    }

    public ConcurrentHashMap getHamWords(){
        return hamWords;
    }
    public ConcurrentHashMap getSpamWords(){
        return spamWords;
    }

    public ConcurrentHashMap getProbabilityHamWords(){
        return probabilityHamWords;
    }
    public ConcurrentHashMap getProbabilitySpamWords(){
        return probabilitySpamWords;
    }
}
