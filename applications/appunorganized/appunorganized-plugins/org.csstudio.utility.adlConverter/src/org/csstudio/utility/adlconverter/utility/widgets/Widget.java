/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.utility.adlconverter.utility.widgets;

import java.util.Map;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.importer.AbstractDisplayImporter;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLBasicAttribute;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLControl;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLDynamicAttribute;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLMonitor;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLObject;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLPoints;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLSensitive;
import org.csstudio.utility.adlconverter.utility.widgetparts.WidgetPart;
import org.eclipse.core.runtime.IPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 12.09.2007
 */
public abstract class Widget extends AbstractDisplayImporter {

    private static final Logger LOG = LoggerFactory.getLogger(Widget.class);

    /** The ADL Widget element as CSS-SDS element. */
    protected AbstractWidgetModel _widget;
    /** The Widget object parameter. */
    private ADLObject _object = null;
    /** The Widget Basic Attribute . */
    protected ADLBasicAttribute _basicAttribute = null;
    /** The Widget Dynamic Attribute . */
    private ADLDynamicAttribute _dynamicAttribute = null;
    /** The Widget points list. */
    private ADLPoints _points = null;
    /** The Widget monitor Attribute. */
    private ADLMonitor _monitor;
    /** The Widget control Attribute. */
    private ADLControl _control;
    /** The Widget sensitive Attribute. */
    private ADLSensitive _sensitive;

    /**
     * @param widget
     *            ADLWidget that describe the Widget.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     */
    public Widget(final ADLWidget widget, ADLWidget storedBasicAttribute,
            ADLWidget storedDynamicAttribute) {
        setDefaults();
        try {
            if (storedBasicAttribute != null) {
                _basicAttribute = new ADLBasicAttribute(storedBasicAttribute, _widget);
            }
            if (storedDynamicAttribute != null) {
                _dynamicAttribute = new ADLDynamicAttribute(storedDynamicAttribute, _widget);
            }
        } catch (WrongADLFormatException e1) {
            LOG.info("Wrong Format: ", e1);
        }

        if (_basicAttribute != null && _dynamicAttribute != null) {
            DynamicsDescriptor colorAdlDynamicAttributes = _dynamicAttribute
                    .getColorAdlDynamicAttributes();
            if (colorAdlDynamicAttributes != null) {
                Map<ConnectionState, Object> connectionStateDependentPropertyValues = colorAdlDynamicAttributes
                        .getConnectionStateDependentPropertyValues();
                connectionStateDependentPropertyValues.put(ConnectionState.CONNECTED,
                        _basicAttribute.getClr());
            }
        }
        if (_basicAttribute != null) {
            _basicAttribute.setParentWidgetModel(_widget);
        }
        if (_dynamicAttribute != null) {
            _dynamicAttribute.setParentWidgetModel(_widget);
        }
        try {
            makeObject(widget);
        } catch (WrongADLFormatException e) {
            // FIXME: Workaround. Der CentralLogger wurde deaktivbiert da viele
            // Nachrichten in Kurzerzeit zum absturz von CSS führen
            // LOG.error(this, e);
        }
    }

    /**
     * @param widget
     * @param root
     */
    public Widget(ADLWidget widget, DisplayModel root) {
        _widget = root;
        // if(_basicAttribute!=null){
        // _basicAttribute.setParentWidgetModel(_widget);
        // }
        // if(_dynamicAttribute!=null){
        // _dynamicAttribute.setParentWidgetModel(_widget);
        // }
        try {
            makeObject(widget);
        } catch (WrongADLFormatException e) {
            LOG.info("Wrong format", e);
        }
    }

    /**
     * Set default values.
     */
    private void setDefaults() {
        setWidgetType();
        _widget.setLayer(""); //$NON-NLS-1$
        _widget.setEnabled(true);
        // WidgetInitializationService instance = WidgetInitializationService.getInstance();
        // instance.initialize(_widget);
    }

    /**
     * Set the Type of Widget.
     */
    abstract void setWidgetType();

