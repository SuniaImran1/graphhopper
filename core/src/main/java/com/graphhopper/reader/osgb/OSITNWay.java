/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  GraphHopper licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.reader.osgb;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TObjectLongHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.reader.Way;

/**
 * Represents an OSM Way
 * <p/>
 * 
 * @author Nop
 */
public class OSITNWay extends OSITNElement implements Way {
    private static final long WAY_NODE_PREFIX_MOD = 100000000000000000L;
    protected final TLongList nodes = new TLongArrayList(5);
    protected String[] wayCoords;
    private static final Logger logger = LoggerFactory.getLogger(OSITNWay.class);

    /**
     * Constructor for XML Parser
     */
    public static OSITNWay create(long id, XMLStreamReader parser) throws XMLStreamException {
        OSITNWay way = new OSITNWay(id);
        parser.nextTag();
        way.readTags(parser);
        logger.info(way.toString());
        return way;
    }

    public OSITNWay(long id) {
        super(id, WAY);
    }

    public TLongList getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return "Way (" + getId() + ", " + nodes.size() + " nodes)";
    }

    @Override
    protected void parseCoords(String lineDefinition) {
        String[] lineSegments = lineDefinition.split(" ");
        wayCoords = Arrays.copyOfRange(lineSegments, 1, lineSegments.length - 1);
        logger.info(toString() + " " + ((wayCoords.length == 0) ? "0" : wayCoords[0]));
    }

    @Override
    protected void parseCoords(int dimensions, String lineDefinition) {
        String[] lineSegments = lineDefinition.split(" ");
        wayCoords = new String[lineSegments.length / dimensions];
        StringBuilder curString = null;
        for (int i = 0; i < lineSegments.length; i++) {
            String string = lineSegments[i];
            switch (i % dimensions) {
            case 0: {
                int coordNumber = i / dimensions;
                if (coordNumber > 0) {
                    wayCoords[coordNumber - 1] = curString.toString();
                }
                curString = new StringBuilder();
                curString.append(string);
                break;
            }

            case 1:
            case 2: {
                curString.append(',');
                curString.append(string);
            }
            }
        }
        wayCoords[wayCoords.length - 1] = curString.toString();
        logger.info(toString() + " " + ((wayCoords.length == 0) ? "0" : wayCoords[0]));
    }

    @Override
    protected void parseNetworkMember(String elementText) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void addDirectedNode(String nodeId, String grade, String orientation) {
        String idStr = nodeId.substring(5);
        if (null != grade) {
            idStr = grade + idStr;
        }
        long id = Long.parseLong(idStr);

        if (0 == nodes.size()) {
            nodes.add(id);
        } else {
            addWayNodes();
            nodes.add(id);
        }
        logger.info(toString());
    }

    protected void addWayNodes() {
        for (int i = 1; i <= wayCoords.length; i++) {
            long idPrefix = i * WAY_NODE_PREFIX_MOD;
            long extraId = idPrefix + getId();
            nodes.add(extraId);
        }
    }

    @Override
    protected void addDirectedLink(String nodeId, String orientation) {
        throw new UnsupportedOperationException();

    }

    /**
     * Look for coords and directed link id in our new map. if it exists then set the barriers tag to "noentry" barrier flag as defined in CarFlagEncoder absoluteBarriers.add("noentry");
     * edgeIdCoordsToNodeFlagsMap = new TObjectLongHashMap<String>()
     * @return
     */
    public List<OSITNNode> evaluateWayNodes( TObjectLongHashMap<String> edgeIdCoordsToNodeFlagsMap) {
        List<OSITNNode> wayNodes = new ArrayList<OSITNNode>();

        for (int i = 0; i < wayCoords.length; i++) {
            String wayCoord = wayCoords[i];
            long idPrefix = (i + 1) * WAY_NODE_PREFIX_MOD;
            long id = idPrefix + getId();
            OSITNNode wayNode = new OSITNNode(id);
            wayNode.parseCoords(wayCoord);
            
            // Here we need to check the ItnReader edgeIdCoordsToNodeFlagsMap for this id-coord pair
            String key = getId()+"-"+wayCoord;
            if (edgeIdCoordsToNodeFlagsMap.containsKey(key)) {
                long direction = edgeIdCoordsToNodeFlagsMap.get(key);
                wayNode.setTag(TAG_KEY_NOENTRY_ORIENTATION, "true");
                if (direction >0l) {
                    wayNode.setTag(TAG_KEY_ONEWAY_ORIENTATION, "true");
                }
                else {
                    wayNode.setTag(TAG_KEY_ONEWAY_ORIENTATION, "-1");
                }
            }
            logger.info("Node " + getId()+ " coords: " + wayCoord + " tags: ");
            for (String tagKey : wayNode.getTags().keySet()) {
                logger.info("\t " + tagKey+ " : " + wayNode.getTag(tagKey));
            }
            
            wayNodes.add(wayNode);
        }
        return wayNodes;
    }
    
    public void clearWayNodes() {
        wayCoords = null;
    }

    @Override
    protected void parseCoordinateString(String elementText, String elementSeparator) {
        throw new UnsupportedOperationException();

    }
}
