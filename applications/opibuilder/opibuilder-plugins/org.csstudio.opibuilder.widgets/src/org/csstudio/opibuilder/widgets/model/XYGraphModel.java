/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;


import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.NameDefinedCategory;
import org.csstudio.opibuilder.properties.PVNameProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.util.UpgradeUtil;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider.PlotMode;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider.UpdateMode;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.osgi.framework.Version;

/**
 * The model for XYGraph
 * @author Xihui Chen
 */
public class XYGraphModel extends AbstractPVWidgetModel {



    public enum AxisProperty{
        Y_AXIS("y_axis", "Y Axis"),
        VISIBLE("visible", "Visible"),
        PRIMARY("left_bottom_side", "Left/Bottom Side"),
        TITLE("axis_title", "Axis Title"),
        TITLE_FONT("title_font", "Title Font"),
        SCALE_FONT("scale_font", "Scale Font"),
        AXIS_COLOR("axis_color", "Axis Color"),
        AUTO_SCALE("auto_scale", "Auto Scale"),
        AUTO_SCALE_THRESHOLD("auto_scale_threshold", "Auto Scale Threshold"),
        LOG("log_scale", "Log Scale"),
        MAX("maximum", "Maximum"),
        MIN("minimum", "Minimum"),
        TIME_FORMAT("time_format", "Time Format"),
        SHOW_GRID("show_grid", "Show Grid"),
        GRID_COLOR("grid_color", "Grid Color"),
        DASH_GRID("dash_grid_line", "Dash Grid Line"),
        SCALE_FORMAT("scale_format", "Scale Format");

        public String propIDPre;
        public String description;

