package ie.gmit.sw;

import java.util.Scanner;

import ie.gmit.sw.cosine_distance.data_structures.RunConfiguration;

public class Menu {

	public static RunConfiguration showMenu() {

		String queryFilePath;
		String subjectFolderPath;
		int shingleSize;

		System.out.println("**** Welcome to Cosine Distance Calculator ****");
		System.out.println("**** Welcome to Cosine Distance Calculator ****");
		
		
		System.out.println("Please enter path to query file:");

		Scanner s = new Scanner(System.in);

		queryFilePath = s.nextLine();

		System.out.println("Query file path: " + queryFilePath);

		System.out.println("Please enter path to subject folder:");

		subjectFolderPath = s.nextLine();
		System.out.println("Subject folder path: " + subjectFolderPath);

		System.out.println("Please enter shingle size (k) to use:");

		shingleSize = s.nextInt();
		System.out.println("Shingle size of " + shingleSize + " will be used");

		s.close();

		return new RunConfiguration(queryFilePath, subjectFolderPath, shingleSize);

	}

}
