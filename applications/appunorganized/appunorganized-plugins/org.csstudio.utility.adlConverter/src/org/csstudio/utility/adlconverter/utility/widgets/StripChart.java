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

import org.csstudio.sds.components.model.AbstractChartModel;
import org.csstudio.sds.components.model.StripChartModel;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.model.DynamicsDescriptor;
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
 * @since 20.09.2007
 */
public class StripChart extends Widget {

    private static final Logger LOG = LoggerFactory.getLogger(StripChart.class);

    /**
     * @param widget ADLWidget that describe the StripChart.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     * @throws WrongADLFormatException
     */
    public StripChart(final ADLWidget widget, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) throws WrongADLFormatException {
        super(widget, storedBasicAttribute, storedDynamicAttribute);

        _widget.setPropertyValue(AbstractChartModel.PROP_SHOW_AXES, 3);
        _widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE, BorderStyleEnum.RAISED.getIndex());
        _widget.setPropertyValue(AbstractChartModel.PROP_AUTOSCALE, true);

        for (ADLWidget srtipChartPart : widget.getObjects()) {
            if(srtipChartPart.getType().equalsIgnoreCase("plotcom")){
                plotcom(srtipChartPart);
            }else if(srtipChartPart.getType().startsWith("pen")){
                pen(srtipChartPart);
            }
        }
        for (FileLine fileLine : widget.getBody()) {
            String ctripChart = fileLine.getLine();
            String[] row = ctripChart.trim().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Bargraph_1);
            }
            if(row[0].equals("period")){ //$NON-NLS-1$
                double period = Double.parseDouble(row[1])/1000;
//                _widget.setPropertyValue(StripChartModel.PROP_X_AXIS_TIMESPAN, period);
                _widget.setPropertyValue(StripChartModel.PROP_UPDATE_INTERVAL, period);
            }else if(row[0].equals("units")){ //$NON-NLS-1$
                _widget.setPropertyValue(AbstractChartModel.PROP_X_AXIS_LABEL, row[1]);
            }

        }
    }

    /**
     * @param stripChartPart
     * @throws WrongADLFormatException
     */
    private void pen(ADLWidget stripChartPart) throws WrongADLFormatException {
        int start = stripChartPart.getType().indexOf("[");
        int end = stripChartPart.getType().indexOf("]");
        String id = stripChartPart.getType().substring(start+1, end);
        int index = Integer.parseInt(id);


        for (FileLine fileLine : stripChartPart.getBody()) {
            String stripChart = fileLine.getLine();
            String[] row = stripChart.split("=");
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Bargraph_1);
            }
            String parameter = row[0].trim();
            /**
             * chan="$(epn_ai)"
             * utilChan="$(epn_ai)_h"
             * clr=62
             */
            if(parameter.equals("chan")){
                DebugHelper.add(this, row[1]);
                row = ADLHelper.cleanString(row[1]);
                if(index==0){
                    _widget.setPrimarPv(row[1]);
                }
                _widget.setPropertyValue(StripChartModel.enablePlotPropertyId(index),true);
                DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();
//                FIXME: Parameter was String[].class
                @SuppressWarnings("restriction")
                ParameterDescriptor parameterDescriptor = new ParameterDescriptor(row[1], "");
                dynamicsDescriptor.addInputChannel(parameterDescriptor);
                _widget.setPropertyValue(StripChartModel.valuePropertyId(index),index);
                _widget.setDynamicsDescriptor(StripChartModel.valuePropertyId(index),dynamicsDescriptor);
            }else if(parameter.equals("utilChan")){
                // not used in SDS
            }else if(parameter.equals("clr")){
                _widget.setPropertyValue(AbstractChartModel.plotColorPropertyId(index), ADLHelper.getRGB(row[1]));
            }else{
                LOG.info("Unknown StripChart {} parameter: {}",stripChartPart.getType(),fileLine);
            }
        }
    }


    /**
     * @param stripChartPart
     * @throws WrongADLFormatException
     */
    private void plotcom(ADLWidget stripChartPart) throws WrongADLFormatException {
        for (FileLine fileLine : stripChartPart.getBody()) {
            String stripChart = fileLine.getLine();
            String[] row = stripChart.split("=");
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Bargraph_1);
            }
            String parameter = row[0].trim();
            /**
             * period=30.000000
             * units="minute"
             */
            if(parameter.equals("title")){
                String name = row[1].replaceAll("\"", "").trim();
                _widget.setPropertyValue(AbstractChartModel.PROP_LABEL, name);
            }else if(parameter.equals("clr")){
                _widget.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, ADLHelper.getRGB(row[1]));
            }else if(parameter.equals("bclr")){
                _widget.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, ADLHelper.getRGB(row[1]));
            }else if(parameter.equals("xlabel")){
                String xLabel = row[1].replaceAll("\"", "").trim();
                _widget.setPropertyValue(AbstractChartModel.PROP_X_AXIS_LABEL,xLabel);
            }else if(parameter.equals("ylabel")){
                String yLabel = row[1].replaceAll("\"", "").trim();
                _widget.setPropertyValue(AbstractChartModel.PROP_Y_AXIS_LABEL,yLabel);
            }else{
                LOG.info("Unknown StripChart {} parameter: {}",stripChartPart.getType(),fileLine);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(StripChartModel.ID);
    }
}
