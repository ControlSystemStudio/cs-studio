package org.csstudio.opibuilder.widgets.editparts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.XYGraphModel;
import org.csstudio.opibuilder.widgets.model.XYGraphModel.AxisProperty;
import org.csstudio.opibuilder.widgets.model.XYGraphModel.TraceProperty;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider.PlotMode;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider.UpdateMode;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**The XYGraph editpart
 * @author Xihui Chen
 *
 */
public class XYGraphEditPart extends AbstractWidgetEditPart {

	private List<Axis> axisList;
	private List<Trace> traceList;
	
	@Override
	public XYGraphModel getCastedModel() {
		return (XYGraphModel)getModel();
	}
	@Override
	protected IFigure doCreateFigure() {
		final XYGraphModel model = getCastedModel();
		ToolbarArmedXYGraph xyGraphFigure = new ToolbarArmedXYGraph();
		XYGraph xyGraph = xyGraphFigure.getXYGraph();
		xyGraph.setTitle(model.getTitle());
		xyGraph.setTitleFont(CustomMediaFactory.getInstance().getFont(model.getTitleFont()));
		xyGraph.getPlotArea().setShowBorder(model.isShowPlotAreaBorder());
		xyGraph.getPlotArea().setBackgroundColor(
				CustomMediaFactory.getInstance().getColor(model.getPlotAreaBackColor()));
		xyGraph.setShowLegend(model.isShowLegend());
		xyGraphFigure.setShowToolbar(model.isShowToolbar());
		xyGraphFigure.setTransparent(model.isTransprent());
		axisList = new ArrayList<Axis>();
		axisList.add(xyGraph.primaryXAxis);
		axisList.add(xyGraph.primaryYAxis);
		traceList = new ArrayList<Trace>();
		//init all axes
		for(int i=0; i<XYGraphModel.MAX_AXES_AMOUNT; i++){			
			if(i>=2){
				axisList.add(new Axis("", true));
				if(i<model.getAxesAmount())
					xyGraphFigure.getXYGraph().addAxis(axisList.get(i));
			}
			for(AxisProperty axisProperty : AxisProperty.values()){
				//there is no primary and y-axis property for primary axes. 
				if(i<2 && (axisProperty == AxisProperty.PRIMARY 
						|| axisProperty == AxisProperty.Y_AXIS)){
					continue;
				}
				String propID = XYGraphModel.makeAxisPropID(
					axisProperty.propIDPre, i);
				setAxisProperty(axisList.get(i), axisProperty, 
						model.getProperty(propID).getPropertyValue());
			}			
		}
		
		//init all traces
		for(int i=0; i<XYGraphModel.MAX_TRACES_AMOUNT; i++){		
			traceList.add(new Trace("", xyGraph.primaryXAxis, xyGraph.primaryYAxis, 
					new  CircularBufferDataProvider(false)));			
			if(i<model.getTracesAmount())
					xyGraph.addTrace(traceList.get(i));
			
			for(TraceProperty traceProperty : TraceProperty.values()){				
				String propID = XYGraphModel.makeTracePropID(
					traceProperty.propIDPre, i);				
				setTraceProperty(traceList.get(i), traceProperty, 
						model.getProperty(propID).getPropertyValue());
			}			
		}
		
		return xyGraphFigure;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		registerAxisPropertyChangeHandlers();
		registerTracePropertyChangeHandlers();
		//Title
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
				graph.getXYGraph().setTitle((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(XYGraphModel.PROP_TITLE, handler);	
		
		//Title Font
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
				graph.getXYGraph().setTitleFont(CustomMediaFactory.getInstance().getFont((FontData)newValue));
				return true;
			}
		};
		setPropertyChangeHandler(XYGraphModel.PROP_TITLE_FONT, handler);	
		
		//Show plot area border
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
				graph.getXYGraph().getPlotArea().setShowBorder((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(XYGraphModel.PROP_SHOW_PLOTAREA_BORDER, handler);	
		
		//Plot area background color
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
				graph.getXYGraph().getPlotArea().setBackgroundColor(
						CustomMediaFactory.getInstance().getColor((RGB) newValue));
				return true;
			}
		};
		setPropertyChangeHandler(XYGraphModel.PROP_PLOTAREA_BACKCOLOR, handler);	
		
		//Transparent
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
				graph.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(XYGraphModel.PROP_TRANSPARENT, handler);	
		
		
		//Show legend
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
				graph.getXYGraph().setShowLegend((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(XYGraphModel.PROP_SHOW_LEGEND, handler);	
		
		//Show Toolbar
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
				graph.setShowToolbar((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(XYGraphModel.PROP_SHOW_TOOLBAR, handler);			
		
		registerAxesAmountChangeHandler();
		registerTraceAmountChangeHandler();
		
	}

	private void registerAxesAmountChangeHandler(){
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {				
				XYGraphModel model = (XYGraphModel)getModel();
				XYGraph xyGraph = ((ToolbarArmedXYGraph)refreshableFigure).getXYGraph();
				int currentAxisAmount = xyGraph.getAxisList().size();
				//add axis
				if((Integer)newValue > currentAxisAmount){
					for(int i=0; i<(Integer)newValue - currentAxisAmount; i++){	
						for(AxisProperty axisProperty : AxisProperty.values()){				
							String propID = XYGraphModel.makeAxisPropID(
								axisProperty.propIDPre, i + currentAxisAmount);
							model.addProperty(propID, 
								model.getTempRemovedProperty(propID));	
						}							
						xyGraph.addAxis(axisList.get(i+currentAxisAmount));
					}						
				}else if((Integer)newValue < currentAxisAmount){ //remove axis
					for(int i=0; i<currentAxisAmount - (Integer)newValue; i++){
						for(AxisProperty axisProperty : AxisProperty.values()){				
							String propID = XYGraphModel.makeAxisPropID(
								axisProperty.propIDPre, i+(Integer)newValue);
							model.tempRemoveProperty(propID);
						}						
						xyGraph.removeAxis(axisList.get(i+(Integer)newValue));
					}					
				}
				return true;
			}			
		};
		setPropertyChangeHandler(XYGraphModel.PROP_AXES_AMOUNT, handler);
	}
	
	
	private void registerAxisPropertyChangeHandlers(){
		XYGraphModel model = (XYGraphModel)getModel();
		//set prop handlers and init all the potential axes
		for(int i=0; i<XYGraphModel.MAX_AXES_AMOUNT; i++){			
			
			for(AxisProperty axisProperty : AxisProperty.values()){
				//there is no primary and y-axis property for primary axes. 
				if(i<2 && (axisProperty == AxisProperty.PRIMARY 
						|| axisProperty == AxisProperty.Y_AXIS)){
					continue;
				}
				String propID = XYGraphModel.makeAxisPropID(
					axisProperty.propIDPre, i);
				IWidgetPropertyChangeHandler handler = new AxisPropertyChangeHandler(i, axisProperty);
				setPropertyChangeHandler(propID, handler);				
			}			
		}
		
		for(int i=XYGraphModel.MAX_AXES_AMOUNT -1; i>= model.getAxesAmount(); i--){
			for(AxisProperty axisProperty : AxisProperty.values()){		
				String propID = XYGraphModel.makeAxisPropID(
					axisProperty.propIDPre, i);
				model.tempRemoveProperty(propID);
			}
		}
	}	
	
	private void setAxisProperty(Axis axis, AxisProperty axisProperty, Object newValue){
			switch (axisProperty) {
			case AUTO_SCALE:
				axis.setAutoScale((Boolean)newValue);
				break;
			case TITLE:
				axis.setTitle((String)newValue);		
				break;
			case AUTO_SCALE_THRESHOLD:
				axis.setAutoScaleThreshold((Double)newValue);
				break;
			case AXIS_COLOR:
				axis.setForegroundColor(CustomMediaFactory.getInstance().getColor((RGB)newValue));
				break;
			case DASH_GRID:
				axis.setDashGridLine((Boolean)newValue);
				break;
			case GRID_COLOR:
				axis.setMajorGridColor(CustomMediaFactory.getInstance().getColor((RGB)newValue));
				break;
			case LOG:
				axis.setLogScale((Boolean)newValue);
				break;
			case MAX:
				axis.setRange(axis.getRange().getLower(), (Double)newValue);
				break;
			case MIN:
				axis.setRange((Double)newValue, axis.getRange().getUpper());
				break;
			case PRIMARY:
				axis.setPrimarySide((Boolean)newValue);
				break;
			case SHOW_GRID:
				axis.setShowMajorGrid((Boolean)newValue);
				break;
			case TIME_FORMAT:
				if((Integer)newValue == 0){
					axis.setDateEnabled(false);
					axis.setAutoFormat(true);
					break;
				}					
				String format = XYGraphModel.TIME_FORMAT_ARRAY[(Integer)newValue];
				axis.setDateEnabled(true);
				axis.setFormatPattern(format);				
				break;
			case TITLE_FONT:
				axis.setTitleFont(CustomMediaFactory.getInstance().getFont((FontData)newValue));
				break;
			case Y_AXIS:
				axis.setYAxis((Boolean)newValue);
				break;
			default:
				break;
			}		
	}
	
	private void registerTraceAmountChangeHandler(){
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {				
				XYGraphModel model = (XYGraphModel)getModel();
				XYGraph xyGraph = ((ToolbarArmedXYGraph)refreshableFigure).getXYGraph();
				int currentTracesAmount = xyGraph.getPlotArea().getTraceList().size();
				//add trace
				if((Integer)newValue > currentTracesAmount){
					for(int i=0; i<(Integer)newValue - currentTracesAmount; i++){	
						for(TraceProperty traceProperty : TraceProperty.values()){				
							String propID = XYGraphModel.makeTracePropID(
								traceProperty.propIDPre, i + currentTracesAmount);
							model.addProperty(propID, 
								model.getTempRemovedProperty(propID));	
						}							
						xyGraph.addTrace(traceList.get(i+currentTracesAmount));
					}						
				}else if((Integer)newValue < currentTracesAmount){ //remove trace
					for(int i=0; i<currentTracesAmount - (Integer)newValue; i++){
						for(TraceProperty traceProperty : TraceProperty.values()){				
							String propID = XYGraphModel.makeTracePropID(
								traceProperty.propIDPre, i+(Integer)newValue);
							model.tempRemoveProperty(propID);
						}						
						xyGraph.removeTrace(traceList.get(i+(Integer)newValue));
					}					
				}
				return true;
			}			
		};
		setPropertyChangeHandler(XYGraphModel.PROP_TRACES_AMOUNT, handler);
	}
	
	
	private void registerTracePropertyChangeHandlers(){
		XYGraphModel model = (XYGraphModel)getModel();
		//set prop handlers and init all the potential axes
		for(int i=0; i<XYGraphModel.MAX_TRACES_AMOUNT; i++){							
			for(TraceProperty traceProperty : TraceProperty.values()){				
				String propID = XYGraphModel.makeTracePropID(
					traceProperty.propIDPre, i);
				IWidgetPropertyChangeHandler handler = new TracePropertyChangeHandler(i, traceProperty);
				setPropertyChangeHandler(propID, handler);				
			}			
		}		
		for(int i=XYGraphModel.MAX_TRACES_AMOUNT -1; i>= model.getTracesAmount(); i--){
			for(TraceProperty traceProperty : TraceProperty.values()){		
				String propID = XYGraphModel.makeTracePropID(
					traceProperty.propIDPre, i);
				model.tempRemoveProperty(propID);
			}
		}
	}	
	
	private void setTraceProperty(Trace trace, TraceProperty traceProperty, Object newValue){
		CircularBufferDataProvider dataProvider = (CircularBufferDataProvider)trace.getDataProvider();
		switch (traceProperty) {
		case ANTI_ALIASING:
			trace.setAntiAliasing((Boolean)newValue);
			break;
		case BUFFER_SIZE:
			dataProvider.setBufferSize((Integer)newValue);
			break;
		case CHRONOLOGICAL:
			dataProvider.setChronological((Boolean)newValue);
			break;
		case CLEAR_TRACE:
			if((Boolean)newValue)
				dataProvider.clearTrace();
			break;
		case LINE_WIDTH:
			trace.setLineWidth((Integer)newValue);
			break;
		case NAME:
			trace.setName((String)newValue);
			break;
		case PLOTMODE:
			dataProvider.setPlotMode(PlotMode.values()[(Integer)newValue]);
			break;
		case POINT_SIZE:
			trace.setPointSize((Integer)newValue);
			break;
		case POINT_STYLE:
			trace.setPointStyle(PointStyle.values()[(Integer)newValue]);
			break;
		case TRACE_COLOR:
			trace.setTraceColor(CustomMediaFactory.getInstance().getColor((RGB)newValue));
			break;
		case TRACE_TYPE:
			trace.setTraceType(TraceType.values()[(Integer)newValue]);
			break;
		case TRIGGER_VALUE:
			dataProvider.triggerUpdate();
			break;
		case UPDATE_DELAY:
			dataProvider.setUpdateDelay((Integer)newValue);
			break;
		case UPDATE_MODE:
			dataProvider.setUpdateMode(UpdateMode.values()[(Integer)newValue]);
			break;
		case XAXIS_INDEX:
			if(!axisList.get((Integer)newValue).isYAxis())
				trace.setXAxis(axisList.get((Integer)newValue));
			break;
		case YAXIS_INDEX:
			if(axisList.get((Integer)newValue).isYAxis())
				trace.setYAxis(axisList.get((Integer)newValue));
			break;
		case XDATA:
			if(newValue instanceof double[])
				dataProvider.setCurrentXDataArray((double[])newValue);
			else
				dataProvider.setCurrentXData((Double)newValue);
			break;
		case YDATA:				
			if(newValue instanceof double[]){
				if(((double[])newValue).length == 1)
					dataProvider.setCurrentYData(((double[])newValue)[0]);
				else
					dataProvider.setCurrentYDataArray((double[])newValue);
			}				
			else 
				dataProvider.setCurrentYData((Double)newValue);
			break;
		case YTIMESTAMP:
			String original = (String)newValue;
			if(original == null || original.length() < 23)
				break;
			original = original.replace('T', ' ');
			original = original.substring(0, 23);
			try {
					Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(original);
					dataProvider.setCurrentYDataTimestamp(date.getTime());
			} catch (Exception e) {					
			}		
			break;
		default:
			break;
		}
	}
	
	class AxisPropertyChangeHandler implements IWidgetPropertyChangeHandler {
		private int axisIndex;
		private AxisProperty axisProperty;
		public AxisPropertyChangeHandler(int axisIndex, AxisProperty axisProperty) {
			this.axisIndex = axisIndex;
			this.axisProperty = axisProperty;
		}
		public boolean handleChange(Object oldValue, Object newValue,
				IFigure refreshableFigure) {
			Axis axis = axisList.get(axisIndex);
			setAxisProperty(axis, axisProperty, newValue);			
			return true;
		}
	}
	
	class TracePropertyChangeHandler implements IWidgetPropertyChangeHandler {
		private int traceIndex;
		private TraceProperty traceProperty;
		public TracePropertyChangeHandler(int traceIndex, TraceProperty traceProperty) {
			this.traceIndex = traceIndex;
			this.traceProperty = traceProperty;
		}
		public boolean handleChange(Object oldValue, Object newValue,
				IFigure refreshableFigure) {
			Trace trace = traceList.get(traceIndex);
			setTraceProperty(trace, traceProperty, newValue);			
			return true;
		}
	}
}
