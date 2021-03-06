package com.graphhopper.reader.osgb;

import static org.junit.Assert.*;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.graphhopper.reader.osgb.itn.OSITNWay;

public class OSITNWayTest {

	@Test
	public void testReadTagsForRoundabout() throws XMLStreamException, MismatchedDimensionException, FactoryException, TransformException {
		String wayWithRoundabout = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
				"<osgb:FeatureCollection xmlns:osgb='http://www.ordnancesurvey.co.uk/xml/namespaces/osgb'\n" + 
				"	xmlns:gml='http://www.opengis.net/gml' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n" + 
				"	xsi:schemaLocation='http://www.ordnancesurvey.co.uk/xml/namespaces/osgb http://www.ordnancesurvey.co.uk/xml/schema/v7/OSDNFFeatures.xsd'\n" + 
				"	fid='GDS-58096-1'>" +
				"		<osgb:RoadLink fid='osgb4000000009314037'>\n" + 
				"<osgb:descriptiveTerm>A Road</osgb:descriptiveTerm>\n" + 
				"<osgb:natureOfRoad>Roundabout</osgb:natureOfRoad>\n" + 
				"<osgb:length>12.04</osgb:length>\n" + 
				"<osgb:polyline>\n" + 
				"<gml:LineString srsName='osgb:BNG'>\n" + 
				"<gml:coordinates>356370.000,430137.000 356367.000,430139.000 356366.000,430141.000 356365.386,430142.099 356365.000,430144.000 356365.000,430146.000 356365.000,430147.000 </gml:coordinates>\n" + 
				"</gml:LineString>\n" + 
				"</osgb:polyline>\n" + 
				"<osgb:directedNode orientation='-' xlink:href='#osgb4000000009126286'/>\n" + 
				"<osgb:directedNode orientation='+' xlink:href='#osgb4000000009126257'/>\n" + 
				"<osgb:referenceToTopographicArea xlink:href='#osgb1000000214086160'/>\n" + 
				"</osgb:RoadLink>"; 
		
		XMLInputFactory factory = XMLInputFactory.newInstance();
		StringReader stringReader = new StringReader(wayWithRoundabout);
		XMLStreamReader parser = factory.createXMLStreamReader(stringReader);
		OSITNWay way = OSITNWay.create(0, parser);
		assertTrue(way.hasTag("junction", "roundabout"));
        assertTrue("ITN Data is uk specific so roundabouts should be clockwise", way.hasTag("direction", "clockwise"));
	}
	
	@Test
	public void testReadTagsForPrivatePublicallyAccessibleRoads() throws XMLStreamException, MismatchedDimensionException, FactoryException, TransformException {
		String privatePublic = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
				"<osgb:FeatureCollection xmlns:osgb='http://www.ordnancesurvey.co.uk/xml/namespaces/osgb'\n" + 
				"	xmlns:gml='http://www.opengis.net/gml' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n" + 
				"	xsi:schemaLocation='http://www.ordnancesurvey.co.uk/xml/namespaces/osgb http://www.ordnancesurvey.co.uk/xml/schema/v7/OSDNFFeatures.xsd'\n" + 
				"	fid='GDS-58096-1'>" +
				"<osgb:RoadLink fid='osgb4000000025030155'>\n" + 
				"<osgb:descriptiveGroup>Road Topology</osgb:descriptiveGroup>\n" + 
				"<osgb:descriptiveTerm>Private Road - Publicly Accessible</osgb:descriptiveTerm>\n" + 
				"<osgb:natureOfRoad>Single Carriageway</osgb:natureOfRoad>\n" + 
				"<osgb:length>103.74</osgb:length>\n" + 
				"<osgb:polyline>\n" + 
				"<gml:LineString srsName='osgb:BNG'>\n" + 
				"<gml:coordinates>166785.778,26371.534 166767.559,26395.371 166746.384,26421.542 166732.941,26438.078 166725.804,26447.595 166722.647,26453.691 </gml:coordinates>\n" + 
				"</gml:LineString>\n" + 
				"</osgb:polyline>\n" + 
				"<osgb:directedNode orientation='-' xlink:href='#osgb4000000025030107'/>\n" + 
				"<osgb:directedNode orientation='+' xlink:href='#osgb4000000025030094'/>\n" + 
				"<osgb:referenceToTopographicArea xlink:href='#osgb1000002033801767'/>\n" + 
				"</osgb:RoadLink>\n";
		
		XMLInputFactory factory = XMLInputFactory.newInstance();
		StringReader stringReader = new StringReader(privatePublic);
		XMLStreamReader parser = factory.createXMLStreamReader(stringReader);
		OSITNWay way = OSITNWay.create(0, parser);
		assertEquals("private", way.getTag("access"));
	}
	
	@Test
	public void testReadTagsPrivateRestrictedRoads() throws XMLStreamException, MismatchedDimensionException, FactoryException, TransformException {
		String privateRestricted = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
				"<osgb:FeatureCollection xmlns:osgb='http://www.ordnancesurvey.co.uk/xml/namespaces/osgb'\n" + 
				"	xmlns:gml='http://www.opengis.net/gml' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n" + 
				"	xsi:schemaLocation='http://www.ordnancesurvey.co.uk/xml/namespaces/osgb http://www.ordnancesurvey.co.uk/xml/schema/v7/OSDNFFeatures.xsd'\n" + 
				"	fid='GDS-58096-1'>" +
				"		<osgb:RoadLink fid='osgb4000000009314037'>\n" + 
				"<osgb:descriptiveTerm>Private Road - Restricted Access</osgb:descriptiveTerm>\n" + 
				"<osgb:natureOfRoad>Single Carriageway</osgb:natureOfRoad>\n" + 
				"<osgb:length>12.04</osgb:length>\n" + 
				"<osgb:polyline>\n" + 
				"<gml:LineString srsName='osgb:BNG'>\n" + 
				"<gml:coordinates>356370.000,430137.000 356367.000,430139.000 356366.000,430141.000 356365.386,430142.099 356365.000,430144.000 356365.000,430146.000 356365.000,430147.000 </gml:coordinates>\n" + 
				"</gml:LineString>\n" + 
				"</osgb:polyline>\n" + 
				"<osgb:directedNode orientation='-' xlink:href='#osgb4000000009126286'/>\n" + 
				"<osgb:directedNode orientation='+' xlink:href='#osgb4000000009126257'/>\n" + 
				"<osgb:referenceToTopographicArea xlink:href='#osgb1000000214086160'/>\n" + 
				"</osgb:RoadLink>"; 
		
		XMLInputFactory factory = XMLInputFactory.newInstance();
		StringReader stringReader = new StringReader(privateRestricted);
		XMLStreamReader parser = factory.createXMLStreamReader(stringReader);
		OSITNWay way = OSITNWay.create(0, parser);
		assertEquals("no", way.getTag("access"));
	}

}
