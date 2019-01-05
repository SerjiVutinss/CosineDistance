package ie.gmit.sw;

import java.util.concurrent.ExecutionException;

public class Runner {

	public static void main(String[] args) {

		CosineSimilarityController tc = new CosineSimilarityController();
		try {
			tc.start();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}
	
}
