package ie.gmit.sw.mapper;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

import ie.gmit.sw.data_structures.ShingleBlock;
import ie.gmit.sw.data_structures.ShingleBlockPoison;

public class CallableMapper implements Callable<Map<Integer, Integer>> {

	private BlockingQueue<ShingleBlock> shingle_queue;
	private ConcurrentMap<Integer, Integer> frequency_map;

	public CallableMapper(BlockingQueue<ShingleBlock> query_shingle_queue,
			ConcurrentMap<Integer, Integer> frequency_map) {
		this.shingle_queue = query_shingle_queue;
		this.frequency_map = frequency_map;
	}

	@Override
	public Map<Integer, Integer> call() throws InterruptedException {

		ShingleBlock sb; // read into this array

		boolean keepAlive = true; // used to decide when to stop reading from queue
		while (keepAlive) {
			// take from the queue
			sb = shingle_queue.take();
			if (sb instanceof ShingleBlockPoison) {
				keepAlive = false;
				return this.frequency_map;
			} else {
				// add to map
				for (int i : sb.getData()) {
					Integer n = this.frequency_map.get(i);
					n = (n == null) ? 1 : ++n;
					this.frequency_map.put(i, n);
				}
			}
		}
		return null;
	}
}