import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class is used to generate text using a word-based Markov Model.
 */
public class WordGenerator {

    // For testing, we will choose different seeds
    private static long seed = 500;

    // Sets the random number generator seed
    public static void setSeed(long s) {
        seed = s;
    }

    /**
     * Reads in the file and builds the WordModel.
     *
     * @param order the order of the Markov Model (word-based)
     * @param fileName the name of the file to read
     * @param model the Markov Model to build
     * @return the first {@code order} words of the file to be used as the seed text
     */
    public static String buildModel(int order, String fileName, WordModel model) {
        // Use StringBuilder to store the text from the file
        StringBuilder text = new StringBuilder();

        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNext()) {
                text.append(scanner.next()).append(" ");
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Problem reading file " + fileName + ".");
            return null;
        }

        // Convert text to words
        String[] words = text.toString().trim().split("\\s+");

        // Make sure text has enough words
        if (words.length < order) {
            System.out.println("Text is shorter than specified Markov Order.");
            return null;
        }

        // Build the Markov Model
        model.initializeText(text.toString());

        // Return the first `order` words as the seed text
        StringBuilder seedText = new StringBuilder();
        for (int i = 0; i < order; i++) {
            if (i > 0) seedText.append(" ");
            seedText.append(words[i]);
        }
        return seedText.toString();
    }

    /**
     * Generates text of the specified length using the given Markov Model.
     *
     * @param model the Markov Model to use
     * @param seedText the initial kgram (sequence of words) used to generate text
     * @param order the order of the Markov Model (number of words in n-gram)
     * @param length the number of words to generate
     */
    public static void generateText(WordModel model, String seedText, int order, int length) {
        StringBuilder generatedText = new StringBuilder(seedText);

        String currentKgram = seedText;
        int wordsGenerated = order;

        while (wordsGenerated < length) {
            String nextWord = model.nextWord(currentKgram);

            // Stop if there is no valid next word
            if (nextWord == null) {
                System.out.println(generatedText);
                return;
            }

            generatedText.append(" ").append(nextWord);
            wordsGenerated++;

            // Update kgram to maintain the correct sequence
            String[] kgramWords = currentKgram.split("\\s+");
            StringBuilder newKgram = new StringBuilder();

            for (int i = 1; i < order; i++) { // Shift words left
                newKgram.append(kgramWords[i]).append(" ");
            }
            newKgram.append(nextWord);
            currentKgram = newKgram.toString();
        }

        // Output the generated text
        System.out.println(generatedText);
    }

    /**
     * The main routine. Takes 3 arguments:
     * args[0]: the order of the Markov Model (word-based)
     * args[1]: the length of the text to generate (in words)
     * args[2]: the filename for the input text
     */
    public static void main(String[] args) {
        // Check that we have three parameters
        if (args.length != 3) {
            System.out.println("Usage: java WordGenerator <order> <length> <filename>");
            return;
        }

        // Parse input parameters
        int order = Integer.parseInt(args[0]);
        int length = Integer.parseInt(args[1]);
        String fileName = args[2];

        // Create the model
        WordModel WordModel = new WordModel(order, seed);
        String seedText = buildModel(order, fileName, WordModel);

        if (seedText == null) {
            System.out.println("Failed to generate seed text.");
            return;
        }

        // Generate text
        generateText(WordModel, seedText, order, length);
    }
}
