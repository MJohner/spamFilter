import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;


public class Dictionary {
    private static final Long parallelismThreshold = Long.MAX_VALUE;
    private static final double minWordOccurrenceProbability = 0.001;
    private int numberOfSpamMailsAnalyzed;
    private int numberOfHamMailsAnalyzed;
    private ConcurrentHashMap<String, Integer> hamWords = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Integer> spamWords = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Double> probabilityHamWords = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Double> probabilitySpamWords = new ConcurrentHashMap();

    // https://www.baeldung.com/java-merge-maps
    public void addHamWords(int analyzedMails, ConcurrentHashMap<String, Integer> mails){
        numberOfHamMailsAnalyzed += analyzedMails;
        mails.forEach(parallelismThreshold, (word, numberOfOccurrence) -> {
            hamWords.merge(word, numberOfOccurrence, (numberOfAnalyzedMails, numberOfNewMails)-> numberOfNewMails + numberOfAnalyzedMails);
        });
    }

    public void addSpamWords(int analyzedMails, ConcurrentHashMap<String, Integer> mails){
        numberOfSpamMailsAnalyzed += analyzedMails;
        mails.forEach(parallelismThreshold, (word, numberOfOccurrence) -> {
            spamWords.merge(word, numberOfOccurrence, (numberOfAnalyzedMails, numberOfNewMails)-> numberOfNewMails + numberOfAnalyzedMails);
        });
    }

    public void calculateProbability(){
        calculateProbability(numberOfHamMailsAnalyzed, hamWords, probabilityHamWords);
        calculateProbability(numberOfSpamMailsAnalyzed, spamWords, probabilitySpamWords);
    }

    private void calculateProbability(int numberOfMailsAnalyzed, ConcurrentHashMap<String, Integer> numberOfWords, ConcurrentHashMap<String, Double> probabilityOfWords){
        probabilityOfWords.clear();
        AtomicReference<Double> probabilityOfOccurrence = new AtomicReference<>((double) 0);
        numberOfWords.forEach(parallelismThreshold,(word, numberOfOccurrence)->{
            probabilityOfOccurrence.set((double) numberOfOccurrence / numberOfMailsAnalyzed);
            if(probabilityOfOccurrence.get() > minWordOccurrenceProbability){
                probabilityOfWords.put(word, probabilityOfOccurrence.get());
            }
        });
    }

    public double probablyToBeSpam(String word){
        if(probabilitySpamWords.containsKey(word)){
            return probabilitySpamWords.get(word);
        }else{
            return minWordOccurrenceProbability;
        }
    }

    public double probablyToBeHam(String word){
        if(probabilityHamWords.containsKey(word)){
            return probabilityHamWords.get(word);
        }else{
            return minWordOccurrenceProbability;
        }
    }
}
