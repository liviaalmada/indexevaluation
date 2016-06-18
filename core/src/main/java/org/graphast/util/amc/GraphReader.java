package org.graphast.util.amc;

import org.graphast.importer.CostGenerator;
import org.graphast.model.GraphImpl;

public class GraphReader {

	private static final String PATH_GRAPH = "C:\\Users\\Lívia\\git\\graphast\\core\\src\\main\\resources\\";

	public static void main(String[] args) {
		GraphImpl graph = new GraphImpl(PATH_GRAPH+"fortaleza_100k");
		graph.load();
		
		CostGenerator.generateAllSyntheticEdgesCosts(graph);
		
		graph.save();
		
	}
}
