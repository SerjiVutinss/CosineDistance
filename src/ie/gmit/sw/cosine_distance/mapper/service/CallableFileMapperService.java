package ie.gmit.sw.cosine_distance.mapper.service;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ie.gmit.sw.cosine_distance.data_structures.CharBlock;
import ie.gmit.sw.cosine_distance.data_structures.MapBlock;
import ie.gmit.sw.cosine_distance.data_structures.ShingleBlock;

/**
 * 
 * Runnable, high level service class which maps a single file into a MapBlock
 * by using the lower level RunnableReader, RunnableShingler and CallableMapper
 * classes
 * 
 * @returns a Future of type MapBlock
 * 
 * @author Justin
 *
 */
public class CallableFileMapperService implements Callable<MapBlock> {

	public static Charset charSet = Charset.forName("UTF-8");
	private String inputFilePath;
	private int shingleSize;
	private int queueCapacity;

	/**
	 * Constructor
	 * 
	 * @param inputFilePath path of source file to be mapped
	 * @param shingleSize   the shingleSize to be used in the RunnableShingler class
	 * @param queueCapacity the capacity of the queues to be used in each thread
	 */
	public CallableFileMapperService(String inputFilePath, int shingleSize, int queueCapacity) {
		this.inputFilePath = inputFilePath;
		this.shingleSize = shingleSize;
		this.queueCapacity = queueCapacity;
	}

	/**
	 * Begin a RunnableReader, RunnableShingler and CallableMapper thread for the
	 * input file.
	 * 
	 * Blocks until the file has been mapped by the CallableMapper
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * 
	 * @returns a Future of type MapBlock, which holds a complete mapping of the
	 *          input file
	 */
	@Override
	public MapBlock call() throws InterruptedException, ExecutionException {

		// instantiate the two BlockingQueues which are to be used by the threads
		BlockingQueue<CharBlock> readQueue = new ArrayBlockingQueue<CharBlock>(queueCapacity);
		BlockingQueue<ShingleBlock> shingleQueue = new ArrayBlockingQueue<>(queueCapacity);

		// create an executor service used to map the file
		ExecutorService executors = Executors.newCachedThreadPool();

		// start the Reader and Shingler threads
		executors.submit(new RunnableReader(inputFilePath, readQueue));
		executors.submit(new RunnableShingler(readQueue, shingleQueue, shingleSize));

		// submit a CallableMapper to the executor service and store it's future
		Future<Map<Integer, Integer>> frequencyMap = executors.submit(new CallableMapper(shingleQueue));

		// return a new MapBlock Future from this thread when it is ready
		return new MapBlock(frequencyMap.get(), inputFilePath);
	}
}