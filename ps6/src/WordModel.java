import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WordModel {
    private Random generator;
    private HashMap<String, HashMap<String, Integer>> hashMap;
    private int order;

    public WordModel(int order, long seed) {
        this.hashMap = new HashMap<>();
        this.order = order;
        this.generator = new Random(seed);
    }

    /**
     * Builds the Markov Model based on the specified text string.
     */
    public void initializeText(String text) {
        String[] words = text.split("\\s+"); // Split by whitespace
        if (words.length <= order) return; // Not enough words

        for (int i = 0; i <= words.length - order; i++) {
            StringBuilder keyBuilder = new StringBuilder();
            for (int j = 0; j < order; j++) {
                if (j > 0) {
                    keyBuilder.append(" ");
                }
                keyBuilder.append(words[i + j]);
            }
            String key = keyBuilder.toString();
            String nextWord = (i + order < words.length) ? words[i + order] : null;

            // Update the HashMap with word sequences
            if (!hashMap.containsKey(key)) {
                hashMap.put(key, new HashMap<>());
            }
            if (nextWord != null) {
                hashMap.get(key).put(nextWord, hashMap.get(key).getOrDefault(nextWord, 0) + 1);
            }
        }
    }

    /**
     * Returns the number of times the specified k-gram (sequence of words) appeared in the text.
     */
    public int getFrequency(String kgram) {
        if (!hashMap.containsKey(kgram)) return 0;
        return hashMap.get(kgram).values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Returns the number of times the word 'word' appears immediately after the specified k-gram.
     */
    public int getFrequency(String kgram, String word) {
        if (!hashMap.containsKey(kgram)) return 0;
        return hashMap.get(kgram).getOrDefault(word, 0);
    }

    /**
     * Generates the next word from the Markov Model.
     * Returns null if the k-gram is not in the table.
     */
    public String nextWord(String kgram) {
        if (!hashMap.containsKey(kgram)) return null;

        HashMap<String, Integer> wordFreqMap = hashMap.get(kgram);
        int totalFreq = wordFreqMap.values().stream().mapToInt(Integer::intValue).sum();
        int randomInt = generator.nextInt(totalFreq);

        for (Map.Entry<String, Integer> entry : wordFreqMap.entrySet()) {
            randomInt -= entry.getValue();
            if (randomInt < 0) return entry.getKey();
        }
        return null;
    }
}
