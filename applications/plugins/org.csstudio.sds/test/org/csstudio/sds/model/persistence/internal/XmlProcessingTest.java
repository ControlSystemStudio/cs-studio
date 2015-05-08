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
package org.csstudio.sds.model.persistence.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.internal.persistence.DisplayModelInputStream;
import org.csstudio.sds.internal.persistence.DisplayModelReader;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.dal.DynamicValueState;
import org.junit.Test;

/**
 * Test case for the SDS XML processing.
 *
 * @author Alexander Will
 * @version $Revision: 1.15 $
 *
 */
public final class XmlProcessingTest {
    /**
     * Sample valid XML content for testing. Border properties are not set (the
     * reader is expected to set default values if none are specified in the XML
     * content).
     */
    private static final String TEST_XML_CONTENT_VALID = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //$NON-NLS-1$
            + "<display modelVersion=\"1.0\">\n" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"height\" value=\"400\" />" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"width\" value=\"500\" />" //$NON-NLS-1$
            + "<property type=\"sds.color\" id=\"color.background\">" //$NON-NLS-1$
            + "<color red=\"128\" green=\"0\" blue=\"255\" />" //$NON-NLS-1$

            + "<dynamicsDescriptor ruleId=\"simpleColor\">" //$NON-NLS-1$
            + "<inputChannel name=\"inp1\" type=\"java.lang.Double\" />" //$NON-NLS-1$
            + "<connectionState state=\"DISCONNECTED\">" //$NON-NLS-1$
            + "<color red=\"255\" green=\"0\" blue=\"0\" />" //$NON-NLS-1$
            + "</connectionState>" //$NON-NLS-1$
            + "<connectionState state=\"CONNECTED\">" //$NON-NLS-1$
            + "<color red=\"0\" green=\"255\" blue=\"0\" />" //$NON-NLS-1$
            + "</connectionState>" //$NON-NLS-1$
            + "<dynamicValueState state=\"NORMAL\">" //$NON-NLS-1$
            + "<color red=\"255\" green=\"255\" blue=\"255\" />" //$NON-NLS-1$
            + "</dynamicValueState>" //$NON-NLS-1$
            + "<dynamicValueState state=\"ERROR\">" //$NON-NLS-1$
            + "<color red=\"255\" green=\"0\" blue=\"0\" />" //$NON-NLS-1$
            + "</dynamicValueState>" //$NON-NLS-1$
            + "</dynamicsDescriptor>" //$NON-NLS-1$