        private AxisProperty(String propertyIDPrefix, String description) {
            this.propIDPre = propertyIDPrefix;
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public enum TraceProperty{
        NAME("name", "Name"),
        PLOTMODE("plot_mode", "Plot Mode"),
        BUFFER_SIZE("buffer_size", "Buffer Size"),
        UPDATE_DELAY("update_delay", "Update Delay"),
        //TRIGGER_VALUE("trigger_value", "Trigger Value"),
        //CLEAR_TRACE("clear_trace", "Clear Plot History"),
        XPV("x_pv", "X PV"),
        YPV("y_pv", "Y PV"),
        XPV_VALUE("x_pv_value", "X PV Value"),
        YPV_VALUE("y_pv_value", "Y PV Value"),
        //CHRONOLOGICAL("chronological", "Chronological"),
        TRACE_COLOR("trace_color","Trace Color"),
        XAXIS_INDEX("x_axis_index", "X Axis Index"),
        YAXIS_INDEX("y_axis_index", "Y Axis Index"),
        TRACE_TYPE("trace_type", "Trace Type"),
        LINE_WIDTH("line_width", "Line Width"),
        POINT_STYLE("point_style", "Point Style"),
        POINT_SIZE("point_size", "Point Size"),
        ANTI_ALIAS("anti_alias", "Anti Alias"),
        UPDATE_MODE("update_mode", "Update Mode"),
        CONCATENATE_DATA("concatenate_data", "Concatenate Data"),
        VISIBLE("visible", "Visible");
        public String propIDPre;
        public String description;

        private TraceProperty(String propertyIDPrefix, String description) {
            this.propIDPre = propertyIDPrefix;
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public final static String[] TIME_FORMAT_ARRAY = new String[]{
        "None", "yyyy-MM-dd\nHH:mm:ss",  "yyyy-MM-dd\nHH:mm:ss.SSS", "HH:mm:ss", "HH:mm:ss.SSS", "HH:mm",
        "yyyy-MM-dd", "MMMMM d", "Auto"};


    /** The ID of the title property. */
    public static final String PROP_TITLE = "title"; //$NON-NLS-1$

    /** The ID of the title font property. */
    public static final String PROP_TITLE_FONT = "title_font"; //$NON-NLS-1$

    /** The ID of the show legend property. */
    public static final String PROP_SHOW_LEGEND = "show_legend"; //$NON-NLS-1$

    /** The ID of the show plot area border property. */
    public static final String PROP_SHOW_PLOTAREA_BORDER = "show_plot_area_border"; //$NON-NLS-1$

    /** The ID of the plot area background color property.*/
    public static final String PROP_PLOTAREA_BACKCOLOR = "plot_area_background_color"; //$NON-NLS-1$

    /** The ID of the transparent property. */
    public static final String PROP_TRANSPARENT = "transparent"; //$NON-NLS-1$

    /** The ID of the number of axes property. */
    public static final String PROP_AXIS_COUNT = "axis_count"; //$NON-NLS-1$

    /** The ID of the number of axes property. */
    public static final String PROP_TRACE_COUNT = "trace_count"; //$NON-NLS-1$

    /** The ID of the show toolbar property. */
    public static final String PROP_SHOW_TOOLBAR = "show_toolbar"; //$NON-NLS-1$

    public static final String PROP_TRIGGER_PV = "trigger_pv"; //$NON-NLS-1$

    public static final String PROP_TRIGGER_PV_VALUE = "trigger_pv_value"; //$NON-NLS-1$

    /** The default color of the plot area background color property. */
    private static final RGB DEFAULT_PLOTAREA_BACKCOLOR = new RGB(255,255,255);

    /** The default color of the axis color property. */
    private static final RGB DEFAULT_AXIS_COLOR = new RGB(0,0,0);

    /** The default color of the grid color property. */
    private static final RGB DEFAULT_GRID_COLOR = new RGB(200,200,200);

    /** The default value of the minimum property. */
    private static final double DEFAULT_MIN = 0;

    /** The default value of the maximum property. */
    private static final double DEFAULT_MAX = 100;

    /** The default value of the buffer size property. */
    private static final int DEFAULT_BUFFER_SIZE = 100;

    /** The maximum allowed buffer size. */
    private static final int MAX_BUFFER_SIZE = 10000000;

    public static final int MAX_AXES_AMOUNT = 4;

    public static final int MAX_TRACES_AMOUNT = 20;

    public final static String[] AXES_ARRAY = new String[MAX_AXES_AMOUNT];
    {
        AXES_ARRAY[0] = "Primary X Axis (0)";
        AXES_ARRAY[1] = "Primary Y Axis (1)";
        for(int i=2; i<MAX_AXES_AMOUNT; i++)
            AXES_ARRAY[i] = "Secondary Axis (" + i + ")";
    }
    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.opibuilder.widgets.xyGraph"; //$NON-NLS-1$

    /** The default value of the height property. */
    private static final int DEFAULT_HEIGHT = 250;

    /** The default value of the width property. */
    private static final int DEFAULT_WIDTH = 400;

    public XYGraphModel() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setForegroundColor(CustomMediaFactory.COLOR_BLUE);
        setTooltip("$(trace_0_y_pv)\n$(trace_0_y_pv_value)");

    }

    @Override
    protected void configureProperties() {
        addPVProperty(new PVNameProperty(PROP_TRIGGER_PV, "Trigger PV",
                WidgetPropertyCategory.Behavior, ""),
                new PVValueProperty(PROP_TRIGGER_PV_VALUE, null));
        addProperty(new StringProperty(PROP_TITLE, "Title",
                WidgetPropertyCategory.Display, ""));
        addProperty(new FontProperty(PROP_TITLE_FONT, "Title Font",
                WidgetPropertyCategory.Display, MediaService.DEFAULT_BOLD_FONT)); //$NON-NLS-1$
        addProperty(new BooleanProperty(PROP_SHOW_LEGEND, "Show Legend",
                WidgetPropertyCategory.Display,true));
        addProperty(new BooleanProperty(PROP_SHOW_PLOTAREA_BORDER, "Show Plot Area Border",
                WidgetPropertyCategory.Display,false));
        addProperty(new BooleanProperty(PROP_SHOW_TOOLBAR, "Show Toolbar",
                WidgetPropertyCategory.Display,true));
        addProperty(new ColorProperty(PROP_PLOTAREA_BACKCOLOR, "Plot Area Background Color",
                WidgetPropertyCategory.Display,DEFAULT_PLOTAREA_BACKCOLOR));
        addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent",
                WidgetPropertyCategory.Display,false));
        addProperty(new IntegerProperty(PROP_AXIS_COUNT, "Axis Count",
                WidgetPropertyCategory.Behavior,2,  2, MAX_AXES_AMOUNT));
        addProperty(new IntegerProperty(PROP_TRACE_COUNT, "Trace Count",
                WidgetPropertyCategory.Behavior, 1, 0, MAX_TRACES_AMOUNT));
        addAxisProperties();
        addTraceProperties();
        setPropertyVisible(PROP_FONT, false);
    }

    @Override
    public void processVersionDifference(Version boyVersionOnFile) {
        super.processVersionDifference(boyVersionOnFile);
        if(UpgradeUtil.VERSION_WITH_PVMANAGER.compareTo(boyVersionOnFile)>0){
            setPropertyValue(PROP_TRIGGER_PV,
                    UpgradeUtil.convertUtilityPVNameToPM(
                            (String) getPropertyValue(PROP_TRIGGER_PV)));

            for(int i=0; i < MAX_TRACES_AMOUNT; i++){
                String traceXPVPropId = makeTracePropID(TraceProperty.XPV.propIDPre, i);
                setPropertyValue(traceXPVPropId,
                        UpgradeUtil.convertUtilityPVNameToPM(
                                (String) getPropertyValue(traceXPVPropId)));

                String traceYPVPropId = makeTracePropID(TraceProperty.YPV.propIDPre, i);
                setPropertyValue(traceYPVPropId,
                        UpgradeUtil.convertUtilityPVNameToPM(
                                (String) getPropertyValue(traceYPVPropId)));
            }
        }
    }


