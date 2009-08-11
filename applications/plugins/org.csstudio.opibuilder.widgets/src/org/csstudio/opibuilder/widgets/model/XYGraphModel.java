package org.csstudio.opibuilder.widgets.model;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider.PlotMode;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider.UpdateMode;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * The model for XYGraph
 * @author Xihui Chen
 */
public class XYGraphModel extends AbstractWidgetModel {
	
	public class XYGraphCategory implements WidgetPropertyCategory{
		private String name;
		
		public XYGraphCategory(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		public String name() {
			return name;
		}
	}
	
	public enum AxisProperty{
		Y_AXIS("YAxis", "Y Axis"),
		PRIMARY("Primary", "Left/Bottom Side"),
		TITLE("Title", "Axis Title"),
		TITLE_FONT("TitleFont", "Title Font"),
		AXIS_COLOR("AxisColor", "Axis Color"),
		AUTO_SCALE("AutoScale", "Auto Scale"),
		AUTO_SCALE_THRESHOLD("AutoScaleThreshold", "Auto Scale Threshold"),
		LOG("Log", "Log Scale"),
		MAX("Maximum", "Maximum"),
		MIN("Minimum", "Minimum"),
		TIME_FORMAT("TimeFormat", "Time Format"),
		SHOW_GRID("ShowGrid", "Show Grid"),
		GRID_COLOR("GridColor", "Grid Color"),
		DASH_GRID("DashGrid", "Dash Grid Line");		
		
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
		NAME("Name", "Name"),
		PLOTMODE("PlotMode", "Plot Mode"),
		BUFFER_SIZE("BufferSize", "Buffer Size"),
		UPDATE_DELAY("UpdateDelay", "Update Delay"),
		TRIGGER_VALUE("TriggerValue", "Trigger Value"),
		CLEAR_TRACE("ClearTrace", "Clear Plot History"),
		XDATA("XData", "X Data"),
		YDATA("YData", "Y Data"),
		CHRONOLOGICAL("Chronological", "Chronological"),
		YTIMESTAMP("YTimeStamp", "Y Data Timestamp"),
		TRACE_COLOR("TraceColor","Trace Color"),
		XAXIS_INDEX("XAxisIndex", "X Axis Index"),
		YAXIS_INDEX("YAxisIndex", "Y Axis Index"),
		TRACE_TYPE("TraceType", "Trace Type"),
		LINE_WIDTH("LineWidth", "Line Width"),
		POINT_STYLE("PointStyle", "Point Style"),
		POINT_SIZE("PointSize", "Point Size"),
		ANTI_ALIASING("AntiAliasing", "Anti-Aliasing"),
		UPDATE_MODE("UpdateMode", "UpdateMode");
		
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
		"yyyy-MM-dd", "MMMMM d"};
	
	
	/** The ID of the title property. */
	public static final String PROP_TITLE = "Title"; //$NON-NLS-1$
	
	/** The ID of the title font property. */
	public static final String PROP_TITLE_FONT = "TitleFont"; //$NON-NLS-1$
	
	/** The ID of the show legend property. */
	public static final String PROP_SHOW_LEGEND = "ShowLegend"; //$NON-NLS-1$
	
	/** The ID of the show plot area border property. */
	public static final String PROP_SHOW_PLOTAREA_BORDER = "ShowPlotAreaBorder"; //$NON-NLS-1$		
	
	/** The ID of the plot area background color property.*/
	public static final String PROP_PLOTAREA_BACKCOLOR = "PlotAreaBackColor"; //$NON-NLS-1$
	
	/** The ID of the transparent property. */
	public static final String PROP_TRANSPARENT = "Transparent"; //$NON-NLS-1$
	
	/** The ID of the number of axes property. */
	public static final String PROP_AXES_AMOUNT = "AxesAmount"; //$NON-NLS-1$
	
	/** The ID of the number of axes property. */
	public static final String PROP_TRACES_AMOUNT = "TracesAmount"; //$NON-NLS-1$
	
	/** The ID of the show toolbar property. */
	public static final String PROP_SHOW_TOOLBAR = "showToolBar"; //$NON-NLS-1$
		
	/** The default color of the plot area background color property. */
	private static final RGB DEFAULT_PLOTAREA_BACKCOLOR = new RGB(255,255,255);

	/** The default color of the axis color property. */
	private static final RGB DEFAULT_AXIS_COLOR = new RGB(0,0,0);
	
	/** The default color of the grid color property. */
	private static final RGB DEFAULT_GRID_COLOR = new RGB(200,200,200);
	
