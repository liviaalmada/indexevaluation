package org.graphast.query.astarrnn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.graphast.model.Graph;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.util.DistanceUtils;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class RTreePoisIndexImpl implements PoisIndex {
	
	RTree<Long, Point> rtree;
	Graph graph;
	
	public RTreePoisIndexImpl() {
		this.rtree = RTree.create();		
	}

	@Override
	public void indexingGraph(Graph graph) {
		this.graph = graph;
		Set<Long> poiIds = graph.getPoiIds();
		for (Long poI : poiIds) {
			com.github.davidmoten.rtree.geometry.Point p = Geometries.point(graph.getNode(poI).getLatitude(),graph.getNode(poI).getLongitude());
			this.rtree = this.rtree.add(poI, p);			
		} 
	}

	@Override
	public List<NearestNeighbor> knnSearch(int k, int maxDist, long queryId) {
		List<NearestNeighbor> nns = new ArrayList<>();
		Point query = Geometries.point(graph.getNode(queryId).getLatitude(), graph.getNode(queryId).getLongitude());
		List<Entry<Long, Point>> list = rtree.nearest(query, maxDist, k).toList().toBlocking().single();
		for (Entry<Long, Point> entry : list) {
			long id = entry.value();
			NearestNeighbor nn = new NearestNeighbor(id, (int) DistanceUtils.distanceLatLong(graph.getNode(id), graph.getNode(queryId)));
			nns.add(nn);
		}
		return nns;
	}

	public List<NearestNeighbor> rangeSearch(double range, long queryId) {
		List<NearestNeighbor> nns = new ArrayList<>();
		Point query = Geometries.point(graph.getNode(queryId).getLatitude(), graph.getNode(queryId).getLongitude());
		List<Entry<Long, Point>> list = rtree.search(query, range).toList().toBlocking().single();
		for (Entry<Long, Point> entry : list) {
			long id = entry.value();
			NearestNeighbor nn = new NearestNeighbor(id, (int) DistanceUtils.distanceLatLong(graph.getNode(id), graph.getNode(queryId)));
			nns.add(nn);
		}	
		return nns;

	}

}
