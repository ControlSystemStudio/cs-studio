/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.csstudio.swt.widgets.figureparts.ColorMapRamp;
import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.util.SingleSourceHelper;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.SWTConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**An intensity graph figure.
 * @author Xihui Chen
 *
 */
public class IntensityGraphFigure extends Figure implements Introspectable {
	
	private static final int MAX_ARRAY_SIZE = 10000000;

	class GraphArea extends Figure{
		private final static int CURSOR_SIZE = 14;
		
		public GraphArea() {
			if(runMode){
				setCursor(null);
				GraphAreaZoomer zoomer = new GraphAreaZoomer();
				addMouseMotionListener(zoomer);
				addMouseListener(zoomer);
			}
		}
		private synchronized double[] cropDataArray(int left, int right, int top, int bottom){
			if((left != 0 || right != 0 || top != 0 || bottom != 0) &&
					(dataWidth - left - right) * (dataHeight - top-bottom) >0){
				int i=0;
				if((dataWidth - left - right) * (dataHeight - top - bottom) > MAX_ARRAY_SIZE)
					return dataArray;
				double[] result = new double[(dataWidth - left - right) * (dataHeight - top - bottom)];
				for(int y = top; y < (dataHeight-bottom); y++){
					for(int x = left; x<(dataWidth - right); x++){
						result[i++] = dataArray[y*dataWidth + x];
					}
				}
				return result;
			}else
				return dataArray;			
		}
		
		
		public Point getDataLocation(int x, int y){
			if(croppedDataArray == null)
				return null;
			int hIndex = croppedDataWidth * (x - getClientArea().x)/getClientArea().width;
			int vIndex = croppedDataHeight * (y - getClientArea().y)/getClientArea().height;
			if(hIndex >= 0 && vIndex >= 0)
				return new Point(hIndex, vIndex);
			else return null;
		}
		
		@Override
		protected void paintClientArea(Graphics graphics) {
			super.paintClientArea(graphics);
			if(dataArray == null)
				return;
			Rectangle clientArea = getClientArea();
			//draw image if data is dirty or bufferedImage has not been created yet
			if(dataDirty || bufferedImage == null){
				dataDirty = false;
				if(bufferedImage != null){
					bufferedImage.dispose();
					bufferedImage = null;
				}
				if(clientArea.width <0 || clientArea.height <0)
					return;
				if(dataWidth == 0 || dataHeight == 0){
					graphics.drawRectangle(new Rectangle(
							clientArea.x - (yAxis.isVisible()? 1:0),
							clientArea.y, 
							clientArea.width-(yAxis.isVisible()? 0:1), clientArea.height - (xAxis.isVisible()? 0:1)));
					return;
				}
											
				//padding with zero if the array length is not long enough
				if(dataArray.length < dataWidth * dataHeight){
					double[] originalData = dataArray;			
					dataArray = new double[dataWidth*dataHeight];
				    System.arraycopy(originalData, 0, dataArray, 0,originalData.length);
				}
				if(dataWidth - cropLeft - cropRight < 0 || dataHeight - cropTop - cropBottom < 0)
					return;
				croppedDataWidth = dataWidth - cropLeft - cropRight;
				croppedDataHeight = dataHeight - cropTop - cropBottom;
				
					
				croppedDataArray = cropDataArray(cropLeft, cropRight, cropTop, cropBottom);
				
				fireProfileDataChanged(croppedDataArray, croppedDataWidth, croppedDataHeight);
				ImageData imageData = colorMap.drawImage(croppedDataArray,
								croppedDataWidth, croppedDataHeight,
								max, min);
				if(imageData == null)
					return;
				bufferedImage = new Image(Display.getCurrent(), imageData);
				
			}
		
			graphics.drawImage(bufferedImage, new Rectangle(bufferedImage.getBounds()), clientArea);
		
				
			if(armed && end != null && start != null){
				graphics.setLineStyle(SWTConstants.LINE_DOT);
				graphics.setLineWidth(1);				
				graphics.setForegroundColor(BLACK_COLOR);
				graphics.drawRectangle(start.x, start.y, end.x - start.x, end.y - start.y);
			}
		
		}
		
