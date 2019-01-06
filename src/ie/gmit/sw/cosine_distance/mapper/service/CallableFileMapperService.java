package ie.gmit.sw.cosine_distance.mapper.service;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ie.gmit.sw.cosine_distance.data_structures.CharBlock;
import ie.gmit.sw.cosine_distance.data_structures.MapBlock;
import ie.gmit.sw.cosine_distance.data_structures.ShingleBlock;

/**
 * 
 * Runnable, high level service class which maps a single file into memory by
 * using the lower level Reader, Shingler and Mapper classes
 * 
 * @returns a Future of type MapBlock
 * 
 * @author Justin
 *
 */
public class CallableFileMapperService implements Callable<MapBlock> {

	public static Charset charSet = Charset.forName("UTF-8");
	private String inputFileName;
	private int shingleSize;
	private int queueCapacity;

	/**
	 * Default constructor
	 * 
	 * @param inputFileName
	 * @param shingleSize
	 */
	public CallableFileMapperService(String inputFileName, int shingleSize, int queueCapacity) {
		this.inputFileName = inputFileName;
		this.shingleSize = shingleSize;
		this.queueCapacity = queueCapacity;
	}

	// read a file from the FS, placing chunks onto a blocking queue
	private BlockingQueue<CharBlock> query_read_queue = null;
	// parse these chunks into shingles, placing onto another blocking queue
	private BlockingQueue<ShingleBlock> query_shingle_queue = null;

	/**
	 * Something
	 */
	@Override
	public MapBlock call() throws Exception {

		query_read_queue = new ArrayBlockingQueue<CharBlock>(queueCapacity);
		query_shingle_queue = new ArrayBlockingQueue<>(queueCapacity);

		ExecutorService mapper_service = Executors.newCachedThreadPool();
		// read the input into the read_queue
		RunnableReader t_reader = new RunnableReader(inputFileName, query_read_queue);
		RunnableShingler t_shingler = new RunnableShingler(query_read_queue, query_shingle_queue, shingleSize);
		mapper_service.submit(t_reader);
		mapper_service.submit(t_shingler);
		Callable<Map<Integer, Integer>> t_mapper = new CallableMapper(query_shingle_queue);
		Future<Map<Integer, Integer>> mapper_frequency = mapper_service.submit(t_mapper);

		mapper_service.shutdown();
		mapper_service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//		System.out.println("Mapper Complete: " + input_filename);

		return new MapBlock(mapper_frequency.get(), inputFileName.hashCode());

	}
}