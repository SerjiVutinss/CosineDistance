package ie.gmit.sw.cosine_distance.data_structures;

import java.util.concurrent.Callable;

public class PoisonService implements Callable<MapBlock> {

	@Override
	public MapBlock call() throws Exception {
		MapBlock mb = new MapBlockPoison();
		return mb;
	}

}