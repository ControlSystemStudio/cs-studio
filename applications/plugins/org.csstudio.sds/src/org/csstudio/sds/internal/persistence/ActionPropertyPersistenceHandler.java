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

import org.csstudio.sds.model.ActionData;
import org.csstudio.sds.model.ActionType;
import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence handler for the property type <code>sds.map</code>.
 *
 * @author Kai Meyer
 *
 */
public final class ActionPropertyPersistenceHandler extends
        AbstractPropertyPersistenceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ActionPropertyPersistenceHandler.class);

    /**
     * XML tag name <code>actiondata</code>.
     */
    public static final String XML_ELEMENT_ACTIONDATA = "actionData"; //$NON-NLS-1$
    /**
     * XML tag name <code>type</code>.
     */
    public static final String XML_ATTRIBUT_ACTION_TYPE = "type"; //$NON-NLS-1$
    /**
     * XML tag name <code>type</code>.
     */
    public static final String XML_ACTION_ATTRIBUT = "action_attribut"; //$NON-NLS-1$
    /**
     * XML tag name <code>type</code>.
     */
    public static final String XML_PROPERTY_ATTRIBUT = "property_attribut"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeProperty(final Element domElement,
            final Object propertyValue) {
        ActionData actionData = (ActionData) propertyValue;

        Element actionDataElement = new Element(XML_ELEMENT_ACTIONDATA);
        for (AbstractWidgetActionModel action : actionData.getWidgetActions()) {
            Element actionTag = createActionTag(action);
            for (String propertyKey : action.getPropertyKeys()) {
                WidgetProperty property = action.getProperty(propertyKey);
                AbstractPropertyPersistenceHandler persistenceHandler = PropertyPersistenceHandlerRegistry
                        .getInstance().getPersistenceHandler(
                                property.getPropertyType());
                Element propertyTag = createPropertyTag(propertyKey, property);
                persistenceHandler.writeProperty(propertyTag, property
                        .getPropertyValue());
                actionTag.addContent(propertyTag);
            }
            actionDataElement.addContent(actionTag);
        }
        domElement.addContent(actionDataElement);
    }

    /**
     * Create a <code>property</code> tag from the given
     * <code>WidgetProperty</code>.
     *
     * @param propertyKey
     *            The ID of the given <code>WidgetProperty</code>.
     * @param elementProperty
     *            An <code>WidgetProperty</code>.
     * @return A <code>property</code> XML tag from the given
     *         <code>WidgetProperty</code>.
     */
    private Element createPropertyTag(final String propertyKey,
            final WidgetProperty elementProperty) {
        Element result = new Element(XML_PROPERTY_ATTRIBUT);
        result.setAttribute(XmlConstants.XML_ATTRIBUTE_PROPERTY_TYPE,
                elementProperty.getPropertyType().toPortableString());
        result
                .setAttribute(XmlConstants.XML_ATTRIBUTE_PROPERTY_ID,
                        propertyKey);

        return result;
    }

    /**
     * Create a <code>property</code> tag from the given
     * <code>WidgetProperty</code>.
     *
     * @param elementAction
     *            An <code>WidgetProperty</code>.
     * @return A <code>property</code> XML tag from the given
     *         <code>WidgetProperty</code>.
     */
    private Element createActionTag(final AbstractWidgetActionModel elementAction) {
        Element result = new Element(XML_ACTION_ATTRIBUT);
        result.setAttribute(XML_ATTRIBUT_ACTION_TYPE, elementAction.getType()
                .toString());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readProperty(final Element domElement) {
        Element actionDataElement = domElement.getChild(XML_ELEMENT_ACTIONDATA);
        ActionData result = new ActionData();
        for (Object action : actionDataElement.getChildren(XML_ACTION_ATTRIBUT)) {
            Element actionElement = (Element) action;
            String actionType = actionElement
                    .getAttributeValue(XML_ATTRIBUT_ACTION_TYPE);

            try {
                ActionType type = ActionType.valueOf(actionType);

                if (type != null) {
                    AbstractWidgetActionModel widgetAction = type.getActionFactory()
                            .createWidgetActionModel();

                    for (Object property : actionElement
                            .getChildren(XML_PROPERTY_ATTRIBUT)) {
                        Element propertyElement = (Element) property;
                        String propertyKey = propertyElement
                                .getAttributeValue(XmlConstants.XML_ATTRIBUTE_PROPERTY_ID);
                        String propertyType = propertyElement
                                .getAttributeValue(XmlConstants.XML_ATTRIBUTE_PROPERTY_TYPE);
                        if (propertyKey != null && propertyType != null) {
                            AbstractPropertyPersistenceHandler persistenceHandler = getPropertyPersistenceHandler(propertyType);
                            Object propertyValue = persistenceHandler
                                    .readProperty(propertyElement);
                            widgetAction.getProperty(propertyKey)
                                    .setPropertyValue(propertyValue);
                        }
                    }
                    result.addAction(widgetAction);
                }
            } catch (Exception e) {
                // if any exceptions are thrown, we just forget the action
                // fragment in the xml (this may happen because of API changes
                // that break older display files)
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
    private AbstractPropertyPersistenceHandler getPropertyPersistenceHandler(
            final String propertyType) {
        AbstractPropertyPersistenceHandler result = null;
        try {
            result = PropertyPersistenceHandlerRegistry.getInstance()
                    .getPersistenceHandler(
                            PropertyTypesEnum.createFromPortable(propertyType));
        } catch (Exception e) {
            LOG.error("Unknown property type <" + propertyType + ">");
        }

        return result;
    }
}
