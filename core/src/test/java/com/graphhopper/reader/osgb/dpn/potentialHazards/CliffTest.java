package com.graphhopper.reader.osgb.dpn.potentialHazards;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.graphhopper.reader.Way;
import com.graphhopper.reader.osgb.OsToOsmAttributeMappingVisitor;
import com.graphhopper.reader.osgb.dpn.potentialhazards.Cliff;

public class CliffTest {
    static OsToOsmAttributeMappingVisitor visitor;
    @Mock
    Way way;

    @BeforeClass
    public static void createVisitor() {
        visitor = new Cliff();
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testVisitWayAttribute() throws Exception {
        visitor.visitWayAttribute("cliff", way);
        verify(way).getTag("natural");
        verify(way).setTag("natural", "cliff");
        verifyNoMoreInteractions(way);
    }
}
