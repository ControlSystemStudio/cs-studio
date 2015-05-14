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
package org.csstudio.sds.internal.persistence;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.internal.model.Layer;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.dal.DynamicValueState;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * <code>InputStream</code> that provides "on the fly" access to the XML
 * representation of <code>DisplayModels</code>.
 *
 * @author Alexander Will
 * @version $Revision: 1.3 $
 *
 */
public final class DisplayModelInputStream extends ByteArrayInputStream {

    /**
     * The outputter that is used for XML generation.
     */
    private XMLOutputter _xmlOutputter;

    /**
     * The XML document header.
     */
    private static String _xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"; //$NON-NLS-1$

    /**
     * The XML document footer.
     */
    private static final String XML_FOOTER = "\n</display>"; //$NON-NLS-1$

    /**
     * The <code>DisplayModel</code>.
     */
    private DisplayModel _displayModel;

    /**
     * The widget models of the <code>DisplayModel</code>.
     */
    private List<AbstractWidgetModel> _widgetModels;

    /**
     * The index of the widget model that currently fills the internal buffer.
     */
    private int _elementIndex;

    /**
     * Flag that indicates whether the model properties have been processed.
     */
    private boolean _finishedModelProperties = false;

    /**
     * Standard constructor.
     *
     * @param displayModel
     *            The <code>DisplayModel</code> that should be accessed.
     */
    public DisplayModelInputStream(final DisplayModel displayModel) {
        super(_xmlHeader.getBytes());

        _xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        _displayModel = displayModel;
        _widgetModels = _displayModel.getWidgets();
        _elementIndex = 0;

        _finishedModelProperties = false;
    }

