package ie.gmit.sw.cosine_distance.mapper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ie.gmit.sw.CosineSimilarityController;
import ie.gmit.sw.cosine_distance.data_structures.MapBlock;
import ie.gmit.sw.cosine_distance.data_structures.PoisonService;
import ie.gmit.sw.cosine_distance.mapper.service.CallableFileMapperService;

public class MapperController {

	Future<MapBlock> query_map;

	private String queryFilePath;
	private List<Path> subjectFolderPaths;
	@SuppressWarnings("unused")
	private Future<MapBlock> queryFrequencyMap;
	private BlockingQueue<Future<MapBlock>> subjectFrequencyMapQueue = new ArrayBlockingQueue<>(100);

//	private ExecutorService executors;

	private CosineSimilarityController csController;

	private int shingleSize;

	private int queueCapacity;

	public MapperController() {
	}

	public MapperController(String queryFilePath, List<Path> subjectFolderPaths, Future<MapBlock> queryFrequencyMap,
			BlockingQueue<Future<MapBlock>> subjectFrequencyMapQueue, CosineSimilarityController csController,
			int shingleSize) throws InterruptedException, ExecutionException {

		this.queryFilePath = queryFilePath;
		this.subjectFolderPaths = subjectFolderPaths;

		this.queryFrequencyMap = queryFrequencyMap;
		this.subjectFrequencyMapQueue = subjectFrequencyMapQueue;

		this.csController = csController; // the controller which has called this one

		this.shingleSize = shingleSize;
	}

	public MapperController(String queryFilePath, List<Path> subjectFolderPath, Future<MapBlock> queryFrequencyMap,
			BlockingQueue<Future<MapBlock>> subjectFrequencyMapQueue, CosineSimilarityController csController,
			int shingleSize, int queueCapacity) throws InterruptedException, ExecutionException {

		this(queryFilePath, subjectFolderPath, queryFrequencyMap, subjectFrequencyMapQueue, csController, shingleSize);
		this.queueCapacity = queueCapacity;
	}

	/**
	 * 
	 * @throws InterruptedException
	 */
	public void run() throws InterruptedException {

		ExecutorService executors = Executors.newCachedThreadPool();
		Callable<MapBlock> query_mapper = new CallableFileMapperService(queryFilePath, shingleSize, queueCapacity);
		this.csController.setQueryFrequencyMap(executors.submit(query_mapper));

		// for each file, submit a new MapperService and add this to the list of mappers
		List<Future<MapBlock>> subject_maps = new ArrayList<>();
		for (Path p : subjectFolderPaths) {
			subject_maps.add(executors
					.submit(new CallableFileMapperService(p.toAbsolutePath().toString(), shingleSize, queueCapacity)));
		}

		for (Future<MapBlock> f : subject_maps) {
			subjectFrequencyMapQueue.put(f);
		}
		Future<MapBlock> poison = executors.submit(new PoisonService());
		subjectFrequencyMapQueue.put(poison);
	}
}