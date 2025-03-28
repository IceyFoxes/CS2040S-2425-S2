import java.util.HashMap;
import java.util.Random;

/**
 * This is the main class for your Markov Model.
 *
 * Assume that the text will contain ASCII characters in the range [1,255].
 * ASCII character 0 (the NULL character) will be treated as a non-character.
 *
 * Any such NULL characters in the original text should be ignored.
 */
public class MarkovModel {

	// Use this to generate random numbers as needed
	private Random generator = new Random();

	// This is a special symbol to indicate no character
	public static final char NOCHARACTER = (char) 0;

	/**
	 * Constructor for MarkovModel class.
	 *
	 * @param order the number of characters to identify for the Markov Model sequence
	 * @param seed the seed used by the random number generator
	 */
	HashMap<String, Integer[]> hashMap;
	int order;
	public MarkovModel(int order, long seed) {
		// Initialize your class here
		this.hashMap = new HashMap<String, Integer[]>();
		this.order = order;
		// Initialize the random number generator
		generator.setSeed(seed);
	}

	/**
	 * Builds the Markov Model based on the specified text string.
	 */
	public void initializeText(String text) {
		// Build the Markov model here
		for (int i = 0; i < text.length() - this.order; i++) {
			String key = text.substring(i, i + this.order);
			int value = (int) text.charAt(i + this.order);
			Integer[] frequencyArray = this.hashMap.get(key);
			if (frequencyArray == null) {
				Integer[] newArray = new Integer[256];
				newArray[value] = 1;
				this.hashMap.put(key, newArray);
			} else {
				if (frequencyArray[value] == null) {
					frequencyArray[value] = 1;
				} else {
					frequencyArray[value] += 1;
				}
			}
		}
	}

	/**
	 * Returns the number of times the specified kgram appeared in the text.
	 */
	public int getFrequency(String kgram) {
		if (kgram.length() != this.order) {
			throw new Error("kgram length does not match order!");
		}
		if (!this.hashMap.containsKey(kgram)) {
			return 0;
		}
		Integer[] frequencyArray = this.hashMap.get(kgram);
		int count = 0;
		for (Integer frequency : frequencyArray) {
			if (frequency != null) {
				count += frequency;
			}
		}
		return count;
	}

	/**
	 * Returns the number of times the character c appears immediately after the specified kgram.
	 */
	public int getFrequency(String kgram, char c) {
		if (kgram.length() != this.order) {
			throw new Error("kgram length does not match order!");
		}
		Integer[] frequencyArray = this.hashMap.get(kgram);
		if (frequencyArray[(int) c] == null) {
			return 0;
		}
		return frequencyArray[(int) c];
	}

	/**
	 * Generates the next character from the Markov Model.
	 * Return NOCHARACTER if the kgram is not in the table, or if there is no
	 * valid character following the kgram.
	 */
	public char nextCharacter(String kgram) {
		// See the problem set description for details
		// on how to make the random selection.
		if (kgram.length() != this.order) {
			throw new Error("kgram length does not match order!");
		}
		int freq = this.getFrequency(kgram);
		if (freq == 0) {
			return MarkovModel.NOCHARACTER;
		}
		int randomInt = generator.nextInt(freq);
		char c = 1;
		while (randomInt >= 0) {
			int charFreq = this.getFrequency(kgram, c);
			if (charFreq != 0) {
				randomInt -= charFreq;
			}
			if (randomInt < 0) {
				return c;
			}
			c++;
		}
		return c;
	}
}
