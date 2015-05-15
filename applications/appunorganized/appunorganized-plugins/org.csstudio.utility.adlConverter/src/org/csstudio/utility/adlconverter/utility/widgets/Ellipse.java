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

import org.csstudio.sds.components.model.EllipseModel;
import org.csstudio.sds.components.model.PolygonModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;


/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 17.09.2007
 */
public class Ellipse extends Widget {

    /**
     * @param ellipse The ADLWidget that describe the Ellipse.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     */
    public Ellipse(final ADLWidget ellipse, AbstractWidgetModel abstractWidgetModel, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) {
        super(ellipse, storedBasicAttribute, storedDynamicAttribute);
        _widget.setDynamicsDescriptor(EllipseModel.PROP_FILL, null);
        if(getBasicAttribute()!=null){
            if((getBasicAttribute()!=null&&(getBasicAttribute().getWidth()==null||getBasicAttribute().getWidth().equals("0")))){ //$NON-NLS-1$
                getBasicAttribute().setStyle("0"); //$NON-NLS-1$
            }else{
                getBasicAttribute().setStyle("6"); //$NON-NLS-1$
            }
        }
        if(getBasicAttribute()!=null&&getBasicAttribute().getFill()!=null){
            _widget.setPropertyValue(EllipseModel.PROP_FILL, getBasicAttribute().getFill());
            if(getBasicAttribute().getFill()<1) {
                uninit();
            }
        }
        _widget.setPropertyValue(PolygonModel.PROP_BORDER_STYLE, 6);
        ADLHelper.checkAndSetLayer(_widget, abstractWidgetModel);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(EllipseModel.ID);
    }

}