            + "</property>" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"position.y\" value=\"100\" />" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"position.x\" value=\"0\" />" //$NON-NLS-1$
            + "<widget type=\"org.csstudio.sds.components.Rectangle\">\n" //$NON-NLS-1$
            + "<property type=\"sds.double\" id=\"fill\" value=\"100.0\">\n" //$NON-NLS-1$
            + "<dynamicsDescriptor ruleId=\"ruleId\">\n" //$NON-NLS-1$
            + "<inputChannel name=\"PV1\" type=\"java.lang.Double\"/>\n" //$NON-NLS-1$
            + "<outputChannel name=\"PV2\" type=\"java.lang.Integer\"/>\n" //$NON-NLS-1$
            + "</dynamicsDescriptor>\n" //$NON-NLS-1$
            + "</property>\n" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"position.x\" value=\"135\"/>\n" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"height\" value=\"114\"/>\n" //$NON-NLS-1$
            + "<property type=\"sds.color\" id=\"color.foreground\">\n" //$NON-NLS-1$
            + "<color red=\"128\" green=\"0\" blue=\"255\" />\n" //$NON-NLS-1$
            + "</property>\n" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"position.y\" value=\"15\"/>\n" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"width\" value=\"290\"/>\n" //$NON-NLS-1$
            + "<property type=\"sds.color\" id=\"color.background\">\n" //$NON-NLS-1$
            + "<color red=\"100\" green=\"100\" blue=\"100\" />\n" //$NON-NLS-1$
            + "</property>\n" //$NON-NLS-1$
            + "</widget>\n" //$NON-NLS-1$
            + "<widget type=\"org.csstudio.sds.components.Waveform\">" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"border.style\" value=\"1\" />" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"position.x\" value=\"61\" />" //$NON-NLS-1$
            + "<property type=\"sds.boolean\" id=\"visibility\" value=\"false\" />" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"width\" value=\"322\" />" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"height\" value=\"142\" />" //$NON-NLS-1$
            + "<property type=\"sds.color\" id=\"border.color\">" //$NON-NLS-1$
            + "<color red=\"100\" green=\"0\" blue=\"0\" />" //$NON-NLS-1$
            + "<dynamicsDescriptor ruleId=\"simpleColor\">" //$NON-NLS-1$
            + "<inputChannel name=\"inp1\" type=\"java.lang.Double\" />" //$NON-NLS-1$
            + "<connectionState state=\"DISCONNECTED\">" //$NON-NLS-1$
            + "<color red=\"255\" green=\"0\" blue=\"0\" />" //$NON-NLS-1$
            + "</connectionState>" //$NON-NLS-1$
            + "<connectionState state=\"CONNECTED\">" //$NON-NLS-1$
            + "<color red=\"0\" green=\"255\" blue=\"0\" />" //$NON-NLS-1$
            + "</connectionState>" //$NON-NLS-1$
            + "<dynamicValueState state=\"NORMAL\">" //$NON-NLS-1$
            + "<color red=\"255\" green=\"255\" blue=\"255\" />" //$NON-NLS-1$
            + "</dynamicValueState>" //$NON-NLS-1$
            + "<dynamicValueState state=\"ERROR\">" //$NON-NLS-1$
            + "<color red=\"255\" green=\"0\" blue=\"0\" />" //$NON-NLS-1$
            + "</dynamicValueState>" //$NON-NLS-1$
            + "</dynamicsDescriptor>" //$NON-NLS-1$
            + "</property>" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"position.y\" value=\"112\" />" //$NON-NLS-1$
            + "<property type=\"sds.color\" id=\"color.foreground\">" //$NON-NLS-1$
            + "<color red=\"200\" green=\"100\" blue=\"100\" />" //$NON-NLS-1$
            + "</property>" //$NON-NLS-1$
            + "<property type=\"sds.color\" id=\"color.background\">" //$NON-NLS-1$
            + "<color red=\"100\" green=\"100\" blue=\"100\" />" //$NON-NLS-1$
            + "</property>" //$NON-NLS-1$
            + "<property type=\"sds.doublearray\" id=\"wave\">" //$NON-NLS-1$
            + "<doubleArray>" //$NON-NLS-1$
            + "<double value=\"20.0\" />" //$NON-NLS-1$
            + "<double value=\"15.0\" />" //$NON-NLS-1$
            + "<double value=\"33.0\" />" //$NON-NLS-1$
            + "<double value=\"44.0\" />" //$NON-NLS-1$
            + "<double value=\"22.0\" />" //$NON-NLS-1$
            + "<double value=\"3.0\" />" //$NON-NLS-1$
            + "<double value=\"25.0\" />" //$NON-NLS-1$
            + "<double value=\"4.0\" />" //$NON-NLS-1$
            + "</doubleArray>" //$NON-NLS-1$
            + "<dynamicsDescriptor ruleId=\"directConnection\">" //$NON-NLS-1$
            + "<inputChannel name=\"inp1\" type=\"java.lang.Double\" />" //$NON-NLS-1$
            + "</dynamicsDescriptor>" //$NON-NLS-1$
            + "</property>" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"border.width\" value=\"0\" />" //$NON-NLS-1$
            + "</widget>\n</display>\n"; //$NON-NLS-1$

