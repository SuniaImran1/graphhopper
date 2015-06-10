package com.graphhopper.reader.osgb.roadclassification;

import com.graphhopper.reader.Way;
import com.graphhopper.reader.osgb.AbstractOsToOsmAttibuteMappingVisitor;

public class PedestrianisedStreet extends AbstractOsToOsmAttibuteMappingVisitor
{

	@Override
	protected void applyAttributes( Way way )
	{
		way.setTag("highway","pedestrian");
	}
}
