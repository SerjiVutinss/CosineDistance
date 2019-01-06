package ie.gmit.sw.cosine_distance.data_structures;

public class RunConfiguration {

	private String queryFilePath;
	private String subjectFolderPath;
	private int shingleSize;

	public RunConfiguration(String queryFilePath, String subjectFolderPath, int shingleSize) {

		this.queryFilePath = queryFilePath;
		this.subjectFolderPath = subjectFolderPath;
		this.shingleSize = shingleSize;

	}

	public String getQueryFilePath() {
		return queryFilePath;
	}

	public String getSubjectFolderPath() {
		return subjectFolderPath;
	}

	public int getShingleSize() {
		return shingleSize;
	}

}