		private synchronized void updateTextCursor(MouseEvent me) {
			if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
				return;
					if(croppedDataArray == null)
						return;
					if(getCursor() != null)
						getCursor().dispose();
					double xCordinate = xAxis.getPositionValue(me.x, false);
					double yCordinate = yAxis.getPositionValue(me.y, false);
					
					Point dataLocation = getDataLocation(me.x, me.y);		
					if(dataLocation == null)
						return;
					if((dataLocation.y)*croppedDataWidth + dataLocation.x >= croppedDataArray.length)
						return;
					String text = "(" + xAxis.format(xCordinate) + ", " + yAxis.format(yCordinate) + ", "+ 
						yAxis.format(croppedDataArray[(dataLocation.y)*croppedDataWidth + dataLocation.x]) + ")";
					Dimension size = FigureUtilities.getTextExtents(
							text, Display.getDefault().getSystemFont());
					Image image = new Image(Display.getDefault(),
							size.width + CURSOR_SIZE, size.height + CURSOR_SIZE);
					
					GC gc = SingleSourceHelper.getImageGC(image);
					//gc.setAlpha(0);
					gc.setBackground(TRANSPARENT_COLOR);					
					gc.fillRectangle(image.getBounds());
					gc.setForeground(BLACK_COLOR);
					gc.drawLine(0, CURSOR_SIZE/2, CURSOR_SIZE, CURSOR_SIZE/2);
					gc.drawLine(CURSOR_SIZE/2, 0, CURSOR_SIZE/2, CURSOR_SIZE);
					gc.setBackground(WHITE_COLOR);
					gc.fillRectangle(CURSOR_SIZE, CURSOR_SIZE, 
							image.getBounds().width-CURSOR_SIZE, 
							image.getBounds().height-CURSOR_SIZE);					
					gc.drawText(text, CURSOR_SIZE, CURSOR_SIZE, true);
					
					ImageData imageData = image.getImageData();
					imageData.transparentPixel = imageData.palette.getPixel(TRANSPARENT_COLOR.getRGB());
					setCursor(SingleSourceHelper.createCursor(Display.getCurrent(),
							imageData, CURSOR_SIZE/2 ,CURSOR_SIZE/2, SWT.CURSOR_CROSS));
					gc.dispose();
					image.dispose();
		}
	}
	class GraphAreaZoomer extends MouseMotionListener.Stub implements MouseListener{	
				
		public void mouseDoubleClicked(MouseEvent me) {
			if(me.button !=1)
				return;
			if(xAxisRange !=null)
				xAxis.setRange(xAxisRange);
			if(yAxisRange != null)
				yAxis.setRange(yAxisRange);
			if(originalCrop != null){
				setCropLeft(originalCrop.x);
				setCropTop(originalCrop.y);
				setCropRight(originalCrop.width);
				setCropBottom(originalCrop.height);
				graphArea.repaint();
			}
				
		}
		
		@Override
		public void mouseDragged(MouseEvent me) {
			if(!armed)
				return;
			if(graphArea.getClientArea().contains(me.getLocation())){
				graphArea.updateTextCursor(me);
				end = me.getLocation();		
				graphArea.repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent me) {
			graphArea.updateTextCursor(me);
		}

		
		public void mousePressed(MouseEvent me) {	
		    // Only react to 'main' mouse button
		    if (me.button != 1)
				return;
			armed = true;
			//get start position
			start = me.getLocation();
			end = null;
			me.consume();			
		}
		
		public void mouseReleased(MouseEvent me) {
			if(!armed || end == null || start == null)
				return;
			zoom();
			armed = false;
			end = null; 
			start = null;			
		}
		
	}
	public interface IProfileDataChangeLisenter extends EventListener{
		void profileDataChanged(double[] xProfileData, double[] yProfileData, 
				Range xAxisRange, Range yAxisRange);
	}
	private int dataWidth, dataHeight;
	private int cropLeft, cropRight, cropTop, cropBottom;
	private double[] dataArray;
	
	private double[] croppedDataArray;
	private int croppedDataWidth, croppedDataHeight;
	private double max, min;
	
	private ColorMapRamp colorMapRamp;
	private GraphArea graphArea;
	private ColorMap colorMap;
	private final Axis xAxis;
	private final Axis yAxis;
	private Range xAxisRange = null;

	private Range yAxisRange = null;
	private Rectangle originalCrop = null;
	private final static int GAP = 3;
	private org.eclipse.draw2d.geometry.Point start;
	private org.eclipse.draw2d.geometry.Point end;
	
