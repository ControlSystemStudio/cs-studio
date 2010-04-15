package org.csstudio.swt.xygraph.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.linearscale.LinearScale;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.undo.AxisPanOrZoomCommand;
import org.csstudio.swt.xygraph.undo.SaveStateCommand;
import org.csstudio.swt.xygraph.undo.ZoomType;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory.CURSOR_TYPE;
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
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

/**
 * The axis figure.
 * 
 * @author Xihui Chen
 * @author Kay Kasemir - Axis zoom/pan tweaks
 */
public class Axis extends LinearScale{
    /** The ratio of the shrink/expand area for one zoom. */
    final static double ZOOM_RATIO = 0.1;
    
    /** The auto zoom interval in ms.*/
    final static int ZOOM_SPEED = 200;
	
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
		
		AxisMouseListener panner = new AxisMouseListener();
		addMouseListener(panner);
		addMouseMotionListener(panner);
		grabbing = XYGraphMediaFactory.getCursor(CURSOR_TYPE.GRABBING);	
		titleFont = XYGraphMediaFactory.getInstance().getFont(
				new FontData("Arial", 9, SWT.BOLD)); //$NON-NLS-1$
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
	
	private void fireAxisRangeChanged(Range old_range, Range new_range){
		for(IAxisListener listener : listeners)
			listener.axisRangeChanged(this, old_range, new_range);
	}
	
