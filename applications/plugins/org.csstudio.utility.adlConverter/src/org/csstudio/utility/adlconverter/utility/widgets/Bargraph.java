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
import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 19.09.2007
 */
public class Bargraph extends Widget {

    /**
     * @param bargraph The ADLWidget that describe the Bargraph.
     * @throws WrongADLFormatException WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public Bargraph(final ADLWidget bargraph) throws WrongADLFormatException {
        super(bargraph);
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
        for (String obj : bargraph.getBody()) {
            String[] row = obj.trim().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Bargraph_1);
            }

            if(row[0].equals("direction")){ //$NON-NLS-1$
//              <property type="sds.boolean" id="orientation" value="false" />
                bool = Boolean.toString(row[1].toLowerCase().equals("up")); //$NON-NLS-1$
                _widget.setPropertyValue(BargraphModel.PROP_ORIENTATION, bool);
            }else if(row[0].equals("scaleType")){ //$NON-NLS-1$
                //TODO: Barghraph-->scaleType (Not Supported from SDS) 
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
                marksShowStatus=0; // No scale
                _widget.setPropertyValue(BargraphModel.PROP_SHOW_VALUES, false); 
                scaleShowStatus=0; // No scale
//              <property type="sds.integer" id="border.width" value="0" />
                _widget.setPropertyValue(BargraphModel.PROP_BORDER_WIDTH, 0); 
            }else if(row[0].equals("format")){ //$NON-NLS-1$
                //TODO: Bargraph --> format
                CentralLogger.getInstance().debug(this, Messages.Bargraph_Format_Debug+row[0]);
            }else if(row[0].equals("limitType")){ //$NON-NLS-1$
                //TODO: Bargraph --> limitType
                CentralLogger.getInstance().debug(this, Messages.Bargraph_Limit_Type_Debug+row[0]);
            }else if(row[0].equals("highLimit")){ //$NON-NLS-1$
                String temp = row[1].replaceAll("\"",""); //$NON-NLS-1$ //$NON-NLS-2$
                _widget.setPropertyValue(BargraphModel.PROP_MAX, new Double(temp).toString()); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("max = "+temp); //$NON-NLS-1$
            }else if(row[0].equals("lowLimit")){ //$NON-NLS-1$
                String temp = row[1].replaceAll("\"",""); //$NON-NLS-1$ //$NON-NLS-2$
                _widget.setPropertyValue(BargraphModel.PROP_MIN, new Double(temp).toString()); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("min = "+temp); //$NON-NLS-1$
            }else if(row[0].equals("clrmod")){ //$NON-NLS-1$
                //TODO: Bargraph --> clrmod
                CentralLogger.getInstance().debug(this, Messages.Bargraph_Clrmod_Debug+row[0]);
            }else{ //Bargraph have no Parameter                
                throw new WrongADLFormatException(Messages.Bargraph_WrongADLFormatException_Parameter_Begin+row[0]+Messages.Bargraph_WrongADLFormatException+bargraph);
            } 
        }
        _widget.setPropertyValue(BargraphModel.PROP_SHOW_MARKS, marksShowStatus); 
        _widget.setPropertyValue(BargraphModel.PROP_SHOW_SCALE,scaleShowStatus);

        final String[] typ = new String[]{"lolo","lo","hi","hihi"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final String[] val = new String[]{"0","25","75","100"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final String[] color= new String[]{"0","1","2","3"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assert( typ.length==val.length&&val.length==color.length);
        for (int i = 0; i < color.length; i++) {
            makeLevel(typ[i],val[i],color[i]);    
        }
        _widget.setPropertyValue(BargraphModel.PROP_MIN, 0.0);
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$.LOPR",Double.class)); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(BargraphModel.PROP_MIN, dynamicsDescriptor );

        _widget.setPropertyValue(BargraphModel.PROP_MAX, 10.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$.HOPR",Double.class)); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(BargraphModel.PROP_MAX, dynamicsDescriptor );

        String postfix = ""; //$NON-NLS-1$
        if(getMonitor()!=null&&getMonitor().getPostfix()!=null){
            postfix=getMonitor().getPostfix();
        }
        _widget.setPropertyValue(BargraphModel.PROP_FILL, 5.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$"+postfix,Double.class)); //$NON-NLS-1$
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
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$.SEVR",Double.class)); //$NON-NLS-1$
        
        _widget.setDynamicsDescriptor(BargraphModel.PROP_DEFAULT_FILL_COLOR, dynamicsDescriptor);

        _widget.setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR, ADLHelper.getRGB(getMonitor().getBclr()));
        dynamicsDescriptor = new DynamicsDescriptor("rule.null"); //$NON-NLS-1$
        ADLHelper.setConnectionState(dynamicsDescriptor);
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$.SEVR",Double.class)); //$NON-NLS-1$
        
        _widget.setDynamicsDescriptor(BargraphModel.PROP_FILLBACKGROUND_COLOR, dynamicsDescriptor);

        _widget.setDynamicsDescriptor(BargraphModel.PROP_COLOR_FOREGROUND, null);
        _widget.setLayer("Bargraph"); //$NON-NLS-1$

        
    }

    /**
     * @param id set id of property. The postfix Level and Color automatically added.
     * @param value set value of Property.
     * @param color set the color of Property.
     */
    private void makeLevel(final String id, final String value, final String color) {

//      <property type="sds.double" id="loloLevel" value="0.2" />
        _widget.setPropertyValue(id+"Level", value); //$NON-NLS-1$
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        String temp = id.toUpperCase();
        if(temp.equals("HI")){
            temp="HIGH";
        }else if(temp.equals("LO")){
            temp="LOW";
        }
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$."+temp,Double.class)); //$NON-NLS-1$
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
