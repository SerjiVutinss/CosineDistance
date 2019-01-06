package ie.gmit.sw.cosine_distance.data_structures;

public class ResultBlock extends Block {

	private double cosineSimilarity;

	public ResultBlock(String fileName, double cosineSimilarity) {
		super(fileName);
		this.cosineSimilarity = cosineSimilarity;
	}

	public double getCosineSimilarity() {
		return cosineSimilarity;
	}

//	public void setCosineSimilarity(double cosineSimilarity) {
//		this.cosineSimilarity = cosineSimilarity;
//	}

}