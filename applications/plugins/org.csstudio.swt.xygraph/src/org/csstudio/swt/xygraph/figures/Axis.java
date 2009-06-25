package org.csstudio.swt.xygraph.figures;

import java.util.ArrayList;
import java.util.List;


import org.csstudio.swt.xygraph.Activator;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.linearscale.LinearScale;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.undo.AxisPanningCommand;
import org.csstudio.swt.xygraph.undo.ZoomType;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * The axis figure.
 * 
 * @author Xihui Chen
 *
 */
public class Axis extends LinearScale implements IDataProviderListener{
	
	private static final Color GRAY_COLOR = XYGraphMediaFactory.getInstance().getColor(
			XYGraphMediaFactory.COLOR_GRAY);
	private String title;
	
	private XYGraph xyGraph;
	private Grid grid;
	
	private Font titleFont;
	
	private List<Trace> traceList;
	
	private boolean autoScale = false;
	
	private boolean showMajorGrid = false;
	
	private boolean showMinorGrid = false;
	
	private Color majorGridColor = GRAY_COLOR;
	
	private Color minorGridColor = GRAY_COLOR;
	
	private boolean dashGridLine = true;	
	
	private final static int GAP = 0;
	
	private double autoScaleThreshold =0.01;
	
	private List<IAxisListener> listeners;
	private ZoomType zoomType = ZoomType.NONE;
	
	private Point start;
	private Point end;
	private boolean armed;
	private Range startRange;
	private Cursor grabbing;
	
	/**Constructor
	 * @param title title of the axis
	 * @param yAxis true if this is the Y-Axis, false if this is the X-Axis.
	 */
	public Axis(String title, boolean yAxis) {
		
		super();
		traceList = new ArrayList<Trace>();
		this.title = title;
		if(yAxis)
			setOrientation(Orientation.VERTICAL);
		listeners = new ArrayList<IAxisListener>();
		
		AxisPanner panner = new AxisPanner();
		addMouseListener(panner);
		addMouseMotionListener(panner);
		Image image = XYGraphMediaFactory.getInstance().getImageFromPlugin(
				Activator.getDefault(), Activator.PLUGIN_ID, "icons/Grabbing.png");
		grabbing = new Cursor(Display.getDefault(), image.getImageData(), 8, 8);	
	}

	public void addListener(final IAxisListener listener){
		if(listeners.contains(listener))
			return;
		listeners.add(listener);
	}
	
	public boolean removeListenr(final IAxisListener listener){
		return listeners.remove(listener);
	}
	
	private void fireRevalidated(){
		for(IAxisListener listener : listeners)
			listener.axisRevalidated(this);
	}
	
	@Override
	protected void layout() {
		super.layout();
		fireRevalidated();
	}
	
