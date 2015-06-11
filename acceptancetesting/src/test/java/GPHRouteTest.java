import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.alternativevision.gpx.beans.Route;
import org.alternativevision.gpx.beans.Waypoint;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.co.ordnancesurvey.gpx.extensions.ExtensionConstants;
import uk.co.ordnancesurvey.gpx.graphhopper.GraphHopperGPXParser;
import uk.co.ordnancesurvey.gpx.graphhopper.GraphHopperGPXUtil;

public class GPHRouteTest {
	String path = getClass().getResource("sampleGraphHopper.gpx").getPath();
	GraphHopperGPXParser ghrt = GraphHopperGPXParser.getParserForgpxFileName(path);
	GraphHopperGPXUtil GPHGPXUtil= new GraphHopperGPXUtil();
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testrouteContainsTurn() {
		
		HashSet<Route> hs = ghrt.getRoutes();
		String turn = "turn sharp right onto Bellemoor Road";
		
		assertTrue(GPHGPXUtil.routeContainsTurn(turn, hs.iterator().next()));
	}
	
	
	@Test
	public void testWayPointIsOnRoute() {

		Waypoint wayPoint = getTestWayPoint("50.927146","-1.416787","339","N","2515","13.974","turn  right onto Wellington Road");
		

		HashSet<Route> hs = ghrt.getRoutes();
		
		assertTrue(GPHGPXUtil.isWayPointOnRoute(wayPoint,hs.iterator().next()));
	}
	
	@Test
	public void testTotalRouteTime() {
		String path = getClass().getResource("sampleGraphHopper.gpx").getPath();
		GraphHopperGPXParser ghrt = GraphHopperGPXParser.getParserForgpxFileName(path);
		assertEquals(269000, ghrt.getTotalRouteTime());
	}
	
	@Test
	public void testGetRouteAsGPX() {
		String path = getClass().getResource("sampleGraphHopper.gpx").getPath();
		GraphHopperGPXParser ghrt = GraphHopperGPXParser.getParserForgpxFileName(path);
		HashSet<Route> hs = ghrt.getRoutes();
		
		Route next = hs.iterator().next();
		assertTrue(GPHGPXUtil.routeContainsTurn("turn sharp right onto Bellemoor Road",next));
	}

	private Waypoint getTestWayPoint(String lat, String lon,String azimuth,String direction,String time,String distance,String description) {
		
		Waypoint wp = new Waypoint();
		wp.setLatitude(new Double(lat));
		wp.setLongitude(new Double(lon));
		wp.setDescription(description);
		wp.addExtensionData(ExtensionConstants.AZIMUTH, azimuth);
		wp.addExtensionData(ExtensionConstants.DIRECTION, direction);
		wp.addExtensionData(ExtensionConstants.TIME, time);
		wp.addExtensionData(ExtensionConstants.DISTANCE, distance);
return wp;
	}
	
	

	

}
