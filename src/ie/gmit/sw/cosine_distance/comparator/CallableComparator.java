package ie.gmit.sw.cosine_distance.comparator;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;

import ie.gmit.sw.Utils;
import ie.gmit.sw.cosine_distance.data_structures.MapBlock;
import ie.gmit.sw.cosine_distance.data_structures.ResultBlock;

/**
 * Compares two MapBlock objects and returns a Future ResultBlock object.
 * 
 * Returned Future is never used as result is simply printed to console from
 * within this class
 * 
 * @author Justin
 *
 */
public class CallableComparator implements Callable<ResultBlock> {

	private MapBlock query;
	private MapBlock subject;

	/**
	 * Constructor
	 * 
	 * @param query   the query MapBlock
	 * @param subject the subject MapBlock
	 */
	public CallableComparator(MapBlock query, MapBlock subject) {
		this.query = query;
		this.subject = subject;
	}

	/*
	 * Performs the operations to calculate the cosine distance between two MapBlock
	 * objects
	 * 
	 * Prints out the results and also returns a ResultBlock object (unused)
	 */
	@Override
	public ResultBlock call() {
		double dotProduct = 0, magQuery = 0, magSubject = 0; // declare some variables

		dotProduct = getDotProduct(query.getMap(), subject.getMap());

		magQuery = getMagnitude(query.getMap());
		magSubject = getMagnitude(subject.getMap());

		// return cosine similarity
		double similarity = dotProduct / Math.sqrt(magQuery * magSubject);
		String s = "Complete: " + query.getFileName() + " = " + Utils.fractionToPercent(similarity);
		System.out.println(s);
		ResultBlock result = new ResultBlock(query.getFileName(), similarity);
		return result;
	}

	/**
	 * Calculates the dot product of two Maps
	 * 
	 * @param a first Map
	 * @param b second Map
	 * @return dot product of the two maps
	 */
	private double getDotProduct(Map<Integer, Integer> a, Map<Integer, Integer> b) {
		double dotProduct = 0;

		// Get unique terms from both sequences
		HashSet<Integer> intersection = new HashSet<>(a.keySet());
		intersection.retainAll(b.keySet());

		// Calculate dot product
		for (int item : intersection) {
			dotProduct += a.get(item) * b.get(item);
		}
		return dotProduct;
	}

	/**
	 * Returns the magnitude of a Map
	 * 
	 * @param m calculate the magnitude of this Map
	 * @return magnitude of Map m
	 */
	private double getMagnitude(Map<Integer, Integer> m) {
		double mag = 0;
		for (int s : m.keySet()) {
			mag += Math.pow(m.get(s), 2);
		}
		return mag;
	}

}
