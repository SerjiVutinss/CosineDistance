package ie.gmit.sw.cosine_distance.data_structures;

import java.util.Map;

public class MapBlock extends Block {

	private Map<Integer, Integer> map;

	public MapBlock(int filenameHash) {
		super(filenameHash);
	}

	public MapBlock(Map<Integer, Integer> map, int filenameHash) {
		super(filenameHash);
		this.map = map;
	}

	public Map<Integer, Integer> getMap() {
		return this.map;
	}
}