	private boolean armed;
	
	private boolean dataDirty;  //true if the image need to be redrawn
	
	private Image bufferedImage; //the buffered image 
	private List<IProfileDataChangeLisenter> listeners; 
	private boolean runMode; 
	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	
	
	private final static Color BLACK_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_BLACK);
	
	private final static Color TRANSPARENT_COLOR = CustomMediaFactory.getInstance().getColor(
			new RGB(123,0,23));
	
	
	
	public IntensityGraphFigure() {
		this(true);
	}
	public IntensityGraphFigure(boolean runMode) {
		this.runMode = runMode;
		dataArray = new double[0];
		max = 255;
		min = 0;
		dataWidth = 0;
		dataHeight = 0;
		listeners = new ArrayList<IProfileDataChangeLisenter>();
		colorMap = new ColorMap(PredefinedColorMap.GrayScale, true, true);
		colorMapRamp = new ColorMapRamp();
		colorMapRamp.setMax(max);
		colorMapRamp.setMin(min);
		colorMapRamp.setColorMap(colorMap);
		
		graphArea = new GraphArea();
		xAxis = new Axis("X", false);
		yAxis = new Axis("Y", true);
		add(colorMapRamp);
		add(graphArea);
		add(xAxis);
		add(yAxis);
	}


	public void addProfileDataListener(IProfileDataChangeLisenter listener){
		if(listener != null)
			listeners.add(listener);
	}


	private double[] calculateXProfileData(double[] data, int dw, int dh){
		double[] output = new double[dw];
		for(int i =0; i<dw; i++){
			for(int j = 0; j < dh; j++)
				output[i] += data[j*dw + i];
			output[i] /= dh;
		}
		return output;
	}


	private double[] calculateYProfileData(double[] data, int dw, int dh){
		double[] output = new double[dh];
		for(int i =0; i<dh; i++){
			for(int j = 0; j < dw; j++)
				output[i] += data[i*dw + j];
			output[i] /= dw;
		}
		return output;
	}

	public void dispose(){
		if(bufferedImage != null){
			bufferedImage.dispose();
			bufferedImage = null;
		}
	}

	private void fireProfileDataChanged(double[] data, int dw, int dh){
		if(listeners.size() <= 0)
			return;
		double[] xProfileData = calculateXProfileData(data, dw, dh);
		double[] yProfileData = calculateYProfileData(data, dw, dh);
		for(IProfileDataChangeLisenter lisenter : listeners)
			lisenter.profileDataChanged(xProfileData, yProfileData, xAxis.getRange(), yAxis.getRange());
	}


	/**
	 * @return the colorMap
	 */
	public ColorMap getColorMap() {
		return colorMap;
	}
	
	
	/**
	 * @return the cropBottom
	 */
	public int getCropBottom() {
		return cropBottom;
	}
	
	/**
	 * @return the cropLeft
	 */
	public int getCropLeft() {
		return cropLeft;
	}
	
	
	/**
	 * @return the cropRigth
	 */
	public int getCropRight() {
		return cropRight;
	}


	/**
	 * @return the cropTop
	 */
	public int getCropTop() {
		return cropTop;
	}

	
	

	public double[] getDataArray() {
		return dataArray;
	}


	/**
	 * @return the dataHeight
	 */
	public int getDataHeight() {
		return dataHeight;
	}


	/**
	 * @return the dataWidth
	 */
	public int getDataWidth() {
		return dataWidth;
	}


	/**
	 * @return the two dimension insets (cropped_width, cropped_height) of graph area
	 */
	public Dimension getGraphAreaInsets() {
		
		int width = getInsets().getWidth();
		int height = getInsets().getHeight();
		if(colorMapRamp.isVisible())
			width += (colorMapRamp.getPreferredSize(
				getClientArea().width, getClientArea().height).width + GAP);
		if(yAxis.isVisible()){
			width += yAxis.getPreferredSize(getClientArea().width, getClientArea().height).width;
			height += yAxis.getMargin();
			if(!xAxis.isVisible())
				height += yAxis.getMargin();
		}
		if(xAxis.isVisible()){
			height += xAxis.getPreferredSize(getClientArea().width, getClientArea().height).height;
			if(!colorMapRamp.isVisible())
				width += xAxis.getMargin();
			if(!yAxis.isVisible())
				width += xAxis.getMargin();
				
		}
	
		return new Dimension(width, height);
	}

