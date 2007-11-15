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

import org.csstudio.sds.components.model.TextInputModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 18.09.2007
 */
public class Textinput extends Widget {

    /**
     * @param textInput The ADLWidget that describe the Textinput.
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public Textinput(final ADLWidget textInput) throws WrongADLFormatException {
        super(textInput);
        for (String obj : textInput.getBody()) {
            String[] row = obj.trim().split("=");
            if(row.length!=2){
                throw new WrongADLFormatException("wrong parameter");
            }
            if(row[0].equals("clrmod")){
                String[] clrmod = ADLHelper.cleanString(row[1]);
                if(clrmod[0].equals("discrete")&&getControl()!=null){
                    getControl().setConnectionState(true);
                }else if(clrmod[0].equals("alarm")){
                    //TODO: Textinput-->clrmod(alarm)                    
                }else if(clrmod[0].equals("static")){
                    //TODO: Textinput-->clrmod(static)                 
                }else{
                    throw new WrongADLFormatException("wrong or untreated Textinput-->clrmod parameter : "+clrmod[0]);
                }

            }else if(row[0].equals("alignment")){
//              <property type="sds.option" id="textAlignment">
//                  <option id="0" />
//              </property>
                int id=0;
                if(row[1].equals("\"North\"")){
                    id = 1;
                }else if(row[1].equals("\"East\"")){
                    id = 4;
                }else if(row[1].equals("\"South\"")){
                    id = 2;
                }else if(row[1].equals("\"West\"")){
                    id = 3;
                }else if(row[1].equals("\"NorthEast\"")){
                    id = 4;
                }
                _widget.setPropertyValue(TextInputModel.PROP_TEXT_ALIGNMENT, id);
            }else if(row[0].equals("format")){
                //TODO: Textinput --> format
            }else if(row[0].equals("align")){
                //TODO: Textinput --> align
            }else{                

                throw new WrongADLFormatException("wrong parameter: "+row[0]);
            } //polygon have no Parameter
        }
//        <property type="sds.boolean" id="transparent" value="false" />
        _widget.setPropertyValue(TextInputModel.PROP_TRANSPARENT, false);
        //      <property type="sds.string" id="inputText" value="">
        //      <dynamicsDescriptor ruleId="directConnection">
        //          <inputChannel name="$channel$" type="java.lang.Double" />
        //      </dynamicsDescriptor>
        //  </property>
        _widget.setPropertyValue(TextInputModel.PROP_INPUT_TEXT, "");
        String postfix = "";
        if(getMonitor()!=null&&getMonitor().getPostfix()!=null){
            postfix=getMonitor().getPostfix();
        }

        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection");
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$"+postfix,Double.class));
        _widget.setDynamicsDescriptor(TextInputModel.PROP_INPUT_TEXT, dynamicsDescriptor);
//        <property type="sds.option" id="border.style">
//        <option id="5" />
//        </property>
        _widget.setPropertyValue(TextInputModel.PROP_BORDER_STYLE, 5);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel("org.csstudio.sds.components.Textinput");
    }
}
