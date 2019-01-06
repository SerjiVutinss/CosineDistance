package ie.gmit.sw.cosine_distance.data_structures;

import java.util.Map;

public class MapBlock extends Block {

	private Map<Integer, Integer> map;

	public MapBlock(String fileName) {
		super(fileName);
	}

	public MapBlock(Map<Integer, Integer> map, String fileName) {
		super(fileName);
		this.map = map;
	}

	public Map<Integer, Integer> getMap() {
		return this.map;
	}
}