import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;


public class Dictionary {

    // the minimum occurrence of a word to be used in the probability calculations
    public static final double minWordOccurrenceProbability = 0.001;
    private int numberOfSpamMailsAnalyzed;
    private int numberOfHamMailsAnalyzed;

    // all words with the number of mails in they occur
    private HashMap<String, Integer> hamWords = new HashMap();
    private HashMap<String, Integer> spamWords = new HashMap();

    // all words with the probability they occur in a spam / ham mail
    private HashMap<String, Double> probabilityHamWords = new HashMap();
    private HashMap<String, Double> probabilitySpamWords = new HashMap();

    /**
     *
     * @param analyzedMails
     * @param mails
     * inspired by https://www.baeldung.com/java-merge-maps
     * Takes a number of analyzed mails and a map of all words from the analyzed mails
     * These words are added to the ham word list
     */

    public void addHamWords(int analyzedMails, HashMap<String, Integer> mails){
        numberOfHamMailsAnalyzed += analyzedMails;
        mails.forEach((word, numberOfOccurrence) -> {
            hamWords.merge(word, numberOfOccurrence, (numberOfAnalyzedMails, numberOfNewMails)-> numberOfNewMails + numberOfAnalyzedMails);
        });
    }

    /**
     *
     * @param analyzedMails
     * @param mails
     *      * inspired by https://www.baeldung.com/java-merge-maps
     *      * Takes a number of analyzed mails and a map of all words from the analyzed mails
     *      * These words are added to the spam word list
     */
    public void addSpamWords(int analyzedMails, HashMap<String, Integer> mails){
        numberOfSpamMailsAnalyzed += analyzedMails;
        mails.forEach((word, numberOfOccurrence) -> {
            spamWords.merge(word, numberOfOccurrence, (numberOfAnalyzedMails, numberOfNewMails)-> numberOfNewMails + numberOfAnalyzedMails);
        });
    }

    /**
     * calculates the probability of the words to occur in ham / spam mails
     * according to the total number of analyzed mails and the number of mails in which the words occur
     */
    public void calculateProbability(){
        calculateProbability(numberOfHamMailsAnalyzed, hamWords, probabilityHamWords);
        calculateProbability(numberOfSpamMailsAnalyzed, spamWords, probabilitySpamWords);
    }

    private void calculateProbability(int numberOfMailsAnalyzed, HashMap<String, Integer> numberOfWords, HashMap<String, Double> probabilityOfWords){
        probabilityOfWords.clear();
        AtomicReference<Double> probabilityOfOccurrence = new AtomicReference<>((double) 0);
        numberOfWords.forEach((word, numberOfOccurrence)->{
            probabilityOfOccurrence.set((double) numberOfOccurrence / numberOfMailsAnalyzed);
            if(probabilityOfOccurrence.get() > minWordOccurrenceProbability){
                probabilityOfWords.put(word, probabilityOfOccurrence.get());
            }
        });
    }

    /**
     *
     * @param word
     * @return
     * returns the probability of a word to be a spam word
     * if the word does not occur in the spam word list, the defined min occurrence will be returned
     * this prevents words from being saved in booth lists and to get divide by 0 errors in the probability calculation
     */
    public double probablyToBeSpam(String word){
        if(probabilitySpamWords.containsKey(word)){
            return probabilitySpamWords.get(word);
        }else{
            return minWordOccurrenceProbability;
        }
    }
    /**
     *
     * @param word
     * @return
     * returns the probability of a word to be a ham word
     * if the word does not occur in the ham word list, the defined min occurrence will be returned
     * this prevents words from being saved in booth lists and to get divide by 0 errors in the probability calculation
     */
    public double probablyToBeHam(String word){
        if(probabilityHamWords.containsKey(word)){
            return probabilityHamWords.get(word);
        }else{
            return minWordOccurrenceProbability;
        }
    }
}