	/**
	 * @return the max
	 */
	public double getMax() {
		return max;
	}
	
	/**
	 * @return the min
	 */
	public double getMin() {
		return min;
	}
	
	
	/**
	 * @return the xAxis
	 */
	public final Axis getXAxis() {
		return xAxis;
	}
	
	
	
	/**
	 * @return the yAxis
	 */
	public final Axis getYAxis() {
		return yAxis;
	}
	
	
	/**
	 * @return the runMode
	 */
	public boolean isRunMode() {
		return runMode;
	}

	public boolean isShowRamp(){
		return colorMapRamp.isVisible();
	}

	@Override
	protected void layout() {	
		Rectangle clientArea = getClientArea().getCopy();	

		Rectangle yAxisBounds = null, xAxisBounds = null, rampBounds;
		if(yAxis.isVisible()){
			Dimension yAxisSize = yAxis.getPreferredSize(clientArea.width, clientArea.height);			
			yAxisBounds = new Rectangle(clientArea.x, clientArea.y, 
					yAxisSize.width, yAxisSize.height); // the height is not correct for now
			clientArea.x += yAxisSize.width;
			clientArea.y += yAxis.getMargin();	
			clientArea.height -= xAxis.isVisible()? yAxis.getMargin() : 2*yAxis.getMargin()-1;				
			clientArea.width -= yAxisSize.width;			
		}
		if(xAxis.isVisible()){
			Dimension xAxisSize = xAxis.getPreferredSize(clientArea.width, clientArea.height);			
			
			xAxisBounds = new Rectangle((yAxis.isVisible() ? yAxisBounds.x + yAxisBounds.width - xAxis.getMargin()-1 : clientArea.x), 
				clientArea.y + clientArea.height - xAxisSize.height,
				xAxisSize.width, xAxisSize.height); // the width is not correct for now
			clientArea.height -= xAxisSize.height;	
			//re-adjust yAxis height					
			if(yAxis.isVisible()){
				yAxisBounds.height -= (xAxisSize.height - yAxis.getMargin()); 
			}else{
				clientArea.x +=xAxis.getMargin();
				clientArea.width -= xAxis.getMargin()-1;
			}
		
		}
		if(colorMapRamp.isVisible()){
			Dimension rampSize = colorMapRamp.getPreferredSize(clientArea.width, clientArea.height);
			rampBounds = new Rectangle(clientArea.x + clientArea.width - rampSize.width, clientArea.y,
					rampSize.width, clientArea.height);
			colorMapRamp.setBounds(rampBounds);
			clientArea.width -= (rampSize.width + GAP);
			//re-adjust xAxis width
			if(xAxis.isVisible())
				if(yAxis.isVisible())
					xAxisBounds.width -=(rampSize.width + GAP - 2*xAxis.getMargin());
				else	
					xAxisBounds.width -= (rampSize.width + GAP - xAxis.getMargin());
		}else{
			//re-adjust xAxis width
			if(xAxis.isVisible()){
				if(yAxis.isVisible())
					xAxisBounds.width += xAxis.getMargin();
				clientArea.width -= xAxis.getMargin();
			}
				
		}
		
		if(yAxis.isVisible()){
			yAxis.setBounds(yAxisBounds);
		}
		
		if(xAxis.isVisible()){
			xAxis.setBounds(xAxisBounds);
		}
			
		graphArea.setBounds(clientArea);
		super.layout();
	}


	/**
	 * @param colorMap the colorMap to set
	 */
	public final void setColorMap(ColorMap colorMap) {
		if(colorMap == null)
			return;
		this.colorMap = colorMap;
		colorMapRamp.setColorMap(colorMap);
		dataDirty = true;
		repaint();
	}


	/**
	 * @param cropBottom the cropBottom to set
	 */
	public final void setCropBottom(int cropBottom) {
		if(cropBottom < 0 || cropBottom + cropTop > dataHeight)
			throw new IllegalArgumentException();
		if(this.cropBottom == cropBottom)
			return;
		this.cropBottom = cropBottom;
		dataDirty = true;
		repaint();

	}


	/**
	 * @param cropLeft the cropLeft to set
	 */
	public final void setCropLeft(int cropLeft) {
		if(cropLeft <0 || cropLeft + cropRight > dataWidth)
			throw new IllegalArgumentException();
		if(this.cropLeft == cropLeft)
			return;
		this.cropLeft = cropLeft;
		dataDirty = true;
		repaint();

	}