	@Override
	public void setRange(double lower, double upper) {
		Range old_range = getRange();
		super.setRange(lower, upper);
		fireAxisRangeChanged(old_range, getRange());
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
			int w = titleSize.height;
			int h = titleSize.width +1;
			Image image = new Image(Display.getCurrent(),w, h);			
				try {
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
					Transform tr = new Transform(Display.getCurrent());
					if(getTickLablesSide() == LabelSide.Primary){
						tr.translate(0, h);
						tr.rotate(-90);
						gc.setTransform(tr);
					}else{
						tr.translate(w, 0);
						tr.rotate(90);
						gc.setTransform(tr);
					}
					gc.drawText(title, 0, 0);
					tr.dispose();
					gc.dispose();
					ImageData imageData = image.getImageData();				
					image.dispose();
					imageData.transparentPixel = imageData.palette.getPixel(transparentRGB);
					image = new Image(Display.getCurrent(), imageData);				
					if(getTickLablesSide() == LabelSide.Primary){					
						graphics.translate(bounds.x, bounds.y);			
						graphics.drawImage(image, 0, bounds.height/2 - h/2);
					} else {				
						//draw vertical title text image				
						graphics.translate(bounds.x, bounds.y);			
						graphics.drawImage(image, bounds.width - w, bounds.height/2 - h/2);
					}
				} finally{
					image.dispose();		
				}
		}
		graphics.popState();		
	}	
	
	/** @return Range that reflects the minimum and maximum value of all
	 *          traces on this axis.
	 *          Returns <code>null</code> if there is no trace data.
	 */
    public Range getTraceDataRange()
    {
        double low = Double.POSITIVE_INFINITY;
        double high = Double.NEGATIVE_INFINITY;
        for (Trace trace : traceList)
        {
            if (trace.getDataProvider() == null)
                continue;
            Range range;
            if (isHorizontal())
                range = trace.getDataProvider().getXDataMinMax();
            else
                range = trace.getDataProvider().getYDataMinMax();
            if (range == null)
                continue;
            if (Double.isInfinite(range.getLower())
                    || Double.isInfinite(range.getUpper())
                    || Double.isNaN(range.getLower())
                    || Double.isNaN(range.getUpper()))
                continue;
            if (low > range.getLower())
                low = range.getLower();
            if (high < range.getUpper())
                high = range.getUpper();
        }
        if (Double.isInfinite(low) || Double.isInfinite(high))
            return null;
        return new Range(low, high);
    }
	
	/** Perform an auto-scale:
	 *  Axis limits are set to the value range of the traces on this axis.
	 *  Includes some optimization:
	 *  Axis range is set a little wider than exact trace data range.
	 *  When auto-scale would only perform a minor axis adjustment,
	 *  axis is left unchanged.
	 * 
	 *  @param force If true, the axis will be auto-scaled by force regardless the autoScale field. 
	 *  Otherwise, it will use the autoScale field to judge whether an auto-scale will be performed.  
	 *  @return true if the axis is repainted due to range change.
	 *  
	 *  @see #autoScaleThreshold
	 */
	public boolean performAutoScale(boolean force){
		if((force || autoScale) && traceList.size() >0){
		    // Get range of data in all traces
            final Range range = getTraceDataRange();
            if (range == null)
                return false;
			double tempMin = range.getLower();
			double tempMax = range.getUpper();
			
			// Get current axis range, determine how 'different' they are
    		double max = getRange().getUpper();
    		double min = getRange().getLower();
    		double thr = (max - min)*autoScaleThreshold;
    		
    		//if both the changes are lower than threshold, return
    		if(((tempMin - min)>=0 && (tempMin - min)<thr)
    				&& ((max - tempMax)>=0 && (max - tempMax)<thr)){
    			return false;
    		}else { //expand more space than needed
    			if((tempMin - min)<0)
    				tempMin -= thr; 
    			if((tempMax - max) > 0)
    				tempMax += thr;
    		}

    		// Any change at all?
    		if((tempMin == min && tempMax == max) || 
    				Double.isInfinite(tempMin) || Double.isInfinite(tempMax) ||
    				Double.isNaN(tempMin) || Double.isNaN(tempMax))
    			return false;
    		
    		// Update axis
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
	public void setXyGraph(final XYGraph xyGraph) {
		this.xyGraph = xyGraph;
	}	
	@Override
	public String toString() {
		return title;
	}

	public void dataChanged(final IDataProvider dataProvider) {
		if(autoScale)
			performAutoScale(false);
	}

	/**The autoScaleThreshold must be a value in range [0,1], which represents a percentage
	 * of the plot area for the threshold when autoScale is performed.The autoScale will performed
	 * only if the spare space exceeds this threshold. So it can reduce the CPU usage by
	 * increasing the threshold.
	 * @param autoScaleThreshold the autoScaleThreshold to set
	 */
	public void setAutoScaleThreshold(final double autoScaleThreshold) {
		if(autoScaleThreshold > 1 || autoScaleThreshold <0)
			throw new RuntimeException("The autoScaleThreshold must be a value in range [0,1]!"); //$NON-NLS-1$
		this.autoScaleThreshold = autoScaleThreshold;
	}
	
	/** @param zoom Zoom Type
	 *  @return <code>true</code> if the zoom type is applicable to this axis
 	 */
	private boolean isValidZoomType(final ZoomType zoom)
	{
	    return zoom == ZoomType.PANNING   ||
	           zoom == ZoomType.RUBBERBAND_ZOOM ||
               zoom == ZoomType.ZOOM_IN   ||    
               zoom == ZoomType.ZOOM_OUT  ||
               (isHorizontal() &&
                (zoom == ZoomType.HORIZONTAL_ZOOM ||
                 zoom == ZoomType.ZOOM_IN_HORIZONTALLY ||
                 zoom == ZoomType.ZOOM_OUT_HORIZONTALLY)
               ) ||
               (!isHorizontal() &&
                (zoom == ZoomType.VERTICAL_ZOOM ||
                 zoom == ZoomType.ZOOM_OUT_VERTICALLY ||
                 zoom == ZoomType.ZOOM_IN_VERTICALLY)
               );
	}

	/**
	 * @param zoomType the zoomType to set
	 */
	public void setZoomType(final ZoomType zoomType)
	{
		this.zoomType = zoomType;
		// Set zoom's cursor if axis allows that type of zoom
		if (isValidZoomType(zoomType))
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
	
	/** Pan axis according to start/end from mouse listener */
	private void pan()
	{
		if(isHorizontal())
		    pan(startRange,
		        getPositionValue(start.x, false), getPositionValue(end.x, false));
		else
            pan(startRange,
                getPositionValue(start.y, false), getPositionValue(end.y, false));
	}
	
	/** Pan the axis
	 *  @param temp Original axis range before the panning started
	 *  @param t1 Start of the panning move
	 *  @param t2 End of the panning move
	 */
	void pan(final Range temp, double t1, double t2)
    {
        if (isLogScaleEnabled())
        {
            final double m = Math.log10(t2) - Math.log10(t1);
            t1 = Math.pow(10,Math.log10(temp.getLower()) - m);
            t2 = Math.pow(10,Math.log10(temp.getUpper()) - m);
        }
        else
        {
            final double m = t2-t1;
            t1 = temp.getLower() - m;
            t2 = temp.getUpper() - m;
        }
        setRange(t1, t2);
    }

    /** Zoom axis
	 *  @param center Axis position at the 'center' of the zoom
	 *  @param factor Zoom factor. Positive to zoom 'in', negative 'out'.
	 */
	public void zoomInOut(final double center, final double factor)
    {
	    final double t1, t2;
	    if (isLogScaleEnabled())
	    {
	        final double l = Math.log10(getRange().getUpper()) - 
                    Math.log10(getRange().getLower());
	        final double r1 = (Math.log10(center) - Math.log10(getRange().getLower()))/l;
	        final double r2 = (Math.log10(getRange().getUpper()) - Math.log10(center))/l;
            t1 = Math.pow(10, Math.log10(getRange().getLower()) + r1 * factor * l);
            t2 = Math.pow(10, Math.log10(getRange().getUpper()) - r2 * factor * l);
        }else{
            final double l = getRange().getUpper() - getRange().getLower();
            final double r1 = (center - getRange().getLower())/l;
            final double r2 = (getRange().getUpper() - center)/l;
            t1 = getRange().getLower() + r1 * factor * l;
            t2 = getRange().getUpper() - r2 * factor * l;              
        }
        setRange(t1, t2);
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


	/** Listener to mouse events, performs panning and some zooms */
	// TODO This is a simpler version of PlotArea.PlotAreaZoomer
	//      Can they become the same, or use same abstract base?
	class AxisMouseListener extends MouseMotionListener.Stub implements MouseListener
	{
		private SaveStateCommand command;
		
		@Override
		public void mouseDragged(final MouseEvent me)
		{
			if (! armed)
				return;
			switch (zoomType)
			{
            case RUBBERBAND_ZOOM:
                // Treat rubberband zoom on axis like horiz/vert. zoom
                if (isHorizontal())
                    end = new Point(me.getLocation().x, bounds.y + bounds.height);
                else
                    end = new Point(bounds.x + bounds.width, me.getLocation().y);
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
			me.consume();				
		}

		public void mouseDoubleClicked(final MouseEvent me) {}

		public void mousePressed(final MouseEvent me)
		{
            // Only react to 'main' mouse button, only react to 'real' zoom
            if (me.button != 1 || ! isValidZoomType(zoomType))
                return;
            armed = true;
            //get start position
            switch (zoomType)
            {
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
                startRange = getRange();
                break;
            case ZOOM_IN:
            case ZOOM_IN_HORIZONTALLY:
            case ZOOM_IN_VERTICALLY:
            case ZOOM_OUT:
            case ZOOM_OUT_HORIZONTALLY:
            case ZOOM_OUT_VERTICALLY:
                start = me.getLocation();
                end = new Point();
                // Start timer that will zoom while mouse button is pressed
                Display.getCurrent().timerExec(ZOOM_SPEED, new Runnable()
                {
                    public void run()
                    {   
                        if (!armed)
                            return;
                        performInOutZoom();
                        Display.getCurrent().timerExec(ZOOM_SPEED, this);
                    }
                });
                break;
            default:
                break;
			}

            //add command for undo operation
            command = new AxisPanOrZoomCommand(zoomType.getDescription(), Axis.this);
            me.consume();
	    }

		@Override
		public void mouseExited(final MouseEvent me)
		{
            // Treat like releasing the button to stop zoomIn/Out timer
		    switch (zoomType)
            {
            case ZOOM_IN:
            case ZOOM_IN_HORIZONTALLY:
            case ZOOM_IN_VERTICALLY:
            case ZOOM_OUT:
            case ZOOM_OUT_HORIZONTALLY:
            case ZOOM_OUT_VERTICALLY:
                mouseReleased(me);
            default:
            }
		}

		public void mouseReleased(final MouseEvent me)
		{
		    if (! armed)
		        return;
            armed = false;
            if (zoomType == ZoomType.PANNING)
                setCursor(zoomType.getCursor());
			if (end == null || start == null || command == null) 
				return;
			
			switch (zoomType)
			{
            case RUBBERBAND_ZOOM:
            case HORIZONTAL_ZOOM:
            case VERTICAL_ZOOM:
                performStartEndZoom();
                break;
            case PANNING:
                pan();                  
                break;  
            case ZOOM_IN:  
            case ZOOM_IN_HORIZONTALLY:
            case ZOOM_IN_VERTICALLY:
            case ZOOM_OUT:
            case ZOOM_OUT_HORIZONTALLY:
            case ZOOM_OUT_VERTICALLY:
                performInOutZoom();
                break;
            default:
                break;
			}
			command.saveState();
			xyGraph.getOperationsManager().addCommand(command);		
			command = null;
            start = null;
            end = null;
		}

		/** Perform the zoom to mouse start/end */
        private void performStartEndZoom()
        {
            final double t1 = getPositionValue(isHorizontal() ? start.x : start.y, false);
            final double t2 = getPositionValue(isHorizontal() ? end.x   : end.y,   false);
            setRange(t1, t2);
        }
		
		/** Perform the in or out zoom according to zoomType */
        private void performInOutZoom()
        {
            final int pixel_pos = isHorizontal() ? start.x : start.y;
            final double center = getPositionValue(pixel_pos, false);
            switch (zoomType)
            {
            case ZOOM_IN:              zoomInOut(center, ZOOM_RATIO); break;
            case ZOOM_IN_HORIZONTALLY: zoomInOut(center, ZOOM_RATIO); break;
            case ZOOM_IN_VERTICALLY:   zoomInOut(center, ZOOM_RATIO); break;
            case ZOOM_OUT:             zoomInOut(center, -ZOOM_RATIO); break;
            case ZOOM_OUT_HORIZONTALLY:zoomInOut(center, -ZOOM_RATIO); break;
            case ZOOM_OUT_VERTICALLY:  zoomInOut(center, -ZOOM_RATIO); break;
            default:                   // NOP
            }
        }
	}
}
