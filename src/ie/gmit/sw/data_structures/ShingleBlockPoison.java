package ie.gmit.sw.data_structures;

public class ShingleBlockPoison extends ShingleBlock {

	public ShingleBlockPoison(int filenameHash) {
		super(filenameHash);
	}

	public ShingleBlockPoison(int[] data, int filenameHash) {
		super(data);
		this.setFilenameHash(filenameHash);
	}

}