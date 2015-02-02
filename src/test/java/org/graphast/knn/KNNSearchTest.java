package org.graphast.knn;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.query.knn.BoundsKNN;
import org.graphast.query.knn.KNNSearch;
import org.graphast.query.knn.NearestNeighbor;
import org.graphast.util.DateUtils;
import org.graphast.util.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public abstract class KNNSearchTest {
	private static KNNSearch knn;
	
	private Graph graphPoI;
	
	private Graph lbgAdapter;
	
	private Graph ubgAdapter;
	
	@Before
	public void setUp() throws Exception{
		graphPoI = new GraphGenerator().generateExamplePoI();
		lbgAdapter.load(getResourcePath("/lbg.json"), FileType.GRAPHSON);
		ubgAdapter.load(getResourcePath("/ubg.json"), FileType.GRAPHSON);
		
		//calculate or load bounds
		BoundsKNN minBounds = new BoundsKNN(lbgAdapter, "localhost", MapName.LBKNN);
		BoundsKNN maxBounds = new BoundsKNN(ubgAdapter, "localhost", MapName.UBKNN);
		
		knn = new KNNSearch(graphPoI, minBounds, maxBounds);
	}
	
	@Test
	public void search() throws ParseException{
		Date date = DateUtils.parseDate(0, 550, 0);
    	
    	ArrayList<NearestNeighbor> nn = knn.search(graphPoI.getNode(1), date, 2);
    			
    	assertEquals(graphPoI.getNode(2).getId(), nn.get(0).getId());
		assertEquals(900000, nn.get(0).getDistance());
		assertEquals(graphPoI.getNode(5).getId(), nn.get(1).getId());
		assertEquals(3120000, nn.get(1).getDistance());
				
	}
	
	@After
	public void tearDown(){
		graphPoI.shutdown();
		lbgAdapter.shutdown();
		ubgAdapter.shutdown();
	}
	
	@AfterClass
	public static void shutdown() throws IOException{
		FileUtils.deleteDirectory(new File());
	}
	
}
