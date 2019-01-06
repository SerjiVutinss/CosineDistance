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

/**
 * Compares a query file to one or more subject files
 * 
 * @author Justin
 *
 */
public class CosineSimilarityController {

	// variables to hold futures of mapped files
	private Future<MapBlock> queryFrequencyMap = null;
	private BlockingQueue<Future<MapBlock>> subjectFrequencyMaps;

	// configuration variables
	private int shingleSize;
	private int queueCapacity = 50;

	private int maxThreadCount = 0;
	private String queryFilePath;
	private String subjectFolderPath;
	private List<Path> file_paths = new ArrayList<>();

	private Map<Integer, String> subject_files;

	/**
	 * Constructor
	 * 
	 * @param rc a RunConfiguration object
	 */
	public CosineSimilarityController(RunConfiguration rc) {
		this.queryFilePath = rc.getQueryFilePath();
		this.subjectFolderPath = rc.getSubjectFolderPath();
		this.shingleSize = rc.getShingleSize();
	}

	/**
	 * Constructor
	 * 
	 * @param queryFilePath     path to query file
	 * @param subjectFolderPath path to subject folder
	 * @param shingleSize       shingle size to be used by Runnable Shingler
	 */
	public CosineSimilarityController(String queryFilePath, String subjectFolderPath, int shingleSize) {
		this(new RunConfiguration(queryFilePath, subjectFolderPath, shingleSize));
	}

	public Future<MapBlock> getQueryFrequencyMap() {
		return queryFrequencyMap;
	}

	public void setQueryFrequencyMap(Future<MapBlock> queryFrequencyMap) {
		this.queryFrequencyMap = queryFrequencyMap;
	}

	public void start() throws InterruptedException, ExecutionException {

		long startTime = System.nanoTime();
		// instantiate data structures
		subjectFrequencyMaps = new ArrayBlockingQueue<>(queueCapacity);
		file_paths = new ArrayList<>();
		subject_files = new HashMap<Integer, String>();

		// build the list of files and the HashMap of filename hash, filename
		this.buildFileList();

		incrementThreadCount();
		// Create and run a MapperController to map all files, maps will be stored in
		// this instance's variables - queryFrequencyMap and subjectFrequencyMap
		new MapperController(queryFilePath, file_paths, queryFrequencyMap, subjectFrequencyMaps, this, shingleSize,
				queueCapacity).run();
		incrementThreadCount();

		ExecutorService executors = Executors.newCachedThreadPool(); // create an ExecutorService
		// create and start a ComparerController for the mapped files - blocks until all
		// have completed
		executors.submit(new ComparerController(queryFrequencyMap, subjectFrequencyMaps)).get();
		incrementThreadCount();
		executors.shutdown(); // not really needed but added for completeness

		incrementThreadCount();
		long endTime = System.nanoTime();

		// print out a summary
		System.out.println("Max Threads used: " + maxThreadCount);
		System.out.println("Total Duration: " + (double) ((double) (endTime - startTime) / 1000000000) + " seconds");
		System.exit(0);
	}

	/**
	 * Build a list of files and a HashMap for quick lookup of filenames
	 */
	private void buildFileList() {
		try (Stream<Path> paths = Files.walk(Paths.get(subjectFolderPath))) {
			paths.filter(Files::isRegularFile).forEach(file_paths::add);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (Path p : file_paths) {
			subject_files.put(p.toAbsolutePath().toString().hashCode(), p.toAbsolutePath().toString());
		}
	}

	/**
	 * Used for testing, try to find max number of threads used
	 */
	public void incrementThreadCount() {
		int newCount = Thread.activeCount();
		if (newCount > maxThreadCount) {
			maxThreadCount = newCount;
		}
	}
}