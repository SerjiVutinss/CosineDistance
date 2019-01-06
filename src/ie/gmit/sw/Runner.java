package ie.gmit.sw;

import java.util.concurrent.ExecutionException;

import ie.gmit.sw.cosine_distance.data_structures.RunConfiguration;

public class Runner {

	public static void main(String[] args) {

		boolean isDebug = true;

		/**
		 * Testing config
		 */
		int shingleSize = 10;
		String queryFilePath = Utils.getResourceDir() + "WarAndPeace-LeoTolstoy.txt";
		String subjectFolderPath = Utils.getResourceDir() + "input_files/";

		System.out.println("Starting...");
		RunConfiguration rc;
		if (isDebug) {
			rc = new RunConfiguration(queryFilePath, subjectFolderPath, shingleSize);
		} else {
			rc = Menu.showMenu();
		}

		CosineSimilarityController tc = new CosineSimilarityController(rc);
		try {
			tc.start();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

}
