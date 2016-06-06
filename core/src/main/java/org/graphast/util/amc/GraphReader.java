package org.graphast.util.amc;

import java.io.IOException;

import org.graphast.config.Configuration;
import org.graphast.importer.amc.POISensorsImporter;
import org.graphast.model.GraphImpl;

public class GraphReader {

	private static final String PATH_GRAPH = Configuration.USER_HOME
			+ "/graphast/test/example";
	
	public static void main(String[] args) {
		GraphImpl graph = new GraphImpl(PATH_GRAPH+"view_exp_100k");
		graph.load();
		System.out.println(graph.getNumberOfEdges());
		System.out.println(graph.getNumberOfNodes());
		try {
			POISensorsImporter.runImport("sensores", graph);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