    private void addAxisProperties(){
        for(int i=0; i < MAX_AXES_AMOUNT; i++){
            WidgetPropertyCategory category;
            if(i ==0)
                category = new NameDefinedCategory("Primary X Axis (0)");
            else if(i == 1)
                category = new NameDefinedCategory("Primary Y Axis (1)");
            else
                category = new NameDefinedCategory("Secondary Axis (" + i + ")");
            for(AxisProperty axisProperty : AxisProperty.values())
                addAxisProperty(axisProperty, i, category);
        }
    }

    private void addAxisProperty(AxisProperty axisProperty, int axisIndex, WidgetPropertyCategory category){
        String propID = makeAxisPropID(axisProperty.propIDPre, axisIndex);

        switch (axisProperty) {
        case Y_AXIS:
            if(axisIndex < 2)
                break;
            addProperty(new BooleanProperty(propID, axisProperty.toString(), category, true));
            break;
        case PRIMARY:
            if(axisIndex < 2)
                break;
            addProperty(new BooleanProperty(propID, axisProperty.toString(), category, true));
            break;
        case TITLE:
            addProperty(new StringProperty(propID, axisProperty.toString(), category, category.toString()));
            break;
        case TITLE_FONT:
            addProperty(new FontProperty(
                    propID, axisProperty.toString(), category, MediaService.DEFAULT_BOLD_FONT)); //$NON-NLS-1$
            break;
        case SCALE_FONT:
            addProperty(new FontProperty(propID, axisProperty.toString(), category,
                    MediaService.DEFAULT_FONT));
            break;
        case AXIS_COLOR:
            addProperty(new ColorProperty(propID, axisProperty.toString(), category, DEFAULT_AXIS_COLOR));
            break;
        case AUTO_SCALE_THRESHOLD:
            addProperty(new DoubleProperty(propID, axisProperty.toString(), category,0,  0, 1));
            break;
        case LOG:
            addProperty(new BooleanProperty(propID, axisProperty.toString(), category,false));
            break;
        case AUTO_SCALE:
        case SHOW_GRID:
        case DASH_GRID:
            addProperty(new BooleanProperty(propID, axisProperty.toString(), category, true));
            break;
        case MAX:
            addProperty(new DoubleProperty(propID, axisProperty.toString(), category, DEFAULT_MAX));
            break;
        case MIN:
            addProperty(new DoubleProperty(propID, axisProperty.toString(), category,DEFAULT_MIN));
            break;
        case TIME_FORMAT:
            addProperty(new ComboProperty(propID,
                    axisProperty.toString(), category, TIME_FORMAT_ARRAY, 0));
            break;
        case GRID_COLOR:
            addProperty(new ColorProperty(propID, axisProperty.toString(), category,DEFAULT_GRID_COLOR));
            break;
        case VISIBLE:
            addProperty(new BooleanProperty(propID, axisProperty.toString(), category, true));
            break;
        case SCALE_FORMAT:
            addProperty(new StringProperty(propID, axisProperty.toString(), category, "")); //$NON-NLS-1$
            break;
        default:
            break;
        }
    }

    public static String makeAxisPropID(String propIDPre, int index){
        return "axis_" + index + "_" + propIDPre; //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void addTraceProperties(){
        for(int i=0; i < MAX_TRACES_AMOUNT; i++){
            for(TraceProperty traceProperty : TraceProperty.values())
                addTraceProperty(traceProperty, i);
        }
    }

