package ie.gmit.sw.cosine_distance.mapper.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import ie.gmit.sw.cosine_distance.data_structures.ShingleBlock;
import ie.gmit.sw.cosine_distance.data_structures.ShingleBlockPoison;

/**
 * Callable class which takes ShingleBlocks from the supplied shingleQueue then
 * builds and returns a map future from them
 * 
 * @author Justin
 *
 */
public class CallableMapper implements Callable<Map<Integer, Integer>> {

	private BlockingQueue<ShingleBlock> shingleQueue;

	/**
	 * Default constructor
	 * 
	 * @param shingleQueue the queue to read ShingleBlocks from
	 * @return frequencyMap the map built from the queue's ShingleBlocks
	 */
	public CallableMapper(BlockingQueue<ShingleBlock> shingleQueue) {
		this.shingleQueue = shingleQueue;
	}

	/**
	 * Performs the operations to build a map from the ShingleBlocks on the supplied
	 * queue and returns this as a future
	 * 
	 * @throws InterruptedException
	 */
	@Override
	public Map<Integer, Integer> call() throws InterruptedException {

		Map<Integer, Integer> frequencyMap = new HashMap<>(); // return map - it does not need to be thread safe
		ShingleBlock sb; // read into this ShingleBlock from the queue
		boolean keepAlive = true; // used to decide when to stop reading from queue
		while (keepAlive) { // while the poison has not been found
			sb = shingleQueue.take(); // take from the queue
			if (sb instanceof ShingleBlockPoison) { // if the next block is a poison block
				keepAlive = false; // break from loop
				return frequencyMap; // and return the map
			} else {
				for (int i : sb.getData()) { // loop through the ShingleBlock integer array
					// check to see if this hash code already exists in the map
					Integer n = frequencyMap.get(i);
					n = (n == null) ? 1 : ++n; // if hash code doesn't exist set n = 1, otherwise, increment the current
												// map value for this hash code key
					frequencyMap.put(i, n); // add key value pair to map
				}
			}
		}
		return null; // something has gone wrong
	}
}