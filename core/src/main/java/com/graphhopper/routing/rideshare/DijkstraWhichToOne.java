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
package com.graphhopper.routing.rideshare;

import com.graphhopper.routing.AbstractRoutingAlgorithm;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.PathBidirRef;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.WeightCalculation;
import com.graphhopper.storage.EdgeEntry;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.PriorityQueue;

/**
 * Public transport represents a collection of Locations. Now it is the aim to find the shortest
 * path of a path ('the public transport') to the destination. In contrast to manyToOne this class
 * only find one shortest path and not all, but it it more memory efficient (ie. the
 * shortest-path-trees do not overlap here)
 * <p/>
 * @author Peter Karich
 */
public class DijkstraWhichToOne extends AbstractRoutingAlgorithm
{
    private PathBidirRef shortest;
    private TIntObjectMap<EdgeEntry> shortestDistMapOther;
    private TIntObjectMap<EdgeEntry> shortestDistMapFrom;
    private TIntObjectMap<EdgeEntry> shortestDistMapTo;
    private TIntArrayList pubTransport = new TIntArrayList();
    private int destination;
    private int visitedFromCount;
    private int visitedToCount;

    public DijkstraWhichToOne( Graph graph, FlagEncoder encoder, WeightCalculation type )
    {
        super(graph, encoder, type);
    }

    public void addPubTransportPoints( int... indices )
    {
        if (indices.length == 0)
        {
            throw new IllegalStateException("You need to add something");
        }

        for (int i = 0; i < indices.length; i++)
        {
            addPubTransportPoint(indices[i]);
        }
    }

    public void addPubTransportPoint( int index )
    {
        if (!pubTransport.contains(index))
        {
            pubTransport.add(index);
        }
    }

    public void setDestination( int index )
    {
        destination = index;
    }

    public Path calcPath()
    {
        // identical
        if (pubTransport.contains(destination))
        {
            return new Path(graph, flagEncoder);
        }

        PriorityQueue<EdgeEntry> prioQueueFrom = new PriorityQueue<EdgeEntry>();
        shortestDistMapFrom = new TIntObjectHashMap<EdgeEntry>();

        EdgeEntry entryTo = new EdgeEntry(EdgeIterator.NO_EDGE, destination, 0);
        EdgeEntry currTo = entryTo;
        PriorityQueue<EdgeEntry> prioQueueTo = new PriorityQueue<EdgeEntry>();
        shortestDistMapTo = new TIntObjectHashMap<EdgeEntry>();
        shortestDistMapTo.put(destination, entryTo);

        shortest = new PathBidirRef(graph, flagEncoder);

        // create several starting points
        if (pubTransport.isEmpty())
        {
            throw new IllegalStateException("You'll need at least one starting point. Set it via addPubTransportPoint");
        }

        EdgeEntry currFrom = null;
        for (int i = 0; i < pubTransport.size(); i++)
        {
            EdgeEntry tmpFrom = new EdgeEntry(EdgeIterator.NO_EDGE, pubTransport.get(i), 0);
            if (i == 0)
            {
                currFrom = tmpFrom;
            }

            shortestDistMapOther = shortestDistMapTo;
            fillEdges(shortest, tmpFrom, prioQueueFrom, shortestDistMapFrom, outEdgeExplorer);
        }

        int finish = 0;
        while (finish < 2 && currFrom.weight + currTo.weight < shortest.getWeight())
        {
            // http://www.cs.princeton.edu/courses/archive/spr06/cos423/Handouts/EPP%20shortest%20path%20algorithms.pdf
            // a node from overlap may not be on the shortest path!!
            // => when scanning an arc (v, w) in the forward search and w is scanned in the reverse 
            //    search, update shortest = μ if df (v) + (v, w) + dr (w) < μ            

            finish = 0;
            shortestDistMapOther = shortestDistMapTo;
            fillEdges(shortest, currFrom, prioQueueFrom, shortestDistMapFrom, outEdgeExplorer);
            if (!prioQueueFrom.isEmpty())
            {
                currFrom = prioQueueFrom.poll();
            } else
            {
                finish++;
            }

            shortestDistMapOther = shortestDistMapFrom;
            fillEdges(shortest, currTo, prioQueueTo, shortestDistMapTo, inEdgeExplorer);
            if (!prioQueueTo.isEmpty())
            {
                currTo = prioQueueTo.poll();
            } else
            {
                finish++;
            }
        }

        Path p = shortest.extract();
        if (!p.isFound())
        {
            return p;
        }
        return p;
    }

    void fillEdges( PathBidirRef shortest, EdgeEntry curr,
            PriorityQueue<EdgeEntry> prioQueue,
            TIntObjectMap<EdgeEntry> shortestDistMap, EdgeExplorer explorer )
    {
        boolean backwards = shortestDistMapFrom == shortestDistMapOther;

        int currNode = curr.endNode;
        EdgeIterator iter = explorer.setBaseNode(currNode);
        while (iter.next())
        {
            int tmpV = iter.getAdjNode();
            double tmp = weightCalc.getWeight(iter.getDistance(), iter.getFlags()) + curr.weight;

            if (!backwards) {
                tmp += turnCostCalc.getTurnCosts(currNode, curr.edge, iter.getEdge());
            } else {
                tmp += turnCostCalc.getTurnCosts(currNode, iter.getEdge(), curr.edge);
            }

            EdgeEntry de = shortestDistMap.get(tmpV);
            if (de == null)
            {
                de = new EdgeEntry(iter.getEdge(), tmpV, tmp);
                de.parent = curr;
                shortestDistMap.put(tmpV, de);
                prioQueue.add(de);
            } else if (de.weight > tmp)
            {
                prioQueue.remove(de);
                de.edge = iter.getEdge();
                de.weight = tmp;
                de.parent = curr;
                prioQueue.add(de);
            }

            updateShortest(de, tmpV);
        }
    }

    @Override
    public void updateShortest( EdgeEntry de, int currLoc )
    {
        EdgeEntry entryOther = shortestDistMapOther.get(currLoc);
        if (entryOther != null)
        {

            //prevents the shortest path to contain the same edge twice, when turn restriction is around the meeting point
            if (de.edge == entryOther.edge) {
                return;
            }

            // update μ
            double newShortest = de.weight + entryOther.weight;

            boolean backwards = shortestDistMapFrom == shortestDistMapOther;
            if (!backwards) {
                newShortest += turnCostCalc.getTurnCosts(currLoc, de.edge, entryOther.edge);
            } else {
                newShortest += turnCostCalc.getTurnCosts(currLoc, entryOther.edge, de.edge);
            }

            if (newShortest < shortest.getWeight())
            {
                shortest.setSwitchToFrom(shortestDistMapFrom == shortestDistMapOther);
                shortest.setEdgeEntry(de);
                shortest.setEdgeEntryTo(entryOther);
                shortest.setWeight(newShortest);
            }
        }
    }

    @Override
    public Path calcPath( int from, int to )
    {
        addPubTransportPoint(from);
        setDestination(to);
        return calcPath();
    }

    @Override
    public int getVisitedNodes()
    {
        return visitedFromCount + visitedToCount;
    }
}
