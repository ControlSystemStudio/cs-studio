/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.model;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.csstudio.sds.internal.model.test.WidgetModelTestHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for class {@link DisplayModel}.
 *
 * @author Alexander Will
 * @version $Revision: 1.1 $
 *
 */
public final class DisplayModelTest {
    /**
     * Listener to be aware of model events.
     */
    private PropertyChangeListener _listener;

    /**
     * The last tracked event.
     */
    private PropertyChangeEvent _lastEvent;

    /**
     * Widget model for testing.
     */
    private AbstractWidgetModel _widgetModel1;

    /**
     * Widget model for testing.
     */
    private AbstractWidgetModel _widgetModel2;

    /**
     * Widget model for testing.
     */
    private AbstractWidgetModel _widgetModel3;

    /**
     * Display model instance that will be tested.
     */
    private DisplayModel _testModel;

    /**
     * Set up the test case.
     *
     * @throws java.lang.Exception
     *             If an execption occurs during setup.
     */
    @Before
    public void setUp() throws Exception {
        _listener = new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                _lastEvent = evt;
            }
        };

        _widgetModel1 = WidgetModelTestHelper.createWidgetModel();
        _widgetModel2 = WidgetModelTestHelper.createWidgetModel();
        _widgetModel3 = WidgetModelTestHelper.createWidgetModel();

        _testModel = new DisplayModel();

        _testModel.addWidget(_widgetModel1);
        _testModel.addWidget(_widgetModel2);
        _testModel.addWidget(_widgetModel3);

        _testModel.addPropertyChangeListener(_listener);
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.model.DisplayModel#getWidgets()}.
     */
    @Test
    public void testGetModelElements() {
        List<AbstractWidgetModel> models = _testModel.getWidgets();

        assertEquals(3, models.size());
        assertEquals(_widgetModel1, models.get(0));
        assertEquals(_widgetModel2, models.get(1));
        assertEquals(_widgetModel3, models.get(2));
    }

    /**
     * Test method for the display model event handling.
     */
    @Test
    public void testEventing() {
        _testModel.removeWidget(_widgetModel3);
        assertEquals(_testModel, _lastEvent.getSource());
        assertEquals(DisplayModel.PROP_CHILD_REMOVED, _lastEvent
                .getPropertyName());
        assertEquals(null, _lastEvent.getNewValue());
        assertEquals(_widgetModel3, _lastEvent.getOldValue());

        List<AbstractWidgetModel> models = _testModel.getWidgets();

        assertEquals(2, models.size());
        assertEquals(_widgetModel1, models.get(0));
        assertEquals(_widgetModel2, models.get(1));

        _testModel.addWidget(_widgetModel3);
        assertEquals(_testModel, _lastEvent.getSource());
        assertEquals(DisplayModel.PROP_CHILD_ADDED, _lastEvent
                .getPropertyName());
        assertEquals(null, _lastEvent.getOldValue());
        assertEquals(_widgetModel3, _lastEvent.getNewValue());

        models = _testModel.getWidgets();

        assertEquals(3, models.size());
        assertEquals(_widgetModel1, models.get(0));
        assertEquals(_widgetModel2, models.get(1));
        assertEquals(_widgetModel3, models.get(2));

        // now remove the listener
        _testModel.removePropertyChangeListener(_listener);

        // the event should remain the old one since there is no listener!
        _testModel.removeWidget(_widgetModel3);
        assertEquals(_testModel, _lastEvent.getSource());
        assertEquals(DisplayModel.PROP_CHILD_ADDED, _lastEvent
                .getPropertyName());
        assertEquals(null, _lastEvent.getOldValue());
        assertEquals(_widgetModel3, _lastEvent.getNewValue());

        // restore the state
        _testModel.addWidget(_widgetModel3);
        _testModel.addPropertyChangeListener(_listener);
    }

    /**
     * Test adding elements with indices).
     */
    @Test
    public void testAddElement() {
        int elementCount = _testModel.getWidgets().size();

        AbstractWidgetModel newFirstModel = WidgetModelTestHelper.createWidgetModel();
        int index = 0;
        _testModel.addWidget(index, newFirstModel);
        assertEquals(elementCount+1, _testModel.getWidgets().size());
        assertEquals(newFirstModel, _testModel.getWidgets().get(index));

        AbstractWidgetModel newLastModel = WidgetModelTestHelper.createWidgetModel();
        index = _testModel.getWidgets().size();
        _testModel.addWidget(index, newLastModel);
        assertEquals(elementCount+2, _testModel.getWidgets().size());
        assertEquals(newLastModel, _testModel.getWidgets().get(index));
    }

}
