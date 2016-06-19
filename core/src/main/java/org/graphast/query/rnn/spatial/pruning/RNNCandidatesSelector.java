package org.graphast.query.rnn.spatial.pruning;

import org.graphast.query.knn.NearestNeighbor;

/**
 * This interface is responsible by select a set of POIs that are candidates to be the k reverse nearest neighbors. 
 * @author Lívia
 *
 */
public interface RNNCandidatesSelector{
	
	public NearestNeighbor[] search(Long idSource);

}
