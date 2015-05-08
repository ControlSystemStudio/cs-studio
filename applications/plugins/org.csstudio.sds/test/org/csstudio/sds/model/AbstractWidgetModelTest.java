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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.internal.model.test.WidgetModelTestHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for class {@link AbstractWidgetModel}.
 *
 * @author Alexander Will
 * @version $Revision: 1.6 $
 *
 */
public final class AbstractWidgetModelTest {
    /**
     * The widget model instance under test.
     */
    private AbstractWidgetModel _testWidgetModel;

    /**
     * Set up the test case.
     *
     * @throws java.lang.Exception
     *             If an execption occurs during setup.
     */
    @Before
    public void setUp() throws Exception {
        _testWidgetModel = WidgetModelTestHelper.createWidgetModel();
    }

    /**
     * Test method for the eventing of diplay widget models.
     */
    @Test
    public void testEventing() {
        // test property value changes.
        int oldHeight = _testWidgetModel.getHeight();
        int oldWidth = _testWidgetModel.getWidth();

        _testWidgetModel.setHeight(oldHeight + 1);
        assertEquals(oldHeight + 1, _testWidgetModel.getHeight());

        _testWidgetModel.setWidth(oldWidth - 1);
        assertEquals(oldWidth - 1, _testWidgetModel.getWidth());
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.model.AbstractWidgetModel#getHeight()}.
     */
    @Test
    public void testGetHeight() {
        int oldValue = _testWidgetModel.getHeight();

        _testWidgetModel.setHeight(100);
        assertEquals(100, _testWidgetModel.getHeight());

        _testWidgetModel.setHeight(oldValue);
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.model.AbstractWidgetModel#hasProperty(java.lang.String)}.
     */
    @Test
    public void testHasProperty() {
        assertTrue(_testWidgetModel
                .hasProperty(AbstractWidgetModel.PROP_HEIGHT));
        assertTrue(_testWidgetModel.hasProperty(AbstractWidgetModel.PROP_WIDTH));
        assertTrue(_testWidgetModel.hasProperty(AbstractWidgetModel.PROP_POS_X));
        assertTrue(_testWidgetModel.hasProperty(AbstractWidgetModel.PROP_POS_Y));
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.model.AbstractWidgetModel#getProperty(java.lang.String)}.
     */
    @Test
    public void testGetProperty() {
        assertNotNull(_testWidgetModel
                .getIntegerProperty(AbstractWidgetModel.PROP_HEIGHT));
        assertNotNull(_testWidgetModel
                .getIntegerProperty(AbstractWidgetModel.PROP_WIDTH));
        assertNotNull(_testWidgetModel
                .getIntegerProperty(AbstractWidgetModel.PROP_POS_X));
        assertNotNull(_testWidgetModel
                .getIntegerProperty(AbstractWidgetModel.PROP_POS_Y));
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.model.AbstractWidgetModel#getWidth()}.
     */
    @Test
    public void testGetWidth() {
        int oldValue = _testWidgetModel.getWidth();

        _testWidgetModel.setWidth(100);
        assertEquals(100, _testWidgetModel.getWidth());

        _testWidgetModel.setWidth(oldValue);
    }

    /**
     * Test method for {@link org.csstudio.sds.model.AbstractWidgetModel#getX()}.
     */
    @Test
    public void testGetX() {
        int oldValue = _testWidgetModel.getX();

        _testWidgetModel.setX(100);
        assertEquals(100, _testWidgetModel.getX());

        _testWidgetModel.setX(oldValue);
    }

    /**
     * Test method for {@link org.csstudio.sds.model.AbstractWidgetModel#getY()}.
     */
    @Test
    public void testGetY() {
        int oldValue = _testWidgetModel.getY();

        _testWidgetModel.setY(100);
        assertEquals(100, _testWidgetModel.getY());

        _testWidgetModel.setY(oldValue);
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.model.AbstractWidgetModel#setLocation(int, int)}.
     */
    @Test
    public void testSetLocation() {
        int oldX = _testWidgetModel.getX();
        int oldY = _testWidgetModel.getY();

        _testWidgetModel.setLocation(700, 600);

        assertEquals(700, _testWidgetModel.getX());
        assertEquals(600, _testWidgetModel.getY());

        _testWidgetModel.setLocation(oldX, oldY);
    }

    @Test
    public void testAddColorProperty() {
    }

}
