package org.graphast.query.rnn.spatial.pruning;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.graphast.model.Graph;
import org.graphast.model.GraphBoundsImpl;
import org.graphast.model.GraphImpl;
import org.graphast.model.Node;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.query.model.LowerBoundEntry;
import org.graphast.query.rnn.RNNBacktrackingSearch;
import org.graphast.query.rnn.RNNBreadthFirstSearch;
import org.graphast.query.route.shortestpath.astar.AStarLinearFunction;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;
import org.graphast.util.DateUtils;
import org.graphast.util.DistanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReversekNNCandidatesFilter  extends AStarLinearFunction implements RNNCandidatesFilter {

	


	private static final String PATH_GRAPH = "C:\\Users\\Lívia\\git\\graphast\\core\\src\\main\\resources\\";
	private static Logger log = LoggerFactory.getLogger(ReversekNNCandidatesFilter.class);
	
	public ReversekNNCandidatesFilter(Graph graph) {
		super(graph);
	}

	

	@Override
	public Path search(Long idQuery, Date startTime, List<NearestNeighbor> nns) {
		AStarLinearFunction astar = new AStarLinearFunction(graph);
		double minCost = Double.MAX_VALUE;
		Path minPath = null;
		NearestNeighbor nn = new NearestNeighbor();
		for (NearestNeighbor candidate : nns) {
			try {
				//log.debug("Test " + candidate.getId());
				Path shortestPath = astar.shortestPath(candidate.getId(), idQuery, startTime);
				if (shortestPath.getTotalCost() < minCost) {
					minCost = shortestPath.getTotalCost();
					minPath = shortestPath;
					nn.setId(candidate.getId());
					nn.setNumberVisitedNodes(shortestPath.getNumberVisitedNodes());
					nn.setTravelTime(shortestPath.getTotalCost());
					nn.setDistance((int) shortestPath.getTotalDistance());
					nn.setPath(new ArrayList<>(shortestPath.getEdges()));
					log.debug("Shortest path cost " + shortestPath.getTotalCost());
				}
			} catch (Exception e) {

			}
		}
		return minPath;
	}

	public ArrayList<NearestNeighbor> astarSearch(Long idQuery, Date time, List<NearestNeighbor> nns) {
		HashMap<Long, Integer> wasTraversed = new HashMap<Long, Integer>();
		HashMap<Long, RouteEntry> parents = new HashMap<Long, RouteEntry>();
		PriorityQueue<LowerBoundEntry> queue = new PriorityQueue<LowerBoundEntry>();
		NearestNeighbor nn = new NearestNeighbor();
		Node target = graph.getNode(idQuery);
		int k = 5;
		ArrayList<NearestNeighbor> ret = new ArrayList<>();
		for (NearestNeighbor candidate : nns) {
			long vid = candidate.getId();
			int arrivalTime =  DateUtils.dateToMilli(time);
			int travelTime = 0;
			int lowerBound = (int) (DistanceUtils.timeCost(graph.getNode(candidate.getId()), graph.getNode(idQuery)));
			queue.offer(new LowerBoundEntry(vid,travelTime,arrivalTime,-1,lowerBound));
			wasTraversed.put(vid, 0);
		}
		int numberOfVisitedVertex = 0; int found=0;
		while((queue.peek()) != null && found < k){
			LowerBoundEntry removed = queue.poll();
			//
			if(removed.getId()==idQuery){
				List<RouteEntry> reconstructPath = super.reconstructPath(idQuery, parents);
				//System.out.println(reconstructPath); 
				ArrayList<Long>path = new ArrayList<>();
				for (RouteEntry routeEntry : reconstructPath) {
					path.add(graph.getEdge(routeEntry.getEdgeId()).getFromNode());
				}
				path.add(idQuery);
				numberOfVisitedVertex++;
				ret.add(new NearestNeighbor(path.get(0), removed.getTravelTime(), path, numberOfVisitedVertex ));
				found ++;
				//log.debug("Query found value: "+removed);
			}else{
				//log.debug("Removed entry value: "+removed);
				expandVertex(target, removed, wasTraversed, queue, parents);	
				numberOfVisitedVertex++;
			}
			
		}
		return ret;
		
	}

	

	public static void main(String[] args) {
		GraphImpl graph = new GraphImpl(PATH_GRAPH + "view_exp_100k500Pois");
		graph.load();

		//long idQuery = 25; // nao encontra
		//long idQuery = 30; // idem
		long idQuery = 520; //idem
		//long idQuery = 5000;
//		long idQuery = 4500;
		Node query = graph.getNode(idQuery);

		List<NearestNeighbor> nns = new ArrayList<>();

		for (long id = 1; id < graph.getNumberOfNodes(); id++) {
			Node node = graph.getNode(id);
			if (node.getCategory() > 0) {
				NearestNeighbor nn = new NearestNeighbor(id, (int) DistanceUtils.distanceLatLong(node, query));
				nns.add(nn);
			}
		}

		ReversekNNCandidatesFilter filter = new ReversekNNCandidatesFilter(graph);
		
		Date timeout = DateUtils.parseDate(1, 30, 00);
//		Date timestamp = DateUtils.parseDate(9, 00, 00);
		Date timestamp = DateUtils.parseDate(12, 00, 00);
		log.debug(System.currentTimeMillis()+"");
		ArrayList<NearestNeighbor> astarSearch = filter.astarSearch(idQuery, timestamp, nns);
		log.debug(System.currentTimeMillis()+"");
		System.out.println(astarSearch );
		
			
		GraphBoundsImpl gbounds = new GraphBoundsImpl(PATH_GRAPH + "view_exp_100k500Pois");
		
		gbounds.loadFromGraph();
		
		RNNBreadthFirstSearch rnnBFS = new RNNBreadthFirstSearch(gbounds);
		log.debug(System.currentTimeMillis()+"");
		NearestNeighbor sol = rnnBFS.search(gbounds.getNode(idQuery), timeout, timestamp);
		log.debug(System.currentTimeMillis()+"");
		System.out.println(sol + " "+ sol.getTravelTime());
		
	//	Path search = filter.search(idQuery, new Date(), nns);
	//	System.out.println(search);

	}

}
