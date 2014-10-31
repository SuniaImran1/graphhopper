package uk.co.ordnancesurvey.routing;

import static org.junit.Assert.assertTrue;
import static uk.co.ordnancesurvey.routing.GraphHopperComponentIdentification.FROM_ROUTE;
import static uk.co.ordnancesurvey.routing.GraphHopperComponentIdentification.INSTRUCTIONS;
import static uk.co.ordnancesurvey.routing.GraphHopperComponentIdentification.ROUTE_SEARCH;
import static uk.co.ordnancesurvey.routing.GraphHopperComponentIdentification.ROUTE_TYPE_BIKE;
import static uk.co.ordnancesurvey.routing.GraphHopperComponentIdentification.ROUTE_TYPE_CAR;
import static uk.co.ordnancesurvey.routing.GraphHopperComponentIdentification.ROUTE_TYPE_WALK;
import static uk.co.ordnancesurvey.routing.GraphHopperComponentIdentification.TO_ROUTE;
import static uk.co.ordnancesurvey.routing.GraphHopperComponentIdentification.WAYPOINT_ONMAP;
import static uk.co.ordnancesurvey.routing.GraphHopperComponentIdentification.TOTAL_ROUTE_TIME;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.RegEx;

import org.alternativevision.gpx.beans.Route;
import org.alternativevision.gpx.beans.TrackPoint;
import org.alternativevision.gpx.beans.Waypoint;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.deps.com.thoughtworks.xstream.converters.basic.DateConverter;
import uk.co.ordnancesurvey.gpx.extensions.ExtensionConstants;
import uk.co.ordnancesurvey.gpx.graphhopper.GraphHopperGPXParserRouteTest;
import uk.co.ordnancesurvey.webtests.IntegrationTestProperties;
import uk.co.ordnancesurvey.webtests.multiplatform.MultiplatformTest;
import uk.co.ordnancesurvey.webtests.platforms.BrowserPlatformOptions;

public class GraphHopperUIUtil extends MultiplatformTest {

	private String baseUrl;
	private String routeStepNumber;
	String testOn = IntegrationTestProperties.getTestProperty("testON");
	GraphHopperGPXParserRouteTest GPHService = new GraphHopperGPXParserRouteTest();
	private static final Logger LOG = LoggerFactory
			.getLogger(GraphHopperUIUtil.class);

	public GraphHopperUIUtil() {
		super(BrowserPlatformOptions.getEnabledOptionsArrayList().get(0)[0]);
		init();
	}

	private void init() {
		baseUrl = IntegrationTestProperties
				.getTestProperty("graphHopperWebUrl");
		if (null == baseUrl) {
			baseUrl = "http://os-graphhopper.elasticbeanstalk.com/";
		}

		if (!testOn.equalsIgnoreCase("SERVICE"))
			initialiseWebDriver();

	}

	@Override
	public void logout() {
		if (!testOn.equalsIgnoreCase("SERVICE"))
			driver.close();
	}

	public void getRouteFromUI(String pointA, String pointB, String routeType) {

		switch (routeType)

		{
		case "car":
			clickElement(ROUTE_TYPE_CAR);
			break;
		case "bike":
			clickElement(ROUTE_TYPE_BIKE);
			break;
		case "walk":
			clickElement(ROUTE_TYPE_WALK);
			break;
		default:
			clickElement(ROUTE_TYPE_CAR);
			break;

		}

		typeIntoField(FROM_ROUTE, pointA);
		typeIntoField(TO_ROUTE, pointB);
		clickElement(ROUTE_SEARCH);
		waitFor(INSTRUCTIONS);

	}

	@Override
	public void login() {
		driver.navigate().to(baseUrl);
	}

	public void verifyInstructionThroughUI(String routeStepNumber,
			String stepInstruction) {
		this.routeStepNumber = routeStepNumber;
		checkTableRow(INSTRUCTIONS, Integer.parseInt(this.routeStepNumber),
				stepInstruction);

	}

	public void getRouteFromService(String pointA, String pointB,
			String routeType) {
		GPHService.parseRoute(pointA + "," + pointB, "gpx", routeType);

	}

	public void verifyInstructionThroughService(String stepInstruction) {
		HashSet<Route> routeInstruction = GPHService.getRoutes();

		Assert.assertTrue(
				"The Route instruction is not found in the gpx response",
				GPHService.routeContainsTurn(stepInstruction.toUpperCase(),
						routeInstruction.iterator().next()));

	}
	
	




	private Waypoint buildWayPoint(String waypointco, String time) throws ParseException {
		
	Waypoint wp = new Waypoint();
	String waypoint[] = waypointco.split(",");
	wp.setLatitude(new Double(waypoint[0]));
	wp.setLongitude(new Double(waypoint[1]));
	
	SimpleDateFormat t =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	Date date=t.parse(time);
	wp.setTime(date);
	return wp;
	}
	

	
	
	
	
