package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;

/**The command for graph configuration.
 * @author Xihui Chen
 *
 */
public class XYGraphConfigCommand implements IUndoableCommand {

	private XYGraph xyGraph;
	private XYGraphMemento previousXYGraphMem, afterXYGraphMem;
	
	public XYGraphConfigCommand(XYGraph xyGraph) {
		this.xyGraph = xyGraph;
		previousXYGraphMem = new XYGraphMemento();		
		afterXYGraphMem = new XYGraphMemento();
		
		for(int i =0; i< xyGraph.getPlotArea().getAnnotationList().size(); i++){
			previousXYGraphMem.addAnnotationMemento(new AnnotationMemento());
			afterXYGraphMem.addAnnotationMemento(new AnnotationMemento());
		}	
		
		for(int i=0; i<xyGraph.getAxisList().size(); i++){
			previousXYGraphMem.addAxisMemento(new AxisMemento());
			afterXYGraphMem.addAxisMemento(new AxisMemento());
		}
		
		for(int i=0; i<xyGraph.getPlotArea().getTraceList().size(); i++){
			previousXYGraphMem.addTraceMemento(new TraceMemento());
			afterXYGraphMem.addTraceMemento(new TraceMemento());
		}
		
	}

	public void redo() {
		restoreXYGraphPropsFromMemento(xyGraph, afterXYGraphMem);
	}

	public void undo() {
		restoreXYGraphPropsFromMemento(xyGraph, previousXYGraphMem);
	}
	
	public void savePreviousStates(){
		saveXYGraphPropsToMemento(xyGraph, previousXYGraphMem);
	}
	
	public void saveAfterStates(){
		saveXYGraphPropsToMemento(xyGraph, afterXYGraphMem);
	}

	@Override
	public String toString() {
		return "Configure XYGraph Settings";
	}
	
	private void saveXYGraphPropsToMemento(XYGraph xyGraph, XYGraphMemento memento){
		memento.setTitle(xyGraph.getTitle());
		memento.setTitleFont(xyGraph.getTitleFont());
		memento.setTitleColor(xyGraph.getTitleColor());
		memento.setPlotAreaBackColor(
				xyGraph.getPlotArea().getBackgroundColor());
		memento.setShowTitle(xyGraph.isShowTitle());
		memento.setShowLegend(xyGraph.isShowLegend());
		memento.setShowPlotAreaBorder(
				xyGraph.getPlotArea().isShowBorder());
		memento.setTransparent(xyGraph.isTransparent());
		int i=0;
		for(Annotation sourceAnno : xyGraph.getPlotArea().getAnnotationList())
			saveAnnotationPropsToMemento(sourceAnno, 
					memento.getAnnotationMementoList().get(i++));
	
		i=0;
		for(Axis axis : xyGraph.getAxisList())
			saveAxisPropsToMemento(axis, memento.getAxisMementoList().get(i++));
		i=0;
		for(Trace trace : xyGraph.getPlotArea().getTraceList())
			saveTracePropsToMemento(trace, memento.getTraceMementoList().get(i++));
	}
	
	private void restoreXYGraphPropsFromMemento(XYGraph xyGraph, XYGraphMemento memento){
		xyGraph.setTitle(memento.getTitle());
		xyGraph.setTitleFont(memento.getTitleFont());
		xyGraph.setTitleColor(memento.getTitleColor());
		xyGraph.getPlotArea().setBackgroundColor(memento.getPlotAreaBackColor());
		xyGraph.setShowTitle(memento.isShowTitle());
		xyGraph.setShowLegend(memento.isShowLegend());
		xyGraph.getPlotArea().setShowBorder(memento.isShowPlotAreaBorder());
		xyGraph.setTransparent(memento.isTransparent());
		int i=0;
		for(AnnotationMemento annotationMemento : memento.getAnnotationMementoList())		
			restoreAnnotationPropsFromMemento(
					xyGraph.getPlotArea().getAnnotationList().get(i++), annotationMemento);
		i=0;
		for(AxisMemento axisMemento : memento.getAxisMementoList())
			restoreAxisPropsFromMemento(xyGraph.getAxisList().get(i++), axisMemento);
		i=0;
		for(TraceMemento traceMemento : memento.getTraceMementoList())
			restoreTracePropsFromMemento(
					xyGraph.getPlotArea().getTraceList().get(i++), traceMemento);
	}
	
	
	private void saveAnnotationPropsToMemento(Annotation annotation, AnnotationMemento memento){		
		memento.setName(annotation.getName());
		if(annotation.isFree()){
			memento.setFree(true);
			memento.setXAxis(annotation.getXAxis());
			memento.setYAxis(annotation.getYAxis());
		}			
		else{
			memento.setFree(false);
			memento.setTrace(annotation.getTrace());
		}
		memento.setAnnotationColor(annotation.getAnnotationColor());
		memento.setFont(annotation.getFont());
		memento.setCursorLineStyle(annotation.getCursorLineStyle());
		memento.setShowName(annotation.isShowName());
		memento.setShowSampleInfo(annotation.isShowSampleInfo());
		memento.setShowPosition(annotation.isShowPosition());	
	}
	