    /**
     * Generate the XML-Element.
     *
     * @throws WrongADLFormatException
     */
    private void makeElemnet() throws WrongADLFormatException {

        _widget.setVisible(true);
        /**
         * Check for dynamic Attribute.
         */
        if ((_basicAttribute != null && _dynamicAttribute != null)) {
            if (_dynamicAttribute.isBoolean()) {
                _widget.setDynamicsDescriptor(AbstractWidgetModel.PROP_VISIBILITY,
                        _dynamicAttribute.getBooleanAdlDynamicAttributes());
            }
            // Wird vom Initializer erledigt.
            if (_dynamicAttribute.isColor()) {
                _widget.setDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,
                        _dynamicAttribute.getColorAdlDynamicAttributes());
            }
        }
        makeConnectionState();
    }

    /**
     * Generate the default Desy Connenction State XML-Element.
     */
    protected void makeConnectionState() {
        if (_monitor != null) {
            DynamicsDescriptor dynamicsDescriptor = _widget
                    .getDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_BACKGROUND);
            if (dynamicsDescriptor != null) {
                Map<ConnectionState, Object> connectionStateDependentPropertyValues = dynamicsDescriptor
                        .getConnectionStateDependentPropertyValues();
                if (connectionStateDependentPropertyValues != null) {
                    connectionStateDependentPropertyValues.put(ConnectionState.CONNECTED, ADLHelper
                            .getRGB(_monitor.getBclr()));
                }
            }
        }
    }

    /**
     * Scan the ADLWidget Object.
     *
     * @param widget
     *            The ADLWidget to generate the XML-Element.
     * @throws WrongADLFormatException
     *             WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    private void makeObject(final ADLWidget widget) throws WrongADLFormatException {

        for (ADLWidget obj : widget.getObjects()) {
            if (obj.isType("dynamic attribute")) { //$NON-NLS-1$
                _dynamicAttribute = new ADLDynamicAttribute(obj, _widget);
            } else if (obj.isType("monitor")) { //$NON-NLS-1$
                _monitor = new ADLMonitor(obj, _widget);
            }
        }

        for (ADLWidget obj : widget.getObjects()) {
            if (obj.isType("object")) { //$NON-NLS-1$
                _object = new ADLObject(obj, _widget);
            } else if (obj.isType("basic attribute")) { //$NON-NLS-1$
                _basicAttribute = new ADLBasicAttribute(obj, _widget);
            } else if (obj.isType("dynamic attribute")) { //$NON-NLS-1$
                // _dynamicAttribute = new ADLDynamicAttribute(obj, _widget);
            } else if (obj.isType("points")) { //$NON-NLS-1$
                _points = new ADLPoints(obj, _widget);
            } else if (obj.isType("monitor")) { //$NON-NLS-1$
                // _monitor = new ADLMonitor(obj, _widget);
            } else if (obj.isType("control")) { //$NON-NLS-1$
                _control = new ADLControl(obj, _widget);
            } else if (obj.isType("sensitive")) { //$NON-NLS-1$
                _sensitive = new ADLSensitive(obj, _widget);
            } // else{} polygon have no Parameter
        }
    }

    /**
     * @return the WidgetModel
     * @throws WrongADLFormatException
     */
    public AbstractWidgetModel getElement() throws WrongADLFormatException {
        makeElemnet();
        return _widget;
    }

    /**
     *
     * @return the Widgetpart {@link ADLObject} of this Widget.
     */
    public ADLObject getObject() {
        return _object;
    }

    /**
     *
     * @return the Widgetpart {@link ADLBasicAttribute} of this Widget.
     */
    public ADLBasicAttribute getBasicAttribute() {
        return _basicAttribute;
    }

    /**
     *
     * @return the Widgetpart {@link ADLDynamicAttribute} of this Widget.
     */
    public WidgetPart getDynamicAttribute() {
        return _dynamicAttribute;
    }

    /**
     *
     * @return the Widgetpart {@link ADLPoints} of this Widget.
     */
    public ADLPoints getPoints() {
        return _points;
    }

    /**
     *
     * @return the Widgetpart {@link ADLMonitor} of this Widget.
     */
    public ADLMonitor getMonitor() {
        return _monitor;
    }

    /**
     *
     * @return the Widgetpart {@link ADLControl} of this Widget.
     */
    public ADLControl getControl() {
        return _control;
    }

    /**
     *
     * @return the Widgetpart {@link ADLSensitive} of this Widget.
     */
    public ADLSensitive getSensitive() {
        return _sensitive;
    }

    /**
     * Convert the absolute coordinate to the relative coordinate of this Widget.
     *
     * @param x
     *            the x coordinate.
     * @param y
     *            the y coordinate.
     */
    public void convertCoordinate(final String x, final String y) {
        int iX = Integer.parseInt(x);
        int iY = Integer.parseInt(y);
        convertCoordinate(iX, iY);
    }

    /**
     * Convert the absolute coordinate to the relative coordinate of this Widget.
     *
     * @param x
     *            the x coordinate.
     * @param y
     *            the y coordinate.
     */
    protected void convertCoordinate(final int x, final int y) {
        getObject().setX(getObject().getX() - x);
        getObject().setY(getObject().getY() - y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean importDisplay(final String sourceFile, final IPath targetProject,
            final String targetFileName) throws Exception {
        // Do nothing.
        return false;
    }

    void uninit() {
        _widget.setDynamicsDescriptor(AbstractWidgetModel.PROP_BORDER_COLOR, null);
        _widget.setDynamicsDescriptor(AbstractWidgetModel.PROP_BORDER_STYLE, null);
        _widget.setDynamicsDescriptor(AbstractWidgetModel.PROP_BORDER_WIDTH, null);
        _widget.setDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, null);
        _widget.setDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, null);

    }

}
