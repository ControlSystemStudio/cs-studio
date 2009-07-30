package org.csstudio.swt.xygraph.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.Activator;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.undo.ZoomCommand;
import org.csstudio.swt.xygraph.undo.ZoomType;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**The plot area figure.
 * @author Xihui Chen
 *
 */
public class PlotArea extends Figure {
	
	private List<Trace> traceList;
	private List<Grid> gridList;
	private List<Annotation> annotationList;
	
	private XYGraph xyGraph;
	
	private boolean showBorder;	
	
	private ZoomType zoomType;
	
	private Point start;
	private Point end;
	private boolean armed;
	
	private Color revertBackColor;

	private List<Range> xAxisStartRangeList;
	private List<Range> yAxisStartRangeList;
	private Cursor grabbing;
	
	/** The ratio of the shrink/expand area for one zoom. */
	private final double ZOOM_RATIO = 0.1;
	
	/** The auto zoom interval in ms.*/
	private final int ZOOM_SPEED = 100;
	
	public PlotArea(XYGraph xyGraph) {
		this.xyGraph = xyGraph;
		traceList = new ArrayList<Trace>();
		gridList = new ArrayList<Grid>();
		xAxisStartRangeList = new ArrayList<Range>();
		yAxisStartRangeList = new ArrayList<Range>();
		annotationList = new ArrayList<Annotation>();
		setBackgroundColor(XYGraph.WHITE_COLOR);
		setForegroundColor(XYGraph.BLACK_COLOR);		
		setOpaque(true);
		RGB backRGB = getBackgroundColor().getRGB();
		revertBackColor = XYGraphMediaFactory.getInstance().getColor(255- backRGB.red, 
				255 - backRGB.green, 255 - backRGB.blue);
		PlotAreaZoomer zoomer = new PlotAreaZoomer();
		addMouseListener(zoomer);
		addMouseMotionListener(zoomer);
		Image image = XYGraphMediaFactory.getInstance().getImageFromPlugin(
				Activator.getDefault(), Activator.PLUGIN_ID, "icons/Grabbing.png");
		grabbing = new Cursor(Display.getDefault(), image.getImageData(), 8, 8);		
		zoomType = ZoomType.NONE;
	}
	
	@Override
	public void setBackgroundColor(Color bg) {
		RGB backRGB = bg.getRGB();
		revertBackColor = XYGraphMediaFactory.getInstance().getColor(255- backRGB.red, 
				255 - backRGB.green, 255 - backRGB.blue);
		super.setBackgroundColor(bg);
		
	}
	
	/**Add a trace to the plot area.
	 * @param trace the trace to be added.
	 */
	public void addTrace(Trace trace){
		traceList.add(trace);
		add(trace);
		revalidate();
	}
	
	/**Remove a trace from the plot area.
	 * @param trace
	 * @return true if this plot area contained the specified trace
	 */	
	public boolean removeTrace(Trace trace){
		boolean result = traceList.remove(trace);
		if(result){
			remove(trace);
			revalidate();
		}
		return result;
	}
	
	/**Add a grid to the plot area.
	 * @param grid the grid to be added.
	 */
	public void addGrid(Grid grid){
		gridList.add(grid);
		add(grid);
		revalidate();
	}
	
	/**Remove a grid from the plot area.
	 * @param grid the grid to be removed.
	 * @return true if this plot area contained the specified grid
	 */	
	public boolean removeGrid(Grid grid){
		boolean result = gridList.remove(grid);
		if(result){
			remove(grid);
			revalidate();
		}
		return result;
	}
	
	
	
	/**Add an annotation to the plot area.
	 * @param annotation the annotation to be added.
	 */
	public void addAnnotation(Annotation annotation){
		annotationList.add(annotation);
		annotation.setxyGraph(xyGraph);
		add(annotation);
		revalidate();
	}
	