    /**
     * Sample invalid XML content for testing.
     * <ul>
     * <li>The data value of property <code>PROP_POS_Y</code> is no valid.
     * <li>The property type <code>color.background2</code> is undefined.
     * <li>Contains the unknown widget model type <code>fantasy</code>.
     * </ul>
     */
    private static final String TEST_XML_CONTENT_INVALID = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //$NON-NLS-1$
            + "<display modelVersion=\"1.0\">\n" //$NON-NLS-1$
            + "<widget type=\"org.csstudio.sds.components.Rectangle\">\n" //$NON-NLS-1$
            + "<property type=\"sds.double\" id=\"fill\" value=\"100.0\"/>\n" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"position.x\" value=\"135\"/>\n" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"height\" value=\"114\"/>\n" //$NON-NLS-1$
            + "<property type=\"sds.color\" id=\"color.foreground\">\n" //$NON-NLS-1$
            + "<color red=\"128\" green=\"0\" blue=\"255\" />\n" //$NON-NLS-1$
            + "</property>\n" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"positin.y\" value=\"ABC\"/>\n" //$NON-NLS-1$
            + "<property type=\"sds.integer\" id=\"width\" value=\"290\"/>\n" //$NON-NLS-1$
            + "<property type=\"sds.color\" id=\"color.background2\">\n" //$NON-NLS-1$
            + "<color red=\"100\" green=\"100\" blue=\"100\" />\n" //$NON-NLS-1$
            + "</property>\n" //$NON-NLS-1$
            + "</widget>\n" //$NON-NLS-1$
            + "<widget type=\"fantasy\"/>\n" //$NON-NLS-1$
            + "</display>\n"; //$NON-NLS-1$

    /**
     * Sample malformed XML content for testing.
     */
    private static final String TEST_XML_CONTENT_MALFORMED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //$NON-NLS-1$
            + "<display displayVersion=\"1.0\">\n" //$NON-NLS-1$
            + "<display>\n"; //$NON-NLS-1$

    /**
     * Test the basic persistence handling.
     */
    @Test
    public void testBasicHandling() {
        InputStream is = new ByteArrayInputStream(TEST_XML_CONTENT_VALID
                .getBytes());
        assertNotNull(is);

        DisplayModel displayModel = new DisplayModel();

        DisplayModelReader reader = new DisplayModelReader();
        reader.readModelFromXml(is, displayModel,
                null);

        assertNotNull(displayModel);
        assertFalse(reader.isErrorOccurred());

        assertEquals(400, displayModel
                .getIntegerProperty(AbstractWidgetModel.PROP_HEIGHT));
        assertEquals(500, displayModel
                .getIntegerProperty(AbstractWidgetModel.PROP_WIDTH));
        assertEquals(100, displayModel
                .getIntegerProperty(AbstractWidgetModel.PROP_POS_Y));
        assertEquals(0, displayModel
                .getIntegerProperty(AbstractWidgetModel.PROP_POS_X));
        assertTrue("#8000FF".equalsIgnoreCase(displayModel
                .getColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND)));

        assertEquals(2, displayModel.getWidgets().size());

        AbstractWidgetModel widgetModel = displayModel.getWidgets().get(0);
        assertEquals(
                "org.csstudio.sds.components.Rectangle", widgetModel.getTypeID()); //$NON-NLS-1$

