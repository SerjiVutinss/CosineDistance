package ie.gmit.sw;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CosineSimilarity {

	// TO calculate cosine similarity...
	// Get the dot product of vectors a and b (dot)
	// Get magnitude a * magnitude b (mag)
	// Divide the dot product by the product of the magnitudes (dot / mag)

	public static double cosineSimilarity(String text1, String text2, int k) {

		int[] shingleHash = Utils.chunkStringChars(text1, k);
		int[] shingleHash2 = Utils.chunkStringChars(text2, k);

		Map<Integer, Integer> a = CosineSimilarity.getIntFrequencyMap(shingleHash);
		Map<Integer, Integer> b = CosineSimilarity.getIntFrequencyMap(shingleHash2);

		double dotProduct = 0, magnitudeA = 0, magnitudeB = 0;
		dotProduct = CosineSimilarity.getDotProduct(a, b);

		magnitudeA = CosineSimilarity.getMagnitude(a);
		magnitudeB = CosineSimilarity.getMagnitude(b);

		// return cosine similarity
		return dotProduct / Math.sqrt(magnitudeA * magnitudeB);
	}

	/**
	 * @param terms values to analyze
	 * @return a map containing unique terms and their frequency
	 */
	public static Map<Integer, Integer> getIntFrequencyMap(int[] terms) {
		Map<Integer, Integer> termFrequencyMap = new HashMap<>();
		for (int i : terms) {
			Integer n = termFrequencyMap.get(i);
			n = (n == null) ? 1 : ++n;
			termFrequencyMap.put(i, n);
		}
		return termFrequencyMap;
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return dot product
	 */
	public static double getDotProduct(Map<Integer, Integer> a, Map<Integer, Integer> b) {
		double dotProduct = 0;

		// Get unique words from both sequences
		HashSet<Integer> intersection = new HashSet<>(a.keySet());
		intersection.retainAll(b.keySet());

		// Calculate dot product
		for (int item : intersection) {
			dotProduct += a.get(item) * b.get(item);
		}
		return dotProduct;
	}

	/**
	 * 
	 * @param a term frequency map - Map<String, Integer>
	 * @return a double representing the magnitude of the frequency
	 */
	public static double getMagnitude(Map<Integer, Integer> m) {
		double mag = 0;
		for (int s : m.keySet()) {
			mag += Math.pow(m.get(s), 2);
		}
		return mag;
	}

}