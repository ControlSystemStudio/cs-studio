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

import java.util.Formatter;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.components.model.AbstractChartModel;
import org.csstudio.sds.components.model.WaveformModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.sds.model.optionEnums.BorderStyleEnum;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLBasicAttribute;
import org.csstudio.utility.adlconverter.utility.widgetparts.ADLDynamicAttribute;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.09.2007
 */
public class Waveform extends Widget {

    /**
     * @param widget ADLWidget that describe the Waveform.
     * @param storedDynamicAttribute 
     * @param storedBasicAttribute 
     * @throws WrongADLFormatException 
     */
    public Waveform(final ADLWidget widget, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) throws WrongADLFormatException {
        super(widget, storedBasicAttribute, storedDynamicAttribute);
        
        _widget.setPropertyValue(AbstractChartModel.PROP_SHOW_AXES, 3);
        _widget.setPropertyValue(WaveformModel.PROP_BORDER_STYLE, BorderStyleEnum.RAISED.getIndex());
        
        for (ADLWidget waveformPart : widget.getObjects()) {
            if(waveformPart.getType().equalsIgnoreCase("plotcom")){
                plotcom(waveformPart);
            }else if(waveformPart.getType().startsWith("trace")){
                trace(waveformPart);
            }else if(waveformPart.getType().equalsIgnoreCase("x_axis")){
                xAxis(waveformPart);
            }else if(waveformPart.getType().startsWith("_axis",2)){
                yAxis(waveformPart);
            }
        }
        for (FileLine fileLine : widget.getBody()) {
            String waveform = fileLine.getLine();
            String[] row = waveform.trim().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Bargraph_1);
            }
            if(row[0].equals("count")){ //$NON-NLS-1$
                // TODO: Waveform-->count
                CentralLogger.getInstance().info(this, "Unhandled Property: "+fileLine);
            }else if(row[0].equals("erase")){ //$NON-NLS-1$
                // TODO: Waveform-->erase
                CentralLogger.getInstance().info(this, "Unhandled Property: "+fileLine);
            }else if(row[0].equals("eraseMode")){ //$NON-NLS-1$
                // TODO: Waveform-->eraseMode
                CentralLogger.getInstance().info(this, "Unhandled Property: "+fileLine);
            }else if(row[0].equals("erase_oldest")){ //$NON-NLS-1$
                // TODO: Waveform-->erase_oldest
                CentralLogger.getInstance().info(this, "Unhandled Property: "+fileLine);
            }else if(row[0].equals("style")){ //$NON-NLS-1$
                String style = "1"; // Line //$NON-NLS-1$
                String value = row[1].toLowerCase();
                boolean line = value.contains("line");
                int dot = 2;
                if(value.contains("only")){
                    dot=0;
                }
                _widget.setPropertyValue(WaveformModel.PROP_LINE_CHART, line);
                _widget.setPropertyValue(WaveformModel.PROP_DATA_POINT_DRAWING_STYLE, dot);
            }else if(row[0].equals("trigger")){ //$NON-NLS-1$
            }
            
        }
        // TODO: The wrong Widget for stripchart
    }

    /**
     * @param waveformPart
     * @throws WrongADLFormatException 
     */
    private void yAxis(ADLWidget waveformPart) throws WrongADLFormatException {
        String id ="";
        int start = waveformPart.getType().indexOf('y');
        if(start >=0){
            int stop = waveformPart.getType().indexOf('_', start);
            if(stop>0){
                int idInt = Integer.parseInt(waveformPart.getType().substring(start+1,stop));
                
                if(idInt>1){
                    id = Integer.toString(idInt-1);
                }
            }
        }

        
        for (FileLine fileLine : waveformPart.getBody()) {
            String waveform = fileLine.getLine();
            String[] row = waveform.split("=");
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Bargraph_1);
            }
            String parameter = row[0].trim();
            /**
             * axisStyle="log10"
             * rangeStyle="user-specified"
             * minRange=-60.000000
             * maxRange=60.000000
             */
            if(parameter.equals("axisStyle")){
                if(row[1].replaceAll("\"", "").trim().equalsIgnoreCase("log10")){
                    _widget.setPropertyValue(WaveformModel.PROP_Y_AXIS_SCALING+id, 1);
                }else{
                    _widget.setPropertyValue(WaveformModel.PROP_Y_AXIS_SCALING+id, 0);
                }
            }else if(parameter.equals("rangeStyle")){
                String rangeStyle = row[1].replaceAll("\"", "").trim();
                if(rangeStyle.equalsIgnoreCase("auto-scale")){
                    _widget.setPropertyValue(WaveformModel.PROP_AUTOSCALE+id, true);
                }else{
                    _widget.setPropertyValue(WaveformModel.PROP_AUTOSCALE+id, false);
                }
            }else if(parameter.equals("minRange")){
                double min = Double.parseDouble(row[1].trim());
                _widget.setPropertyValue(WaveformModel.PROP_MIN+id, min);
            }else if(parameter.equals("maxRange")){
                double max = Double.parseDouble(row[1].trim());
                _widget.setPropertyValue(WaveformModel.PROP_MAX+id, max);
            }else{
                CentralLogger.getInstance().info(this, "Unknown Waveform "+waveformPart.getType()+" paramerter: "+fileLine);
            }
        }
    }

    /**
     * @param waveformPart
     * @throws WrongADLFormatException 
     */
    private void xAxis(ADLWidget waveformPart) throws WrongADLFormatException {
        for (FileLine fileLine : waveformPart.getBody()) {
            String waveform = fileLine.getLine();
            String[] row = waveform.split("=");
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Bargraph_1);
            }
            String parameter = row[0].trim();
            /**
             * axisStyle="time"
             * rangeStyle="user-specified"
             * rangeStyle="auto-scale"
             * minRange=1.000000
             * maxRange=3.000000
             * timeFormat="hh:mm"
             */
            if(parameter.equals("axisStyle")){
                //TODO: axisStyle
            }else if(parameter.equals("rangeStyle")){
                // TODO: Ist im SDS nicht für die x Achse Unterstützt.
//                String rangeStyle = row[1].replaceAll("\"", "").trim();
//                if(rangeStyle.equalsIgnoreCase("auto-scale")){
//                    _widget.setPropertyValue(WaveformModel.PROP_AUTO_SCALE, true);
//                }else{
//                    _widget.setPropertyValue(WaveformModel.PROP_AUTO_SCALE, false);
//                }
            }else if(parameter.equals("minRange")){
                // TODO: Für die X-Achse wird es nicht vom SDS unterstützt
//                double min = Double.parseDouble(row[1].trim());
//                _widget.setPropertyValue(WaveformModel.PROP_MIN, min);
            }else if(parameter.equals("maxRange")){
                // TODO: Für die X-Achse wird es nicht vom SDS unterstützt
//                double max = Double.parseDouble(row[1].trim());
//                _widget.setPropertyValue(WaveformModel.PROP_MAX, max);
            }else if(parameter.equals("timeFormat")){
                //TODO: timeFormat
                String xLabel = row[1].replaceAll("\"", "").trim();
                _widget.setPropertyValue(WaveformModel.PROP_X_AXIS_LABEL, xLabel);
                _widget.setPropertyValue(WaveformModel.PROP_SHOW_AXES, 3);
            }else{
                CentralLogger.getInstance().info(this, "Unknown Waveform "+waveformPart.getType()+" paramerter: "+fileLine);
            }
        }
    }

    /**
     * @param waveformPart
     * @throws WrongADLFormatException 
     */
    private void trace(ADLWidget waveformPart) throws WrongADLFormatException {
        int idInt =0;
        int start = waveformPart.getType().indexOf('[');
        if(start >=0){
            int stop = waveformPart.getType().indexOf(']', start);
            if(stop>0){
                idInt = Integer.parseInt(waveformPart.getType().substring(start+1,stop));
            }
        }
        for (FileLine fileLine : waveformPart.getBody()) {
            String waveform = fileLine.getLine();
            String[] row = waveform.split("=");
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Bargraph_1);
            }
            String parameter = row[0].trim();
            /**
             * xdata="CPHLOOP_CAPCAV_ERRXwf"
             * ydata="CPHLOOP_CAPCAV_ERRSwf"
             * data_clr=20
             */
            if(parameter.equals("xdata")){
//                _widget.
                //TODO: trace
            }else if(parameter.equals("ydata")){
                if(idInt<WaveformModel.NUMBER_OF_ARRAYS){
                    String record = row[1].replaceAll("\"", "").trim();
                    if(record.length()>0){
                        String alias = "$channel"+idInt+"$";
                        _widget.addAlias("channel"+idInt, record);
                        if(idInt==0){
                            _widget.setPrimarPv(alias);
                        }
                        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();
                        dynamicsDescriptor.addInputChannel(new ParameterDescriptor(alias, String[].class ));
                        _widget.setDynamicsDescriptor(WaveformModel.dataPropertyId(idInt), dynamicsDescriptor);
                    }
                    //TODO: trace
                }else{
                    Formatter f = new Formatter();
                    f.format("Can not convert correct the Waveform. The Source have to many Wave's. They are only %s possible", WaveformModel.NUMBER_OF_ARRAYS);
                    CentralLogger.getInstance().info(this, f.toString());
                }
            }else if(parameter.equals("data_clr")){
                if(idInt<WaveformModel.NUMBER_OF_ARRAYS){
                    _widget.setPropertyValue(WaveformModel.plotColorPropertyId(idInt), ADLHelper.getRGB(row[1].trim()));
                }else{
                    Formatter f = new Formatter();
                    f.format("Can not convert correct the Waveform. The Source have to many Wave's colors. They are only %s possible", WaveformModel.NUMBER_OF_ARRAYS);
                    CentralLogger.getInstance().info(this, f.toString());
                }
            }else{
                CentralLogger.getInstance().info(this, "Unknown Waveform "+waveformPart.getType()+" paramerter: "+fileLine);
            }
        }
    }

    /**
     * @param waveformPart
     * @throws WrongADLFormatException 
     */
    private void plotcom(ADLWidget waveformPart) throws WrongADLFormatException {
        for (FileLine fileLine : waveformPart.getBody()) {
            String waveform = fileLine.getLine();
            String[] row = waveform.split("=");
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.Bargraph_1);
            }
            String parameter = row[0].trim();
            /**
             * title="$(name):gtr:waveform3"
             * xlabel="Zeit in count"
             * ylabel="U in count"
             * clr=14
             * bclr=31
             */
            if(parameter.equals("title")){
                String name = row[1].replaceAll("\"", "").trim();
                _widget.setPropertyValue(WaveformModel.PROP_LABEL, name);
            }else if(parameter.equals("clr")){
                _widget.setForegroundColor(ADLHelper.getRGB(row[1]));
            }else if(parameter.equals("bclr")){
                _widget.setBackgroundColor(ADLHelper.getRGB(row[1]));
            }else if(parameter.equals("xlabel")){
                String xLabel = row[1].replaceAll("\"", "").trim();
                _widget.setPropertyValue(WaveformModel.PROP_X_AXIS_LABEL,xLabel);
            }else if(parameter.equals("ylabel")){
                String yLabel = row[1].replaceAll("\"", "").trim();
                _widget.setPropertyValue(WaveformModel.PROP_Y_AXIS_LABEL,yLabel);
            }else{
                CentralLogger.getInstance().info(this, "Unknown Waveform "+waveformPart.getType()+" paramerter: "+fileLine);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(WaveformModel.ID);  
    }
}
