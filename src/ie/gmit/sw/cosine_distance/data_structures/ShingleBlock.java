package ie.gmit.sw.cosine_distance.data_structures;

public class ShingleBlock extends Block {

	private int[] data;

	public ShingleBlock(String fileName) {
		super(fileName);
	}

	public ShingleBlock(int[] data) {
		this.setData(data);
	}

	public ShingleBlock(int[] data, String fileName) {
		super(fileName);
		this.setData(data);
	}

	public int[] getData() {
		return this.data;
	}

	public void setData(int[] data) {
		this.data = data;
	}
}