/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import java.util.ArrayList;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.XYGraphModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLPlotTrace;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLPlotcom;
import org.csstudio.utility.adlparser.fileParser.widgets.CartesianPlot;
import org.eclipse.swt.graphics.RGB;

public class CartesianPlot2Model extends AbstractADL2Model {
//    XYGraphModel graphModel = new XYGraphModel();

    public CartesianPlot2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
        super(adlWidget, colorMap, parentModel);
    }

    /**
     * @param adlWidget
     * @param colorMap
     */
    @Override
    public void processWidget(ADLWidget adlWidget) {
        CartesianPlot plotWidget = new CartesianPlot(adlWidget);
        if (plotWidget != null) {
            setADLObjectProps(plotWidget, widgetModel);
        }
        //Add Title & X/Y Labels
        ADLPlotcom plotcom = plotWidget.getAdlPlotcom();
        if (plotcom != null ) {
            widgetModel.setPropertyValue(XYGraphModel.PROP_TITLE, plotcom.getTitle());
            widgetModel.setPropertyValue("axis_0_axis_title", plotcom.getXLabel());
            widgetModel.setPropertyValue("axis_1_axis_title", plotcom.getYLabel());
            //TODO set foreground and background color
        }
        //Add Trace data to CartesianPlot2Model
        ArrayList<ADLPlotTrace> traces = plotWidget.getTraces();
        if (traces.size() > 0){
            widgetModel.setPropertyValue(XYGraphModel.PROP_TRACE_COUNT, traces.size());
            for (int ii = 0; ii< traces.size(); ii++){
                String tracePropertyPrefix = new String("trace_"+ii+"_");
                ADLPlotTrace trace = traces.get(ii);
                widgetModel.setPropertyValue(new String(tracePropertyPrefix+"x_pv"), trace.getxData());
                widgetModel.setPropertyValue(new String(tracePropertyPrefix+"y_pv"), trace.getyData());
                widgetModel.setPropertyValue(new String(tracePropertyPrefix+"trace_color"), colorMap[trace.getDataColor()]);

                // MEDM cartesian plot and BOY XY Graph support different modes:
                // 1) Plot one 'Y' array PV over array index
                // 2) Plot 'X' and 'Y' array PVs over each other
                // 3) Plot scalar 'X' PV over array index, using fixed size buffer
                // 4) .. more
                // Some behavior is based on configuration,
                // other behavior based on type of received PV data,
                // which can only be determined at runtime.

                // The following right now support 1 and 2,
                // auto-sizing the "buffer_size" to match the received array PV data

                // Leave plot_mode at default 'LAST_N'
//                widgetModel.setPropertyValue(new String(tracePropertyPrefix+"plot_mode"), plotWidget.getPlotMode());

                // 'count' isn't fully handled.
                // MEDM allows both a 'count' (number?) and a 'countpvname' (name of PV that provides count)
                // This code always sets the BOY buffer_size to 0, which enables auto-sizing based
                // on x_pv and y_pv being waveform PVs.

                // Auto-size to array PV data
                widgetModel.setPropertyValue(tracePropertyPrefix+"buffer_size",  Integer.valueOf(0));
//                try {
//                    widgetModel.setPropertyValue(new String(tracePropertyPrefix+"buffer_size"),  Integer.parseInt(plotWidget.getCount()));
//                }
//                catch (NumberFormatException ex){
//                    System.out.println("***CartesianPlot2Model - Cannot set buffer size. Count is set to " + plotWidget.getCount() );
//                    System.out.println("***This may be a channel name.  This is not yet supported");
//                    //TODO Add ability to get buffer size to XYGraph
//                }
            }
        }


        //TODO Add Point Style to CartesianPlot2Model
        //TODO CartesianPlot2Model - Add TriggerChannel
        //TODO CartesianPlot2Model - Add EraseChannel.  Not supported by XYGraph.
        //TODO CartesianPlot2Model - Add EraseMode.  Not supported by XYGraph.
    }

    @Override
    public void makeModel(ADLWidget adlWidget,
            AbstractContainerModel parentModel) {
        widgetModel = new XYGraphModel();
        parentModel.addChild(widgetModel, true);
    }
}
