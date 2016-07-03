package org.graphast.query.astarrnn;

import java.util.List;

import org.graphast.model.Graph;
import org.graphast.query.knn.NearestNeighbor;

public interface PoisIndex {
	void indexingGraph(Graph graph);
	List<NearestNeighbor> knnSearch(int k, int maxDist, long queryId);
	List<NearestNeighbor> rangeSearch(double range, long queryId);
}
