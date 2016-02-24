/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.perspectives;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.osgi.service.datalocation.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.service.event.Event;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveSaverUnitTest
{
    @Mock
    private Location instanceLocation;

    @Mock
    private IEventBroker mockBroker;

    @Mock
    private EModelService mockModelService;

    @Mock
    private PerspectiveUtils perspectiveUtils;

    @InjectMocks
    private PerspectiveSaver saver;
    
    @Before
    public void setUp() {
        try {
            when(instanceLocation.getDataArea(any(String.class))).thenReturn(new URL("file://dummy"));
            saver.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createPerspectiveSaver()
    {
        System.out.println("Saver " + saver);
    }

    @Test
    public void handleEventIgnoresEventsWherePropertyIsNotPresent() {
        Event testEvent = createTestEvent("hello", "world");
        saver.handleEvent(testEvent);
        verify(mockModelService, never()).findElements(any(MUIElement.class), any(String.class), any(Class.class), any(List.class));
    }

    @Test
    public void handleEventIgnoresEventsWherePropertiesAreNotMPerspectives() {
        Event testEvent = createTestEvent(UIEvents.EventTags.ELEMENT, new Object());
        saver.handleEvent(testEvent);
        verify(mockModelService, never()).findElements(any(MUIElement.class), any(String.class), any(Class.class), any(List.class));
    }

    @Test
    public void handleEventHandlesEventsWherePropertiesAreMPerspectives() {
        MPerspective mockPerspective = mock(MPerspective.class);
        when(mockPerspective.getLabel()).thenReturn("dummy");
        Event testEvent = createTestEvent(UIEvents.EventTags.ELEMENT, mockPerspective);
        saver.handleEvent(testEvent);
        verify(mockModelService, never()).findElements(any(MUIElement.class), any(String.class), any(Class.class), any(List.class));
    }

    public Event createTestEvent(String key, Object value) {
        Dictionary props = new Hashtable<String, Object>();
        props.put(key, value);
        Event e = new Event("mytopic", props);
        return e;
        
    }

}