        assertTrue(widgetModel.hasProperty("fill")); //$NON-NLS-1$
        assertTrue(widgetModel.hasProperty("orientation")); //$NON-NLS-1$
        assertTrue(widgetModel.hasProperty("transparency")); //$NON-NLS-1$
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_POS_X));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_POS_Y));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_HEIGHT));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_WIDTH));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_LAYER));
        assertTrue(widgetModel
                .hasProperty(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
        assertTrue(widgetModel
                .hasProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_VISIBILITY));
        assertTrue(widgetModel
                .hasProperty(AbstractWidgetModel.PROP_BORDER_COLOR));
        assertTrue(widgetModel
                .hasProperty(AbstractWidgetModel.PROP_BORDER_STYLE));
        assertTrue(widgetModel
                .hasProperty(AbstractWidgetModel.PROP_BORDER_WIDTH));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_ACTIONDATA));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_ENABLED));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_ALIASES));
        assertTrue(widgetModel
                .hasProperty(AbstractWidgetModel.PROP_PERMISSSION_ID));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_NAME));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_PRIMARY_PV));
        assertTrue(widgetModel.hasProperty(AbstractWidgetModel.PROP_TOOLTIP));

        assertEquals(135, widgetModel.getIntegerProperty(
                AbstractWidgetModel.PROP_POS_X));
        assertEquals(15, widgetModel
                .getIntegerProperty(AbstractWidgetModel.PROP_POS_Y));
        assertEquals(290, widgetModel.getIntegerProperty(
                AbstractWidgetModel.PROP_WIDTH));
        assertEquals(114, widgetModel.getIntegerProperty(
                AbstractWidgetModel.PROP_HEIGHT));

        Object colorForegroundObj = widgetModel.getPropertyInternal(
                AbstractWidgetModel.PROP_COLOR_FOREGROUND).getPropertyValue();
        assertTrue(colorForegroundObj instanceof String);

        String colorForeground = (String) colorForegroundObj;
        assertTrue("#8000FF".equalsIgnoreCase(colorForeground));

        Object colorBackgroundObj = widgetModel.getPropertyInternal(
                AbstractWidgetModel.PROP_COLOR_BACKGROUND).getPropertyValue();
        assertTrue(colorBackgroundObj instanceof String);
        String colorBackground = (String) colorBackgroundObj;
        assertTrue("#646464".equalsIgnoreCase(colorBackground));

        WidgetProperty fillProperty = widgetModel.getPropertyInternal("fill"); //$NON-NLS-1$
        DynamicsDescriptor dynamicsDescriptor = fillProperty
                .getDynamicsDescriptor();

        assertNotNull(dynamicsDescriptor);
        assertEquals("ruleId", dynamicsDescriptor.getRuleId()); //$NON-NLS-1$

        assertEquals(1, dynamicsDescriptor.getInputChannels().length);
        assertEquals("PV1", dynamicsDescriptor.getInputChannels()[0] //$NON-NLS-1$
                .getChannel());

        assertNotNull(dynamicsDescriptor.getOutputChannel());
        assertEquals("PV2", dynamicsDescriptor.getOutputChannel().getChannel()); //$NON-NLS-1$
    }

    /**
     * Test the persistence handling of a comples widget.
     */
    @Test
    public void testComplexElementHandling() {
        InputStream is = new ByteArrayInputStream(TEST_XML_CONTENT_VALID
                .getBytes());
        assertNotNull(is);

        DisplayModel displayModel = new DisplayModel();

        DisplayModelReader reader = new DisplayModelReader();
        reader.readModelFromXml(is, displayModel,
                null);

        assertNotNull(displayModel);
        reader.isErrorOccurred();
        assertEquals(2, displayModel.getWidgets().size());

        // test the connection state & dynamic value state definitions on
        // display model level
        assertTrue(displayModel.hasProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND));
        assertNotNull(displayModel.getColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND));

        DynamicsDescriptor backgroundColorDynamicsDescriptor = displayModel.getDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_BACKGROUND);
        assertNotNull(backgroundColorDynamicsDescriptor);

        testConnectionStates(backgroundColorDynamicsDescriptor
                .getConnectionStateDependentPropertyValues());

        testDynamicValueStates(backgroundColorDynamicsDescriptor
                .getConditionStateDependentPropertyValues());

        AbstractWidgetModel widgetModel = displayModel.getWidgets().get(1);

        // test a widget model with complex property types
        assertEquals(
                "org.csstudio.sds.components.Waveform", widgetModel.getTypeID()); //$NON-NLS-1$
        assertTrue(widgetModel.hasProperty("data1")); //$NON-NLS-1$

        WidgetProperty waveProperty = widgetModel.getPropertyInternal("data1"); //$NON-NLS-1$
        assertNotNull(waveProperty);

        Object value = waveProperty.getPropertyValue();
        assertNotNull(value);
        assertTrue(value instanceof double[]);

        double[] doubleArray = (double[]) value;

        assertEquals(0, doubleArray.length);

        // test the connection state & dynamic value state definitions on widget
        // level
        assertTrue(widgetModel.hasProperty("border.color")); //$NON-NLS-1$
        assertNotNull(widgetModel.getColor(AbstractWidgetModel.PROP_BORDER_COLOR));

        DynamicsDescriptor borderColorDynamicsDescriptor = widgetModel.getDynamicsDescriptor(AbstractWidgetModel.PROP_BORDER_COLOR);
        assertNotNull(borderColorDynamicsDescriptor);

        testConnectionStates(borderColorDynamicsDescriptor
                .getConnectionStateDependentPropertyValues());

        testDynamicValueStates(borderColorDynamicsDescriptor
                .getConditionStateDependentPropertyValues());
    }

    /**
     * Test the persistence handling of invalid documents.
     */
    @Test
    public void testInvalidDocumentHandling() {
        DisplayModel displayModel = new DisplayModel();

        // now try to read an invalid XML document
        InputStream generatedInputStream = new DisplayModelInputStream(
                displayModel);

        assertNotNull(generatedInputStream);

        InputStream is = new ByteArrayInputStream(TEST_XML_CONTENT_INVALID
                .getBytes());

        // this causes an exception because of the invalid test data
        DisplayModel otherModel = new DisplayModel();

        DisplayModelReader reader = new DisplayModelReader();
        reader.readModelFromXml(is, otherModel, null);

        assertTrue(reader.isErrorOccurred());

        // now try the malformed XML document
        is = new ByteArrayInputStream(TEST_XML_CONTENT_MALFORMED.getBytes());
        otherModel = new DisplayModel();

        reader.readModelFromXml(is, otherModel, null);

        assertTrue(reader.isErrorOccurred());
    }

    /**
     * Test the given connection states map.
     *
     * @param connectionStatesMap
     *            map holding the test contents.
     */
    protected void testConnectionStates(
            Map<ConnectionState, Object> connectionStatesMap) {
        assertNotNull(connectionStatesMap);
        assertEquals(2, connectionStatesMap.values().size());
        assertTrue(connectionStatesMap.containsKey(ConnectionState.CONNECTED));
        assertTrue(connectionStatesMap
                .containsKey(ConnectionState.DISCONNECTED));

        Object connectedColorObject = connectionStatesMap
                .get(ConnectionState.CONNECTED);
        Object disconnectedColorObject = connectionStatesMap
                .get(ConnectionState.DISCONNECTED);

        assertTrue(connectedColorObject instanceof String);
        assertTrue(disconnectedColorObject instanceof String);

        String connectedColor = (String) connectedColorObject;
        String disconnectedColor = (String) disconnectedColorObject;

        assertEquals("#00ff00", connectedColor);
        assertEquals("#ff0000", disconnectedColor);
    }

    /**
     * Test the given dynamic value states map.
     *
     * @param dynamicValueStatesMap
     *            map holding the test contents.
     */
    protected void testDynamicValueStates(
            Map<DynamicValueState, Object> dynamicValueStatesMap) {
        assertNotNull(dynamicValueStatesMap);
        assertEquals(2, dynamicValueStatesMap.values().size());
        assertTrue(dynamicValueStatesMap.containsKey(DynamicValueState.NORMAL));
        assertTrue(dynamicValueStatesMap.containsKey(DynamicValueState.ERROR));

        Object normalColorObject = dynamicValueStatesMap
                .get(DynamicValueState.NORMAL);
        Object errorColorObject = dynamicValueStatesMap
                .get(DynamicValueState.ERROR);

        assertTrue(normalColorObject instanceof String);
        assertTrue(errorColorObject instanceof String);

        String normalColor = (String) normalColorObject;
        String errorColor = (String) errorColorObject;

        assertTrue("#ffffff".equalsIgnoreCase(normalColor));
        assertTrue("#ff0000".equalsIgnoreCase(errorColor));
    }
}
