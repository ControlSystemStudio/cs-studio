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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.csstudio.dal.DynamicValueState;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.EventType;
import org.csstudio.sds.internal.model.Layer;
import org.csstudio.sds.internal.model.LayerSupport;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.GroupingContainerModel;
import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * SAX handler for display models. <br>
 * This handler incrementally parses <code>widget</code> tags by transforming
 * them into DOM elements. After these DOM elements have been parsed, the
 * underlying display model is updated concurrently.
 *
 * @author Alexander Will
 * @version $Revision: 1.27 $
 *
 */
public final class SaxDisplayModelHandler extends DefaultHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SaxDisplayModelHandler.class);

    /**
     * Element stack for the created DOM elements.
     */
    private Stack<Element> _elementStack;

    /**
     * Display model.
     */
    private DisplayModel _displayModel;

    /**
     * Optional listener that will be notified of model loading events.
     */
    private IDisplayModelLoadListener _loadListener;

    /**
     * Flag that indicates if the model properties have been loaded completely.
     */
    private boolean _modelPropertiesLoaded;

    /**
     * Standard constructor.
     *
     * @param displayModel
     *            The underlying display model.
     * @param loadListener
     *            Optional listener that will be notified of model loading
     *            events (can be null).
     */
    public SaxDisplayModelHandler(final DisplayModel displayModel,
                                  final IDisplayModelLoadListener loadListener) {
        assert displayModel != null : "displayModel != null"; //$NON-NLS-1$

        _elementStack = new Stack<Element>();
        _displayModel = displayModel;
        _loadListener = loadListener;

        _modelPropertiesLoaded = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(final String uri,
                             final String localName,
                             final String qName,
                             final Attributes attributes) throws SAXException {

        Element element = createElement(qName, attributes);

        if (!_modelPropertiesLoaded && XmlConstants.XML_ELEMENT_WIDGET.equals(element.getName())) {
            _modelPropertiesLoaded = true;
            if (_loadListener != null) {
                _loadListener.onDisplayPropertiesLoaded();
            }
        }

        if (_elementStack.size() > 0) {
            Element parentElement = _elementStack.peek();
            parentElement.addContent(element);
        }

        _elementStack.push(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        Element element = _elementStack.pop();

        assert qName.equals(element.getName());

        if (_elementStack.size() == 1) {
            if (XmlConstants.XML_ELEMENT_WIDGET.equals(element.getName())) {
                final AbstractWidgetModel widgetModel = parseWidgetModel(element);
                _displayModel.addWidget(widgetModel);
            } else if (XmlConstants.XML_ELEMENT_PROPERTY.equals(element.getName())) {
                setProperty(_displayModel, element);
            } else if (XmlConstants.XML_LAYER.equals(element.getName())) {
                parseLayer(element, _displayModel);
            }
        }
    }

    /**
     * Parses the Layers contained in the given Element to thr
     * {@link ContainerModel}.
     *
     * @param element
     *            The Element
     * @param container
     *            The ContainerModel
     * @throws SAXException
     *             if an error occurred during parsing.
     */
    private void parseLayer(final Element element, final ContainerModel container) throws SAXException {
        String layerId = element.getAttributeValue(XmlConstants.XML_LAYER_ID);
        String layerName = element.getAttributeValue(XmlConstants.XML_LAYER_NAME);
        boolean visibility = true;
        try {
            visibility = element.getAttribute(XmlConstants.XML_LAYER_VISIBILITY).getBooleanValue();
        } catch (DataConversionException e) {
            throw new SAXException("Unable to read <" + XmlConstants.XML_LAYER_VISIBILITY
                    + "> property from layer element", e);
        }
        final boolean isVisible = visibility;
        int index = -1;
        try {
            index = element.getAttribute(XmlConstants.XML_LAYER_INDEX).getIntValue();
        } catch (DataConversionException e) {
            throw new SAXException("Unable to read <" + XmlConstants.XML_LAYER_INDEX
                    + "> property from layer element", e);
        }
        final int layerIndex = index;
        if (layerId == null && LayerSupport.DEFAULT_NAME.equals(layerName)) {
            layerId = LayerSupport.DEFAULT_LAYER_ID;
        }

        Layer layer = new Layer(layerId, layerName);
        layer.setVisible(isVisible);
        container.getLayerSupport().addLayer(layer, layerIndex);
    }

    /**
     * Parse the widget model form the given DOM element.
     *
     * @param element
     *            The widget model DOM element.
     * @return The widget model that was parsed from the given DOM element.
     * @throws SAXException
     *             if an error occurred during parsing.
     */
    private AbstractWidgetModel parseWidgetModel(final Element element) throws SAXException {
        String elementType = element.getAttributeValue(XmlConstants.XML_ATTRIBUTE_WIDGET_TYPE);

        // .. let the model factory create the base model
        AbstractWidgetModel result = WidgetModelFactoryService.getInstance()
                .getWidgetModel(elementType);

        // .. configure model details (properties, dynamics) from xml
        fillWidgetModel(result, element);

        // .. update model to ensure invariants that have been declared by {@link SdsPlugin#EXTPOINT_WIDGET_PROPERTY_POSTPROCESSORS}
        SdsPlugin.getDefault().getWidgetPropertyPostProcessingService()
                .applyForAllProperties(result, EventType.ON_DISPLAY_MODEL_LOADED);

        return result;
    }

    /**
     * Fill the given widget model with the values that are parsed from its DOM
     * representation.
     *
     * @param widgetModel
     *            The widget model that is to be filled.
     * @param widgetElement
     *            The DOM representation of the widget model.
     * @throws SAXException
     *             if an error occurred during parsing.
     */
    @SuppressWarnings("rawtypes")
    private void fillWidgetModel(final AbstractWidgetModel widgetModel, final Element widgetElement) throws SAXException {

        // 1. alias descriptors
        widgetModel.setAliases(parseAliasDescriptors(widgetElement
                .getChild(XmlConstants.XML_ELEMENT_ALIAS_DESCRIPTORS)));

        // 2. layer
        if (widgetModel instanceof ContainerModel) {
            List layerList = widgetElement.getChildren(XmlConstants.XML_LAYER);
            for (Object o : layerList) {
                Element layerElement = (Element) o;
                parseLayer(layerElement, (ContainerModel) widgetModel);
            }
        }

        // 3. properties
        List propertiesList = widgetElement.getChildren(XmlConstants.XML_ELEMENT_PROPERTY);

        for (Object o : propertiesList) {
            setProperty(widgetModel, (Element) o);
        }

        // 4. widgets
        if (widgetModel instanceof DisplayModel || widgetModel instanceof GroupingContainerModel) {
            List widgetList = widgetElement.getChildren(XmlConstants.XML_ELEMENT_WIDGET);
            for (Object o : widgetList) {
                Element childElement = (Element) o;
                final AbstractWidgetModel childModel = parseWidgetModel(childElement);
                ((ContainerModel) widgetModel).addWidget(childModel);
            }
        }
    }

    /**
     * Set the property that is described by the given DOM element to the given
     * widget model.
     *
     * @param widgetModel
     *            the widget model to set the property to.
     * @param propertyElement
     *            the DOM element describing the propery.
     * @throws SAXException
     *             if an error occurred during parsing.
     */
    private void setProperty(final AbstractWidgetModel widgetModel, final Element propertyElement) throws SAXException {
        String propertyType = propertyElement
                .getAttributeValue(XmlConstants.XML_ATTRIBUTE_PROPERTY_TYPE);

        AbstractPropertyPersistenceHandler persistenceHandler = getPropertyPersistenceHandler(propertyType);

        if (persistenceHandler != null) {
            String propertyId = propertyElement
                    .getAttributeValue(XmlConstants.XML_ATTRIBUTE_PROPERTY_ID);
            Object propertyValue = parsePropertyValue(persistenceHandler, propertyElement);

            Element dynamicsDescriptorElement = propertyElement
                    .getChild(XmlConstants.XML_ELEMENT_DYNAMICS_DESCRIPTOR);

            if (propertyId != null && propertyValue != null) {
                widgetModel.setPropertyValue(propertyId, propertyValue);
            }

            if (dynamicsDescriptorElement != null) {
                DynamicsDescriptor dynamicsDescriptor = parseDynamicsDescriptor(persistenceHandler,
                                                                                dynamicsDescriptorElement);

                widgetModel.setDynamicsDescriptor(propertyId, dynamicsDescriptor);
            }
        }
    }

    /**
     * Parse the property value from the given DOM element.
     *
     * @param persistenceHandler
     *            The persistence handler that is used for parsing.
     * @param propertyElement
     *            The property DOM element.
     * @return The property value that was parsed from the given property DOM
     *         element.
     */
    private Object parsePropertyValue(final AbstractPropertyPersistenceHandler persistenceHandler,
                                      final Element propertyElement) {

        Object result = null;

        if (persistenceHandler != null) {
            result = persistenceHandler.readProperty(propertyElement);
        }

        return result;
    }

    /**
     * Parse the dynamics descriptor from the given DOM element.
     *
     * @param persistenceHandler
     *            The persistence handler that is used for reading included
     *            property values.
     * @param dynamicsDescriptorElement
     *            The dynamics descriptor DOM element.
     * @return The dynamics descriptor that was parsed from the given dynamics
     *         descriptor DOM element.
     * @throws SAXException
     *             if the included state values could not be parsed.
     */
    @SuppressWarnings("rawtypes")
    private DynamicsDescriptor parseDynamicsDescriptor(final AbstractPropertyPersistenceHandler persistenceHandler,
                                                       final Element dynamicsDescriptorElement) throws SAXException {
        String ruleId = dynamicsDescriptorElement
                .getAttributeValue(XmlConstants.XML_ATTRIBUTE_RULE_ID);

        String useConnectionStatesString = dynamicsDescriptorElement
                .getAttributeValue(XmlConstants.XML_ATTRIBUTE_USE_CONNECTION_STATES);
        boolean useConnectionStates = false;
        if (useConnectionStatesString != null) {
            try {
                Boolean b = new Boolean(useConnectionStatesString);
                useConnectionStates = b;
            } catch (Exception e) {
                // do nothing
            }
        }

        DynamicsDescriptor result = new DynamicsDescriptor(ruleId);

        result.setUsingOnlyConnectionStates(useConnectionStates);
        // input channels
        List inputChannelElements = dynamicsDescriptorElement
                .getChildren(XmlConstants.XML_ELEMENT_INPUT_CHANNEL);
        if (inputChannelElements != null) {
            for (Object o : inputChannelElements) {
                ParameterDescriptor pd = parseChannel((Element) o);
                if (pd != null) {
                    result.addInputChannel(pd);
                }

            }
        }

        // output channel
        Element outputChannelElement = dynamicsDescriptorElement
                .getChild(XmlConstants.XML_ELEMENT_OUTPUT_CHANNEL);
        if (outputChannelElement != null) {
            ParameterDescriptor pd = parseChannel(outputChannelElement);
            if (pd != null) {
                result.setOutputChannel(pd);
            }
        }

        // connection states
        result.setConnectionStateDependentPropertyValues(parseConnectionStates(persistenceHandler,
                                                                               dynamicsDescriptorElement));

        // dynamic value states

        result.setConditionStateDependentPropertyValues(parseDynamicValueStates(persistenceHandler,
                                                                                dynamicsDescriptorElement));

        return result;
    }

    /**
     * Parse the connection state values from the given dynamics descriptor DOM
     * element.
     *
     * @param persistenceHandler
     *            The persistence handler that is used for reading included
     *            property values.
     * @param dynamicsDescriptorElement
     *            The dynamics descriptor DOM element.
     * @return The connection state values that were parsed from the given
     *         dynamics descriptor DOM element.
     * @throws SAXException
     *             if a connection state could not be parsed.
     */
    @SuppressWarnings("rawtypes")
    private HashMap<ConnectionState, Object> parseConnectionStates(final AbstractPropertyPersistenceHandler persistenceHandler,
                                                                   final Element dynamicsDescriptorElement) throws SAXException {
        HashMap<ConnectionState, Object> result = new HashMap<ConnectionState, Object>();
        List connectionStateElements = dynamicsDescriptorElement
                .getChildren(XmlConstants.XML_ELEMENT_CONNECTION_STATE);
        if (connectionStateElements != null) {
            for (Object o : connectionStateElements) {
                Element connectionStateElement = (Element) o;

                ConnectionState state = null;
                try {
                    String attributeValue = connectionStateElement
                            .getAttributeValue(XmlConstants.XML_ATTRIBUTE_STATE);
                    state = ConnectionState.valueOf(attributeValue);
                    result.put(state, persistenceHandler.readProperty(connectionStateElement));
                } catch (Exception e) {
                    throw new SAXException("Could not parse connection state < "
                                                   + connectionStateElement.getAttributeValue(XmlConstants.XML_ATTRIBUTE_STATE)
                                                   + ">",
                                           e);
                }

            }
        }

        return result;
    }

    /**
     * Parse the danymics value state values from the given dynamics descriptor
     * DOM element.
     *
     * @param persistenceHandler
     *            The persistence handler that is used for reading included
     *            property values.
     * @param dynamicsDescriptorElement
     *            The dynamics descriptor DOM element.
     * @return The dynamic value state values that were parsed from the given
     *         dynamics descriptor DOM element.
     * @throws SAXException
     *             if a connection state could not be parsed.
     */
    @SuppressWarnings("rawtypes")
    private HashMap<DynamicValueState, Object> parseDynamicValueStates(final AbstractPropertyPersistenceHandler persistenceHandler,
                                                                       final Element dynamicsDescriptorElement) throws SAXException {
        HashMap<DynamicValueState, Object> result = new HashMap<DynamicValueState, Object>();
        List dynamicValueStateElements = dynamicsDescriptorElement
                .getChildren(XmlConstants.XML_ELEMENT_DYNAMIC_VALUE_STATE);
        if (dynamicValueStateElements != null) {
            for (Object o : dynamicValueStateElements) {
                Element dynamicValueStateElement = (Element) o;

                DynamicValueState state = null;
                try {
                    state = DynamicValueState.valueOf(dynamicValueStateElement
                            .getAttributeValue(XmlConstants.XML_ATTRIBUTE_STATE));
                } catch (Exception e) {
                    throw new SAXException("Could not parse dynamic value state <"
                                                   + dynamicValueStateElement.getAttributeValue(XmlConstants.XML_ATTRIBUTE_STATE)
                                                   + ">",
                                           e);
                }
                result.put(state, persistenceHandler.readProperty(dynamicValueStateElement));
            }
        }

        return result;
    }

    /**
     * Parse the channel from the given DOM element.
     *
     * @param channelElement
     *            The channel DOM element.
     * @return The channel that was parsed from the given DOM element.
     * @throws SAXException
     *             if an error occurred during parsing.
     */
    private ParameterDescriptor parseChannel(final Element channelElement) throws SAXException {
        String channelName = channelElement
                .getAttributeValue(XmlConstants.XML_ATTRIBUTE_CHANNEL_NAME);
        String value = channelElement.getAttributeValue(XmlConstants.XML_ATTRIBUTE_CHANNEL_VALUE);
        if (value == null) {
            value = "";
        }
        ParameterDescriptor result = null;

        if ((channelName != null || value != null)) {
            result = new ParameterDescriptor(channelName, value);
        }

        return result;
    }

    /**
     * Parse the alias descriptors from the given JDOM element.
     *
     * @param element
     *            A JDOM representation of alias descriptors.
     * @return The alias descriptors that were parsed from the given JDOM
     *         element.
     */
    @SuppressWarnings("rawtypes")
    private Map<String, String> parseAliasDescriptors(final Element element) {
        Map<String, String> result = new HashMap<String, String>();

        if (element != null) {
            List aliases = element.getChildren(XmlConstants.XML_ELEMENT_ALIAS);
            if (aliases != null) {
                for (Object o : aliases) {
                    Element aliasElement = (Element) o;
                    String name = aliasElement
                            .getAttributeValue(XmlConstants.XML_ATTRIBUTE_ALIAS_NAME);
                    String value = aliasElement
                            .getAttributeValue(XmlConstants.XML_ATTRIBUTE_ALIAS_VALUE);
                    result.put(name, value);
                }
            }
        }

        return result;
    }

    /**
     * Create a JDOM element from the given <code>qName</code> and the given
     * <code>SAX attributes</code>.
     *
     * @param qName
     *            The given <code>qName</code>.
     * @param attributes
     *            The given <code>SAX attributes</code>.
     * @return The JDOM element that was created from the given
     *         <code>qName</code> and the given <code>SAX attributes</code>.
     */
    private Element createElement(final String qName, final Attributes attributes) {
        Element result = new Element(qName);

        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                result.setAttribute(attributes.getQName(i), attributes.getValue(i));
            }
        }

        return result;
    }

    /**
     * Return the property persistence handler for the given property type.
     *
     * @param propertyType
     *            The property type.
     * @return The property persistence handler for the given property type.
     */
    private AbstractPropertyPersistenceHandler getPropertyPersistenceHandler(final String propertyType) {
        AbstractPropertyPersistenceHandler result = null;
        try {
            result = PropertyPersistenceHandlerRegistry.getInstance()
                    .getPersistenceHandler(PropertyTypesEnum.createFromPortable(propertyType));
        } catch (Exception e) {
            LOG.error("Unknown property type <" + propertyType + ">");
        }

        return result;
    }
}