	/**Remove a annotation from the plot area.
	 * @param annotation the annotation to be removed.
	 * @return true if this plot area contained the specified annotation
	 */	
	public boolean removeAnnotation(Annotation annotation){
		boolean result = annotationList.remove(annotation);
		if(!annotation.isFree())
			annotation.getTrace().getDataProvider().removeDataProviderListener(annotation);
		if(result){
			remove(annotation);
			revalidate();
		}
		return result;
	}
	
	
	
	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea();
		for(Trace trace : traceList){
			if(trace != null && trace.isVisible())
				//Shrink will make the trace has no intersection with axes,
				//which will make it only repaints the trace area.
				trace.setBounds(clientArea.getCopy().shrink(1, 1));				
		}		
		for(Grid grid : gridList){
			if(grid != null && grid.isVisible())
				grid.setBounds(clientArea);
		}
		
		for(Annotation annotation : annotationList){
			if(annotation != null && annotation.isVisible())
				annotation.setBounds(clientArea.getCopy().shrink(1, 1));
		}
		super.layout();
	}
	
	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
		if(showBorder){
			graphics.setLineWidth(2);
			graphics.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
			graphics.drawLine(bounds.x + bounds.width, bounds.y, 
				bounds.x + bounds.width, bounds.y + bounds.height );
		}
		if(armed && end != null && start != null){
			switch (zoomType) {
			case RUBBERBAND_ZOOM:
			case HORIZONTAL_ZOOM:
			case VERTICAL_ZOOM:
				graphics.setLineStyle(SWT.LINE_DOT);
				graphics.setLineWidth(1);				
				graphics.setForegroundColor(revertBackColor);
				graphics.drawRectangle(start.x, start.y, end.x - start.x, end.y - start.y);
				break;
	
			default:
				break;
			}
			
		}
	}
	
	/**
	 * @param showBorder the showBorder to set
	 */
	public void setShowBorder(boolean showBorder) {
		this.showBorder = showBorder;
	}

	/**
	 * @return the showBorder
	 */
	public boolean isShowBorder() {
		return showBorder;
	}

	/**
	 * @param zoomType the zoomType to set
	 */
	public void setZoomType(ZoomType zoomType) {
		this.zoomType = zoomType;
		setCursor(zoomType.getCursor());
	}
	
	private void zoom(){
		double t1, t2;
		for(Axis axis : xyGraph.getXAxisList()){
			t1 = axis.getPositionValue(start.x, false);
			t2 = axis.getPositionValue(end.x, false);
			axis.setRange(t1, t2);			
		}
		for(Axis axis : xyGraph.getYAxisList()){
			t1 = axis.getPositionValue(start.y, false);
			t2 = axis.getPositionValue(end.y, false);
			axis.setRange(t1, t2);			
		}
	}
	
	private void zoomIn(){
		double oValue, l, r1, r2, t1, t2;
		for(Axis axis : xyGraph.getXAxisList()){
			oValue = axis.getPositionValue(start.x, false);
			if(axis.isLogScaleEnabled()){
				l = Math.log10(axis.getRange().getUpper()) - 
						Math.log10(axis.getRange().getLower());
				r1 = (Math.log10(oValue) - Math.log10(axis.getRange().getLower()))/l;
				r2 = (Math.log10(axis.getRange().getUpper()) - Math.log10(oValue))/l;
				t1 = Math.pow(10, Math.log10(axis.getRange().getLower()) + r1 * ZOOM_RATIO * l);
				t2 = Math.pow(10, Math.log10(axis.getRange().getUpper()) - r2 * ZOOM_RATIO * l);
			}else{
				l = axis.getRange().getUpper()-axis.getRange().getLower();
				r1 = (oValue - axis.getRange().getLower())/l;
				r2 = (axis.getRange().getUpper() - oValue)/l;
				t1 = axis.getRange().getLower() + 
					r1 * ZOOM_RATIO * l;
				t2 = axis.getRange().getUpper() - r2 * ZOOM_RATIO * l;				
			}
			axis.setRange(t1, t2);
		}	
		for(Axis axis : xyGraph.getYAxisList()){
			oValue = axis.getPositionValue(start.y, false);
			if(axis.isLogScaleEnabled()){
				l = Math.log10(axis.getRange().getUpper()) - 
						Math.log10(axis.getRange().getLower());
				r1 = (Math.log10(oValue) - Math.log10(axis.getRange().getLower()))/l;
				r2 = (Math.log10(axis.getRange().getUpper()) - Math.log10(oValue))/l;
				t1 = Math.pow(10, Math.log10(axis.getRange().getLower()) + r1 * ZOOM_RATIO * l);
				t2 = Math.pow(10, Math.log10(axis.getRange().getUpper()) - r2 * ZOOM_RATIO * l);
			}else{
				l = axis.getRange().getUpper()-axis.getRange().getLower();
				r1 = (oValue - axis.getRange().getLower())/l;
				r2 = (axis.getRange().getUpper() - oValue)/l;
				t1 = axis.getRange().getLower() + 
					r1 * ZOOM_RATIO * l;
				t2 = axis.getRange().getUpper() - r2 * ZOOM_RATIO * l;				
			}
			axis.setRange(t1, t2);
		}		
	}
	
	private void zoomOut(){
		double oValue, l, r1, r2, t1, t2;
		for(Axis axis : xyGraph.getXAxisList()){
			oValue = axis.getPositionValue(start.x, false);
			if(axis.isLogScaleEnabled()){
				l = Math.log10(axis.getRange().getUpper()) - 
						Math.log10(axis.getRange().getLower());
				r1 = (Math.log10(oValue) - Math.log10(axis.getRange().getLower()))/l;
				r2 = (Math.log10(axis.getRange().getUpper()) - Math.log10(oValue))/l;
				t1 = Math.pow(10, Math.log10(axis.getRange().getLower()) - r1 * ZOOM_RATIO * l);
				t2 = Math.pow(10, Math.log10(axis.getRange().getUpper()) + r2 * ZOOM_RATIO * l);
			}else{
				l = axis.getRange().getUpper()-axis.getRange().getLower();
				r1 = (oValue - axis.getRange().getLower())/l;
				r2 = (axis.getRange().getUpper() - oValue)/l;
				t1 = axis.getRange().getLower() - 
					r1 * ZOOM_RATIO * l;
				t2 = axis.getRange().getUpper() + r2 * ZOOM_RATIO * l;				
			}
			axis.setRange(t1, t2);
		}	
		for(Axis axis : xyGraph.getYAxisList()){
			oValue = axis.getPositionValue(start.y, false);
			if(axis.isLogScaleEnabled()){
				l = Math.log10(axis.getRange().getUpper()) - 
						Math.log10(axis.getRange().getLower());
				r1 = (Math.log10(oValue) - Math.log10(axis.getRange().getLower()))/l;
				r2 = (Math.log10(axis.getRange().getUpper()) - Math.log10(oValue))/l;
				t1 = Math.pow(10, Math.log10(axis.getRange().getLower()) - r1 * ZOOM_RATIO * l);
				t2 = Math.pow(10, Math.log10(axis.getRange().getUpper()) + r2 * ZOOM_RATIO * l);
			}else{
				l = axis.getRange().getUpper()-axis.getRange().getLower();
				r1 = (oValue - axis.getRange().getLower())/l;
				r2 = (axis.getRange().getUpper() - oValue)/l;
				t1 = axis.getRange().getLower() - 
					r1 * ZOOM_RATIO * l;
				t2 = axis.getRange().getUpper() + r2 * ZOOM_RATIO * l;				
			}
			axis.setRange(t1, t2);
		}		
	}
	
	/**
	 * @return the traceList
	 */
	public List<Trace> getTraceList() {
		return traceList;
	}

	/**
	 * @return the annotationList
	 */
	public List<Annotation> getAnnotationList() {
		return annotationList;
	}

	private void pan(){
		double t1, t2, m;
		int i=0;
		Range temp;
		for(Axis axis : xyGraph.getXAxisList()){
			t1 = axis.getPositionValue(start.x, false);
			t2 = axis.getPositionValue(end.x, false);
			temp = xAxisStartRangeList.get(i);
			if(axis.isLogScaleEnabled()){
				m = Math.log10(t2) - Math.log10(t1);
				t1 = Math.pow(10,Math.log10(temp.getLower()) - m);
				t2 = Math.pow(10,Math.log10(temp.getUpper()) - m);
			}else {
				m = t2-t1;
				t1 = temp.getLower() - m;
				t2 = temp.getUpper() - m;
			}
			axis.setRange(t1, t2);
			i++;
		}
		i=0;
		for(Axis axis : xyGraph.getYAxisList()){			
			t1 = axis.getPositionValue(start.y, false);
			t2 = axis.getPositionValue(end.y, false);
			temp = yAxisStartRangeList.get(i);
			if(axis.isLogScaleEnabled()){				
				m = Math.log10(t2) - Math.log10(t1);
				t1 = Math.pow(10.0,Math.log10(temp.getLower()) - m);
				t2 = Math.pow(10.0,(Math.log10(temp.getUpper()) - m));
			}
			else{
				m = t2-t1;
				t1 = temp.getLower() - m;
				t2 = temp.getUpper() - m;
			}
			axis.setRange(t1, t2);
			i++;
		}
	}

	class PlotAreaZoomer extends MouseMotionListener.Stub implements MouseListener{	
		
		private ZoomCommand command;
		
		@Override
		public void mouseDragged(MouseEvent me) {
			if(!armed)
				return;
			switch (zoomType) {
			case RUBBERBAND_ZOOM:
				end = me.getLocation();				
				break;
			case HORIZONTAL_ZOOM:
				end = new Point(me.getLocation().x, bounds.y + bounds.height);
				break;
			case VERTICAL_ZOOM:
				end = new Point(bounds.x + bounds.width, me.getLocation().y);
				break;
			case PANNING:
				end = me.getLocation();
				pan();
				break;
			default:
				break;
			}
			PlotArea.this.repaint();
			
		}
		
		public void mouseDoubleClicked(MouseEvent me) {}

		public void mousePressed(MouseEvent me) {	
			if(zoomType == ZoomType.NONE)
				return;
			armed = true;
			//get start position
			switch (zoomType) {
			case RUBBERBAND_ZOOM:
				start = me.getLocation();
				end = null;
				break;
			case HORIZONTAL_ZOOM:
				start = new Point(me.getLocation().x, bounds.y);
				end = null;
				break;
			case VERTICAL_ZOOM:
				start = new Point(bounds.x, me.getLocation().y);
				end = null;
				break;
			case PANNING:
				setCursor(grabbing);
				start = me.getLocation();
				end = null;
				xAxisStartRangeList.clear();
				yAxisStartRangeList.clear();
				for(Axis axis : xyGraph.getXAxisList())
					xAxisStartRangeList.add(axis.getRange());
				for(Axis axis : xyGraph.getYAxisList())
					yAxisStartRangeList.add(axis.getRange());
				break;
			case ZOOM_IN:
			case ZOOM_OUT:
				start = me.getLocation();
				end = new Point();
				Display.getCurrent().timerExec(ZOOM_SPEED, new Runnable(){
					public void run() {	
						if(armed){	
							if(zoomType == ZoomType.ZOOM_IN)
								zoomIn();
							else
								zoomOut();
							Display.getCurrent().timerExec(ZOOM_SPEED, this);
						}
					}
				});
				break;
			default:
				break;
			}
			
			//add command for undo operation
			if(zoomType != ZoomType.NONE){
				command = new ZoomCommand(zoomType.getDescription(), 
						xyGraph.getXAxisList(), xyGraph.getYAxisList());
				command.savePreviousStates();
			}
			me.consume();			
		}

		@Override
		public void mouseExited(MouseEvent me) {
			//make sure the zoomIn/Out timer could be stopped
			if(zoomType == ZoomType.ZOOM_IN || zoomType == ZoomType.ZOOM_OUT)
				mouseReleased(me);
		}
		
		public void mouseReleased(MouseEvent me) {
			if(zoomType == ZoomType.PANNING)
				setCursor(zoomType.getCursor());
			if(!armed || end == null || start == null)
				return;
			
			switch (zoomType) {
			case RUBBERBAND_ZOOM:
			case HORIZONTAL_ZOOM:
			case VERTICAL_ZOOM:
				zoom();
				break;
			case PANNING:
				pan();					
				break;	
			case ZOOM_IN:
				zoomIn();
				break;
			case ZOOM_OUT:
				zoomOut();
				break;
			default:
				break;
			}
			
			if(zoomType != ZoomType.NONE && command != null){
				command.saveAfterStates();
				xyGraph.getOperationsManager().addCommand(command);				
			}		
			armed = false;
			end = null; 
			start = null;			
			PlotArea.this.repaint();
			
		}
		
	}
	
}
