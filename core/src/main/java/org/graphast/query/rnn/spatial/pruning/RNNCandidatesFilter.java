package org.graphast.query.rnn.spatial.pruning;

import org.graphast.query.knn.NearestNeighbor;

/**
 * This interface is responsible to filter a set of POIs that are candidates to be reverse nearest neighbors.
 * @author Lívia
 *
 */
public interface RNNCandidatesFilter {
	public NearestNeighbor[] search(Long idSource);
}
