package ie.gmit.sw.cosine_distance.data_structures;

public class ShingleBlockPoison extends ShingleBlock {

	public ShingleBlockPoison(String fileName) {
		super(fileName);
	}

	public ShingleBlockPoison(int[] data, String fileName) {
		super(data);
		this.setFileName(fileName);
	}

}