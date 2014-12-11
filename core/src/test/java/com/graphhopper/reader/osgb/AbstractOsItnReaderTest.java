package com.graphhopper.reader.osgb;

import static com.graphhopper.util.GHUtility.count;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;

import com.graphhopper.routing.util.BikeFlagEncoder;
import com.graphhopper.routing.util.DefaultEdgeFilter;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.FootFlagEncoder;
import com.graphhopper.routing.util.RelationCarFlagEncoder;
import com.graphhopper.storage.ExtendedStorage;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.RAMDirectory;
import com.graphhopper.storage.TurnCostStorage;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;

public abstract class AbstractOsItnReaderTest {

    protected EncodingManager encodingManager;// = new
    // EncodingManager("CAR");//"car:com.graphhopper.routing.util.RelationCarFlagEncoder");
    protected RelationCarFlagEncoder carEncoder;// = (RelationCarFlagEncoder)
      // encodingManager
// .getEncoder("CAR");
    protected EdgeFilter carOutEdges;// = new DefaultEdgeFilter(
// carEncoder, false, true);
    protected EdgeFilter carInEdges;
    protected boolean turnCosts = true;
    protected EdgeExplorer carOutExplorer;
    protected EdgeExplorer carAllExplorer;
    protected BikeFlagEncoder bikeEncoder;
    protected FootFlagEncoder footEncoder;

    //RoadNode 880
    protected static double node0Lat = 50.6992070044d;
    protected static double node0Lon = -3.55893724720532d;

    //RoadNode 881
    protected static double node1Lat = 50.6972276414d;
    protected static double node1Lon = -3.70047108174d;

    //RoadNode 882
    protected static double node2Lat = 50.6950765311d;
    protected static double node2Lon = -3.84198830979d;

    //RoadNode 883
    protected static double node3Lat = 50.6522837438d;
    protected static double node3Lon = -3.69884731399d;

    //RoadNode 884
    protected static double node4Lat = 50.7421711523d;
    protected static double node4Lon = -3.70209900111d;
    
    @Before
    public void initEncoding() {
        if (turnCosts) {
            carEncoder = new RelationCarFlagEncoder(5, 5, 3);
            bikeEncoder = new BikeFlagEncoder(4, 2, 3);
        } else {
            carEncoder = new RelationCarFlagEncoder();
            bikeEncoder = new BikeFlagEncoder();
        }

        footEncoder = new FootFlagEncoder();
        carOutEdges = new DefaultEdgeFilter(carEncoder, false, true);
        carInEdges  = new DefaultEdgeFilter(carEncoder, true, false);
        encodingManager = new EncodingManager(footEncoder, carEncoder, bikeEncoder);
    }
    
    protected OsItnReader readGraphFile(GraphHopperStorage graph, File file) throws IOException {
        OsItnReader osItnReader = new OsItnReader(graph);
        osItnReader.setOSMFile(file);
        osItnReader.setEncodingManager(encodingManager);
        osItnReader.readGraph();
        return osItnReader;
    }

    protected GraphHopperStorage configureStorage(boolean turnRestrictionsImport, boolean is3D) {
        String directory = "/tmp";
        ExtendedStorage extendedStorage = turnRestrictionsImport ? new TurnCostStorage() : new ExtendedStorage.NoExtendedStorage();
        GraphHopperStorage graph = new GraphHopperStorage(new RAMDirectory(directory, false), encodingManager, is3D, extendedStorage);
        return graph;
    }

    protected void checkSimpleNodeNetwork(GraphHopperStorage graph) {
        EdgeExplorer explorer = graph.createEdgeExplorer(carOutEdges);
        assertEquals(4, count(explorer.setBaseNode(0)));
        assertEquals(1, count(explorer.setBaseNode(1)));
        assertEquals(1, count(explorer.setBaseNode(2)));
        assertEquals(1, count(explorer.setBaseNode(3)));
        assertEquals(1, count(explorer.setBaseNode(4)));

        EdgeIterator iter = explorer.setBaseNode(0);
        assertTrue(iter.next());
        assertEquals("OTHER ROAD", iter.getName());
        iter.next();
        assertEquals("OTHER ROAD", iter.getName());
        iter.next();
        assertEquals("BONHAY ROAD", iter.getName());
        iter.next();
        assertEquals("BONHAY ROAD", iter.getName());
        assertFalse(iter.next());
    }
    protected int getEdge(int from, int to) {
        EdgeIterator iter = carOutExplorer.setBaseNode(from);
        while (iter.next()) {
            if (iter.getAdjNode() == to) {
                return iter.getEdge();
            }
        }
        return EdgeIterator.NO_EDGE;
    }
    protected void evaluateRouting(final EdgeIterator iter, final int node, final boolean forward, final boolean backward, final boolean finished) {
        assertEquals("Incorrect adjacent node", node, iter.getAdjNode());
        assertEquals("Incorrect forward instructions", forward, carEncoder.isBool(iter.getFlags(), FlagEncoder.K_FORWARD));
        assertEquals("Incorrect backward instructions", backward, carEncoder.isBool(iter.getFlags(), FlagEncoder.K_BACKWARD));
        assertEquals(!finished, iter.next());
    }
}