	@Override
	public void setForegroundColor(Color color) {
		super.setForegroundColor(color);
		if(xyGraph != null)
			xyGraph.repaint();
	}
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension d = super.getPreferredSize(wHint, hHint);
		titleFont = XYGraphMediaFactory.getInstance().getFont(
				new FontData("Arial", 9, SWT.BOLD));
		if(isHorizontal())
			d.height += FigureUtilities.getTextExtents(title, titleFont).height + GAP;
		else
			d.width += FigureUtilities.getTextExtents(title, titleFont).height + GAP;
		return d;
	}
	
	@Override
	protected void paintClientArea(Graphics graphics) {	
		super.paintClientArea(graphics);
		graphics.pushState();
		graphics.setFont(titleFont);		
		Dimension titleSize = FigureUtilities.getTextExtents(title, titleFont);
		if(isHorizontal()){
			if(getTickLablesSide() == LabelSide.Primary)
				graphics.drawText(title, 
						bounds.x + bounds.width/2 - titleSize.width/2,
						bounds.y + bounds.height - titleSize.height);
			else
				graphics.drawText(title, 
						bounds.x + bounds.width/2 - titleSize.width/2,
						bounds.y);
		}else{	
			Image image = new Image(Display.getCurrent(), titleSize.width+1, titleSize.height);			
				GC gc = new GC(image);	
				Color titleColor = graphics.getForegroundColor();
				RGB transparentRGB = new RGB(240, 240, 240);
				if(xyGraph !=null)
					if(!xyGraph.isTransparent())
						transparentRGB = xyGraph.getBackgroundColor().getRGB();	
								
				gc.setBackground(XYGraphMediaFactory.getInstance().getColor(transparentRGB));
				gc.fillRectangle(image.getBounds());
				gc.setForeground(titleColor);
				gc.setFont(titleFont);
				gc.drawText(title, 0, 0);
				gc.dispose();
				ImageData imageData = image.getImageData();				
				image.dispose();
				imageData.transparentPixel = imageData.palette.getPixel(transparentRGB);
				image = new Image(Display.getCurrent(), imageData);
			if(getTickLablesSide() == LabelSide.Primary){					
				graphics.translate(bounds.x, bounds.y);
				graphics.translate(0, bounds.height/2 + titleSize.width/2);
				graphics.rotate(270);
				graphics.drawImage(image, 0, 0);
			} else {				
				//draw vertical title text image				
				graphics.translate(bounds.x, bounds.y);
				graphics.translate(bounds.width - titleSize.height, bounds.height/2 + titleSize.width/2);
				graphics.rotate(90);
				graphics.drawImage(image, -titleSize.width, -titleSize.height);
			}
			image.dispose();				

		}
		graphics.popState();		
	}	
	
	
	/**@param force If true, the axis will be auto-scaled by force regardless the autoScale field. 
	 * Otherwise, it will use the autoScale field to judge whether an auto-scale will be performed.  
	 * @return true if the axis is repainted due to range change.
	 */
	public boolean performAutoScale(boolean force){
		if((force || autoScale) && traceList.size() >0){
			double tempMin, tempMax;
			Range range;
			
			tempMin = Double.POSITIVE_INFINITY;
			tempMax = Double.NEGATIVE_INFINITY;
			for(Trace trace : traceList){
				if(trace.getDataProvider() == null)
					continue;
				if(isHorizontal())
					range = trace.getDataProvider().getXDataMinMax();
				else
					range = trace.getDataProvider().getYDataMinMax();
				if(range == null)
					continue;
				if(Double.isInfinite(range.getLower()) || Double.isInfinite(range.getUpper()) ||
						Double.isNaN(range.getLower()) || Double.isNaN(range.getUpper()))
					continue;
				if(tempMin > range.getLower())
					tempMin = range.getLower();
				if(tempMax < range.getUpper())
					tempMax = range.getUpper();					
			}
			
		double max = getRange().getUpper();
		double min = getRange().getLower();
		double thr = (max - min)*autoScaleThreshold;
		
		if(((tempMin - min)>=0 && (tempMin - min)<thr))
			tempMin = min;
		if((max - tempMax)>=0 && (max - tempMax)<thr)
			tempMax = max;
		
		if((tempMin == min && tempMax == max) || 
				Double.isInfinite(tempMin) || Double.isInfinite(tempMax) ||
				Double.isNaN(tempMin) || Double.isNaN(tempMax))
			return false;
		
		setRange(tempMin, tempMax);
		repaint();
		return true;
		}
		return false;
	}
	
	/**Add a trace to the axis.
	 * @param trace the trace to be added.
	 */
	public void addTrace(Trace trace){
		if(traceList.contains(trace))
			return;
		traceList.add(trace);
		performAutoScale(false);
	}
	
	/**Remove a trace from the axis.
	 * @param trace
	 * @return true if this axis contained the specified trace
	 */	
	public boolean removeTrace(Trace trace){
		boolean r = traceList.remove(trace);
		performAutoScale(false);
		return r;
	}
	
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the autoScale
	 */
	public boolean isAutoScale() {
		return autoScale;
	}

	/**
	 * @param autoScale the autoScale to set
	 */
	public void setAutoScale(boolean autoScale) {
		this.autoScale = autoScale;
		performAutoScale(false);
	}

	/**
	 * @return the showMajorGrid
	 */
	public boolean isShowMajorGrid() {
		return showMajorGrid;
	}

	/**
	 * @param showMajorGrid the showMajorGrid to set
	 */
	public void setShowMajorGrid(boolean showMajorGrid) {
		this.showMajorGrid = showMajorGrid;
	}

	/**
	 * @return the showMinorGrid
	 */
	public boolean isShowMinorGrid() {
		return showMinorGrid;
	}

	/**
	 * @param showMinorGrid the showMinorGrid to set
	 */
	public void setShowMinorGrid(boolean showMinorGrid) {
		this.showMinorGrid = showMinorGrid;
	}

	/**
	 * @return the majorGridColor
	 */
	public Color getMajorGridColor() {
		return majorGridColor;
	}

	/**
	 * @param majorGridColor the majorGridColor to set
	 */
	public void setMajorGridColor(Color majorGridColor) {
		this.majorGridColor = majorGridColor;
	}

	/**
	 * @return the minorGridColor
	 */
	public Color getMinorGridColor() {
		return minorGridColor;
	}

	/**
	 * @param minorGridColor the minorGridColor to set
	 */
	public void setMinorGridColor(Color minorGridColor) {
		this.minorGridColor = minorGridColor;
	}

	/**
	 * @param titleFont the titleFont to set
	 */
	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}


	/**
	 * @return the dashGridLine
	 */
	public boolean isDashGridLine() {
		return dashGridLine;
	}

	/**
	 * @param dashGridLine the dashGridLine to set
	 */
	public void setDashGridLine(boolean dashGridLine) {
		this.dashGridLine = dashGridLine;
	}

	/**
	 * @param xyGraph the xyGraph to set
	 */
	public void setXyGraph(XYGraph xyGraph) {
		this.xyGraph = xyGraph;
	}	
	@Override
	public String toString() {
		return title;
	}

	public void dataChanged(IDataProvider dataProvider) {
		if(autoScale)
			performAutoScale(false);
	}

	/**The autoScaleThreshold must be a value in range [0,1], which represents a percentage
	 * of the plot area for the threshold when autoScale is performed.The autoScale will performed
	 * only if the spare space exceeds this threshold. So it can reduce the CPU usage by
	 * increasing the threshold.
	 * @param autoScaleThreshold the autoScaleThreshold to set
	 */
	public void setAutoScaleThreshold(double autoScaleThreshold) {
		if(autoScaleThreshold > 1 || autoScaleThreshold <0)
			throw new RuntimeException("The autoScaleThreshold must be a value in range [0,1]!");
		this.autoScaleThreshold = autoScaleThreshold;
	}
	
	/**
	 * @param zoomType the zoomType to set
	 */
	public void setZoomType(ZoomType zoomType) {
		this.zoomType = zoomType;
		if(zoomType == ZoomType.PANNING)
			setCursor(zoomType.getCursor());
		else
			setCursor(ZoomType.NONE.getCursor());
	}
	
	/**
	 * @return the titleFont
	 */
	public Font getTitleFont() {
		return titleFont;
	}

	/**
	 * @return the autoScaleThreshold
	 */
	public double getAutoScaleThreshold() {
		return autoScaleThreshold;
	}
	
	/**Set this axis as Y-Axis or X-Axis.
	 * @param isYAxis set true if the axis is Y-Axis; false if it is X-Axis.
	 */
	public void setYAxis(boolean isYAxis){
		if(xyGraph != null)
			xyGraph.removeAxis(this);
		setOrientation(isYAxis ? Orientation.VERTICAL : Orientation.HORIZONTAL);
		if(xyGraph != null)
			xyGraph.addAxis(this);
	}
	
	/**Set the axis on primary side (Bottom/Left) or secondary side (Top/Right).
	 * @param onPrimarySide set true if the axis on primary side(Bottom/Left);
	 * false if it is not on the primary side of xy graph(Top/Right).
	 */
	public void setPrimarySide(boolean onPrimarySide){
		setTickLableSide(onPrimarySide ? LabelSide.Primary : LabelSide.Secondary);
	}
	
	/**
	 * @return true if the axis is Y-Axis; false if it is X-Axis;
	 */
	public boolean isYAxis(){
		return !isHorizontal();
	}
	
	/**
	 * @return true if the axis is on the primary side of xy graph(Bottom/Left);
	 * false if it is on the secondary side(Top/Right).
	 */
	public boolean isOnPrimarySide(){
		return getTickLablesSide() == LabelSide.Primary;
	}
	
	private void pan(){
		double t1, t2, m;
		int i=0;
		Range temp;
		if(isHorizontal())	{
			t1 = getPositionValue(start.x, false);
			t2 = getPositionValue(end.x, false);
		}else{
			t1 = getPositionValue(start.y, false);
			t2 = getPositionValue(end.y, false);
		}
			
			temp = startRange;
			if(isLogScaleEnabled()){
				m = Math.log10(t2) - Math.log10(t1);
				t1 = Math.pow(10,Math.log10(temp.getLower()) - m);
				t2 = Math.pow(10,Math.log10(temp.getUpper()) - m);
			}else {
				m = t2-t1;
				t1 = temp.getLower() - m;
				t2 = temp.getUpper() - m;
			}
			setRange(t1, t2);
			i++;

	}
	
	
	/**
	 * @param grid the grid to set
	 */
	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	/**
	 * @return the grid
	 */
	public Grid getGrid() {
		return grid;
	}


	class AxisPanner extends MouseMotionListener.Stub implements MouseListener {
		
		private AxisPanningCommand command;
		
		@Override
		public void mouseDragged(MouseEvent me) {
			if(!armed)
				return;
			end = me.getLocation();
			pan();
			me.consume();				
		}

		public void mouseDoubleClicked(MouseEvent me) {}

		public void mousePressed(MouseEvent me) {
			if(zoomType == ZoomType.PANNING){
				setCursor(grabbing);
				armed = true;				
				start = me.getLocation();
				end = null;
				startRange = getRange();
				command = new AxisPanningCommand(Axis.this);
				command.savePreviousStates();
				me.consume();
			}			
		}

		public void mouseReleased(MouseEvent me) {
			if(zoomType != ZoomType.PANNING || !armed || 
					end == null || start == null || command == null) 
				return;
			
			setCursor(zoomType.getCursor());
			armed = false;
			start = null;
			end = null;
			command.saveAfterStates();
			xyGraph.getOperationsManager().addCommand(command);		
			me.consume();
		}
		
	}
	
	

}
