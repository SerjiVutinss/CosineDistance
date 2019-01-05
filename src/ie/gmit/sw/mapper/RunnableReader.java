package ie.gmit.sw.mapper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import ie.gmit.sw.data_structures.CharBlock;
import ie.gmit.sw.data_structures.CharBlockPoison;

public class RunnableReader implements Runnable {

	// chunks of characters will be placed on this queue
	private BlockingQueue<CharBlock> input_queue;
	// the file to be read from
	private String input_filename;

	// determines how many characters are placed in each block on the queue
	private int chunkSize = 100000; // this seems to work well but could be tweaked

	// constructor - takes in the queue from the ThreadController, and the filename
	// to be read
	public RunnableReader(String input_filename, BlockingQueue<CharBlock> queue) {
		this.input_queue = queue;
		this.input_filename = input_filename;
	}

	@Override
	public void run() {

		BufferedReader in = null;
		int c;
		// try to open the file for reading in UTF8 mode
		try {
			in = new BufferedReader(
					new InputStreamReader(new FileInputStream(input_filename), CallableFileMapperService.charSet));

			char charBuf[]; // read chunks of characters into this character array
			charBuf = new char[chunkSize]; // initialise a new character array
			while ((c = in.read(charBuf, 0, chunkSize)) > 0) {
				// place a new CharBlock on the queue using the character array and actual
				// characters read from file
				input_queue.put(new CharBlock(charBuf, c, input_filename.hashCode()));
				// and reinitialise the char array
				charBuf = new char[chunkSize];
			}
			input_queue.put(new CharBlockPoison(input_filename.hashCode()));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}