    /**
     * Reset the underlying buffer to the given array of bytes.
     *
     * @param buffer
     *            The new state of the internal buffer.
     */
    private void resetBuffer(final byte[] buffer) {
        this.mark = 0;
        this.pos = 0;
        this.buf = buffer;
        this.count = buf.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int read() {
        int result = super.read();

        if (available() == 0) {
            handleNextElement();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int read(final byte[] b, final int off, final int len) {
        int result = super.read(b, off, len);

        if (available() == 0) {
            handleNextElement();
        }

        return result;
    }

    /**
     * Handle the next element by shifting it into the underlying buffer.
     */
    private void handleNextElement() {
        if (!_finishedModelProperties) {
            resetBuffer(writeModelElementToByteArr(_displayModel));
            _finishedModelProperties = true;
        } else {
            if (_elementIndex < _widgetModels.size()) {
                AbstractWidgetModel widgetModel = _widgetModels
                        .get(_elementIndex);
                resetBuffer(writeWidgetToByteArr(widgetModel));

                _elementIndex++;
            } else if (_elementIndex == _widgetModels.size()) {
                resetBuffer(XML_FOOTER.getBytes());
                _elementIndex++;
            }
        }
    }

    /**
     * Return the XML representation of the given widget model as an array of
     * bytes.
     *
     * @param modelElement
     *            A widget model.
     * @return The XML representation of the given widget model as an array of
     *         bytes.
     */
    public byte[] writeWidgetToByteArr(final AbstractWidgetModel modelElement) {
        return writeWidgetToString(modelElement).getBytes();
    }

    /**
     * Return the XML representation of the given widget model as a String.
     *
     * @param modelElement
     *            A widget model.
     * @return The XML representation of the given widget model as a String.
     */
    private String writeWidgetToString(final AbstractWidgetModel modelElement) {
        Element domModelElement = createModelTag(modelElement);
        String outputString = _xmlOutputter.outputString(domModelElement)
                + "\n"; //$NON-NLS-1$
        return outputString;
    }

    /**
     * Return the XML representation of the given display model as an array of
     * bytes.
     *
     * @param displayModel
     *            The display model.
     * @return The XML representation of the given display model as an array of
     *         bytes.
     */
    public byte[] writeModelElementToByteArr(final DisplayModel displayModel) {
        return writeModelElementToString(displayModel).getBytes();
    }

    /**
     * Return the XML representation of the given display model as an array of
     * bytes.
     *
     * @param displayModel
     *            The display model.
     * @return The XML representation of the given display model as an array of
     *         bytes.
     */
    private String writeModelElementToString(final DisplayModel displayModel) {
        Element domDisplayElement = createDisplayTag(displayModel);
        String outputString = _xmlOutputter.outputString(domDisplayElement)
                .replace("</" + XmlConstants.XML_ELEMENT_DISPLAY + ">", "");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return outputString;
    }

    /**
     * Create the XML representation of the given display model.
     *
     * @param displayModel
     *            The display model.
     * @return The XML representation of the given display model.
     */
    private Element createDisplayTag(final DisplayModel displayModel) {
        Element result = new Element(XmlConstants.XML_ELEMENT_DISPLAY);
        result.setAttribute(XmlConstants.XML_ATTRIBUTE_MODEL_VERSION, "1.1"); //$NON-NLS-1$

        for (WidgetProperty property : displayModel.getProperties()) {
            result.addContent(createPropertyTag(property));
        }

        for (Layer layer : displayModel.getLayerSupport().getLayers()) {
            Element layerTag = createLayerTag(layer.getId(), layer.getDescription(), displayModel
                    .getLayerSupport().getLayerIndex(layer), layer.isVisible());
            result.addContent(layerTag);
        }

        return result;
    }

    /**
     * Create the XML representation of the given widget model.
     *
     * @param widgetModel
     *            A widget model.
     * @return The XML representation of the given widget model.
     */
    private Element createModelTag(final AbstractWidgetModel widgetModel) {
        Element result = new Element(XmlConstants.XML_ELEMENT_WIDGET);
        result.setAttribute(XmlConstants.XML_ATTRIBUTE_WIDGET_TYPE, widgetModel
                .getTypeID());
        Element aliasDescriptorsTag = createAliasDescriptorsTag(widgetModel);

        if (aliasDescriptorsTag.getChildren().size() > 0) {
            result.addContent(aliasDescriptorsTag);
        }

        if (widgetModel instanceof ContainerModel) {
            ContainerModel container = (ContainerModel) widgetModel;

            for (Layer layer : container.getLayerSupport().getLayers()) {
                Element layerTag = createLayerTag(layer.getId(), layer.getDescription(), container
                        .getLayerSupport().getLayerIndex(layer), layer
                        .isVisible());
                result.addContent(layerTag);
            }

            for (AbstractWidgetModel childModel : container.getWidgets()) {
                Element childTag = createModelTag(childModel);
                result.addContent(childTag);
            }
        }

        for (WidgetProperty property : widgetModel.getProperties()) {
            result.addContent(createPropertyTag(property));
        }

        return result;
    }

    /**
     * Create a <code>property</code> tag from the given
     * <code>WidgetProperty</code>.
     *
     * @param propertyId
     *            The ID of the given <code>WidgetProperty</code>.
     * @param elementProperty
     *            An <code>WidgetProperty</code>.
     * @return A <code>property</code> XML tag from the given
     *         <code>WidgetProperty</code>.
     */
    private Element createPropertyTag(final WidgetProperty elementProperty) {
        Element result = new Element(XmlConstants.XML_ELEMENT_PROPERTY);
        result.setAttribute(XmlConstants.XML_ATTRIBUTE_PROPERTY_TYPE,
                elementProperty.getPropertyType().toPortableString());
        result.setAttribute(XmlConstants.XML_ATTRIBUTE_PROPERTY_ID, elementProperty.getId());

        writePropertyValue(elementProperty, result);

        Element dynamicsDescriptorTag = createDynamicsDescriptorTag(elementProperty);

        if (dynamicsDescriptorTag != null) {
            result.addContent(dynamicsDescriptorTag);
        }
        return result;
    }

    /**
     * Create a <code>layer</code> XML tag from the given
     * <code>layerName</code>. <code>layerName</code>.
     *
     * @param layerName
     *            The id of the given Layer.
     * @param layerName
     *            The name of the given Layer.
     * @param index
     *            The index of the Layer
     * @param isVisible
     *            Representing if the Layer is visible or not
     * @return A <code>layer</code> XML tag from the given
     *         <code>layerName</code>
     */
    private Element createLayerTag(final String layerId, final String layerName, final int index,
            final boolean isVisible) {
        Element result = new Element(XmlConstants.XML_LAYER);
        result.setAttribute(XmlConstants.XML_LAYER_ID, layerId);
        result.setAttribute(XmlConstants.XML_LAYER_NAME, layerName);
        result.setAttribute(XmlConstants.XML_LAYER_INDEX, String
                        .valueOf(index));
        result.setAttribute(XmlConstants.XML_LAYER_VISIBILITY, String
                .valueOf(isVisible));

        return result;
    }

    /**
     * Create a <code>dynamicsDescriptor</code> XML tag from the given
     * <code>WidgetProperty</code>.
     *
     * @param elementProperty
     *            An <code>WidgetProperty</code>.
     * @return A <code>dynamicsDescriptor</code> XML tag from the given
     *         <code>WidgetProperty</code>.
     */
    private Element createDynamicsDescriptorTag(
            final WidgetProperty elementProperty) {
        Element result = null;

        DynamicsDescriptor dynamicsDescriptor = elementProperty
                .getDynamicsDescriptor();

        if (dynamicsDescriptor != null) {
            result = new Element(XmlConstants.XML_ELEMENT_DYNAMICS_DESCRIPTOR);

            result.setAttribute(XmlConstants.XML_ATTRIBUTE_RULE_ID,
                    dynamicsDescriptor.getRuleId());
            String string = Boolean.toString(dynamicsDescriptor.isUsingOnlyConnectionStates());
            result.setAttribute(XmlConstants.XML_ATTRIBUTE_USE_CONNECTION_STATES, string);

            // add the input parameter bindings as well!
            for (ParameterDescriptor inputChannel : dynamicsDescriptor
                    .getInputChannels()) {
                result.addContent(createParameterElement(
                        XmlConstants.XML_ELEMENT_INPUT_CHANNEL, inputChannel));
            }

            // if there is an output parameter, add it as well!
            if (dynamicsDescriptor.getOutputChannel() != null) {
                result.addContent(createParameterElement(
                        XmlConstants.XML_ELEMENT_OUTPUT_CHANNEL,
                        dynamicsDescriptor.getOutputChannel()));
            }

            AbstractPropertyPersistenceHandler persistenceHandler = PropertyPersistenceHandlerRegistry
                    .getInstance().getPersistenceHandler(
                            elementProperty.getPropertyType());

            // add values for connection states
            Map<ConnectionState, Object> connectionStateValues = dynamicsDescriptor
                    .getConnectionStateDependentPropertyValues();
            if (connectionStateValues != null) {
                List<ConnectionState> connectionStates = new ArrayList<ConnectionState>(connectionStateValues.keySet());
                Collections.sort(connectionStates);
                for (ConnectionState connectionState : connectionStates) {
                    Object value = connectionStateValues.get(connectionState);

                    if (persistenceHandler != null) {
                        Element connectionStateTag = new Element(
                                XmlConstants.XML_ELEMENT_CONNECTION_STATE);
                        connectionStateTag.setAttribute(
                                XmlConstants.XML_ATTRIBUTE_STATE,
                                connectionState.name());

                        persistenceHandler.writeProperty(connectionStateTag,
                                value);

                        result.addContent(connectionStateTag);
                    }
                }
            }

            // add values for dynamic value states
            Map<DynamicValueState, Object> dynamicValueStateValues = dynamicsDescriptor
                    .getConditionStateDependentPropertyValues();
            if (dynamicValueStateValues != null) {
                for (DynamicValueState dynamicValueState : dynamicValueStateValues
                        .keySet()) {
                    Object value = dynamicValueStateValues
                            .get(dynamicValueState);

                    if (persistenceHandler != null) {
                        Element conditionStateTag = new Element(
                                XmlConstants.XML_ELEMENT_DYNAMIC_VALUE_STATE);
                        conditionStateTag.setAttribute(
                                XmlConstants.XML_ATTRIBUTE_STATE,
                                dynamicValueState.name());

                        persistenceHandler.writeProperty(conditionStateTag,
                                value);

                        result.addContent(conditionStateTag);
                    }
                }
            }

        }

        return result;
    }

    /**
     * Create a parameter element.
     *
     * @param elementName
     *            The element name (for the input or output parameter tag).
     * @param parameter
     *            The parameter descriptor.
     * @return A parameter element.
     */
    private Element createParameterElement(final String elementName,
            final ParameterDescriptor parameter) {
        Element result = new Element(elementName);

        result.setAttribute(XmlConstants.XML_ATTRIBUTE_CHANNEL_NAME, parameter
                .getChannel());

        result.setAttribute(XmlConstants.XML_ATTRIBUTE_CHANNEL_VALUE, parameter.getValue());

        return result;
    }

    /**
     * Write the given widget property to the given XML property tag. In case of
     * a simple property, a "value" attribute is added. Complex properties might
     * also add child tags.
     *
     * @param elementProperty
     *            An <code>WidgetProperty</code>.
     * @param propertyTag
     *            The XML property tag the property should be written to.
     */
    private void writePropertyValue(final WidgetProperty elementProperty,
            final Element propertyTag) {
        AbstractPropertyPersistenceHandler persistenceHandler = PropertyPersistenceHandlerRegistry
                .getInstance().getPersistenceHandler(
                        elementProperty.getPropertyType());

        if (persistenceHandler != null) {
            persistenceHandler.writeProperty(propertyTag, elementProperty
                    .getPropertyValue());
        }
    }

    /**
     * Create a <code>aliasDescriptors</code> XML tag from the given display
     * widget model.
     *
     * @param modelElement
     *            A widget model.
     * @return A <code>aliasDescriptors</code> XML tag from the given display
     *         widget model.
     */
    private Element createAliasDescriptorsTag(
            final AbstractWidgetModel modelElement) {
        Element result = null;

        Map<String, String> aliases = modelElement.getAliases();

        result = new Element(XmlConstants.XML_ELEMENT_ALIAS_DESCRIPTORS);

        for (String key : aliases.keySet()) {
            Element aliasElement = new Element(XmlConstants.XML_ELEMENT_ALIAS);

            aliasElement.setAttribute(XmlConstants.XML_ATTRIBUTE_ALIAS_NAME,
                    key);
            aliasElement.setAttribute(XmlConstants.XML_ATTRIBUTE_ALIAS_VALUE,
                    aliases.get(key));
            // aliasElement.setAttribute(
            // XmlConstants.XML_ATTRIBUTE_ALIAS_DESCRIPTION, descriptor
            // .getDescription());

            result.addContent(aliasElement);
        }

        return result;
    }

    public String getAsString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.writeModelElementToString(_displayModel));
        for (AbstractWidgetModel model : _widgetModels) {
            buffer.append(this.writeWidgetToString(model));
        }
        return buffer.toString();
    }

    /**
     * Set the XML Header for the Stream. The default is "<?xml version=\"1.0\"
     * encoding=\"UTF-8\"?>\n"
     *
     * @param header
     *            The XML Header.
     */
    public static void setXMLHeader(final String header) {
        _xmlHeader = header;
    }
}
