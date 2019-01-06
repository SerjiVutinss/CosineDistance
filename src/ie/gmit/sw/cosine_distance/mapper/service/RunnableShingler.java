package ie.gmit.sw.cosine_distance.mapper.service;

import java.util.concurrent.BlockingQueue;

import ie.gmit.sw.Utils;
import ie.gmit.sw.cosine_distance.data_structures.CharBlock;
import ie.gmit.sw.cosine_distance.data_structures.CharBlockPoison;
import ie.gmit.sw.cosine_distance.data_structures.ShingleBlock;
import ie.gmit.sw.cosine_distance.data_structures.ShingleBlockPoison;

/**
 * Runnable class which reads from the supplied readQueue, shingles the
 * CharBlocks and adds the shingled data to the shingleQueue, using the supplied
 * shingleSize
 * 
 * @author Justin
 *
 */
public class RunnableShingler implements Runnable {

	private BlockingQueue<CharBlock> readQueue;
	private BlockingQueue<ShingleBlock> shingleQueue;
	private int shingleSize;

	/**
	 * Only constructor for this class
	 * 
	 * @param readQueue    CharBlocks are read from this queue
	 * @param shingleQueue ShingleBlocks are placed on this queue
	 * @param shingleSize  ShingleBlocks are created using thisnumber of characters
	 */
	public RunnableShingler(BlockingQueue<CharBlock> readQueue, BlockingQueue<ShingleBlock> shingleQueue,
			int shingleSize) {
		this.readQueue = readQueue;
		this.shingleQueue = shingleQueue;
		this.shingleSize = shingleSize;
	}

	/**
	 * Performs the operations to convert each CharBlock on the readQueue to a
	 * ShingleBlock and places this shingleBlock on the shingleQueue
	 */
	@Override
	public void run() {
		try {
			CharBlock cb;
			ShingleBlock sb = null;
			boolean keepAlive = true; // used to decide when to stop reading from queue
			while (keepAlive) {
				// take from the queue
				cb = readQueue.take();
				// compare to POISON, if true, break from while loop
				if (cb instanceof CharBlockPoison) {
					sb = new ShingleBlockPoison(cb.getFilenameHash());
					this.shingleQueue.put(sb);
					keepAlive = false;
				} else {
					// still looping, shingle this char array
					int[] shingle = Utils.chunkStringChars(cb.getChars(), shingleSize);
					sb = new ShingleBlock(shingle, cb.getFilenameHash());
					// add to the shingle queue
					this.shingleQueue.put(sb);
				}
				sb = null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}