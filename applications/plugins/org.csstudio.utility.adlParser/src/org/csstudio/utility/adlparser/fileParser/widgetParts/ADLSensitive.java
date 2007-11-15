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

import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.csstudio.utility.adlconverter.utility.widgets.Widget;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 08.11.2007
 */
public class ADLSensitive extends WidgetPart {
    /**
     * The channel.
     */
    private String[] _chan;
    /**
     * The used rule to set the Sensitive.
     */
    private String _sensitiveMode;
    /**
     * The Record property/Feldname.
     */
    private String _postfix;
    private ADLWidget _sen;

    /**
     * The default constructor.
     * 
     * @param sensitive An ADLWidget that correspond a ADL Sensitive Item. 
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ADLSensitive(final ADLWidget sensitive, final AbstractWidgetModel parentWidgetModel) throws WrongADLFormatException {
        super(sensitive, parentWidgetModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateElements() {
        if(_chan!=null&&_chan[0].length()>0){
            _postfix = ADLHelper.setChan(_widgetModel,_chan);
            if(_sensitiveMode!=null && _sensitiveMode.equals("if not zero")){
                DynamicsDescriptor adlBooleanDynamicAttribute = new DynamicsDescriptor("org.css.sds.color.if_not_zero");
                adlBooleanDynamicAttribute.addInputChannel(new ParameterDescriptor("$channel$"+_postfix,Double.class));
                _widgetModel.setDynamicsDescriptor(AbstractWidgetModel.PROP_ENABLED, adlBooleanDynamicAttribute);
            }else if(_sensitiveMode!=null && _sensitiveMode.equals("if zero")){
                DynamicsDescriptor adlBooleanDynamicAttribute = new DynamicsDescriptor("org.css.sds.color.if_zero");
                adlBooleanDynamicAttribute.addInputChannel(new ParameterDescriptor("$channel$"+_postfix,Double.class));
                _widgetModel.setDynamicsDescriptor(AbstractWidgetModel.PROP_ENABLED, adlBooleanDynamicAttribute);
            }
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void init() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void parseWidgetPart(final ADLWidget sensitive) throws WrongADLFormatException {
        assert !sensitive.isType("sensitive") :  "This "+sensitive.getType()+" is not a ADL Sensitive Item";

        for (String parameter : sensitive.getBody()) {
            if(parameter.trim().startsWith("//")){
                continue;
            }
            String[] row = parameter.split("=");
//            if(row.length!=2){
//                throw new Exception("This "+parameter+" is a wrong ADL Menu Item");
//            }
            if(row[0].trim().toLowerCase().equals("chan")){
                _chan=ADLHelper.cleanString(row[1]);
            }else if(row[0].trim().toLowerCase().equals("sensitive_mode")){
                _sensitiveMode=ADLHelper.cleanString(row[1])[0];
            }else {
                throw new WrongADLFormatException("This "+parameter+" is a wrong ADL Sensitive Item");
            }
        }
        if(_chan==null&&_sensitiveMode!=null){
            System.out.println("no chan :" + sensitive);
        }
    }

    public String getPostfix() {
        return _postfix;
    }
    
    

}
