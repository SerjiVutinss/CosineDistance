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

	private String query_filename;
	private List<Path> subject_file_paths;
	@SuppressWarnings("unused")
	private Future<MapBlock> query_frequency_map;
	private BlockingQueue<Future<MapBlock>> subject_frequency_maps = new ArrayBlockingQueue<>(100);

	private ExecutorService executors;

	private CosineSimilarityController csController;

	private int shingleSize;

	public MapperController() {
	}

	public MapperController(String query_filename, List<Path> subject_file_paths, Future<MapBlock> query_frequency_map,
			BlockingQueue<Future<MapBlock>> subject_frequency_maps, int shingleSize,
			CosineSimilarityController csController) throws InterruptedException, ExecutionException {

		this.shingleSize = shingleSize;
		this.csController = csController;
		this.executors = Executors.newCachedThreadPool();
		this.query_filename = query_filename;
		this.subject_file_paths = subject_file_paths;
		this.query_frequency_map = query_frequency_map;
		this.subject_frequency_maps = subject_frequency_maps;

		this.run();
	}

	/**
	 * 
	 * @throws InterruptedException
	 */
	private void run() throws InterruptedException {

		this.executors = Executors.newCachedThreadPool();
		Callable<MapBlock> query_mapper = new CallableFileMapperService(query_filename, shingleSize);
		this.csController.setQueryFrequencyMap(executors.submit(query_mapper));

		// for each file, submit a new MapperService and add this to the list of mappers
		List<Future<MapBlock>> subject_maps = new ArrayList<>();
		for (Path p : subject_file_paths) {
			subject_maps.add(
					this.executors.submit(new CallableFileMapperService(p.toAbsolutePath().toString(), shingleSize)));
		}

		for (Future<MapBlock> f : subject_maps) {
			subject_frequency_maps.put(f);
		}
		Future<MapBlock> poison = this.executors.submit(new PoisonService());
		subject_frequency_maps.put(poison);
	}
}