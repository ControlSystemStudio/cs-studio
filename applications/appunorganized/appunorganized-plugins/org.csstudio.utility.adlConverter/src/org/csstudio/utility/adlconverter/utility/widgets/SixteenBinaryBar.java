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

import org.csstudio.sds.components.model.SixteenBinaryBarModel;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 04.09.2008
 */
public class SixteenBinaryBar extends Widget {

    /**
     * @param sixteenBinaryBar
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     */
    public SixteenBinaryBar(ADLWidget sixteenBinaryBar, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) throws WrongADLFormatException {
        super(sixteenBinaryBar, storedBasicAttribute, storedDynamicAttribute);
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
//        FIXME: Parameter was Integer.class!!
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor(_widget.getPrimaryPV(),"")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(SixteenBinaryBarModel.PROP_VALUE, dynamicsDescriptor );
        _widget.setColor(SixteenBinaryBarModel.PROP_ON_COLOR, _widget.getColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
        _widget.setColor(SixteenBinaryBarModel.PROP_OFF_COLOR, _widget.getColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND));
        for (FileLine fileLine : sixteenBinaryBar.getBody()) {
            String bodyPart = fileLine.getLine();
            String[] row = bodyPart.trim().split("="); //$NON-NLS-1$
            if(row.length<2){
                throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin+bodyPart+Messages.Label_WrongADLFormatException_Parameter_End);
            }
            if(row[0].equals("clrmod")){ //$NON-NLS-1$
                //TODO: Label-->clrmod (CSS-SDS unterstüzung fehlt!)
            // The Text alignment.
            }else if(row[0].equals("direction")){
                if(row[1].equals("\"up\"")){
                    _widget.setPropertyValue(SixteenBinaryBarModel.PROP_HORIZONTAL, false);
                }else if(row[1].equals("\"right\"")){
                    _widget.setPropertyValue(SixteenBinaryBarModel.PROP_HORIZONTAL, true);
                }
            }else if(row[0].equals("sbit")){
                _widget.setPropertyValue(SixteenBinaryBarModel.PROP_BITS_FROM, row[1]);
            }else if(row[0].equals("ebit")){
                _widget.setPropertyValue(SixteenBinaryBarModel.PROP_BITS_TO, row[1]);
            }
        }
    }



    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(SixteenBinaryBarModel.ID);
    }

}