	private void restoreAnnotationPropsFromMemento( Annotation annotation, AnnotationMemento memento){		
		annotation.setName(memento.getName());
		if(memento.isFree())
			annotation.setFree(memento.getXAxis(), memento.getYAxis());		
		else
			annotation.setTrace(memento.getTrace());		
		annotation.setAnnotationColor(memento.getAnnotationColor());
		annotation.setFont(memento.getFont());
		annotation.setCursorLineStyle(memento.getCursorLineStyle());
		annotation.setShowName(memento.isShowName());
		annotation.setShowSampleInfo(memento.isShowSampleInfo());
		annotation.setShowPosition(memento.isShowPosition());	
	}
	
	private void saveAxisPropsToMemento(Axis axis, AxisMemento memento){
		memento.setTitle(axis.getTitle());
		memento.setTitleFont(axis.getTitleFont());
		memento.setForegroundColor(axis.getForegroundColor());
		memento.setPrimarySide(axis.isOnPrimarySide());
		memento.setLogScale(axis.isLogScaleEnabled());
		memento.setAutoScale(axis.isAutoScale());		
		memento.setAutoScaleThreshold(axis.getAutoScaleThreshold());			
		memento.setRange(axis.getRange());
		memento.setDateEnabled(axis.isDateEnabled());
		memento.setAutoFormat(axis.isAutoFormat());
		memento.setFormatPattern(axis.getFormatPattern());		
		memento.setShowMajorGrid(axis.isShowMajorGrid());
		memento.setDashGridLine(axis.isDashGridLine());
		memento.setMajorGridColor(axis.getMajorGridColor());		
	}
	
	
	private void restoreAxisPropsFromMemento(Axis axis, AxisMemento memento){
		axis.setTitle(memento.getTitle());
		axis.setTitleFont(memento.getTitleFont());
		axis.setForegroundColor(memento.getForegroundColor());
		axis.setPrimarySide(memento.isOnPrimarySide());
		axis.setLogScale(memento.isLogScaleEnabled());
		axis.setAutoScale(memento.isAutoScale());		
		axis.setAutoScaleThreshold(memento.getAutoScaleThreshold());			
		axis.setRange(memento.getRange());
		axis.setDateEnabled(memento.isDateEnabled());
		axis.setAutoFormat(memento.isAutoFormat());
		axis.setFormatPattern(memento.getFormatPattern());		
		axis.setShowMajorGrid(memento.isShowMajorGrid());
		axis.setDashGridLine(memento.isDashGridLine());
		axis.setMajorGridColor(memento.getMajorGridColor());		
	}
	
	private void saveTracePropsToMemento(Trace trace, TraceMemento memento){
		memento.setName(trace.getName());
		memento.setXAxis(trace.getXAxis());
		memento.setYAxis(trace.getYAxis());
		memento.setTraceColor(trace.getTraceColor());
		memento.setTraceType(trace.getTraceType());
		memento.setLineWidth(trace.getLineWidth());
		memento.setPointStyle(trace.getPointStyle());
		memento.setPointSize(trace.getPointSize());
		memento.setBaseLine(trace.getBaseLine());
		memento.setAreaAlpha(trace.getAreaAlpha());
		memento.setAntiAliasing(trace.isAntiAliasing());
		memento.setErrorBarEnabled(trace.isErrorBarEnabled());
		memento.setXErrorBarType(trace.getXErrorBarType());
		memento.setYErrorBarType(trace.getYErrorBarType());
		memento.setErrorBarColor(trace.getErrorBarColor());
		memento.setErrorBarCapWidth(trace.getErrorBarCapWidth());
		memento.setDrawYErrorInArea(trace.isDrawYErrorInArea());
	}
	
	private void restoreTracePropsFromMemento(Trace trace, TraceMemento memento){
		trace.setName(memento.getName());
		trace.setXAxis(memento.getXAxis());
		trace.setYAxis(memento.getYAxis());
		trace.setTraceColor(memento.getTraceColor());
		trace.setTraceType(memento.getTraceType());
		trace.setLineWidth(memento.getLineWidth());
		trace.setPointStyle(memento.getPointStyle());
		trace.setPointSize(memento.getPointSize());
		trace.setBaseLine(memento.getBaseLine());
		trace.setAreaAlpha(memento.getAreaAlpha());
		trace.setAntiAliasing(memento.isAntiAliasing());
		trace.setErrorBarEnabled(memento.isErrorBarEnabled());
		trace.setXErrorBarType(memento.getXErrorBarType());
		trace.setYErrorBarType(memento.getYErrorBarType());
		trace.setErrorBarColor(memento.getErrorBarColor());
		trace.setErrorBarCapWidth(memento.getErrorBarCapWidth());
		trace.setDrawYErrorInArea(memento.isDrawYErrorInArea());
	}
	
}
