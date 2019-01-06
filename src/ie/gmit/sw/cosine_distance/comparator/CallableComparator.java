package ie.gmit.sw.cosine_distance.comparator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import ie.gmit.sw.CosineSimilarityController;
import ie.gmit.sw.Utils;
import ie.gmit.sw.cosine_distance.CosineSimilarity;
import ie.gmit.sw.cosine_distance.data_structures.MapBlock;
import ie.gmit.sw.cosine_distance.data_structures.ResultBlock;

/**
 * 
 * @author Justin
 *
 */
public class CallableComparator implements Callable<ResultBlock> {

//	private BlockingQueue<MapBlock> subject_frequency_maps;
	private MapBlock query;
	private MapBlock subject;
	@SuppressWarnings("unused")
	private BlockingQueue<ResultBlock> result_queue;
	@SuppressWarnings("unused")
	private CosineSimilarityController cs;

	public CallableComparator(MapBlock query, MapBlock subject, CosineSimilarityController cs) {
		this.query = query;
		this.subject = subject;
//		this.result_queue = result_queue;
		this.cs = cs;
	}

	@Override
	public ResultBlock call() {
//		System.out.println("COMPARATOR");
//		System.out.println(subject.getMap().toString());
		double dotProduct = 0, magQuery = 0, magSubject = 0;
		dotProduct = CosineSimilarity.getDotProduct(query.getMap(), subject.getMap());

		magQuery = CosineSimilarity.getMagnitude(query.getMap());
		magSubject = CosineSimilarity.getMagnitude(subject.getMap());

		// return cosine similarity
		double similarity = dotProduct / Math.sqrt(magQuery * magSubject);

//		String query_filename = CosineSimilarityController.query_filename;
//		String subject_filename = cs.subject_files.get(subject.getFilenameHash());

		String s = "Complete: " + query.getFileName() + " = " + Utils.fractionToPercent(similarity);
		System.out.println(s);
//		return;
		ResultBlock result = new ResultBlock(query.getFileName(), similarity);
//		result.setCosineSimilarity(similarity);
//		result.setSubjectHash(query.getFileName());

		return result;

//		result_queue.put(e);

	}

}
