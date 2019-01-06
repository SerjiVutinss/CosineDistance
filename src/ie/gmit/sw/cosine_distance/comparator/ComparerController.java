package ie.gmit.sw.cosine_distance.comparator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ie.gmit.sw.cosine_distance.data_structures.MapBlock;
import ie.gmit.sw.cosine_distance.data_structures.MapBlockPoison;

public class ComparerController implements Callable<Boolean> {

	private Future<MapBlock> query_frequency_map;
	private BlockingQueue<Future<MapBlock>> subject_frequency_maps;
//	private CosineSimilarityController cs;

	public ComparerController(Future<MapBlock> query_frequency_map,
			BlockingQueue<Future<MapBlock>> subject_frequency_maps) {
		this.query_frequency_map = query_frequency_map;
		this.subject_frequency_maps = subject_frequency_maps;
//		this.cs = cs;
	}

	@Override
	public Boolean call() throws Exception {

		MapBlock mb;
		boolean keepALive = true;

		ExecutorService executors = Executors.newCachedThreadPool();

		System.out.println("STARTING COMPARATORS...");
		while (keepALive) {
			mb = subject_frequency_maps.take().get();
			if (mb instanceof MapBlockPoison) {
				System.out.println("Got maps AND POISON");
				keepALive = false;
			} else {
				executors.submit(new CallableComparator(query_frequency_map.get(), mb));
			}
		}
		executors.shutdown();
		executors.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		System.out.println("ALL COMPARATORS COMPLETED");
		return true;
	}

}