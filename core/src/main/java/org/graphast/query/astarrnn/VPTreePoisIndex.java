package org.graphast.query.astarrnn;

import java.util.ArrayList;
import java.util.List;

import org.graphast.model.Graph;
import org.graphast.model.Node;
import org.graphast.query.knn.NearestNeighbor;

import com.eatthepath.jvptree.VPTree;
//import com.google.common.collect.TreeBasedTable;

public class VPTreePoisIndex implements PoisIndex {

	private VPTree<Node> vpTree;
	Graph graph;

	public VPTreePoisIndex() {
		vpTree = new VPTree<Node>(new VPTreeEuclideanDistance());
		
	}


	@Override
	public void indexingGraph(Graph graph) {
		this.graph = graph;
		for (Long poi : graph.getPoiIds()) {
			Node node = graph.getNode(poi);
			vpTree.add(node);
		}

	}

	@Override
	public List<NearestNeighbor> knnSearch(int k, int maxDist, long queryId) {
		// TODO Auto-generated method stub
		Node node = graph.getNode(queryId);
		VPTreeEuclideanDistance d = new VPTreeEuclideanDistance();
		List<NearestNeighbor> knn = new ArrayList<NearestNeighbor>();
		List<Node> vpTreeSearch = new ArrayList<Node>();

		vpTreeSearch = vpTree.getNearestNeighbors(node, k);
		for (Node n : vpTreeSearch) {
			knn.add(new NearestNeighbor(n.getId(), (int) d.getDistance(node, n)));
		}

		return knn;
	}

	@Override
	public List<NearestNeighbor> rangeSearch(double range, long queryId) {
		// TODO Auto-generated method stub
		return null;
	}

}
