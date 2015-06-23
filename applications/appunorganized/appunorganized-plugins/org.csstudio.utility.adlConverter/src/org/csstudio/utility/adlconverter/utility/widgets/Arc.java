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

import org.csstudio.sds.components.model.ArcModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 17.09.2007
 */
public class Arc extends Widget {

    /**
     * @param arc The ADLWidget that describe the Arc.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public Arc(final ADLWidget arc, AbstractWidgetModel abstractWidgetModel,ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) throws WrongADLFormatException {
        super(arc, storedBasicAttribute, storedDynamicAttribute);
        for (FileLine fileLine : arc.getBody()) {
            String obj = fileLine.getLine();
            String[] row = obj.trim().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Arc_WrongADLFormatException);
            }
            if(row[0].equals("begin")){ //$NON-NLS-1$
//              <property type="sds.integer" id="start_angle" value="270" />
                int angle = Integer.parseInt(row[1])/64;
                _widget.setPropertyValue(ArcModel.PROP_STARTANGLE, Integer.toString(angle));
            }else if(row[0].equals("path")){ //$NON-NLS-1$
//              <property type="sds.integer" id="angle" value="180" />
                int angle = Integer.parseInt(row[1])/64;
                _widget.setPropertyValue(ArcModel.PROP_ANGLE, Integer.toString(angle));
            }else{
                throw new WrongADLFormatException(Messages.Arc_WrongADLFormatException);
            }
        }

//          <property type="sds.integer" id="linewidth" value="2" />
        if(getBasicAttribute()!=null&&getBasicAttribute().getWidth()!=null){
            _widget.setPropertyValue(ArcModel.PROP_LINEWIDTH, getBasicAttribute().getWidth());
            getBasicAttribute().setWidth("0"); //$NON-NLS-1$
        }
        if(getBasicAttribute().getFill()>0) {
            _widget.setPropertyValue(ArcModel.PROP_FILLCOLOR, _widget.getColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
            _widget.setPropertyValue(ArcModel.PROP_LINEWIDTH, 0);
            _widget.setPropertyValue(ArcModel.PROP_FILLED, true);

        }

        ADLHelper.checkAndSetLayer(_widget, abstractWidgetModel);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(ArcModel.ID);
    }
}
