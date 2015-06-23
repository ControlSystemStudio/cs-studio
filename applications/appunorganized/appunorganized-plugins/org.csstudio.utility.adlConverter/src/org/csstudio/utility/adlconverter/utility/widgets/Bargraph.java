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

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.cosyrules.color.DefaultEpicsAlarmForeground;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 19.09.2007
 */
public class Bargraph extends Widget {

    private static final Logger LOG = LoggerFactory.getLogger(Bargraph.class);

    /**
     * @param bargraph The ADLWidget that describe the Bargraph.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    @SuppressWarnings("restriction")
    public Bargraph(final ADLWidget bargraph, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) throws WrongADLFormatException {
        super(bargraph, storedBasicAttribute, storedDynamicAttribute);
        boolean barOnly = false;
        String bool;
        int marksShowStatus = 0;
        int scaleShowStatus = 0;
        /*  direction="up"
            scaleType="linear"
            showAlarmLimits="off"
            showScale="off"
            showBar="normal"
         */
        for (FileLine fileLine : bargraph.getBody()) {
            String obj = fileLine.getLine();
            String[] row = obj.trim().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Bargraph_1);
            }

            if(row[0].equals("direction")){ //$NON-NLS-1$
//              <property type="sds.boolean" id="orientation" value="false" />
                bool = Boolean.toString(row[1].toLowerCase().equals("up")); //$NON-NLS-1$
                _widget.setPropertyValue(BargraphModel.PROP_ORIENTATION, bool);
            }else if(row[0].equals("scaleType")){ //$NON-NLS-1$
                String type = row[1].trim();
                if(!type.equals("\"linear\"")){
                    //TODO: Barghraph-->scaleType (Not Supported from SDS)
                    LOG.debug(Messages.Bargraph_Clrmod_Debug, fileLine);
                }
            }else if(row[0].equals("showAlarmLimits")&&!barOnly){ //$NON-NLS-1$
                if(!row[1].toLowerCase().equals("\"off\"")){ //$NON-NLS-1$
                    marksShowStatus=1; // Bottom / Right
                }
            }else if(row[0].equals("showScale")&&!barOnly){ //$NON-NLS-1$
//              <property type="sds.boolean" id="showValues" value="false" />
                bool = Boolean.toString(row[1].toLowerCase().equals("off")); //$NON-NLS-1$
                _widget.setPropertyValue(BargraphModel.PROP_SHOW_VALUES, bool);
//              <property type="sds.option" id="marksShowStatus">
//                  <option id="0" />
//              </property>
                if(!row[1].toLowerCase().equals("off")){ //$NON-NLS-1$
                    scaleShowStatus=1; // Bottom / Right
                }
            }else if(row[0].equals("showBar")){ //$NON-NLS-1$
                barOnly=true;
                _widget.setPropertyValue(BargraphModel.PROP_SHOW_VALUES, false);
                scaleShowStatus=0; // No scale
//              <property type="sds.integer" id="border.width" value="0" />
                _widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH, 0);
            }else if(row[0].equals("format")){ //$NON-NLS-1$
                //TODO: Bargraph --> format
                LOG.debug(Messages.Bargraph_Format_Debug, fileLine);
            }else if(row[0].equals("limitType")){ //$NON-NLS-1$
                if(row[1].equals("\"from channel\"")){
                    final String[] typ = new String[]{"lolo","lo","hi","hihi"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    final String[] val = new String[]{"0","25","75","100"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    final String[] color= new String[]{"0","1","2","3"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    assert( typ.length==val.length&&val.length==color.length);
                    for (int i = 0; i < color.length; i++) {
                        makeLevel(typ[i],val[i],color[i]);
                    }
                }else{
                    //TODO: Bargraph --> limitType
                    LOG.debug(Messages.Bargraph_Limit_Type_Debug, fileLine);
                }
            }else if(row[0].equals("highLimit")){ //$NON-NLS-1$
                String temp = row[1].replaceAll("\"",""); //$NON-NLS-1$ //$NON-NLS-2$
                _widget.setPropertyValue(BargraphModel.PROP_MAX, new Double(temp).toString()); //$NON-NLS-1$ //$NON-NLS-2$
            }else if(row[0].equals("lowLimit")){ //$NON-NLS-1$
                String temp = row[1].replaceAll("\"",""); //$NON-NLS-1$ //$NON-NLS-2$
                _widget.setPropertyValue(BargraphModel.PROP_MIN, new Double(temp).toString()); //$NON-NLS-1$ //$NON-NLS-2$
            }else if(row[0].equals("clrmod")){ //$NON-NLS-1$
                String type = row[1].trim();
                if(!type.equals("alarm")){
                    DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor(DefaultEpicsAlarmForeground.TYPE_ID);
                    dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$"));
                    _widget.setDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, dynamicsDescriptor);
                }else{
                    //  TODO: Bargraph --> clrmod
                    LOG.debug(Messages.Bargraph_Clrmod_Debug, fileLine);
                }
            }else if(row[0].equals("label")){ //$NON-NLS-1$
                //TODO: Bargraph --> label
                LOG.debug(Messages.Bargraph_Clrmod_Debug, fileLine);
            }else if(row[0].equals("fillmod")){ //$NON-NLS-1$
                //TODO: Bargraph --> fillmod
                LOG.debug(Messages.Bargraph_Clrmod_Debug, fileLine);
            }else{ //Bargraph have no Parameter
                throw new WrongADLFormatException(Messages.Bargraph_WrongADLFormatException_Parameter_Begin+fileLine);
            }
        }
        _widget.setPropertyValue(BargraphModel.PROP_SHOW_MARKS, marksShowStatus);
        _widget.setPropertyValue(BargraphModel.PROP_SHOW_SCALE,scaleShowStatus);

        _widget.setPropertyValue(BargraphModel.PROP_MIN, 0.0);
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[graphMin]","")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(BargraphModel.PROP_MIN, dynamicsDescriptor );

        _widget.setPropertyValue(BargraphModel.PROP_MAX, 10.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[graphMax]","")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(BargraphModel.PROP_MAX, dynamicsDescriptor );

        String postfix = ""; //$NON-NLS-1$
        if(getMonitor()!=null&&getMonitor().getPostfix()!=null){
            postfix=getMonitor().getPostfix();
        }
        _widget.setPropertyValue(BargraphModel.PROP_FILL, 5.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$"+postfix,"")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(BargraphModel.PROP_FILL, dynamicsDescriptor );

//        <property type="sds.boolean" id="value_representation" value="true" />
        if(bargraph.isType("bar")){ //$NON-NLS-1$
            _widget.setPropertyValue(BargraphModel.PROP_SHOW_ONLY_VALUE, false);
        }else if(bargraph.isType("indicator")){ //$NON-NLS-1$
            _widget.setPropertyValue(BargraphModel.PROP_SHOW_ONLY_VALUE, true);
        }else {
            throw new WrongADLFormatException(Messages.Bargraph_WrongADLFormatException_Type);
        }

        _widget.setPropertyValue(BargraphModel.PROP_DEFAULT_FILL_COLOR, ADLHelper.getRGB(getMonitor().getClr()));
        dynamicsDescriptor = new DynamicsDescriptor("org.css.sds.color.default_epics_alarm_foreground"); //$NON-NLS-1$
        ADLHelper.setConnectionState(dynamicsDescriptor);
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[severity]","")); //$NON-NLS-1$

        _widget.setDynamicsDescriptor(BargraphModel.PROP_DEFAULT_FILL_COLOR, dynamicsDescriptor);

        _widget.setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR, ADLHelper.getRGB(getMonitor().getBclr()));
        /*
        dynamicsDescriptor = new DynamicsDescriptor("rule.null"); //$NON-NLS-1$
        ADLHelper.setConnectionState(dynamicsDescriptor);
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[severity]","")); //$NON-NLS-1$

        _widget.setDynamicsDescriptor(BargraphModel.PROP_FILLBACKGROUND_COLOR, dynamicsDescriptor);
        */
        _widget.setDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, null);
        _widget.setLayer("Bargraph"); //$NON-NLS-1$


    }

    /**
     * @param id set id of property. The postfix Level and Color automatically added.
     * @param value set value of Property.
     * @param color set the color of Property.
     */
    @SuppressWarnings("restriction")
    private void makeLevel(final String id, final String value, final String color) {

//      <property type="sds.double" id="loloLevel" value="0.2" />
        _widget.setPropertyValue(id+"Level", value); //$NON-NLS-1$
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        String temp = id.toUpperCase();
        if(temp.equals("HI")){
            temp="[warningMin]";
        }else if(temp.equals("LO")){
            temp="[warningMax]";
        }
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$"+temp,"")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(id+"Level", dynamicsDescriptor ); //$NON-NLS-1$
//      <property type="sds.color" id="loColor">
//          <color red="255" green="100" blue="100" />
//      </property>
        _widget.setPropertyValue(id+"Color", ADLHelper.getRGB(color)); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(BargraphModel.ID);
    }

}