	/**
	 * @param cropRight the cropRigth to set
	 */
	public final void setCropRight(int cropRight) {
		if(cropRight < 0 || cropRight + cropLeft > dataWidth)
			throw new IllegalArgumentException();
		if(this.cropRight == cropRight)
			return;
		this.cropRight = cropRight;
		dataDirty = true;
		repaint();
	}


	/**
	 * @param cropTop the cropTop to set
	 */
	public final void setCropTop(int cropTop) {
		if(cropTop < 0 || cropTop + cropBottom > dataHeight)
			throw new IllegalArgumentException();
		if(this.cropTop == cropTop)
			return;
		this.cropTop = cropTop;
		dataDirty = true;
		repaint();
	}


	/**
	 * @param dataArray the dataArray to set
	 */
	public final void setDataArray(double[] dataArray) {
		this.dataArray = dataArray;
		croppedDataArray = null;
		dataDirty = true;
		graphArea.repaint();
	}


	/**
	 * @param dataHeight the dataHeight to set
	 */
	public final void setDataHeight(int dataHeight) {
		if(dataHeight <0|| dataWidth * dataHeight > MAX_ARRAY_SIZE || dataWidth * dataHeight < 0)
			throw new IllegalArgumentException();
		if(this.dataHeight == dataHeight)
			return;
		this.dataHeight = dataHeight;
		dataDirty = true;
		repaint();
	}


	/**
	 * @param dataWidth the dataWidth to set
	 */
	public final void setDataWidth(int dataWidth) {
		if(dataWidth < 0 || dataWidth * dataHeight > MAX_ARRAY_SIZE || dataWidth * dataHeight < 0)
			throw new IllegalArgumentException();
		if(this.dataWidth == dataWidth)
			return;
		this.dataWidth = dataWidth;
		dataDirty = true;
		repaint();
	}


	/**
	 * @param max the max to set
	 */
	public final void setMax(double max) {
		if(this.max == max)
			return;
		this.max = max;
		colorMapRamp.setMax(max);
		repaint();
	}
	
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		colorMapRamp.setFont(f);
	}
	
	/**
	 * @param min the min to set
	 */
	public final void setMin(double min) {
		if(this.min == min)
			return;
		this.min = min;
		colorMapRamp.setMin(min);
		repaint();
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(boolean runMode) {
		this.runMode = runMode;
	}

	public void setShowRamp(boolean show){
		if(isShowRamp() == show)
			return;
		dataDirty = true;
		colorMapRamp.setVisible(show);
		revalidate();
	}

	private void zoom(){
		double t1, t2;
		if(xAxisRange == null || yAxisRange == null){
			xAxisRange = xAxis.getRange();
			yAxisRange = yAxis.getRange();	
		}
		if(originalCrop == null){
			originalCrop = new Rectangle(cropLeft, cropTop, cropRight, cropBottom);
		}
			
				Point leftTop = graphArea.getDataLocation(
					Math.min(start.x, end.x), Math.min(start.y, end.y));
			Point rightBottom = graphArea.getDataLocation(
					Math.max(start.x, end.x), Math.max(start.y, end.y));
			if(leftTop == null || rightBottom == null || leftTop.equals(rightBottom))
				return;
			int toBeCropLeft = cropLeft + leftTop.x;
			int toBeCropTop = cropTop + leftTop.y;
			int toBeCropRight = cropRight + croppedDataWidth - rightBottom.x;
			int toBeCropBottom = cropBottom + croppedDataHeight - rightBottom.y;
			if(toBeCropLeft + toBeCropRight >= dataWidth || 
					toBeCropBottom + toBeCropTop >=dataHeight)
				return;
			setCropLeft(toBeCropLeft);
			setCropTop(toBeCropTop);
			
			setCropRight(toBeCropRight);
			setCropBottom(toBeCropBottom);
			graphArea.repaint();	
			
			t1 = xAxis.getPositionValue(start.x, false);
			t2 = xAxis.getPositionValue(end.x, false);
			xAxis.setRange(t1, t2, true);			
			t1 = yAxis.getPositionValue(start.y, false);
			t2 = yAxis.getPositionValue(end.y, false);
			yAxis.setRange(t1, t2,true);	
	}
	
	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}
	
}
