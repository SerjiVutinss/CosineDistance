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

import ie.gmit.sw.cosine_distance.comparator.ComparerController;
import ie.gmit.sw.cosine_distance.data_structures.MapBlock;
import ie.gmit.sw.cosine_distance.data_structures.RunConfiguration;
import ie.gmit.sw.cosine_distance.mapper.MapperController;

public class CosineSimilarityController {

	private Future<MapBlock> queryFrequencyMap = null;
	private int shingleSize;
	@SuppressWarnings("unused")
	private int queueCapacity = 50;

	private volatile MapBlock queryMap;
	private BlockingQueue<Future<MapBlock>> subjectFrequencyMaps = new ArrayBlockingQueue<>(queueCapacity);

	private int maxThreadCount = 0;
	private String queryFilePath;
	private String subjectFolderPath;
	private List<Path> file_paths = new ArrayList<>();

	public static Map<Integer, String> subject_files;

	public CosineSimilarityController(String queryFilePath, String subjectFolderPath, int shingleSize) {
		this.queryFilePath = queryFilePath;
		this.subjectFolderPath = subjectFolderPath;
		this.shingleSize = shingleSize;
	}

	public CosineSimilarityController(RunConfiguration rc) {
		this.queryFilePath = rc.getQueryFilePath();
		this.subjectFolderPath = rc.getSubjectFolderPath();
		this.shingleSize = rc.getShingleSize();
	}

	public Future<MapBlock> getQueryFrequencyMap() {
		return queryFrequencyMap;
	}

	public void setQueryFrequencyMap(Future<MapBlock> queryFrequencyMap) {
		this.queryFrequencyMap = queryFrequencyMap;
	}

	public void start() throws InterruptedException, ExecutionException {

		long startTime = System.nanoTime();

		// input files/folder
//		queryFilePath = Utils.getResourceDir() + "WarAndPeace-LeoTolstoy.txt";
//		subjectFolderPath = Utils.getResourceDir() + "input_files/";

		//
		file_paths = new ArrayList<>();

		subject_files = new HashMap<Integer, String>();

		try (Stream<Path> paths = Files.walk(Paths.get(subjectFolderPath))) {
			paths.filter(Files::isRegularFile).forEach(file_paths::add);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		incrementThreadCount(Thread.activeCount());

		for (Path p : file_paths) {
			subject_files.put(p.toAbsolutePath().toString().hashCode(), p.toAbsolutePath().toString());
		}

		new MapperController(queryFilePath, file_paths, queryFrequencyMap, subjectFrequencyMaps, this, shingleSize,
				queueCapacity).run();

		incrementThreadCount(Thread.activeCount());

		ExecutorService executors = Executors.newCachedThreadPool();
		ComparerController cc = new ComparerController(queryFrequencyMap, subjectFrequencyMaps);
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