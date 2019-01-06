package ie.gmit.sw.cosine_distance.comparator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import ie.gmit.sw.CosineSimilarityController;
import ie.gmit.sw.Utils;
import ie.gmit.sw.cosine_distance.CosineSimilarity;
import ie.gmit.sw.cosine_distance.data_structures.MapBlock;
import ie.gmit.sw.cosine_distance.data_structures.Result;

public class CallableComparator implements Callable<Result> {

//	private BlockingQueue<MapBlock> subject_frequency_maps;
	private MapBlock query;
	private MapBlock subject;
	@SuppressWarnings("unused")
	private BlockingQueue<Result> result_queue;

	public CallableComparator(MapBlock query, MapBlock subject, BlockingQueue<Result> result_queue) {
		this.query = query;
		this.subject = subject;
		this.result_queue = result_queue;
	}

	@Override
	public Result call() {
//		System.out.println("COMPARATOR");
//		System.out.println(subject.getMap().toString());
		double dotProduct = 0, magQuery = 0, magSubject = 0;
		dotProduct = CosineSimilarity.getDotProduct(query.getMap(), subject.getMap());

		magQuery = CosineSimilarity.getMagnitude(query.getMap());
		magSubject = CosineSimilarity.getMagnitude(subject.getMap());

		// return cosine similarity
		double similarity = dotProduct / Math.sqrt(magQuery * magSubject);

//		String query_filename = CosineSimilarityController.query_filename;
		String subject_filename = CosineSimilarityController.subject_files.get(subject.getFilenameHash());

		String s = "Complete: " + subject_filename + " = " + Utils.fractionToPercent(similarity);
		System.out.println(s);
//		return;
		Result result = new Result();
		result.setCosineSimilarity(similarity);
		result.setSubjectHash(query.getFilenameHash());

		return result;

//		result_queue.put(e);

	}

}
