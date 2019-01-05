package ie.gmit.sw.data_structures;

public class ShingleBlock extends Block {

	private int[] data;

	public ShingleBlock(int filenameHash) {
		super(filenameHash);
	}

	public ShingleBlock(int[] data) {
		this.setData(data);
	}

	public ShingleBlock(int[] data, int filenameHash) {
		super(filenameHash);
		this.setData(data);
	}

	public int[] getData() {
		return this.data;
	}

	public void setData(int[] data) {
		this.data = data;
	}
}