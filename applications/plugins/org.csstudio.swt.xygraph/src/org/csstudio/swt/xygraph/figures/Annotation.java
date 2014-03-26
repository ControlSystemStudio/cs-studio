/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.figures;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.swt.xygraph.Preferences;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.undo.MovingAnnotationCommand;
import org.csstudio.swt.xygraph.undo.MovingAnnotationLabelCommand;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * Annotation Figure. Annotation could be used to indicate the information for a particular
 * point.
 * @author Xihui Chen
 *
 */
public class Annotation extends Figure implements IAxisListener, IDataProviderListener {
	/** The way how the cursor line will be drawn. */
	/**
	 * @author Xihui Chen
	 *
	 */
	public enum CursorLineStyle {
		NONE(0, "None"),
		UP_DOWN(1, "Up&Down"),
		LEFT_RIGHT(2, "Left&Right"),
		FOUR_DIRECTIONS(3, "Four Directions");		
		
		private CursorLineStyle(int index, String description) {
			 this.index = index;
			 this.description = description;
		}
		private int index;
		private String description;
		
		@Override
		public String toString() {
			return description;
		}
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(CursorLineStyle p : values())
				sv[i++] = p.toString();
			return sv;
		}	
		/**
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}
		
		
	}
	
	
	
	private Axis xAxis;	
	private Axis yAxis;	
	private String name;
	private FontData fontData;
	
	private CursorLineStyle cursorLineStyle = CursorLineStyle.NONE;
	private Point currentPosition;

	private double xValue;
	private double yValue;
	
	
	private Trace trace;
	private ISample currentSnappedSample;
	
	private boolean showName = true;
	private boolean showSampleInfo = true;
	private boolean showPosition = true;
	
	private Color annotationColor = null;
	private RGB annotationColorRGB = null;
	
	private Label infoLabel;
	//label's relative position to currentPosition
	private double dx = 40;
	private double dy = -40;
	//label's relative center position to currentPosition
	private double x0, y0;
	private boolean knowX0Y0 = false;
	private boolean infoLabelArmed = false;
	
	private Pointer  pointer;
	
	private Polyline vLine, hLine;
	
	private XYGraph xyGraph;
	
	private final static int POINT_SIZE = 6;
	private final static int CURSOR_LINE_LENGTH = 3;
	private final static int ARROW_LINE_LENGTH = 12;
	private boolean pointerDragged;
	
	private List<IAnnotationListener> listeners;
	

	/**Construct an annotation on a trace.
	 * @param name the name of the annotation.
	 * @param trace the trace which the annotation will snap to.
	 */	
	public Annotation(String name, Trace trace) {
		this(name, trace.getXAxis(), trace.getYAxis());
		this.trace = trace;
		trace.getDataProvider().addDataProviderListener(this);
	}
	
	/**Construct a free annotation.
	 * @param xAxis the xAxis of the annotation.
	 * @param yAxis the yAxis of the annotation.
	 * @param name the name of the annotation.
	 */
	public Annotation(String name, Axis xAxis, Axis yAxis) {
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.name = name;
		trace = null;			
		infoLabel = new Label();
		infoLabel.setOpaque(false);
		infoLabel.setCursor(Cursors.SIZEALL);		
		add(infoLabel);
		InfoLabelDragger infoLabelDragger = new InfoLabelDragger();
		infoLabel.addMouseMotionListener(infoLabelDragger);
		infoLabel.addMouseListener(infoLabelDragger);		
		pointer = new Pointer();
		add(pointer);
		updateToDefaultPosition();	
		xAxis.addListener(this);
		yAxis.addListener(this);
	}
	
	public synchronized void addAnnotationListener(IAnnotationListener listener){
		if(listeners == null)
			listeners = new CopyOnWriteArrayList<IAnnotationListener>();
		listeners.add(listener);
	}
	
	public synchronized void removeAnnotationListener(IAnnotationListener listener){
		if(listeners != null)
			listeners.remove(listener);
	}
	
	private void fireAnnotationMoved(double oldX, double oldY, double newX, double newY){
		if(listeners == null)
			 return;
		for(IAnnotationListener listener : listeners){
			listener.annotationMoved(oldX, oldY, newX, newY);
		}
	}
	
