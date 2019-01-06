package ie.gmit.sw.cosine_distance.data_structures;

public abstract class Block {

	private String fileName;

	public Block() {
	}

	public Block(String fileName) {
		this.setFileName(fileName);
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return this.fileName;
	}

}