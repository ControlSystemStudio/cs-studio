/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.perspectives;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MSnippetContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.osgi.service.datalocation.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.osgi.service.event.Event;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveSaverUnitTest {

    @Mock
    private Location instanceLocation;

    @Mock
    private IEclipsePreferences preferences;

    @Mock
    private IPreferencesService prefsService;

    @Mock
    private IEventBroker mockBroker;

    @Mock
    private EModelService mockModelService;

    @Mock
    private PerspectiveUtils perspectiveUtils;

    @Spy
    private IFileUtils fileUtils = new FileUtils();

    @InjectMocks
    private PerspectiveSaver saver;

    private String workspaceLocation = "/dummy";

    @Before
    public void setUp() {
        try {
            URL workspaceUrl = new URL("file:" + workspaceLocation);
            when(instanceLocation.getDataArea(anyString())).thenReturn(workspaceUrl);
            doNothing().when(preferences).put(anyString(), anyString());
            when(prefsService.getString(anyString(), anyString(), anyString(), any(IScopeContext[].class))).thenReturn("dummy");
            // Return first argument if cloneElement() is called on mockModelService.
            when(mockModelService.cloneElement(any(MUIElement.class), any(MSnippetContainer.class))).thenAnswer(new Answer<MUIElement>() {
                @Override
                public MUIElement answer(InvocationOnMock invocation) {
                    Object[] args = invocation.getArguments();
                    return (MUIElement) args[0];
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * The handleEvent tests check whether modelService.findElements() is called as a
     * proxy for whether the private method saver.savePerspectiveToDirectory() is called.
     */
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
    public void handleEventIgnoresEventsIfNoSaveDirPreferenceIsSet() {
        // Return null from preference query.
        when(prefsService.getString(anyString(), anyString(), anyString(), any(IScopeContext[].class))).thenReturn(null);
        Event testEvent = createTestEvent(UIEvents.EventTags.ELEMENT, new Object());
        saver.handleEvent(testEvent);
        verify(mockModelService, never()).findElements(any(MUIElement.class), any(String.class), any(Class.class), any(List.class));
    }

    @Test
    public void handleEventHandlesEventsWherePropertiesAreMPerspectives() throws IOException {
        MPerspective mockPerspective = mock(MPerspective.class);
        when(mockPerspective.getLabel()).thenReturn("dummy");
        Event testEvent = createTestEvent(UIEvents.EventTags.ELEMENT, mockPerspective);
        saver.handleEvent(testEvent);
        verify(mockModelService).findElements(mockPerspective, null, MPlaceholder.class, null);
        verify(perspectiveUtils).savePerspective(eq(mockPerspective), any(URI.class));
    }

    /**
     * The Event object can't be mocked because its methods are final.
     * @param key
     * @param value
     * @return dummy event object
     */
    public Event createTestEvent(String key, Object value) {
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(key, value);
        Event e = new Event("mytopic", props);
        return e;
    }

    @Test
    public void constructUriCreatesSensibleFilename() throws MalformedURLException {
        String perspectiveName = "dummy";
        String sensibleFilename = workspaceLocation + "/perspective_" + perspectiveName + ".xmi";
        URI uri = URI.createFileURI(sensibleFilename);
        URI u =  saver.constructUri(Paths.get(workspaceLocation), perspectiveName);
        assertEquals(u,  uri);
    }

    @Test(expected=NullPointerException.class)
    public void constructUriThrowsNullPointerExceptionIfFilenameIsNull() {
        String perspectiveName = "dummy";
        saver.constructUri(null, perspectiveName);
    }

    @Test(expected=NullPointerException.class)
    public void constructUriThrowsNullPointerExceptionIfPerspectiveNameIsNull() throws MalformedURLException {
        Path dummy = Paths.get("file:///dummy");
        saver.constructUri(dummy, null);
    }

}
