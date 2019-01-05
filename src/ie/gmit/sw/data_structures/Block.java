package ie.gmit.sw.data_structures;

public abstract class Block {

	private int filenameHash;

	public Block() {
	}

	public Block(int filenameHash) {
		this.setFilenameHash(filenameHash);
	}

	public void setFilenameHash(int filenameHash) {
		this.filenameHash = filenameHash;
	}

	public int getFilenameHash() {
		return this.filenameHash;
	}

}