		public void isWayPointonRouteMap(String wayPointIndex,
			String wayPoint_Coordinates, String wayPointDescription,
			String azimuth, String direction, String time, String distance) {
		final List<WebElement> WAY_POINTS;
		Waypoint wp;

		switch (testOn.toUpperCase()) {
		case "WEB":
			WAY_POINTS = driver.findElements(By
					.xpath("//*[@id='instructions']/tbody/tr[*]/td[2]"));
			WAY_POINTS.get(Integer.parseInt(wayPointIndex) - 1).click();
			Assert.assertTrue(getValue(WAYPOINT_ONMAP) + " comparison failed",
					wayPointDescription
							.equalsIgnoreCase(getValue(WAYPOINT_ONMAP)));
			break;
		case "SERVICE":
			wp = buildWayPoint(wayPoint_Coordinates, wayPointDescription,
					azimuth, direction, time, distance);
			Assert.assertTrue(GPHService.isWayPointOnGPXRoutes(wp));
			
			break;

		default:
			WAY_POINTS = driver.findElements(By
					.xpath("//*[@id='instructions']/tbody/tr[*]/td[2]"));
			WAY_POINTS.get(Integer.parseInt(wayPointIndex) - 1).click();
			Assert.assertTrue(getValue(WAYPOINT_ONMAP) + " comparison failed",
					wayPointDescription
							.equalsIgnoreCase(getValue(WAYPOINT_ONMAP)));
			wp = buildWayPoint(wayPoint_Coordinates, wayPointDescription,
					azimuth, direction, time, distance);
			Assert.assertTrue(GPHService.isWayPointOnGPXRoutes(wp));
			break;
		}

	}

	private Waypoint buildWayPoint(String wayPoint_Coordinates,
			String wayPointDescription, String azimuth, String direction,
			String time, String distance) {

		Waypoint wp = new Waypoint();
		String waypoint[] = wayPoint_Coordinates.split(",");
		wp.setLatitude(new Double(waypoint[0]));
		wp.setLongitude(new Double(waypoint[1]));
		
		wp.setDescription(wayPointDescription);
		wp.addExtensionData(ExtensionConstants.AZIMUTH, azimuth);
		wp.addExtensionData(ExtensionConstants.DIRECTION, direction);
		wp.addExtensionData(ExtensionConstants.TIME, time);
		wp.addExtensionData(ExtensionConstants.DISTANCE, distance);
		LOG.info(wp.toString());
		return wp;
	}

	public void isWayPointonRouteMap(List<Map> waypointList) {

		for (int i = 0; i < waypointList.size(); i++) {
			String wayPointIndex = (String) waypointList.get(i).get(
					"wayPointIndex");
			String waypointco = (String) waypointList.get(i).get("waypointco");
			String waypointdesc = (String) waypointList.get(i).get(
					"waypointdesc");
			String azimuth = (String) waypointList.get(i).get("azimuth");
			String direction = (String) waypointList.get(i).get("direction");
			String time = (String) waypointList.get(i).get("time");
			String distance = (String) waypointList.get(i).get("distance");
			isWayPointonRouteMap(wayPointIndex, waypointco, waypointdesc,
					azimuth, direction, time, distance);
		}

	}

	public void verifyTotalRouteTime(String totalRouteTime) {
		long actualTime;
		long expectedTime = Long.parseLong(totalRouteTime);
		String pattern;

		switch (testOn.toUpperCase()) {
		case "WEB":
			pattern = "((?<=\\D)(?=\\d)|(?<=\\d)(?=\\D))";
			actualTime = Long.parseLong(getValue(TOTAL_ROUTE_TIME).split(
					"take ")[1].split(pattern)[0]);
			LOG.info("The total route time expected is " + expectedTime
					+ " and actual is " + actualTime);
			assertTrue("The total route time expected " + expectedTime
					+ " is not matchin with actual " + actualTime,
					actualTime <= expectedTime);

			break;
		case "SERVICE":
			actualTime = GPHService.getTotalRouteTime() / (60 * 1000);
			expectedTime = Long.parseLong(totalRouteTime);
			LOG.info("The total route time expected is " + expectedTime
					+ " and actual is " + actualTime);
			assertTrue("The total route time expected " + expectedTime
					+ " is not matchin with actual " + actualTime,
					actualTime <= expectedTime);

			break;

		default:
			actualTime = GPHService.getTotalRouteTime() / (60 * 1000);
			expectedTime = Long.parseLong(totalRouteTime);
			assertTrue("The total route time expected" + expectedTime
					+ " is not matchin with actual " + actualTime,
					actualTime <= expectedTime);
			pattern = "((?<=\\D)(?=\\d)|(?<=\\d)(?=\\D))";
			actualTime = Long.parseLong(getValue(TOTAL_ROUTE_TIME).split(
					"take ")[1].split(pattern)[0]);
			LOG.info("The total route time expected is " + expectedTime
					+ " and actual is " + actualTime);
			assertTrue("The total route time expected" + expectedTime
					+ " is not matchin with actual " + actualTime,
					actualTime <= expectedTime);

			break;
		}

	}
	
	


	public void isTrackPointonRouteMap(List<Map> trackPointsList) throws ParseException {
		
		

		for (int i = 0; i < trackPointsList.size(); i++) {
			
			String waypointco = (String) trackPointsList.get(i).get("trackPointco");
			String time=(String) trackPointsList.get(i).get("time");
					
			Waypoint trackPoint = buildWayPoint(waypointco,time);
			assertTrue(GPHService.isWayPointOnTrack(trackPoint, GPHService.getTracks().iterator().next()));
			
		}
		
		
	}



}