	@Override
	public boolean containsPoint(int x, int y) {
		
		return infoLabel.containsPoint(x, y) || pointer.containsPoint(x,y) ||
				(vLine !=null?vLine.containsPoint(x, y):false)||
				(hLine !=null?hLine.containsPoint(x, y):false);
	}
	
	@Override
	protected void layout() {
		super.layout();
		if(trace != null && currentSnappedSample == null &&!pointerDragged)
			updateToDefaultPosition();
		Dimension size = infoLabel.getPreferredSize();
		updateX0Y0Fromdxdy(size);
		Rectangle infoBounds = new Rectangle((int) (currentPosition.x + x0 - size.width/2.0), 
				(int) (currentPosition.y +y0 - size.height/2.0), size.width, size.height);
		
		infoLabel.setBounds(infoBounds);		
		
		pointer.setBounds(new Rectangle(currentPosition.x - POINT_SIZE, currentPosition.y - POINT_SIZE,
				2*POINT_SIZE, 2*POINT_SIZE));
		// layout Cursor Line
		switch (cursorLineStyle) {
		case NONE:
			break;
		case FOUR_DIRECTIONS:
		case LEFT_RIGHT:
			hLine.setPoints(new PointList(new int[] {
					xAxis.getValuePosition(xAxis.getRange().getLower(), false), currentPosition.y,
					xAxis.getValuePosition(xAxis.getRange().getUpper(), false), currentPosition.y }));
			if (cursorLineStyle != CursorLineStyle.FOUR_DIRECTIONS)
				break;
		case UP_DOWN:
			vLine.setPoints(new PointList(new int[] { currentPosition.x,
					yAxis.getValuePosition(yAxis.getRange().getUpper(), false), currentPosition.x,
					yAxis.getValuePosition(yAxis.getRange().getLower(), false) }));
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		if(trace != null && currentSnappedSample == null &&!pointerDragged)
			updateToDefaultPosition();
			
        if (Preferences.useAdvancedGraphics())
            graphics.setAntialias(SWT.ON);
		Color tempColor;
		if(annotationColor == null){
			tempColor = yAxis.getForegroundColor();			
		}else
			tempColor = annotationColor;
		infoLabel.setForegroundColor(tempColor);
		pointer.setForegroundColor(tempColor);
		if(vLine !=null)
			vLine.setForegroundColor(tempColor);
		if(hLine != null)
			hLine.setForegroundColor(tempColor);
		graphics.setForegroundColor(tempColor);			
		
		if(infoLabelArmed) //draw infoLabel Armed rect
			graphics.drawRectangle(infoLabel.getBounds());
		if (showName || showPosition || showSampleInfo) {
			// draw indicate line
			graphics.drawLine(currentPosition.x + (int) dx, currentPosition.y + (int) dy,
					currentPosition.x, currentPosition.y);
			// draw Arrow
			int x1 = (int) (ARROW_LINE_LENGTH * Math.cos(Math.atan(-dy / dx) - Math.PI / 9));
			int y1 = (int) (ARROW_LINE_LENGTH * Math.sin(Math.atan(-dy / dx) - Math.PI / 9));
			if (dx < 0) {
				x1 = -x1;
				y1 = -y1;
			}
			graphics.drawLine(currentPosition.x + x1, currentPosition.y - y1, currentPosition.x,
					currentPosition.y);
			x1 = (int) (ARROW_LINE_LENGTH * Math.cos(Math.atan(-dy / dx) + Math.PI / 9));
			y1 = (int) (ARROW_LINE_LENGTH * Math.sin(Math.atan(-dy / dx) + Math.PI / 9));
			if (dx < 0) {
				x1 = -x1;
				y1 = -y1;
			}
			graphics.drawLine(currentPosition.x + x1, currentPosition.y - y1, currentPosition.x,
					currentPosition.y);
		}
		//draw Cursor Line
		switch (cursorLineStyle) {
		case NONE:
			//left
			graphics.drawLine(currentPosition.x - POINT_SIZE/2, currentPosition.y, 
					currentPosition.x - POINT_SIZE/2 - CURSOR_LINE_LENGTH, currentPosition.y);
			//right
			graphics.drawLine(currentPosition.x + POINT_SIZE/2, currentPosition.y, 
					currentPosition.x + POINT_SIZE/2 + CURSOR_LINE_LENGTH, currentPosition.y);
			//up
			graphics.drawLine(currentPosition.x , currentPosition.y - POINT_SIZE/2, 
					currentPosition.x, currentPosition.y - POINT_SIZE/2 - CURSOR_LINE_LENGTH);
			//down
			graphics.drawLine(currentPosition.x , currentPosition.y + POINT_SIZE/2, 
					currentPosition.x, currentPosition.y + POINT_SIZE/2 + CURSOR_LINE_LENGTH);			
			break;		
		default:
			break;
		}
		
		
	}

	
	
	/**update (x0, y0) if it is unknown.
	 * @param size the label size
	 * @return
	 */
	public void updateX0Y0Fromdxdy(Dimension size) {
		if(!knowX0Y0){
			knowX0Y0 = true;
			int h = size.height;
			int w = size.width;
			if(dy != 0){
				//assume this is the intersection
				y0 = dy-h/2.0;
				x0 = (dx/dy)*y0;
				if(new Range(0, x0).inRange(dx) && new Range(0, y0).inRange(dy) && 
						new Range(x0-w/2.0, x0 + w/2.0).inRange(dx))
					return;
				
				y0 = dy+h/2.0;
				x0 = (dx/dy)*y0;
				if(new Range(0, x0).inRange(dx) && new Range(0, y0).inRange(dy) && 
						new Range(x0-w/2.0, x0 + w/2.0).inRange(dx))
					return;
			}else{
				
			}
			if(dx!=0){
				x0 = dx+w/2.0;
				y0 = (dy/dx)*x0;
				if(new Range(0, x0).inRange(dx) && new Range(0, y0).inRange(dy) && 
						new Range(y0-h/2.0, y0+h/2.0).inRange(dy))
					return;
				
				x0 = dx-w/2.0;
				y0 = (dy/dx)*x0;
				if(new Range(0, x0).inRange(dx) && new Range(0, y0).inRange(dy) && 
						new Range(y0-h/2.0, y0+h/2.0).inRange(dy))
					return;
			}
		}else		
			return;
	}
	
	/**update (dx, dy) if (x0, y0) has been updated by dragging.
	 * @param size the label size
	 * @return
	 */
	private void updatedxdyFromX0Y0() {		
		Dimension size = infoLabel.getPreferredSize();
		int h = size.height;
		int w = size.width;
		if(y0 != 0){
			dy = y0+h/2.0;
			dx = x0*dy/y0;
			if(new Range(0, x0).inRange(dx) && new Range(0, y0).inRange(dy) && 
					new Range(x0-w/2.0, x0 + w/2.0).inRange(dx))
				return;
			
			dy = y0-h/2.0;
			dx = x0*dy/y0;
			if(new Range(0, x0).inRange(dx) && new Range(0, y0).inRange(dy) &&
					new Range(x0-w/2.0, x0 + w/2.0).inRange(dx))
				return;
		}
		else
			dy=0;
		if(x0 != 0){
			dx = x0-size.width/2.0;
			dy = y0*dx/x0;
			if(new Range(0, x0).inRange(dx) && new Range(0, y0).inRange(dy) && 
					new Range(y0-h/2.0, y0+h/2.0).inRange(dy))
				return;
			
			dx = x0+size.width/2.0;
			dy = y0*dx/x0;
			if(new Range(0, x0).inRange(dx) && new Range(0, y0).inRange(dy) &&
					new Range(y0-h/2.0, y0+h/2.0).inRange(dy))
				return;	
		}else
			dx=0;
	}
	
	
	/**
	 * move the annotation to the center of the plot area or trace.
	 */
	private void updateToDefaultPosition(){	
		double oldX = xValue;
		double oldY = yValue;
		if(trace != null && trace.getHotSampleList().size()>0){
			currentSnappedSample = trace.getHotSampleList().get(trace.getHotSampleList().size()/2);
			currentPosition = new Point(xAxis.getValuePosition(currentSnappedSample.getXValue(), false),
				 yAxis.getValuePosition(currentSnappedSample.getYValue(), false));
			xValue = currentSnappedSample.getXValue();
			yValue = currentSnappedSample.getYValue();
		}else{
			currentSnappedSample = null;
			if(xAxis.isLogScaleEnabled())
				xValue = Math.pow(10, (Math.log10(xAxis.getRange().getLower()) +
						Math.log10(xAxis.getRange().getUpper()))/2);
			else
				xValue = (xAxis.getRange().getLower() + xAxis.getRange().getUpper())/2;
			if(yAxis.isLogScaleEnabled())
				yValue = Math.pow(10, (Math.log10(yAxis.getRange().getLower()) +
						Math.log10(yAxis.getRange().getUpper()))/2);
			else
				yValue = (yAxis.getRange().getLower() + yAxis.getRange().getUpper())/2;

			currentPosition = new Point(xAxis.getValuePosition(xValue, false),
				yAxis.getValuePosition(yValue, false));	
		}
		
		updateInfoLableText(true);
		if (oldX != xValue || oldY != yValue) {
			revalidate();
			fireAnnotationMoved(oldX, oldY, xValue, yValue);
		}
	}

	/** Set the position of the annotation based on plot values
	 *  @param x Position as value on the X axis
	 *  @param y Position as value on the Y axis
	 *  @see #setCurrentPosition(Point, boolean) for setting the position based on screen coordinates
	 */
	public void setValues(final double x, final double y)
	{
		double oldX = xValue;
		double oldY = yValue;
		xValue = x;
		yValue = y;

		currentPosition = new Point(xAxis.getValuePosition(xValue, false),
				yAxis.getValuePosition(yValue, false));	
		updateInfoLableText(true);
		revalidate();
		if(oldX != xValue || oldY != yValue)
			fireAnnotationMoved(oldX, oldY, xValue, yValue);
	}
	
	/**
	 * 
	 */
	private void updateInfoLableText(boolean updateX0Y0) {
		String info = "";		
		if(showName)
			info = name;
		if(showSampleInfo && currentSnappedSample != null && !currentSnappedSample.getInfo().equals(""))
			info += "\n" + currentSnappedSample.getInfo();
		if(showPosition)
				info += "\n" + "(" + xAxis.format(xValue) + ", " + 
				(Double.isNaN(yValue) ? "NaN" : yAxis.format(yValue)) + ")";				
		infoLabel.setText(info);
		knowX0Y0 = !updateX0Y0;		
	}
	
	private void updateInfoLableText(){
		updateInfoLableText(true);
	}
	
	
	/**
	 * @param axis the xAxis to set
	 */
	public void setXAxis(Axis axis) {
		if(this.xAxis == axis)
			return;
		xAxis = axis;
		updateToDefaultPosition();	
		revalidate();
		repaint();
	}
	/**
	 * @param axis the yAxis to set
	 */
	public void setYAxis(Axis axis) {
		if(this.yAxis == axis)
			return;
		yAxis = axis;			
		updateToDefaultPosition();
		revalidate();
		repaint();
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
		updateInfoLableText();
	}
	
	
	
	
	@Override
	public void setFont(Font f) {
		// TODO Auto-generated method stub
		super.setFont(f);
		
		if(f != null)
			this.fontData = getFont().getFontData()[0];
	}

	public FontData getFontData() {
		return fontData;
	}

	/**
	 * @param trace the trace to set
	 */
	public void setTrace(Trace trace) {		
		if(this.trace == trace)
			return;
		this.xAxis = trace.getXAxis();
		this.yAxis = trace.getYAxis();			
		if(!isFree() && this.trace != trace)
			this.trace.getDataProvider().removeDataProviderListener(this);		
		if(isFree() || this.trace != trace){
			this.trace = trace;
			updateToDefaultPosition();
		}
		this.trace = trace;
		trace.getDataProvider().addDataProviderListener(this);
		repaint();
	}
	
	/**Make the annotation free.
	 * @param xAxis 
	 * @param yAxis
	 */
	public void setFree(Axis xAxis, Axis yAxis){		
		if(trace != null){			
			trace.getDataProvider().removeDataProviderListener(this);
			trace = null;
			updateToDefaultPosition();	
		}			
		setXAxis(xAxis);
		setYAxis(yAxis);
		revalidate();
		repaint();
	}
	
	/**
	 * @return true if the annotation is free.
	 */
	public boolean isFree(){
		return trace == null;
	}
	
	/**
	 * @param showName the showName to set
	 */
	public void setShowName(boolean showName) {
		this.showName = showName;
		updateInfoLableText();
	}
	/**
	 * @param showSampleInfo the showSampleInfo to set
	 */
	public void setShowSampleInfo(boolean showSampleInfo) {
		this.showSampleInfo = showSampleInfo;
		updateInfoLableText();
	}
	/**
	 * @param showPosition the showPosition to set
	 */
	public void setShowPosition(boolean showPosition) {
		this.showPosition = showPosition;
		updateInfoLableText();
	}
	/**
	 * @param annotationColor the annotationColor to set
	 */
	public void setAnnotationColor(Color annotationColor) {
		this.annotationColor = annotationColor;
		
		if(annotationColor != null)
			this.annotationColorRGB = annotationColor.getRGB();
		
		infoLabel.setForegroundColor(annotationColor);
		pointer.setForegroundColor(annotationColor);
	}

	/**
	 * @param annotationFont the annotationFont to set
	 */
	public void setAnnotationFont(Font annotationFont) {
		infoLabel.setFont(annotationFont);
	}

	/**
	 * @param cursorLineStyle the cursorLineStyle to set
	 */
	public void setCursorLineStyle(CursorLineStyle cursorLineStyle) {
		this.cursorLineStyle = cursorLineStyle;
		if(cursorLineStyle == CursorLineStyle.UP_DOWN || cursorLineStyle == CursorLineStyle.FOUR_DIRECTIONS){
			if(vLine == null){
				vLine = new Polyline();
				vLine.setCursor(Cursors.SIZEWE);
				AnnotationDragger annotationDragger = new AnnotationDragger(true, false);
				vLine.addMouseListener(annotationDragger);
				vLine.addMouseMotionListener(annotationDragger);
				vLine.setTolerance(3);
				add(vLine, 0);				
			}
		}
		if(cursorLineStyle == CursorLineStyle.LEFT_RIGHT || cursorLineStyle == CursorLineStyle.FOUR_DIRECTIONS){
			if(hLine == null){				
				hLine = new Polyline();
				hLine.setCursor(Cursors.SIZENS);
				AnnotationDragger annotationDragger = new AnnotationDragger(false, true);
				hLine.addMouseListener(annotationDragger);
				hLine.addMouseMotionListener(annotationDragger);
				hLine.setTolerance(3);
				add(hLine,0);				
			}
		}
		if(cursorLineStyle == CursorLineStyle.UP_DOWN){
			if(hLine != null){
				remove(hLine);
				hLine=null;
			}
		}
		if(cursorLineStyle == CursorLineStyle.LEFT_RIGHT){
			if(vLine !=null){
				remove(vLine);
				vLine = null;
			}
		}
	}
	
	
	/**
	 * @param currentPosition the currentPosition to set
	 */
	public void setCurrentPosition(Point currentPosition, boolean keepLablePosition, boolean calcValueFromPosition) {
		if(keepLablePosition){
			int deltaX = this.currentPosition.x - currentPosition.x;
			int deltaY = this.currentPosition.y - currentPosition.y;
			x0 +=deltaX;
			y0 +=deltaY;
			knowX0Y0 = true;
			updatedxdyFromX0Y0();			
		}
		this.currentPosition = currentPosition;
		if(calcValueFromPosition){
			double oldX=xValue;
			double oldY=yValue;
			xValue = xAxis.getPositionValue(currentPosition.x, false);
			yValue = yAxis.getPositionValue(currentPosition.y, false);
			fireAnnotationMoved(oldX, oldY, xValue, yValue);
		}
		updateInfoLableText(keepLablePosition);
		revalidate();
		repaint();	
	}
	
	public void setCurrentPosition(Point currentPosition, boolean keepLablePosition){
		setCurrentPosition(currentPosition, keepLablePosition, true);
	}

	/**
	 * @param currentSnappedSample the currentSnappedSample to set
	 * @param keepLabelPosition 
	 */
	public void setCurrentSnappedSample(ISample currentSnappedSample, boolean keepLabelPosition) {
		if(!trace.getHotSampleList().contains(currentSnappedSample))
			updateToDefaultPosition();
		else{
			this.currentSnappedSample = currentSnappedSample;
			Point newPosition = new Point(xAxis.getValuePosition(currentSnappedSample.getXValue(), false),
				 yAxis.getValuePosition(currentSnappedSample.getYValue(), false));
			double oldX=xValue;
			double oldY=yValue;
			xValue = currentSnappedSample.getXValue();
			yValue = currentSnappedSample.getYValue();
			if(Double.isNaN(currentSnappedSample.getXPlusError()))
				yValue = Double.NaN;
			setCurrentPosition(newPosition, keepLabelPosition, false);
			fireAnnotationMoved(oldX, oldY, xValue, yValue);
		}
		repaint();
	}
	
	

	public void axisRevalidated(Axis axis) {
		currentPosition = new Point(xAxis.getValuePosition(xValue, false),
				yAxis.getValuePosition(yValue, false));
		updateInfoLableText();
		if(getParent()!=null)
			layout();
	}
	
	public void axisRangeChanged(Axis axis, Range old_range, Range new_range) {
		//do nothing
	}

	public void dataChanged(IDataProvider dataProvider) {
		if(trace == null)
			return;
		if(trace.getHotSampleList().contains(currentSnappedSample)){
			double oldX=xValue;
			double oldY=yValue;
			if (yValue != currentSnappedSample.getYValue())
			{	// When waveform index is changed, Y value of the 
				// snapped sample is also changed. In that case,
				// the position of this annotation must be updated
				// accordingly.
				yValue = currentSnappedSample.getYValue(); 
			}
			if (xValue != currentSnappedSample.getXValue())
			{
				xValue = currentSnappedSample.getXValue();
			}
			currentPosition = new Point(xAxis.getValuePosition(xValue, false),
				yAxis.getValuePosition(yValue, false));		
			fireAnnotationMoved(oldX, oldY, xValue, yValue);
		} 
		else if(trace.getHotSampleList().size() > 0){
			updateToDefaultPosition();	
			pointerDragged = false;
		}
	}

/**
	 * @param xyGraph the xyGraph to set
	 */
	public void setxyGraph(XYGraph xyGraph) {
		this.xyGraph = xyGraph;
	}
	
	public void setdxdy(double dx, double dy){
		this.dx = dx;
		this.dy = dy;
		knowX0Y0 = false;
		repaint();
	}



/**
	 * @return the xAxis
	 */
	public Axis getXAxis() {
		return xAxis;
	}

	/**
	 * @return the yAxis
	 */
	public Axis getYAxis() {
		return yAxis;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/** @return X value, i.e. value of this annotation on the X Axis */
	public double getXValue() {
		return xValue;
	}

	/** @return Y value, i.e. value of this annotation on the Y Axis */
	public double getYValue() {
		return yValue;
	}
	
	/**
	 * @return the cursorLineStyle
	 */
	public CursorLineStyle getCursorLineStyle() {
		return cursorLineStyle;
	}

	/**
	 * @return the trace
	 */
	public Trace getTrace() {
		return trace;
	}

	/**
	 * @return the showName
	 */
	public boolean isShowName() {
		return showName;
	}

	/**
	 * @return the showSampleInfo
	 */
	public boolean isShowSampleInfo() {
		return showSampleInfo;
	}

	/**
	 * @return the showPosition
	 */
	public boolean isShowPosition() {
		return showPosition;
	}

	/**
	 * @return the annotationColor
	 */
	public Color getAnnotationColor() {
		return annotationColor;
	}



class InfoLabelDragger extends MouseMotionListener.Stub implements MouseListener{
	
	private MovingAnnotationLabelCommand command;
	@Override
	public void mouseDragged(MouseEvent me) {
		x0 = me.getLocation().x - currentPosition.x;
		y0 = me.getLocation().y - currentPosition.y;
		knowX0Y0 = true;
		updatedxdyFromX0Y0();
		Annotation.this.revalidate();
		Annotation.this.repaint();
		me.consume();
	}

	public void mouseDoubleClicked(MouseEvent me) {}
	
	public void mousePressed(MouseEvent me) {
		command = new MovingAnnotationLabelCommand(Annotation.this);
		command.setBeforeMovingDxDy(dx, dy);
		infoLabelArmed = true;
		Annotation.this.revalidate();
		Annotation.this.repaint();
		me.consume(); //it must be consumed to make dragging smoothly.
	}
	
	public void mouseReleased(MouseEvent me) {
		command.setAfterMovingDxDy(dx, dy);
		xyGraph.getOperationsManager().addCommand(command);
		infoLabelArmed = false;
		Annotation.this.revalidate();
		Annotation.this.repaint();
		me.consume();
	}
	
}

	private class AnnotationDragger extends MouseMotionListener.Stub implements MouseListener {

		private MovingAnnotationCommand command;
		
		private boolean horizontalMoveable;
		private boolean verticalMoveable;
		
		

		public AnnotationDragger(boolean horizontalMoveable, boolean verticalMoveable) {
			this.horizontalMoveable = horizontalMoveable;
			this.verticalMoveable = verticalMoveable;
		}

		@Override
		public void mouseDragged(MouseEvent me) {
			Point mouseLocation = new Point(horizontalMoveable? me.getLocation().x: currentPosition.x,
						verticalMoveable?me.getLocation().y:currentPosition.y);
			// free
			if (trace == null) {
				setCurrentPosition(mouseLocation,
						me.getState() == (SWT.BUTTON1 | SWT.CONTROL));
			} else { // snap to trace
						// double tempX =
						// xAxis.getPositionValue(me.getLocation().x, false);
						// double tempY =
						// yAxis.getPositionValue(me.getLocation().y, false);
				ISample tempSample = null;
				double minD = Double.POSITIVE_INFINITY;
				double d;
				for (ISample s : trace.getHotSampleList()) {
					d = Math.sqrt(Math.pow(
							xAxis.getValuePosition(s.getXValue(), false) - mouseLocation.x, 2)
							+ Math.pow(
									yAxis.getValuePosition(s.getYValue(), false)
											- mouseLocation.y, 2));
					if (minD > d) {
						minD = d;
						tempSample = s;
					}
				}
				if (tempSample != null && currentSnappedSample != tempSample)
					setCurrentSnappedSample(tempSample,
							me.getState() == (SWT.BUTTON1 | SWT.CONTROL));
				else if (tempSample == null) {
					setCurrentPosition(mouseLocation,
							me.getState() == (SWT.BUTTON1 | SWT.CONTROL));
					pointerDragged = true;
				}

			}
			me.consume();
		}

		public void mouseDoubleClicked(MouseEvent me) {
		}

		public void mousePressed(MouseEvent me) {
			command = new MovingAnnotationCommand(Annotation.this);
			if (isFree())
				command.setBeforeMovePosition(currentPosition);
			else
				command.setBeforeMoveSnappedSample(currentSnappedSample);
			command.setBeforeDxDy(dx, dy);
			me.consume(); // it must be consumed to make dragging smoothly.
		}

		public void mouseReleased(MouseEvent me) {
			if (command != null) {
				if (isFree())
					command.setAfterMovePosition(currentPosition);
				else
					command.setAfterMoveSnappedSample(currentSnappedSample);
				command.setAfterDxDy(dx, dy);
				xyGraph.getOperationsManager().addCommand(command);
			}

		}
	}
	
class Pointer extends Figure{	
	
	public Pointer() {
		setCursor(Cursors.CROSS);
		AnnotationDragger dragger = new AnnotationDragger(true, true);
		addMouseMotionListener(dragger);
		addMouseListener(dragger);
	}
	
	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
        if (Preferences.useAdvancedGraphics())
            graphics.setAntialias(SWT.ON);
		//draw X-cross point		
		Rectangle clientArea = getClientArea().getCopy().shrink(POINT_SIZE/2, POINT_SIZE/2);		
		graphics.drawLine(clientArea.x, clientArea.y,
				clientArea.x + clientArea.width, clientArea.y + clientArea.height);
		graphics.drawLine(clientArea.x + clientArea.width, clientArea.y,
				clientArea.x, clientArea.y + clientArea.height);
				
	}
}  

public void axisForegroundColorChanged(Axis axis, Color oldColor,
		Color newColor) {

}

public void axisTitleChanged(Axis axis, String oldTitle, String newTitle) {
	
}

public void axisAutoScaleChanged(Axis axis, boolean oldAutoScale,
		boolean newAutoScale) {
	
}

public void axisLogScaleChanged(Axis axis, boolean old, boolean logScale) {
	
}

public RGB getAnnotationColorRGB() {
	return annotationColorRGB;
}


}


