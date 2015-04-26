package org.graphast.query.route.shortestpath;

import static org.junit.Assert.assertEquals;

import org.graphast.config.Configuration;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.query.route.shortestpath.dijkstra.DijkstraConstantWeight;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public class DijkstraConstantWeightTest {

	private static Graph graphMonaco;
	private static Graph graphExample;
	private static Graph graphExample2;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@BeforeClass
	public static void setup() {
		graphMonaco = new GraphGenerator().generateMonaco();
		graphExample = new GraphGenerator().generateExample();
		graphExample2 = new GraphGenerator().generateExample2();
	}

	@Test
	public void shortestPathMonacoTest() {

		Long source = graphMonaco.getNodeId(43.7294668047756,7.413772473047058);
		Long target = graphMonaco.getNodeId(43.73079058671274,7.415815422292399);

		AbstractShortestPathService dj = new DijkstraConstantWeight(graphMonaco);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = dj.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(228910, shortestPath.getPathCost(), 0);

	}

	@Test
	public void shortestPathMonacoTest2() {

		Long source = graphMonaco.getNodeId(43.72842465479131, 7.414896579419745);
		Long target = graphMonaco.getNodeId(43.7354373276704, 7.4212202598427295);

		AbstractShortestPathService dj = new DijkstraConstantWeight(graphMonaco);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = dj.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest2(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(1136643.0, shortestPath.getPathCost(), 0);

	}

	@Test
	public void shortestPathExampleTest() {

		Long source = 0L; // External ID = 1
		Long target = 5L; // External ID = 4

		AbstractShortestPathService dj = new DijkstraConstantWeight(graphExample);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = dj.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathExampleTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(8100, shortestPath.getPathCost(), 0);

	}

	@Test
	public void shortestPathExample2Test() {

		Long source = 0L;
		Long target = 6L;

		AbstractShortestPathService dj = new DijkstraConstantWeight(graphExample2);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = dj.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathExample2Test(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(12, shortestPath.getPathCost(), 0);

	}

	@Test
	public void shortestPathMonacoTest3() {

		Long source = graphMonaco.getNodeId(43.72636792197156, 7.417292499928754);
		Long target = graphMonaco.getNodeId(43.74766484829034, 7.430716770083832);

		AbstractShortestPathService dj = new DijkstraConstantWeight(graphMonaco);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = dj.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest3(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(3610712.0, shortestPath.getPathCost(), 0);

	}

	@Test
	public void shortestPathGraphExampleReverseTest1() {

		Long source = 5L; // External ID = 1
		Long target = 0L; // External ID = 4

		graphExample.reverseGraph();

		AbstractShortestPathService dj = new DijkstraConstantWeight(graphExample);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = dj.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathExampleTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(8100, shortestPath.getPathCost(), 0);		
	}

	@Test
	public void shortestPathGraphExampleReverseTest2() {

		Long target = graphMonaco.getNodeId(43.72636792197156, 7.417292499928754);
		Long source = graphMonaco.getNodeId(43.74766484829034, 7.430716770083832);

		graphMonaco.reverseGraph();

		AbstractShortestPathService dj = new DijkstraConstantWeight(graphMonaco);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = dj.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest3(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(3610712.0, shortestPath.getPathCost(), 0);
	}

	@AfterClass
	public static void tearDown() {

		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");

	}

}