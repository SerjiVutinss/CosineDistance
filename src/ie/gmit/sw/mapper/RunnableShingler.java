package ie.gmit.sw.mapper;

import java.util.concurrent.BlockingQueue;

import ie.gmit.sw.Utils;
import ie.gmit.sw.data_structures.CharBlock;
import ie.gmit.sw.data_structures.CharBlockPoison;
import ie.gmit.sw.data_structures.ShingleBlock;
import ie.gmit.sw.data_structures.ShingleBlockPoison;

public class RunnableShingler implements Runnable {

	private BlockingQueue<CharBlock> read_queue;
	private BlockingQueue<ShingleBlock> query_shingle_queue;
	private int shingleSize = 10;

	public RunnableShingler(BlockingQueue<CharBlock> read_queue, BlockingQueue<ShingleBlock> query_shingle_queue) {
		this.read_queue = read_queue;
		this.query_shingle_queue = query_shingle_queue;
	}

	@Override
	public void run() {
		try {
			CharBlock cb;
			ShingleBlock sb = null;
			boolean keepAlive = true; // used to decide when to stop reading from queue
			while (keepAlive) {
				// take from the queue
				cb = read_queue.take();
				// compare to POISON, if true, break from while loop
				if (cb instanceof CharBlockPoison) {
					sb = new ShingleBlockPoison(cb.getFilenameHash());
					this.query_shingle_queue.put(sb);
					keepAlive = false;
				} else {
					// still looping, shingle this char array
					int[] shingle = Utils.chunkStringChars(cb.getChars(), shingleSize);
					sb = new ShingleBlock(shingle, cb.getFilenameHash());
					// add to the shingle queue
					this.query_shingle_queue.put(sb);
				}
				sb = null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}