	/** The default color of the trace color property. */
	private static final RGB DEFAULT_TRACE_COLOR = new RGB(255,0,0);
	
	
	/** The default value of the minimum property. */
	private static final double DEFAULT_MIN = 0;
	
	/** The default value of the maximum property. */	
	private static final double DEFAULT_MAX = 100;	
	
	/** The default value of the buffer size property. */
	private static final int DEFAULT_BUFFER_SIZE = 100;	
	
	/** The maximum allowed buffer size. */
	private static final int MAX_BUFFER_SIZE = 10000;	
	
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
	public static final String ID = "org.csstudio.sns.widgets.XYGraph"; //$NON-NLS-1$	

	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PROP_TITLE, "Title",
				WidgetPropertyCategory.Display, true, ""));	
		addProperty(new FontProperty(PROP_TITLE_FONT, "Title Font",
				WidgetPropertyCategory.Display,true,  new FontData("Arial", 12, SWT.BOLD))); //$NON-NLS-1$
		addProperty(new BooleanProperty(PROP_SHOW_LEGEND, "Show Legend",
				WidgetPropertyCategory.Display,true,  true));		
		addProperty(new BooleanProperty(PROP_SHOW_PLOTAREA_BORDER, "Show Plot Area Border",
				WidgetPropertyCategory.Display,true, false));	
		addProperty(new BooleanProperty(PROP_SHOW_TOOLBAR, "Show Graph Toolbar",
				WidgetPropertyCategory.Display,true, true));
		addProperty(new ColorProperty(PROP_PLOTAREA_BACKCOLOR, "Plot Area Background Color",
				WidgetPropertyCategory.Display,true,  DEFAULT_PLOTAREA_BACKCOLOR));
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent",
				WidgetPropertyCategory.Display,true, false));
		addProperty(new IntegerProperty(PROP_AXES_AMOUNT, "Axes Amount",
				WidgetPropertyCategory.Behavior,true,  2, 2, MAX_AXES_AMOUNT));
		addProperty(new IntegerProperty(PROP_TRACES_AMOUNT, "Traces Amount",
				WidgetPropertyCategory.Behavior, true, 0, 0, MAX_TRACES_AMOUNT));	
		addAxisProperties();
		addTraceProperties();
		
	}
	
	
	private void addAxisProperties(){
		for(int i=0; i < MAX_AXES_AMOUNT; i++){
			for(AxisProperty axisProperty : AxisProperty.values())
				addAxisProperty(axisProperty, i);
		}
	}
	
	private void addAxisProperty(AxisProperty axisProperty, int axisIndex){		
		String propID = makeAxisPropID(axisProperty.propIDPre, axisIndex);
		
		WidgetPropertyCategory category;
		if(axisIndex ==0)
			category = new XYGraphCategory("Primary X Axis (0)");
		else if(axisIndex == 1)
			category = new XYGraphCategory("Primary Y Axis (1)");
		else
			category = new XYGraphCategory("Secondary Axis (" + axisIndex + ")");
		
		switch (axisProperty) {
		case Y_AXIS:
			if(axisIndex < 2)
				break;
			addProperty(new BooleanProperty(propID, axisProperty.toString(), category, true, true));
			break;
		case PRIMARY:
			if(axisIndex < 2)
				break;
			addProperty(new BooleanProperty(propID, axisProperty.toString(), category, true, true));
			break;			
		case TITLE:
			addProperty(new StringProperty(propID, axisProperty.toString(), category, true, category.toString()));
			break;
		case TITLE_FONT:
			addProperty(new FontProperty(propID, axisProperty.toString(), category, true, 
					new FontData("Arial", 9, SWT.NONE)));
			break;
		case AXIS_COLOR:
			addProperty(new ColorProperty(propID, axisProperty.toString(), category, true, DEFAULT_AXIS_COLOR));
			break;
		case AUTO_SCALE_THRESHOLD:
			addProperty(new DoubleProperty(propID, axisProperty.toString(), category,true,  0, 0, 1));
			break;
		case LOG:
			addProperty(new BooleanProperty(propID, axisProperty.toString(), category,true,  false));
			break;
		case AUTO_SCALE:
		case SHOW_GRID:
		case DASH_GRID:
			addProperty(new BooleanProperty(propID, axisProperty.toString(), category, true, true));
			break;	
		case MAX:
			addProperty(new DoubleProperty(propID, axisProperty.toString(), category, true, DEFAULT_MAX));
			break;
		case MIN:
			addProperty(new DoubleProperty(propID, axisProperty.toString(), category,true,  DEFAULT_MIN));
			break;	
		case TIME_FORMAT:
			addProperty(new ComboProperty(propID, 
					axisProperty.toString(), category, true, TIME_FORMAT_ARRAY, 0));
			break;	
		case GRID_COLOR:
			addProperty(new ColorProperty(propID, axisProperty.toString(), category,true, DEFAULT_GRID_COLOR));
			break;		
		default:
			break;
		}
	}
	
	public static String makeAxisPropID(String propIDPre, int index){
		return propIDPre + "Axis"+ index;
	}
	
	private void addTraceProperties(){
		for(int i=0; i < MAX_TRACES_AMOUNT; i++){
			for(TraceProperty traceProperty : TraceProperty.values())
				addTraceProperty(traceProperty, i);
		}
	}
	
	private void addTraceProperty(TraceProperty traceProperty, int traceIndex){		
		String propID = makeTracePropID(traceProperty.propIDPre, traceIndex);		
		WidgetPropertyCategory category = new XYGraphCategory("Trace " + traceIndex);
		switch (traceProperty) {
		case NAME:
			addProperty(new StringProperty(propID, traceProperty.toString(), category, true, category.toString()));
			break;
		case ANTI_ALIASING:
		case CHRONOLOGICAL:
			addProperty(new BooleanProperty(propID, traceProperty.toString(), category, true, true));
			break;
		case BUFFER_SIZE:
			addProperty(new IntegerProperty(propID, 
					traceProperty.toString(), category, true, DEFAULT_BUFFER_SIZE, 0, MAX_BUFFER_SIZE));
			break;
		case CLEAR_TRACE:
			addProperty(new BooleanProperty(propID, traceProperty.toString(), category, true, false));
			break;
		case LINE_WIDTH:
			addProperty(new IntegerProperty(propID, traceProperty.toString(), category, true, 1, 1, 100));
			break;
		case PLOTMODE:
			addProperty(new ComboProperty(propID, traceProperty.toString(), category, true, 
					PlotMode.stringValues(), 0));
			break;
		case POINT_SIZE:
			addProperty(new IntegerProperty(propID, traceProperty.toString(), category,  true, 4, 1, 200));
			break;
		case POINT_STYLE:
			addProperty(new ComboProperty(propID, traceProperty.toString(), category,  true,
					PointStyle.stringValues(), 0));
			break;
		case TRACE_COLOR:
			addProperty(new ColorProperty(propID, traceProperty.toString(), category, true, 
					traceIndex < XYGraph.DEFAULT_TRACES_COLOR.length?
							XYGraph.DEFAULT_TRACES_COLOR[traceIndex].getRGB() : DEFAULT_TRACE_COLOR));
			break;	
		case TRACE_TYPE:
			addProperty(new ComboProperty(propID, traceProperty.toString(), category, true, 
					TraceType.stringValues(), 0));
			break;
		case TRIGGER_VALUE:
			addProperty(new DoubleProperty(propID, traceProperty.toString(), category, true, 0));
			break;
		case UPDATE_DELAY:
			addProperty(new IntegerProperty(propID, traceProperty.toString(), category, true, 0, 0, 655350));
			break;
		case UPDATE_MODE:
			addProperty(new ComboProperty(propID, traceProperty.toString(), category, true, 
					UpdateMode.stringValues(), 0));
			break;
		case XAXIS_INDEX:
			addProperty(new ComboProperty(propID, traceProperty.toString(), category, true, AXES_ARRAY, 0));
			break;
		case XDATA:
		case YDATA:
			addProperty(new DoubleArrayProperty(propID, traceProperty.toString(), category, true, new double[0]));
			break;
		case YAXIS_INDEX:
			addProperty(new ComboProperty(propID, traceProperty.toString(), category, true, AXES_ARRAY, 1));
			break;
		case YTIMESTAMP:
			addProperty(new StringProperty(propID, traceProperty.toString(), category, true, ""));
			break;
		default:
			break;
		}
		
		
	}
	
	
	public static String makeTracePropID(String propIDPre, int index){
		return propIDPre + "Trace" + index;
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
	public FontData getTitleFont() {
		return (FontData) getProperty(PROP_TITLE_FONT).getPropertyValue();
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
		return (RGB) getProperty(PROP_PLOTAREA_BACKCOLOR).getPropertyValue();
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
		return (Integer) getProperty(PROP_AXES_AMOUNT).getPropertyValue();
	}
	
	/**
	 * @return The number of traces.
	 */
	public int getTracesAmount() {
		return (Integer) getProperty(PROP_TRACES_AMOUNT).getPropertyValue();
	}

	@Override
	public String getTypeID() {
		return ID;
	}
}
