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
package com.graphhopper.routing;

import com.graphhopper.routing.util.*;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.Helper;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Karich
 */
public class DijkstraBidirectionRefTest extends AbstractRoutingAlgorithmTester
{
    @Override
    public AlgorithmPreparation prepareGraph( Graph g, final FlagEncoder encoder, final WeightCalculation calc)
    {
        return new NoOpAlgorithmPreparation()
        {
            @Override
            public RoutingAlgorithm createAlgo()
            {
                return new DijkstraBidirectionRef(_graph, encoder, calc);
            }
        }.setGraph(g);
    }

    @Test
    public void testCannotCalculateSP2()
    {
        Graph g = createGraph();
        DijkstraBidirectionRef db = new DijkstraBidirectionRef(g, carEncoder, new ShortestCalc());
        Path p = db.calcPath(0, 2);
        assertFalse(p.isFound());
    }
}
