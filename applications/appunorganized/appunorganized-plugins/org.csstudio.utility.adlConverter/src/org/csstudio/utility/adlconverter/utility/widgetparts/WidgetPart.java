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
package org.csstudio.utility.adlconverter.utility.widgetparts;

import org.csstudio.sds.components.model.MenuButtonModel;
import org.csstudio.sds.components.model.PolygonModel;
import org.csstudio.sds.components.model.RectangleModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.initializers.WidgetInitializationService;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 24.10.2007
 */
public abstract class WidgetPart {

    /**
     * The Widget that set the parameter from ADLWidget.
     */
    protected AbstractWidgetModel _widgetModel;

//    protected AbstractWidgetModel _parentWidgetModel;

    /**
     * The default constructor.
     *
     * @param widgetPart An ADLWidget that correspond to the Child Widget Part.
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public WidgetPart(final ADLWidget widgetPart, final AbstractWidgetModel parentWidgetModel)
        throws WrongADLFormatException {

        _widgetModel = parentWidgetModel;
        if (((this instanceof ADLMonitor) || (this instanceof ADLDynamicAttribute) || (this instanceof ADLControl)) &&!((parentWidgetModel instanceof PolygonModel) || (parentWidgetModel instanceof MenuButtonModel) || (parentWidgetModel instanceof RectangleModel))) {
            final WidgetInitializationService instance = WidgetInitializationService.getInstance();
            instance.initialize(parentWidgetModel);
        }
        init();
        parseWidgetPart(widgetPart);
        generateElements();
    }

    /**
     * Initialization.
     */
    abstract void init();

    /**
     * Pars the {@link ADLWidget}.
     *
     * @param widgetPart the widget Part to pars.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    abstract void parseWidgetPart(ADLWidget widgetPart) throws WrongADLFormatException;



    /**
     * Set all property's to the Parent Widget Model.
     */
    abstract void generateElements();

    public final void setParentWidgetModel(final AbstractWidgetModel parentWidgetModel) {
        _widgetModel = parentWidgetModel;
    }

    protected void uninit() {
        _widgetModel.setDynamicsDescriptor(AbstractWidgetModel.PROP_BORDER_COLOR, null);
        _widgetModel.setDynamicsDescriptor(AbstractWidgetModel.PROP_BORDER_STYLE, null);
        _widgetModel.setDynamicsDescriptor(AbstractWidgetModel.PROP_BORDER_WIDTH, null);
        _widgetModel.setDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, null);
        _widgetModel.setDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, null);
    }


}
