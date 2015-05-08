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
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractTextTypeWidgetModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.TextTypeEnum;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.DebugHelper;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 18.09.2007
 */
public class Textinput extends Widget {

    private static final Logger LOG = LoggerFactory.getLogger(Textinput.class);

    /**
     * @param textInput The ADLWidget that describe the Textinput.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    @SuppressWarnings("restriction")
    public Textinput(final ADLWidget textInput, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) throws WrongADLFormatException {
        super(textInput, storedBasicAttribute, storedDynamicAttribute);
        for (FileLine fileLine : textInput.getBody()) {
            String obj = fileLine.getLine();
            String[] row = obj.trim().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Textinput_WrongADLFormatException_Parameter_Begin+row[0]);
            }
            if(row[0].equals("clrmod")){ //$NON-NLS-1$
                DebugHelper.add(this, row[1]);
                String[] clrmod = ADLHelper.cleanString(row[1]);
                if(clrmod[0].equals("discrete")&&getControl()!=null){ //$NON-NLS-1$
                    getControl().setConnectionState(true);
                }else if(clrmod[0].equals("alarm")){ //$NON-NLS-1$
                    //TODO: Textinput-->clrmod(alarm)
                }else if(clrmod[0].equals("static")){ //$NON-NLS-1$
                    //TODO: Textinput-->clrmod(static)
                }else{
                    throw new WrongADLFormatException(Messages.Textinput_WrongADLFormatException+fileLine);
                }

            }else if(row[0].startsWith("align")){ //$NON-NLS-1$
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
                }else if(row[1].equals("\"West\"")){ //$NON-NLS-1$
                    id = 3;
                }else if(row[1].equals("\"NorthEast\"")){ //$NON-NLS-1$
                    id = 4;
                }else{
                    //TODO: Textinput --> align formats
                    LOG.debug(Messages.Textinput_Align_Debug, fileLine);
                }
                _widget.setPropertyValue(TextInputModel.PROP_TEXT_ALIGNMENT, id);
            }else if(row[0].equals("format")){ //$NON-NLS-1$
                if(row[1].equals("\"string\"")){ //$NON-NLS-1$
                    _widget.setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.TEXT);
                }else if(row[1].equals("\"exponential\"")){ //$NON-NLS-1$
                    _widget.setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.EXP);
                }else if(row[1].equals("\"decimal\"")){ //$NON-NLS-1$
                    _widget.setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.DOUBLE);
                }else if(row[1].equals("\"hexadecimal\"")){ //$NON-NLS-1$
                    _widget.setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.HEX);
                }else{
                    LOG.debug(Messages.Textinput_Format_Debug, fileLine);
                }
            }else{

                throw new WrongADLFormatException(Messages.Textinput_WrongADLFormatException_Parameter_Begin+fileLine);
            } //polygon have no Parameter
        }
//        <property type="sds.boolean" id="transparent" value="false" />
        _widget.setPropertyValue(TextInputModel.PROP_TRANSPARENT, false);
        //      <property type="sds.string" id="inputText" value="">
        //      <dynamicsDescriptor ruleId="directConnection">
        //          <inputChannel name="$channel$" type="java.lang.Double" />
        //      </dynamicsDescriptor>
        //  </property>
        _widget.setPropertyValue(TextInputModel.PROP_INPUT_TEXT, ""); //$NON-NLS-1$
        String postfix = ""; //$NON-NLS-1$
        if(getMonitor()!=null&&getMonitor().getPostfix()!=null){
            postfix=getMonitor().getPostfix();
        }

        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$"+postfix,"")); //$NON-NLS-1$
        dynamicsDescriptor.setOutputChannel(new ParameterDescriptor("$channel$"+postfix,"")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(TextInputModel.PROP_INPUT_TEXT, dynamicsDescriptor);
//        <property type="sds.option" id="border.style">
//        <option id="5" />
//        </property>
        _widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE, 5);
        _widget.setLayer(Messages.ADLDisplayImporter_ADLActionLayerName);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(TextInputModel.ID);
    }
}
