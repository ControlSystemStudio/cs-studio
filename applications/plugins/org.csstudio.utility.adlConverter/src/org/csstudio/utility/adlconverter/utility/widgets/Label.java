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

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.components.model.LabelModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.sds.model.optionEnums.TextTypeEnum;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 17.09.2007
 */
public class Label extends Widget {

    /**
     * @param label ADLWidget that describe the Label.
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public Label(final ADLWidget label) throws WrongADLFormatException {
        super(label);
        
        String labelText=null;
        if(getBasicAttribute()!=null){
            getBasicAttribute().setWidth("0"); //$NON-NLS-1$
        }

        for (String obj : label.getBody()) {
            String[] row = obj.trim().split("="); //$NON-NLS-1$
            if(row.length<2){
                throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin+obj+Messages.Label_WrongADLFormatException_Parameter_End);
            }
            if(row[0].equals("textix")){ //$NON-NLS-1$
//              <property type="sds.string" id="value.text" value="CMTB" />
                String[] textit = ADLHelper.cleanString(row[1]);
                _widget.setPropertyValue(LabelModel.PROP_TEXTVALUE, textit[1]);
                labelText = textit[1];
                if(textit[1].startsWith("$")&&textit.length>2){ //$NON-NLS-1$
                    _widget.setAliasValue("channel", textit[2]); //$NON-NLS-1$
                    _widget.setPrimarPv(textit[2]);
                    _widget.setPropertyValue(LabelModel.PROP_TYPE, TextTypeEnum.TYPE_ALIAS);
                }
            }else if(row[0].equals("alignment")||row[0].equals("align")){ //$NON-NLS-1$ //$NON-NLS-2$
//              <property type="sds.option" id="textAlignment">
//                  <option id="0" />
//              </property>
                int id=0;
                if(row[1].equals("\"North\"")){ //$NON-NLS-1$
                    id = 1;
                }else if(row[1].equals("\"East\"")){ //$NON-NLS-1$
                    id = 4;
                }else if(row[1].equals("\"South\"")){ //$NON-NLS-1$
                    id = 2;
                }else if(row[1].equals("\"West\"")||row[1].equals("\"horiz. left\"")){ //$NON-NLS-1$ //$NON-NLS-2$
                    id = 3;
                }else if(row[1].equals("\"NorthEast\"")||row[1].equals("\"horiz. right\"")){ //$NON-NLS-1$ //$NON-NLS-2$
                    id = 4;
                }
                _widget.setPropertyValue(LabelModel.PROP_TEXT_ALIGN, id);
            }else if(row[0].equals("clrmod")){ //$NON-NLS-1$
                //TODO: Label-->clrmod (CSS-SDS unterstüzung fehlt!)
            }else if(row[0].equals("format")){ //$NON-NLS-1$
                String test = row[1];
                CentralLogger.getInstance().debug(this,"Format = " + test);
                if(test.equals("\"exponential\"")){
                    _widget.setPropertyValue(LabelModel.PROP_TYPE, TextTypeEnum.TYPE_TEXT); //TODO: Label->format->exponential wird noch nicht vom ASDS unterstützt.
                }else if(test.equals("\"decimal\"")){
                    _widget.setPropertyValue(LabelModel.PROP_TYPE, TextTypeEnum.TYPE_DOUBLE); 
                }else if(test.equals("\"engr.notation\"")){
                    _widget.setPropertyValue(LabelModel.PROP_TYPE, TextTypeEnum.TYPE_TEXT);  //TODO: Label->format->engr.notation wird noch nicht vom ASDS unterstützt.
                }else if(test.equals("\"compact\"")){
                    _widget.setPropertyValue(LabelModel.PROP_TYPE, TextTypeEnum.TYPE_TEXT);  //TODO: Label->format->compact wird noch nicht vom ASDS unterstützt.
                }else if(test.equals("\"octal\"")){
                    _widget.setPropertyValue(LabelModel.PROP_TYPE, TextTypeEnum.TYPE_TEXT);  //TODO: Label->format->octal wird noch nicht vom ASDS unterstützt.
                }else if(test.equals("\"hexadecimal\"")){
                    _widget.setPropertyValue(LabelModel.PROP_TYPE, TextTypeEnum.TYPE_HEX); 
                }else if(test.equals("\"truncated\"")){
                    _widget.setPropertyValue(LabelModel.PROP_TYPE, TextTypeEnum.TYPE_TEXT); //TODO: Label->format->truncated wird noch nicht vom ASDS unterstützt.
                }else{// Unknown or String
                    _widget.setPropertyValue(LabelModel.PROP_TYPE, TextTypeEnum.TYPE_TEXT);
                }
            }else{                
                throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin+ obj+Messages.Label_WrongADLFormatException_Parameter_End);
            } //Label have no Parameter
        }
        if(getMonitor()!=null){
            String postfix = ""; //$NON-NLS-1$
            if(getMonitor()!=null&&getMonitor().getPostfix()!=null){
                postfix=getMonitor().getPostfix();
            }
            DynamicsDescriptor dd = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
            dd.addInputChannel(new ParameterDescriptor("$channel$"+postfix+", string",Double.class)); //$NON-NLS-1$ //$NON-NLS-2$
            _widget.setDynamicsDescriptor(LabelModel.PROP_TEXTVALUE, dd);

        }
        int fontSize = ADLHelper.getFontSize("Times New Roman",labelText, getObject().getHeight(), getObject().getWidth(),"0"); //$NON-NLS-1$ //$NON-NLS-2$
        _widget.setPropertyValue(LabelModel.PROP_FONT, new FontData("Times New Roman", fontSize, SWT.NONE)); //$NON-NLS-1$
        if(getBasicAttribute()==null){
            _widget.setPropertyValue(LabelModel.PROP_TRANSPARENT, false);
        }else if(label.isType("Text")){ //$NON-NLS-1$
            _widget.setPropertyValue(LabelModel.PROP_TRANSPARENT, true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(LabelModel.ID);
    }
}
