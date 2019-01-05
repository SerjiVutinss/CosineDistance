package ie.gmit.sw;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import ie.gmit.sw.comparator.ComparerController;
import ie.gmit.sw.data_structures.MapBlock;
import ie.gmit.sw.mapper.MapperController;

public class CosineSimilarityController {

	private static Future<MapBlock> queryFrequencyMap = null;

	public Future<MapBlock> getQueryFrequencyMap() {
		return queryFrequencyMap;
	}

	public void setQueryFrequencyMap(Future<MapBlock> queryFrequencyMap) {
		CosineSimilarityController.queryFrequencyMap = queryFrequencyMap;
	}

	@SuppressWarnings("unused")
	private volatile MapBlock queryMap;
	private int queueCapacity = 50;
	private BlockingQueue<Future<MapBlock>> subject_frequency_maps = new ArrayBlockingQueue<>(queueCapacity);

	private int maxThreadCount = 0;
	private String query_filename;
	private String subjects_folder;
	private List<Path> file_paths = new ArrayList<>();

	public static Map<Integer, String> subject_files;

	public void start() throws InterruptedException, ExecutionException {

		long startTime = System.nanoTime();

		// input files/folder
		query_filename = Utils.getResourceDir() + "WarAndPeace-LeoTolstoy.txt";
		subjects_folder = Utils.getResourceDir() + "input_files/";

		//
		file_paths = new ArrayList<>();

		subject_files = new HashMap<Integer, String>();

		try (Stream<Path> paths = Files.walk(Paths.get(subjects_folder))) {
			paths.filter(Files::isRegularFile).forEach(file_paths::add);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		incrementThreadCount(Thread.activeCount());

		for (Path p : file_paths) {
			subject_files.put(p.toAbsolutePath().toString().hashCode(), p.toAbsolutePath().toString());
		}

		new MapperController(query_filename, file_paths, queryFrequencyMap, subject_frequency_maps, this);

		incrementThreadCount(Thread.activeCount());

		ExecutorService executors = Executors.newCachedThreadPool();
		ComparerController cc = new ComparerController(queryFrequencyMap, subject_frequency_maps);
		incrementThreadCount(Thread.activeCount());

		Future<Boolean> result = executors.submit(cc);

		boolean keepAlive = true;
		while (keepAlive) {
			try {
				queryMap = queryFrequencyMap.get();
				incrementThreadCount(Thread.activeCount());
			} catch (Exception e) {
				e.printStackTrace();
			}
			keepAlive = !result.isDone();
			incrementThreadCount(Thread.activeCount());
		}
		executors.shutdown();
//		executors.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		incrementThreadCount(Thread.activeCount());
		if (executors.isShutdown()) {
			long endTime = System.nanoTime();
			System.out.println("Max Threads used: " + maxThreadCount);
			System.out
					.println("Total Duration: " + (double) ((double) (endTime - startTime) / 1000000000) + " seconds");
		}
		System.exit(0);
	}

	public void incrementThreadCount(int newCount) {
		if (newCount > maxThreadCount) {
			maxThreadCount = newCount;
		}
	}
}