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

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLBasicAttribute;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLDynamicAttribute;
import org.epics.css.dal.context.ConnectionState;

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
            String[] row = obj.trim().split("=");
            if(row.length!=2){
                throw new WrongADLFormatException("wrong parameter");
            }

            if(row[0].equals("direction")){
//              <property type="sds.boolean" id="orientation" value="false" />
                bool = Boolean.toString(row[1].toLowerCase().equals("up"));
                _widget.setPropertyValue(BargraphModel.PROP_ORIENTATION, bool);
            }else if(row[0].equals("scaleType")){
                //TODO: Barghraph-->scaleType (Not Supported from SDS) 
            }else if(row[0].equals("showAlarmLimits")&&!barOnly){
                if(!row[1].toLowerCase().equals("\"off\"")){
                    marksShowStatus=1; // Bottom / Right
                }
            }else if(row[0].equals("showScale")&&!barOnly){
//              <property type="sds.boolean" id="showValues" value="false" />
                bool = Boolean.toString(row[1].toLowerCase().equals("off"));
                _widget.setPropertyValue(BargraphModel.PROP_SHOW_VALUES, bool);
//              <property type="sds.option" id="marksShowStatus">
//                  <option id="0" />
//              </property>
                if(!row[1].toLowerCase().equals("off")){
                    scaleShowStatus=1; // Bottom / Right
                }
            }else if(row[0].equals("showBar")){
                barOnly=true;
                marksShowStatus=0; // No scale
                _widget.setPropertyValue(BargraphModel.PROP_SHOW_VALUES, false); 
                scaleShowStatus=0; // No scale
//              <property type="sds.integer" id="border.width" value="0" />
                _widget.setPropertyValue(BargraphModel.PROP_BORDER_WIDTH, 0); 
            }else if(row[0].equals("format")){
                //TODO: Bargraph --> format
            }else if(row[0].equals("limitType")){
                //TODO: Bargraph --> limitType
            }else if(row[0].equals("highLimit")){
                //TODO: Bargraph --> highLimit
            }else if(row[0].equals("lowLimit")){
                //TODO: Bargraph --> lowLimit
            }else if(row[0].equals("clrmod")){
                //TODO: Bargraph --> clrmod
            }else{ //Bargraph have no Parameter                
                throw new WrongADLFormatException("wrong parameter: "+row[0]);
            } 
        }
        _widget.setPropertyValue(BargraphModel.PROP_SHOW_MARKS, marksShowStatus); 
        _widget.setPropertyValue(BargraphModel.PROP_SHOW_SCALE,scaleShowStatus);

        final String[] typ = new String[]{"lolo","lo","hi","hihi"};
        final String[] val = new String[]{"0","25","75","100"};
        final String[] color= new String[]{"0","1","2","3"};
        assert( typ.length==val.length&&val.length==color.length);
        for (int i = 0; i < color.length; i++) {
            makeLevel(typ[i],val[i],color[i]);    
        }
        _widget.setPropertyValue(BargraphModel.PROP_MIN, 0.0);
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection");
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$.LOPR",Double.class));
        _widget.setDynamicsDescriptor(BargraphModel.PROP_MIN, dynamicsDescriptor );

        _widget.setPropertyValue(BargraphModel.PROP_MAX, 10.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection");
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$.HOPR",Double.class));
        _widget.setDynamicsDescriptor(BargraphModel.PROP_MAX, dynamicsDescriptor );

        String postfix = "";
        if(getMonitor()!=null&&getMonitor().getPostfix()!=null){
            postfix=getMonitor().getPostfix();
        }
        _widget.setPropertyValue(BargraphModel.PROP_FILL, 5.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection");
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$"+postfix,Double.class));
        _widget.setDynamicsDescriptor(BargraphModel.PROP_FILL, dynamicsDescriptor );

//        <property type="sds.boolean" id="value_representation" value="true" />
        if(bargraph.isType("bar")){
            _widget.setPropertyValue(BargraphModel.PROP_SHOW_ONLY_VALUE, false);
        }else if(bargraph.isType("indicator")){
            _widget.setPropertyValue(BargraphModel.PROP_SHOW_ONLY_VALUE, true);
        }else {
            throw new WrongADLFormatException("wrong bargraph type");
        }
        
        _widget.setPropertyValue(BargraphModel.PROP_DEFAULT_FILL_COLOR, ADLHelper.getRGB(getMonitor().getClr()));
        dynamicsDescriptor = new DynamicsDescriptor("org.css.sds.color.default_epics_alarm_foreground");
        ADLHelper.setConnectionState(dynamicsDescriptor);
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$.SEVR",Double.class));
        
        _widget.setDynamicsDescriptor(BargraphModel.PROP_DEFAULT_FILL_COLOR, dynamicsDescriptor);

        _widget.setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR, ADLHelper.getRGB(getMonitor().getBclr()));
        dynamicsDescriptor = new DynamicsDescriptor("rule.null");
        ADLHelper.setConnectionState(dynamicsDescriptor);
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$.SEVR",Double.class));
        
        _widget.setDynamicsDescriptor(BargraphModel.PROP_FILLBACKGROUND_COLOR, dynamicsDescriptor);

        _widget.setDynamicsDescriptor(BargraphModel.PROP_COLOR_FOREGROUND, null);
        _widget.setLayer("Bargraph");

        
    }

    /**
     * @param id set id of property. The postfix Level and Color automatically added.
     * @param value set value of Property.
     * @param color set the color of Property.
     */
    private void makeLevel(final String id, final String value, final String color) {

//      <property type="sds.double" id="loloLevel" value="0.2" />
        _widget.setPropertyValue(id+"Level", value);
//      <dynamicsDescriptor ruleId="directConnection">
//          <inputChannel name="dal-epics://&lt;&lt;Channel&gt;&gt;" type="java.lang.Double" />
//      </dynamicsDescriptor>
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection");
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$."+id.toUpperCase(),Double.class));
        _widget.setDynamicsDescriptor(id+"Level", dynamicsDescriptor );
//      <property type="sds.color" id="loColor">
//          <color red="255" green="100" blue="100" />
//      </property>
        _widget.setPropertyValue(id+"Color", ADLHelper.getRGB(color));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel("org.csstudio.sds.components.Bargraph");
    }

}