    private void addTraceProperty(TraceProperty traceProperty, int traceIndex){
        String propID = makeTracePropID(traceProperty.propIDPre, traceIndex);
        WidgetPropertyCategory category = new NameDefinedCategory("Trace " + traceIndex);
        switch (traceProperty) {
        case NAME:
            addProperty(new StringProperty(propID, traceProperty.toString(), category,
                    "$(" + makeTracePropID(TraceProperty.YPV.propIDPre, traceIndex) + ")"));
            break;
        case ANTI_ALIAS:
//        case CHRONOLOGICAL:
            addProperty(new BooleanProperty(propID, traceProperty.toString(), category, true));
            break;
        case BUFFER_SIZE:
            addProperty(new IntegerProperty(propID,
                    traceProperty.toString(), category, DEFAULT_BUFFER_SIZE, 1, MAX_BUFFER_SIZE));
            break;
        case CONCATENATE_DATA:
            addProperty(new BooleanProperty(propID, traceProperty.toString(), category, true));
            break;
        //case CLEAR_TRACE:
        //    addProperty(new BooleanProperty(propID, traceProperty.toString(), category, false));
        //    break;
        case LINE_WIDTH:
            addProperty(new IntegerProperty(propID, traceProperty.toString(), category, 1, 1, 100));
            break;
        case PLOTMODE:
            addProperty(new ComboProperty(propID, traceProperty.toString(), category, PlotMode.stringValues(),
                    0));
            break;
        case POINT_SIZE:
            addProperty(new IntegerProperty(propID, traceProperty.toString(), category,  4, 1, 200));
            break;
        case POINT_STYLE:
            addProperty(new ComboProperty(propID, traceProperty.toString(), category,  PointStyle.stringValues(),
                    0));
            break;
        case TRACE_COLOR:
            addProperty(new ColorProperty(propID, traceProperty.toString(), category,
                    XYGraph.DEFAULT_TRACES_COLOR[traceIndex%XYGraph.DEFAULT_TRACES_COLOR.length]));
            break;
        case TRACE_TYPE:
            addProperty(new ComboProperty(propID, traceProperty.toString(), category, TraceType.stringValues(),
                    0));
            break;
    //    case TRIGGER_VALUE:
    //        addProperty(new DoubleProperty(propID, traceProperty.toString(), category, 0));
    //        break;
        case UPDATE_DELAY:
            addProperty(new IntegerProperty(propID, traceProperty.toString(), category, 100, 0, 655350));
            break;
        case UPDATE_MODE:
            addProperty(new ComboProperty(propID, traceProperty.toString(), category, UpdateMode.stringValues(),
                    0));
            break;
        case XAXIS_INDEX:
            addProperty(new ComboProperty(propID, traceProperty.toString(), category, AXES_ARRAY, 0));
            break;
        case XPV:
            addPVProperty(new PVNameProperty(propID, traceProperty.toString(), category, ""),
                    new PVValueProperty(makeTracePropID(TraceProperty.XPV_VALUE.propIDPre, traceIndex), null));
            break;
        case YPV:
            addPVProperty(new PVNameProperty(propID, traceProperty.toString(), category,
                    traceIndex == 0 ? "$(" + PROP_PVNAME + ")" : ""), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    new PVValueProperty(makeTracePropID(TraceProperty.YPV_VALUE.propIDPre, traceIndex), null));
            break;
        case YAXIS_INDEX:
            addProperty(new ComboProperty(propID, traceProperty.toString(), category, AXES_ARRAY, 1));
            break;
        case VISIBLE:
            addProperty(new BooleanProperty(propID, traceProperty.toString(), category, true));
            break;
        default:
            break;
        }


    }


    public static String makeTracePropID(String propIDPre, int index){
        return "trace_" +index + "_" + propIDPre; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return (String) getProperty(PROP_TITLE).getPropertyValue();
    }

    /**
     * Return the title font.
     *
     * @return The title font.
     */
    public OPIFont getTitleFont() {
        return (OPIFont) getProperty(PROP_TITLE_FONT).getPropertyValue();
    }

    /**
     * @return true if the plot area border should be shown, false otherwise
     */
    public boolean isShowPlotAreaBorder() {
        return (Boolean) getProperty(PROP_SHOW_PLOTAREA_BORDER).getPropertyValue();
    }


    /**
     * @return the plot area background color
     */
    public RGB getPlotAreaBackColor() {
        return getRGBFromColorProperty(PROP_PLOTAREA_BACKCOLOR);
    }

    /**
     * @return true if the XY Graph is transparent, false otherwise
     */
    public boolean isTransprent() {
        return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
    }

    /**
     * @return true if the legend should be shown, false otherwise
     */
    public boolean isShowLegend() {
        return (Boolean) getProperty(PROP_SHOW_LEGEND).getPropertyValue();
    }

    /**
     * @return true if the legend should be shown, false otherwise
     */
    public boolean isShowToolbar() {
        return (Boolean) getProperty(PROP_SHOW_TOOLBAR).getPropertyValue();
    }

    /**
     * @return The number of axes.
     */
    public int getAxesAmount() {
        return (Integer) getProperty(PROP_AXIS_COUNT).getPropertyValue();
    }

    /**
     * @return The number of traces.
     */
    public int getTracesAmount() {
        return (Integer) getProperty(PROP_TRACE_COUNT).getPropertyValue();
    }

    @Override
    public String getTypeID() {
        return ID;
    }
}
