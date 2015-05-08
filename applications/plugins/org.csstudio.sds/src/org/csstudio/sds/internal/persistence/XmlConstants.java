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

/**
 * Definition of SDS XML tag names.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class XmlConstants {
    /**
     * XML tag name for a display.
     */
    public static final String XML_ELEMENT_DISPLAY = "display"; //$NON-NLS-1$

    /**
     * XML tag name for a widget element.
     */
    public static final String XML_ELEMENT_WIDGET = "widget"; //$NON-NLS-1$

    /**
     * XML tag name <code>dynamicsDescriptor</code>.
     */
    public static final String XML_ELEMENT_DYNAMICS_DESCRIPTOR = "dynamicsDescriptor"; //$NON-NLS-1$

    /**
     * XML tag name <code>connectionState</code>.
     */
    public static final String XML_ELEMENT_CONNECTION_STATE = "connectionState"; //$NON-NLS-1$

    /**
     * XML tag name <code>connectionState</code>.
     */
    public static final String XML_ELEMENT_DYNAMIC_VALUE_STATE = "dynamicValueState"; //$NON-NLS-1$

    /**
     * XML tag name <code>state</code>.
     */
    public static final String XML_ATTRIBUTE_STATE = "state"; //$NON-NLS-1$

    /**
     * XML tag name <code>property</code>.
     */
    public static final String XML_ELEMENT_PROPERTY = "property"; //$NON-NLS-1$

    /**
     * XML tag name <code>layer</code>.
     */
    public static final String XML_LAYER = "layer"; //$NON-NLS-1$

    /**
     * XML tag name <code>layerName</code>.
     */
    public static final String XML_LAYER_ID = "layer_id"; //$NON-NLS-1$

    /**
     * XML tag name <code>layerName</code>.
     */
    public static final String XML_LAYER_NAME = "layer_name"; //$NON-NLS-1$

    /**
     * XML tag name <code>layerIndex</code>.
     */
    public static final String XML_LAYER_INDEX = "layer_index"; //$NON-NLS-1$

    /**
     * XML tag name <code>layerVisibility</code>.
     */
    public static final String XML_LAYER_VISIBILITY = "layer_visibility"; //$NON-NLS-1$

    /**
     * XML attribute name <code>value</code>.
     */
    public static final String XML_ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$

    /**
     * XML attribute name <code>version</code>.
     */
    public static final String XML_ATTRIBUTE_MODEL_VERSION = "modelVersion"; //$NON-NLS-1$

    /**
     * XML attribute name <code>id</code>.
     */
    public static final String XML_ATTRIBUTE_PROPERTY_ID = "id"; //$NON-NLS-1$

    /**
     * XML attribute name <code>type</code> (for properties).
     */
    public static final String XML_ATTRIBUTE_PROPERTY_TYPE = "type"; //$NON-NLS-1$

    /**
     * XML attribute name <code>type</code> (for widgets).
     */
    public static final String XML_ATTRIBUTE_WIDGET_TYPE = "type"; //$NON-NLS-1$

    /**
     * XML attribute name <code>value</code> (for channels).
     */
    public static final String XML_ATTRIBUTE_CHANNEL_VALUE = "value"; //$NON-NLS-1$

    /**
     * XML attribute name <code>ruleId</code>.
     */
    public static final String XML_ATTRIBUTE_RULE_ID = "ruleId"; //$NON-NLS-1$

    /**
     * XML attribute name <code>useConnectionStates</code>.
     */
    public static final String XML_ATTRIBUTE_USE_CONNECTION_STATES = "useConnectionStates"; //$NON-NLS-1$

    /**
     * XML attribute name <code>name</code>.
     */
    public static final String XML_ATTRIBUTE_CHANNEL_NAME = "name"; //$NON-NLS-1$

    /**
     * XML attribute name <code>data</code>.
     */
    public static final String XML_ATTRIBUTE_DATA = "data"; //$NON-NLS-1$

    /**
     * XML tag name <code>inputChannel</code>.
     */
    public static final String XML_ELEMENT_INPUT_CHANNEL = "inputChannel"; //$NON-NLS-1$

    /**
     * XML tag name <code>outputChannel</code>.
     */
    public static final String XML_ELEMENT_OUTPUT_CHANNEL = "outputChannel"; //$NON-NLS-1$

    /**
     * XML tag name <code>aliasDescriptors</code>.
     */
    public static final String XML_ELEMENT_ALIAS_DESCRIPTORS = "aliasDescriptors"; //$NON-NLS-1$

    /**
     * XML tag name <code>aliasDescriptor</code>.
     */
    public static final String XML_ELEMENT_ALIAS = "aliasDescriptor"; //$NON-NLS-1$

    /**
     * XML attribute name <code>name</code> (for aliases).
     */
    public static final String XML_ATTRIBUTE_ALIAS_NAME = "name"; //$NON-NLS-1$

    /**
     * XML attribute name <code>value</code> (for aliases).
     */
    public static final String XML_ATTRIBUTE_ALIAS_VALUE = "value"; //$NON-NLS-1$

    /**
     * XML attribute name <code>description</code> (for aliases).
     */
    public static final String XML_ATTRIBUTE_ALIAS_DESCRIPTION = "description"; //$NON-NLS-1$

    /**
     * The XML version number.
     */
    public static final String XML_VERSION = "1.0"; //$NON-NLS-1$


    /**
     * This class is not intended to be instantiated.
     */
    private XmlConstants() {
    }
}
