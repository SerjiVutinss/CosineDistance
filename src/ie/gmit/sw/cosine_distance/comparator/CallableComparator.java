package ie.gmit.sw.cosine_distance.comparator;

import java.util.concurrent.Callable;

import ie.gmit.sw.Utils;
import ie.gmit.sw.cosine_distance.CosineSimilarity;
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
	 * Performs the operations to calculate the cosine distance between two MapBlock objects
	 * 
	 * Prints out the results and also returns a ResultBlock object (unimplemented)
	 */
	@Override
	public ResultBlock call() {
		double dotProduct = 0, magQuery = 0, magSubject = 0;
		dotProduct = CosineSimilarity.getDotProduct(query.getMap(), subject.getMap());

		magQuery = CosineSimilarity.getMagnitude(query.getMap());
		magSubject = CosineSimilarity.getMagnitude(subject.getMap());

		// return cosine similarity
		double similarity = dotProduct / Math.sqrt(magQuery * magSubject);
		String s = "Complete: " + query.getFileName() + " = " + Utils.fractionToPercent(similarity);
		System.out.println(s);
		ResultBlock result = new ResultBlock(query.getFileName(), similarity);
		return result;

	}

}
