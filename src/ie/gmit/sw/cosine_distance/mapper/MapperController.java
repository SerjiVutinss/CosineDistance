package ie.gmit.sw.cosine_distance.mapper;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ie.gmit.sw.CosineSimilarityController;
import ie.gmit.sw.cosine_distance.data_structures.MapBlock;
import ie.gmit.sw.cosine_distance.data_structures.PoisonService;
import ie.gmit.sw.cosine_distance.mapper.service.CallableFileMapperService;

public class MapperController {

	Future<MapBlock> query_map;

	private String queryFilePath; // path of query file
	private List<Path> subjectFolderPaths; // path of subject folder
	@SuppressWarnings("unused")
	private Future<MapBlock> queryFrequencyMap; // future map of the query file
	private BlockingQueue<Future<MapBlock>> subjectFrequencyMapQueue;

	private CosineSimilarityController csController;

	// options
	private int shingleSize; // default shingle size, can be overridden in Advanced Constructor
	private int queueCapacity = 100; // default queueCapacity, can be overridden in constructors

	/**
	 * Basic constructor
	 * 
	 * @param queryFilePath            path of query file
	 * @param subjectFolderPaths       path of subject folder
	 * @param queryFrequencyMap        holds a Future of the query file map
	 * @param subjectFrequencyMapQueue holds the future of each subject file map
	 * @param csController             the CosineSimilarityController which created
	 *                                 this instance
	 * @param shingleSize              number of characters to be used in each
	 *                                 shingle
	 * @throws InterruptedException thrown by subjectFrequencyMapQueue put() method
	 */
	public MapperController(String queryFilePath, List<Path> subjectFolderPaths, Future<MapBlock> queryFrequencyMap,
			BlockingQueue<Future<MapBlock>> subjectFrequencyMapQueue, CosineSimilarityController csController,
			int shingleSize) throws InterruptedException {

		this.queryFilePath = queryFilePath;
		this.subjectFolderPaths = subjectFolderPaths;
		this.queryFrequencyMap = queryFrequencyMap;
		this.subjectFrequencyMapQueue = subjectFrequencyMapQueue;
		this.csController = csController;
		this.shingleSize = shingleSize;
	}

	/**
	 * Basic constructor
	 * 
	 * @param queryFilePath            path of query file
	 * @param subjectFolderPaths       path of subject folder
	 * @param queryFrequencyMap        holds a Future of the query file map
	 * @param subjectFrequencyMapQueue holds the future of each subject file map
	 * @param csController             the CosineSimilarityController which created
	 *                                 this instance
	 * @param shingleSize              number of characters to be used in each
	 *                                 shingle
	 * @param queueCapacity            the capacity of each BlockingQueue used
	 * @throws InterruptedException thrown by subjectFrequencyMapQueue put() method
	 */
	public MapperController(String queryFilePath, List<Path> subjectFolderPath, Future<MapBlock> queryFrequencyMap,
			BlockingQueue<Future<MapBlock>> subjectFrequencyMapQueue, CosineSimilarityController csController,
			int shingleSize, int queueCapacity) throws InterruptedException {

		this(queryFilePath, subjectFolderPath, queryFrequencyMap, subjectFrequencyMapQueue, csController, shingleSize);
		this.queueCapacity = queueCapacity;
	}

	/**
	 * Start a service for the query file and each subject file, each service
	 * returns a Future of type MapBlock.
	 * 
	 * Query file future is assigned to queryFrequencyMap.
	 * 
	 * Subject file futures are placed on the subjectFrequencyMapQueue.
	 * 
	 * @throws InterruptedException thrown by subjectFrequencyQueue put() method
	 */
	public void run() throws InterruptedException {

		ExecutorService executors = Executors.newCachedThreadPool(); // create a new executor service
		// store the query file Map Future in the calling instance (higher level
		// CosineSimilarityController)
		this.csController.setQueryFrequencyMap(
				executors.submit(new CallableFileMapperService(queryFilePath, shingleSize, queueCapacity)));

		// for each file, submit a new MapperService and add the Map Future to the queue
		for (Path p : subjectFolderPaths) {
			Future<MapBlock> mb = executors
					.submit(new CallableFileMapperService(p.toAbsolutePath().toString(), shingleSize, queueCapacity));
			subjectFrequencyMapQueue.put(mb);
		}
		// all subject file Map Futures have been put on the queue, add a poison
		subjectFrequencyMapQueue.put(executors.submit(new PoisonService()));
	}
}