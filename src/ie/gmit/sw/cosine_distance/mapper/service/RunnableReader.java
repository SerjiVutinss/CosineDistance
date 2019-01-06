package ie.gmit.sw.cosine_distance.mapper.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import ie.gmit.sw.cosine_distance.data_structures.CharBlock;
import ie.gmit.sw.cosine_distance.data_structures.CharBlockPoison;

/**
 * Runnable class which reads chunks from the file at the supplied path and
 * places those chunks on the supplied BlockingQueue as CharBlocks
 * 
 * @author Justin
 *
 */
public class RunnableReader implements Runnable {

	// chunks of characters will be placed on this queue
	private BlockingQueue<CharBlock> readQueue;
	// the file to be read from
	private String inputFilePath;
	// determines how many characters are placed in each block on the queue
	private int chunkSize = 1000; // this seems to work well but could be tweaked

	// constructor - takes in the queue from the ThreadController, and the filename
	// to be read

	/**
	 * Basic Constructor
	 * 
	 * @param inputFilePath the path of the file to be read from
	 * @param readQueue     the blocking queue which CharBlocks will be placed on
	 */
	public RunnableReader(String inputFilePath, BlockingQueue<CharBlock> readQueue) {
		this.readQueue = readQueue;
		this.inputFilePath = inputFilePath;
	}

	/**
	 * Advanced Constructor
	 * 
	 * @param input_filename the path of the file to be read from
	 * @param readQueue      the blocking queue which CharBlocks will be placed on
	 * @param chunkSize      number of chars to be read from source file at a time
	 *                       and place in CharBlock on queue
	 */
	public RunnableReader(String input_filename, BlockingQueue<CharBlock> readQueue, int chunkSize) {
		this.readQueue = readQueue;
		this.inputFilePath = input_filename;
		this.chunkSize = chunkSize;
	}

	/**
	 * Performs the operations to read a chunkSize number of characters from the
	 * source file and add these to the queue as CharBlock objects
	 */
	@Override
	public void run() {

		BufferedReader in = null;
		int c;
		// try to open the file for reading in UTF8 mode
		try {
			in = new BufferedReader(
					new InputStreamReader(new FileInputStream(inputFilePath), CallableFileMapperService.charSet));

			char charBuf[]; // read chunks of characters into this character array
			charBuf = new char[chunkSize]; // initialize a new character array using the chunkSize

			// read chunkSize chars into the charBuf variable
			while ((c = in.read(charBuf, 0, chunkSize)) > 0) {
				// try to place a new CharBlock on the queue using the character array and
				// actual characters read from file
				readQueue.put(new CharBlock(charBuf, c, inputFilePath.hashCode()));
				// reinitialize the char array for reuse
				charBuf = new char[chunkSize];
			}
			// we have reached the end of the file, add the Poison to this queue
			readQueue.put(new CharBlockPoison(inputFilePath.hashCode()));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}