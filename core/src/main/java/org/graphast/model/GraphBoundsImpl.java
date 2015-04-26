package org.graphast.model;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

import java.io.IOException;

import org.graphast.enums.CompressionType;
import org.graphast.util.FileUtils;

public class GraphBoundsImpl extends GraphImpl implements GraphBounds {

	private Long2IntMap edgesUpperBound, edgesLowerBound;
	private Long2IntMap nodesUpperBound, nodesLowerBound;

	public GraphBoundsImpl(String directory) {
		this(directory, CompressionType.GZIP_COMPRESSION);
	}

	public GraphBoundsImpl(String directory, CompressionType compressionType) {
		super(directory, compressionType);
		edgesUpperBound = new Long2IntOpenHashMap();
		edgesLowerBound = new Long2IntOpenHashMap();
		nodesUpperBound = new Long2IntOpenHashMap();
		nodesLowerBound = new Long2IntOpenHashMap();

	}

	public void save() throws IOException {
		super.save();
		FileUtils.saveLong2IntMap(directory + "/edgesUpperBound", edgesUpperBound, blockSize, compressionType);
		FileUtils.saveLong2IntMap(directory + "/edgesLowerBound", edgesLowerBound, blockSize, compressionType);

		FileUtils.saveLong2IntMap(directory + "/nodesUpperBound", nodesUpperBound, blockSize, compressionType);
		FileUtils.saveLong2IntMap(directory + "/nodesLowerBound", nodesLowerBound, blockSize, compressionType);
	}


	public void load() throws IOException {
		super.load();
		FileUtils.loadLong2IntMap(directory + "/edgesUpperBound", blockSize, compressionType);
		FileUtils.loadLong2IntMap(directory + "/edgesLowerBound", blockSize, compressionType);

		FileUtils.loadLong2IntMap(directory + "/nodesUpperBound", blockSize, compressionType);
		FileUtils.loadLong2IntMap(directory + "/nodesLowerBound", blockSize, compressionType);
	}

	public void createEdgesLowerBounds() {
		int numberOfEdges = getNumberOfEdges();
		Edge edge; 

		for(long i=0; i<numberOfEdges; i++) {
			edge = super.getEdge(i);
			edgesLowerBound.put((long)edge.getId(), getMinimunCostValue(edge.getCosts()));
		}
	}

	public void createEdgesUpperBounds() {

		int numberOfEdges = getNumberOfEdges();
		Edge edge; 

		for(int i=0; i<numberOfEdges; i++) {
			edge = getEdge(i);
			edgesUpperBound.put((long)edge.getId(), getMaximunCostValue(edge.getCosts()));
		}
	}

	public void createNodesLowerBounds() {
		int numberOfNodes = getNumberOfNodes();
		Node node; 

		for(long i=0; i<numberOfNodes; i++) {
			node = super.getNode(i);
			nodesLowerBound.put((long)node.getId(), getMinimunCostValue(node.getCosts()));
		}

		//		System.out.println(nodesLowerBound);

	}

	public void createNodesUpperBounds() {

		int numberOfNodes = getNumberOfNodes();
		Node node; 

		for(int i=0; i<numberOfNodes; i++) {
			node = getNode(i);
			nodesUpperBound.put((long)node.getId(), getMaximunCostValue(node.getCosts()));
		}
	}

	/**
	 * 
	 * @param v
	 * @param graphType The type of graph the will be used to retrieve costs needed. 0 = Regular Costs; 1 = Lower Bound Costs;
	 * 					3 = Upper Bound Costs.
	 * @param time	The time that will be used to get the time-dependent cost
	 * @return	all neighbors for the given parameters
	 */
	public Long2IntMap accessNeighborhood(Node v, short graphType, int time){

		Long2IntMap neighbors = new Long2IntOpenHashMap();
		int cost;

		for (Long e : this.getOutEdges(v.getId()) ) {

			Edge edge = this.getEdge(e);
			long neighborNodeId =  edge.getToNode();

			if(graphType == 0) {
				cost = this.getEdgeCost(edge, time);
			} else if(graphType == 1) {
				cost = getEdgesLowerBound().get(edge.getId());
			} else {
				cost = getEdgesUpperBound().get(edge.getId());
			}

			if(!neighbors.containsKey(neighborNodeId)) {
				neighbors.put(neighborNodeId, cost);
			}else{
				if(neighbors.get(neighborNodeId) > cost){
					neighbors.put(neighborNodeId, cost);
				}
			}
		}

		return neighbors;

	}

	//TODO Refactor this method
	public Integer getEdgeCost(Edge e, int t){

		LinearFunction[] lf = convertToLinearFunction(getEdgeCosts(e.getId()));

		int x = lf.length;
		int minutesInADay = 86400000;
		int position = t/(minutesInADay/x);
		return lf[position].calculateCost(t);

		//		int[] costs = getEdgeCosts(e.getId());
		//		int t1 = t;
		//		t = t - (t % 2);
		//		int pos = t/2;
		//		int s = costs[pos];
		//		//LinearFunction f = new LinearFunction(s);
		//		//return f.calculateCost(t1);
		//		return 0;


	}


	public int poiGetCost(long vid, short graphType){

		if(graphType == 0) {
			LinearFunction[] lf = convertToLinearFunction(getPoiCost(vid));
			return lf[0].calculateCost(0);
		} else if(graphType == 1){
			int[] nodeLowerBound = new int[] {getNodesLowerBound().get(vid), getNodesLowerBound().get(vid)};
			LinearFunction[] lf = convertToLinearFunction(nodeLowerBound);
			return lf[0].calculateCost(0);
		} else {
			int[] nodeUpperBound = new int[] {getNodesUpperBound().get(vid), getNodesUpperBound().get(vid)};
			LinearFunction[] lf = convertToLinearFunction(nodeUpperBound);
			return lf[0].calculateCost(0);
		}
	}

	public int[] getPoiCost(long vid){
		return getNodeCosts(vid);
	}

	public int[] getNodeCosts(long nodeId) {
		//		int[] lowerBound = {getNodesLowerBound().get(nodeId)};
		//		return lowerBound;

		NodeImpl node = (NodeImpl)getNode(nodeId);
		long costsIndex = node.getCostsIndex();

		if(costsIndex == -1 ) {
			return null;
		} else {
			return getNodeCostsByCostsIndex(costsIndex);
		}

	}

	@Override
	public void createBounds() {
		createEdgesUpperBounds();
		createEdgesLowerBounds();
		createNodesUpperBounds();
		createNodesLowerBounds();
	}

	@Override
	public Long2IntMap getEdgesUpperBound() {
		return edgesUpperBound;
	}

	@Override
	public Long2IntMap getEdgesLowerBound() {
		return edgesLowerBound;
	}

	@Override
	public Long2IntMap getNodesUpperBound() {
		return nodesUpperBound;
	}
	public int getEdgeLowerCost(long id){
		return edgesLowerBound.get(id);
	}

	@Override
	public Long2IntMap getNodesLowerBound() {
		return nodesLowerBound;
	}
	public int getEdgeUpperCost(long id){
		return edgesUpperBound.get(id);
	}
}