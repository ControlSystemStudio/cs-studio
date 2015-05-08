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

import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.DebugHelper;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.09.2007
 */
public class ADLControl extends WidgetPart{

    /**
     * The foreground color.
     */
    private String _clr;
    /**
     * The background color.
     */
    private String _bclr;
    /**
     * The channel.
     */
    private String[] _chan;
    /**
     * If true backgroundColor get the ConnectionState.
     * Default is false.
     */
    private boolean _connectionState;
    /**
     * The Record property/Feldname.
     */
    private String _postfix;


    /**
     * The default constructor.
     *
     * @param adlControl An ADLWidget that correspond a ADL Control.
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ADLControl(final ADLWidget adlControl, final AbstractWidgetModel parentWidgetModel) throws WrongADLFormatException {
        super(adlControl, parentWidgetModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void init() {
        _connectionState = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void parseWidgetPart(final ADLWidget adlControl) throws WrongADLFormatException {

        assert adlControl.isType("control") : Messages.ADLControl_AssertError_Begin+adlControl.getType()+Messages.ADLControl_AssertError_End; //$NON-NLS-1$

        for (FileLine parameter : adlControl.getBody()) {
            if(parameter.getLine().trim().startsWith("//")){ //$NON-NLS-1$
                continue;
            }
            String[] row = parameter.getLine().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.ADLControl_WrongADLFormatException_Begin+parameter+Messages.ADLControl_WrongADLFormatException_End);
            }
            if(row[0].trim().toLowerCase().equals("clr")){ //$NON-NLS-1$
                _clr=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("bclr")){ //$NON-NLS-1$
                _bclr=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("chan")){ // chan and ctrl means both the same.  //$NON-NLS-1$
                DebugHelper.add(this, row[1]);
                _chan=ADLHelper.cleanString(row[1]);
            }else if(row[0].trim().toLowerCase().equals("ctrl")){ //$NON-NLS-1$
                DebugHelper.add(this, row[1]);
                _chan=ADLHelper.cleanString(row[1]);
            }else {
                throw new WrongADLFormatException(Messages.ADLControl_WrongADLFormatException_Parameter_Begin+parameter+Messages.ADLControl_WrongADLFormatException_Parameter_End);
            }
        }
    }

    /**
     * Generate all Elements from ADL Control Attributes.
     */
    @Override
    final void generateElements() {
        if(_clr!=null){
            _widgetModel.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, ADLHelper.getRGB(_clr));
        }
        if(_bclr!=null){
            _widgetModel.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, ADLHelper.getRGB(_bclr));
            if(_connectionState){
                DynamicsDescriptor dynDis = new DynamicsDescriptor("rule.null"); //$NON-NLS-1$
                dynDis.addInputChannel(new ParameterDescriptor("$channel$","")); //$NON-NLS-1$
                _widgetModel.setDynamicsDescriptor("color.background", dynDis); //$NON-NLS-1$
            }
        }
        if(_chan!=null){
            _postfix = ADLHelper.setChan(_widgetModel,_chan);
        }
    }

    /**
     * @return the state of background color display the ConnectionState.
     */
    public final boolean isConnectionState() {
        return _connectionState;
    }

    /**
     * @param connectionState set background color display the ConnectionState.
     */
    public final void setConnectionState(final boolean connectionState) {
        _connectionState = connectionState;
    }

    /**
     * @return the postfix (property/Feldname) of the record.
     */
    public final String getPostfix() {
        return _postfix;
    }
}
