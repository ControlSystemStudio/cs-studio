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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.swt.widgets.datadefinition.ByteArrayWrapper;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.csstudio.swt.widgets.datadefinition.DoubleArrayWrapper;
import org.csstudio.swt.widgets.datadefinition.FloatArrayWrapper;
import org.csstudio.swt.widgets.datadefinition.IPrimaryArrayWrapper;
import org.csstudio.swt.widgets.datadefinition.IntArrayWrapper;
import org.csstudio.swt.widgets.datadefinition.LongArrayWrapper;
import org.csstudio.swt.widgets.datadefinition.ShortArrayWrapper;
import org.csstudio.swt.widgets.figureparts.ColorMapRamp;
import org.csstudio.swt.widgets.figureparts.ROIFigure;
import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.util.SingleSourceHelper;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.SWTConstants;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**An intensity graph figure.
 * @author Xihui Chen
 *
 */
public class IntensityGraphFigure extends Figure implements Introspectable {
	
	/**
	 * Color depth of the image data in RGB1 mode, since SWT only support 8 bit color depth,
	 * it has to convert all data to [0,255].
	 * @author Xihui Chen
	 *
	 */
	public enum ColorDepth {
		BIT8("8 bit"), //No need to convert
		BIT16("16 bit"), //Convert by >>8
		BIT24("24 bit"), //Convert by >>16
		BIT30("30 bit"), //Convert by >>22
		SCALE("Scaled to [Max, Min]"), //Convert by (x-min)/(max-min)*255
		LOWER8BIT("Use only lower 8 bits"); //Convert by &0xFF

		private ColorDepth(String description) {
			this.description = description;
		}

		private String description;

		@Override
		public String toString() {
			return description;
		}

		public static String[] stringValues() {
			String[] sv = new String[values().length];
			int i = 0;
			for (ColorDepth p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}
	
	private static final int MAX_ARRAY_SIZE = 10000000;
	
	/**
	 * ROI listener which will be notified whenever ROI moved.
	 * @author Xihui
	 *
	 */
	public interface IROIListener{
		/**Called whenever ROI updated.
		 * @param xIndex x index of ROI start.
		 * @param yIndex y index of ROI start
		 * @param width width of ROI
		 * @param height height of ROI
		 */
		public void roiUpdated(int xIndex, int yIndex, int width, int height);
	}
	
	/**Provides info to be displayed on ROI label.
	 * @author Xihui Chen
	 *
	 */
	public interface IROIInfoProvider{
		/**Return the information to be displayed on ROI label.
		 * It will called whenever the ROI is repainted.
		 * @param xIndex x index of ROI start.
		 * @param yIndex y index of ROI start
		 * @param width width of ROI
		 * @param height height of ROI
		 */
		public String getROIInfo(int xIndex, int yIndex, int width, int height);
	}	
	
	class SinglePixelProfileCrossHair extends Figure {
		/**
		 * Center coordinates 
		 */
		private int crossX, crossY;
		
		/**
		 * Data index of cross center on cropped data array.
		 */
		private Point crossDataIndex;
		
		private boolean inDefaultPosition = true;
		
		private Polyline hLine, vLine;
		
		private Figure crossPoint;
		
		public SinglePixelProfileCrossHair() {
			hLine = new Polyline();
			hLine.setCursor(Cursors.SIZENS);
			hLine.setForegroundColor(ColorConstants.yellow);
			hLine.setTolerance(3);
			hLine.addMouseMotionListener(new MouseMotionListener.Stub(){
				@Override
				public void mouseDragged(MouseEvent me) {
					setCrossPosition(crossX, me.y, true);
					me.consume();
				}
			});
			hLine.addMouseListener(new MouseListener.Stub(){
				@Override
				public void mousePressed(MouseEvent me) {
					me.consume();
				}
			});
			vLine = new Polyline();
			vLine.setCursor(Cursors.SIZEWE);
			vLine.setForegroundColor(ColorConstants.yellow);
			vLine.setTolerance(3);
			vLine.addMouseListener(new MouseListener.Stub(){
				@Override
				public void mousePressed(MouseEvent me) {
					me.consume();
				}
			});
			vLine.addMouseMotionListener(new MouseMotionListener.Stub(){
				@Override
				public void mouseDragged(MouseEvent me) {
					setCrossPosition(me.x, crossY, true);
					me.consume();
				}
			});
			
			crossPoint = new Figure();
			crossPoint.setCursor(Cursors.SIZEALL);
			crossPoint.addMouseListener(new MouseListener.Stub(){
				@Override
				public void mousePressed(MouseEvent me) {
					me.consume();
				}
			});
			crossPoint.addMouseMotionListener(new MouseMotionListener.Stub(){
				@Override
				public void mouseDragged(MouseEvent me) {
					setCrossPosition(me.x, me.y, true);
					me.consume();
				}
			});
			add(hLine);
			add(vLine);	
			add(crossPoint);
			
			addFigureListener(new FigureListener() {
				
				public void figureMoved(IFigure source) {
					if(crossDataIndex != null){
						Point p = graphArea.getGeoLocation(crossDataIndex.x, crossDataIndex.y);
						setCrossPosition(p.x, p.y, false);
					}
				}
			});	
			
			addCroppedDataSizeListener(new ICroppedDataSizeListener() {
				
				public void croppedDataSizeChanged(int croppedDataWidth,
						int croppedDataHeight) {
					crossDataIndex = graphArea.getDataLocation(crossX, crossY);	
				}
			});
			
			
		}
		
		@Override
		public boolean containsPoint(int x, int y) {
			return hLine.containsPoint(x, y) || vLine.containsPoint(x, y)
					|| crossPoint.containsPoint(x, y);
		}
		
		@Override
		protected void layout() {
			Rectangle bounds = getBounds();
			//First time when it was created.
			if(inDefaultPosition){
				setCrossPosition(bounds.x + bounds.width/2, bounds.y + bounds.height/2, true);
			}else{
				Point p = graphArea.getGeoLocation(crossDataIndex.x, crossDataIndex.y);
				setCrossPosition(p.x, p.y, false);
			}
		}	
		
		public void setCrossHairColor(Color crossHairColor) {
			hLine.setForegroundColor(crossHairColor);
			vLine.setForegroundColor(crossHairColor);
		}
		
		/**set Cross Position
		 * @param x
		 * @param y
		 */
		public void setCrossPosition(int x, int y, boolean updatedCrossDataIndex){
			Rectangle bounds = getBounds();
			if(x < bounds.x)
				crossX = bounds.x;
			else if(x>=bounds.x + bounds.width)
				crossX = bounds.x + bounds.width-1;
			else				
				crossX = x;
			if(y < bounds.y)
				crossY = bounds.y;
			else if(y>=bounds.y + bounds.height)
				crossY = bounds.y + bounds.height-1;
			else
				crossY = y;
			inDefaultPosition = false;
			if(updatedCrossDataIndex){
				crossDataIndex = graphArea.getDataLocation(crossX, crossY);			
				if(croppedDataArray != null)
					fireProfileDataChanged(croppedDataArray, croppedDataWidth, croppedDataHeight);
			}
			hLine.setPoints(new PointList(new int[]{bounds.x,crossY, bounds.width+bounds.x, crossY}));
			vLine.setPoints(new PointList(new int[]{crossX, bounds.y, crossX, bounds.y + bounds.height}));
			crossPoint.setBounds(new Rectangle(crossX-5, crossY-5, 10,10));
		}
		
	}
	
	public class GraphArea extends Figure{
		private final static int CURSOR_SIZE = 14;
		private SinglePixelProfileCrossHair crossHair;
		public GraphArea() {
			if(runMode){
				setCursor(null);
				GraphAreaZoomer zoomer = new GraphAreaZoomer();
				addMouseMotionListener(zoomer);
				addMouseListener(zoomer);				
			}
			setSinglePixelProfiling(isSingleLineProfiling());
		}		
		
		protected void setSinglePixelProfiling(boolean isSinglePixelProfiling) {
			if(!runMode)
				return;
			if(isSingleLineProfiling()){
				if(crossHair == null)
					crossHair = new SinglePixelProfileCrossHair();
				add(crossHair);
			}else if(crossHair != null && crossHair.getParent()==this)
				remove(crossHair);
			dataDirty = true;
			repaint();
		}
			
		@Override
		protected void layout() {
			Rectangle clientArea = getClientArea();
			if(runMode && isSingleLineProfiling()){
				crossHair.setBounds(clientArea);
			}				
			for(ROIFigure roiFigure : roiMap.values()){
				roiFigure.setBounds(clientArea);
			}
		}
		
		
		private synchronized IPrimaryArrayWrapper cropDataArray(int left, int right, int top, int bottom){
			if((left != 0 || right != 0 || top != 0 || bottom != 0) &&
					(dataWidth - left - right) * (dataHeight - top-bottom) >0){
				int i=0;
				if((dataWidth - left - right) * (dataHeight - top - bottom) > MAX_ARRAY_SIZE)
					return dataArray;
				double[] result = null;
				if (inRGBMode) {
					result = new double[(dataWidth - left - right)
							* (dataHeight - top - bottom)*3];
					for (int y = top; y < (dataHeight - bottom); y++) {
						for (int x = left; x < (dataWidth - right); x++) {
							int p=y * dataWidth*3 + x*3;
							result[i] = dataArray.get(p);
							result[i+1]=dataArray.get(p+1);
							result[i+2]=dataArray.get(p+2);
							i+=3;
						}
					}
				} else {
					result = new double[(dataWidth - left - right)
							* (dataHeight - top - bottom)];
					for (int y = top; y < (dataHeight - bottom); y++) {
						for (int x = left; x < (dataWidth - right); x++) {
							result[i++] = dataArray.get(y * dataWidth + x);
						}
					}
				}				
				return new DoubleArrayWrapper(result);
			}else
				return dataArray;			
		}
		
		
		/**Get data index location on cropped data array from geometry location.
		 * @param x x much be inside graph area.
		 * @param y y much be inside graph area
		 * @return
		 */
		public PrecisionPoint getDataLocation(double x, double y){
			Rectangle clientArea = getClientArea();
			double hIndex = croppedDataWidth * (x - clientArea.x)/(double)clientArea.width;
			double vIndex = croppedDataHeight * (y - clientArea.y)/(double)clientArea.height;	
			return new PrecisionPoint(hIndex, vIndex);

		}

		
		/**Get geometry location from data index location on cropped data array.
		 * @param xIndex x index location on cropped data array
		 * @param yIndex y index location on cropped data array
		 * @return
		 */
		public PrecisionPoint getGeoLocation(double xIndex, double yIndex){
			Rectangle clientArea = getClientArea();
			if(croppedDataHeight == 0 || croppedDataWidth ==0)
				return new PrecisionPoint(clientArea.x, clientArea.y);
			double x = (xIndex*clientArea.width)/(double)croppedDataWidth + clientArea.x;
			double y = (yIndex*clientArea.height)/(double)croppedDataHeight + clientArea.y;
			return new PrecisionPoint(x, y);
		}
		
		@Override
		protected synchronized void paintClientArea(Graphics graphics) {			
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
				if(dataWidth == 0 || dataHeight == 0 || (!isInRGBMode() && dataArray.getSize() < dataWidth * dataHeight)
						|| (isInRGBMode() && dataArray.getSize() < 3*dataWidth * dataHeight)){
					graphics.drawRectangle(new Rectangle(
							clientArea.x - (yAxis.isVisible()? 1:0),
							clientArea.y, 
							clientArea.width-(yAxis.isVisible()? 0:1), clientArea.height - (xAxis.isVisible()? 0:1)));
					if(dataArray.getSize() ==0)
						graphics.drawText("No data.", clientArea.getLocation());					
					else if(!isInRGBMode() && dataArray.getSize() < dataWidth * dataHeight)
						graphics.drawText("Size of input data is less than dataWidth*dataHeight!",
								clientArea.getLocation());
					else if(isInRGBMode() && dataArray.getSize() < 3*dataWidth * dataHeight)
						graphics.drawText("Size of input data is less than 3*dataWidth*dataHeight!" + 
								"\nPlease make sure the data is in RGB mode.",
								clientArea.getLocation());
					return;
				}										

				if(dataWidth - cropLeft - cropRight < 0 || dataHeight - cropTop - cropBottom < 0)
					return;
				
				croppedDataArray = cropDataArray(cropLeft, cropRight, cropTop, cropBottom);
				
				fireProfileDataChanged(croppedDataArray, croppedDataWidth, croppedDataHeight);
//				for(ROIFigure roiFigure : roiMap.values()){
//					roiFigure.fireROIUpdated();
//				}
				boolean shrink= false;
				if(clientArea.width*clientArea.height < croppedDataHeight * croppedDataWidth){
					shrink = true;
				}
				
				
				if(shrink){
					if(bufferedImageData == null || bufferedImageData.width != clientArea.width 
							|| bufferedImageData.height !=clientArea.height){
						bufferedImageData = new ImageData(clientArea.width, clientArea.height, 24, colorMap.getPalette());
					}					
				}else if(bufferedImageData == null || bufferedImageData.width != croppedDataWidth
						|| bufferedImageData.height !=croppedDataHeight)
					bufferedImageData = new ImageData(croppedDataWidth, croppedDataHeight, 24, colorMap.getPalette());

					
				ImageData imageData = null;
				if(inRGBMode)
					try {
						imageData = drawRGBImage(croppedDataArray,
								croppedDataWidth, croppedDataHeight,
								max, min, bufferedImageData, shrink);
					} catch (IllegalArgumentException e) {
						graphics.drawText("Drawing Exception: RGB value is not between 0 and 255." +
					"\nPlease check if the data or color depth is correct.",
								clientArea.getLocation());
					}
				else
					imageData = colorMap.drawImage(croppedDataArray,
								croppedDataWidth, croppedDataHeight,
								max, min, bufferedImageData, shrink);		

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
//			System.out.println((System.nanoTime() - startTime)/1000000);
//			startTime = System.nanoTime();
			super.paintClientArea(graphics);

		}
		
		private synchronized void updateTextCursor(MouseEvent me) {
			if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
				return;
					if(croppedDataArray == null)
						return;
					if(getCursor() != null)
						getCursor().dispose();
					double xCoordinate = xAxis.getPositionValue(me.x, false);
					double yCoordinate = yAxis.getPositionValue(me.y, false);
					
					Point dataLocation = getDataLocation(me.x, me.y);		
					if(dataLocation == null)
						return;
					if((dataLocation.y)*croppedDataWidth + dataLocation.x >= croppedDataArray.getSize())
						return;
					double valueUnderMouse;
					if(inRGBMode){
						int index = (dataLocation.y) * croppedDataWidth * 3
								+ dataLocation.x * 3;
						valueUnderMouse = (croppedDataArray.get(index)
								+ croppedDataArray.get(index + 1) + croppedDataArray
								.get(index + 2)) / 3;
					}
					else
						valueUnderMouse = croppedDataArray.get((dataLocation.y)*croppedDataWidth + dataLocation.x);
					String text = "(" + xAxis.format(xCoordinate) + ", " + yAxis.format(yCoordinate) + ", "+ 
						yAxis.format(valueUnderMouse) + ")";
					text = text + getPixelInfo(dataLocation.x + cropLeft, dataLocation.y + cropTop,
							xCoordinate, yCoordinate, valueUnderMouse);							
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
			requestFocus();
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
	
	public interface ICroppedDataSizeListener {
		void croppedDataSizeChanged(int croppedDataWidth, int croppedDataHeight);
	}
	
	public interface IProfileDataChangeLisenter{
		/**Called whenever profile data changed. This is called in a non-UI thread.
		 * @param xProfileData Profile data on x Axis.
		 * @param yProfileData Profile data on y Axis.
		 * @param xAxisRange x Axis range.
		 * @param yAxisRange y Axis range.
		 */
		void profileDataChanged(double[] xProfileData, double[] yProfileData, 
				Range xAxisRange, Range yAxisRange);
	}
	
	public interface IPixelInfoProvider{
		/**Get related information on this pixel, which will be displayed below the cursor.
		 * @param xIndex x index of the pixel
		 * @param yIndex y index of the pixel
		 * @param xCoordinate x axis coordinate of the pixel
		 * @param yCoordinate y axis coordinate of the pixel
		 * @param pixelValue value of the pixel
		 * @return the information about this pixel.
		 */
		public String getPixelInfo(int xIndex, int yIndex,
				double xCoordinate, double yCoordinate, double pixelValue);
	}
	

	private int dataWidth, dataHeight;
	private int cropLeft, cropRight, cropTop, cropBottom;
//	private double[] dataArray;
	private IPrimaryArrayWrapper dataArray;
	
	private IPrimaryArrayWrapper croppedDataArray;
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
	private ImageData bufferedImageData;
	private Image bufferedImage; //the buffered image 
	private List<IProfileDataChangeLisenter> profileListeners;
	private List<IPixelInfoProvider> pixelInfoProviders;
	private List<ICroppedDataSizeListener> croppedDataSizeListeners;
	
	private boolean runMode; 
	
	private Map<String, ROIFigure> roiMap;
//	private long startTime = System.nanoTime();
	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	
	
	private final static Color BLACK_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_BLACK);
	
	private final static Color TRANSPARENT_COLOR = CustomMediaFactory.getInstance().getColor(
			new RGB(123,0,23));
	
	private boolean inRGBMode = false;
	
	private ColorDepth colorDepth = ColorDepth.BIT8;
	
	private PaletteData palette = new PaletteData(0xff, 0xff00, 0xff0000);
	private Boolean savedShowRamp;
	
	private boolean isSingleLineProfiling = false;
	
	private Color roiColor = ColorConstants.cyan;
	
	public IntensityGraphFigure() {
		this(true);
	}
	public IntensityGraphFigure(boolean runMode) {
		this.runMode = runMode;
		dataArray = new DoubleArrayWrapper(new double[0]);
		max = 255;
		min = 0;
		dataWidth = 0;
		dataHeight = 0;
		profileListeners = new ArrayList<IProfileDataChangeLisenter>();
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
		
		roiMap = new HashMap<String, ROIFigure>(2);
		setFocusTraversable(true);
		setRequestFocusEnabled(true);
	}


	public void addProfileDataListener(IProfileDataChangeLisenter listener){
		if(listener != null)
			profileListeners.add(listener);
	}

	public void addPixelInfoProvider(IPixelInfoProvider pixelInfoProvider){
		if(pixelInfoProvider != null){
			if(pixelInfoProviders == null)
				pixelInfoProviders = new ArrayList<IPixelInfoProvider>();
			pixelInfoProviders.add(pixelInfoProvider);
		}			
	}
	
	public void addCroppedDataSizeListener(ICroppedDataSizeListener listener){
		if(croppedDataSizeListeners == null)
			croppedDataSizeListeners = new ArrayList<ICroppedDataSizeListener>();
		croppedDataSizeListeners.add(listener);
	}
	
	/** Add a new ROI to the graph.
	 * @param name name of the ROI. It must be unique for this graph.
	 * @param color color of the ROI.
	 * @param roiListener listener on ROI updates. Can be null.
	 * @param roiInfoProvider provides information for the ROI. Can be null.
	 */
	public void addROI(String name, IROIListener roiListener, IROIInfoProvider roiInfoProvider){
		ROIFigure roiFigure = new ROIFigure(this, name, roiColor, roiListener, roiInfoProvider);
		roiMap.put(name, roiFigure);
		graphArea.add(roiFigure);
	}
	
	public void removeROI(String name){
		if(roiMap.containsKey(name)){
			ROIFigure roiFigure = roiMap.get(name);
			roiMap.remove(name);
			graphArea.remove(roiFigure);			
		}
	}
	
	public void setROIVisible(String name, boolean visible){
		if(roiMap.containsKey(name)){
			roiMap.get(name).setVisible(visible);			
		}
	}

	private double[] calculateXProfileData(IPrimaryArrayWrapper data, int dw, int dh){
		double[] output = new double[dw];
		if(isSingleLineProfiling()){
			Point dataloc = graphArea.getDataLocation(graphArea.crossHair.crossX, 
					graphArea.crossHair.crossY);
			for(int i=0; i<dw; i++){
				if(inRGBMode){
					int index = dataloc.y*dw*3 + i*3;
					output[i] = (data.get(index) + data.get(index + 1) + data
							.get(index + 2)) / 3;
				}else
					output[i] = data.get(dataloc.y*dw + i);
			}
			
		}else {
			for (int i = 0; i < dw; i++) {
				for (int j = 0; j < dh; j++)
					if (inRGBMode) {
						int index = j * dw * 3 + i * 3;
						output[i] += (data.get(index) + data.get(index + 1) + data
								.get(index + 2)) / 3;
					} else
						output[i] += data.get(j * dw + i);
				output[i] /= dh;
			}
		}
		
		return output;
	}

	private double[] calculateYProfileData(IPrimaryArrayWrapper data, int dw,
			int dh) {
		double[] output = new double[dh];
		if (isSingleLineProfiling()) {
			Point dataloc = graphArea.getDataLocation(graphArea.crossHair.crossX, 
					graphArea.crossHair.crossY);
			for (int i = 0; i < dh; i++) {
				if (inRGBMode) {
					int index = dataloc.x *3 + i*dw* 3;
					output[i] = (data.get(index) + data.get(index + 1) + data
							.get(index + 2)) / 3;
				} else
					output[i] = data.get(dataloc.x + i*dw);
			}

		} else {
			for (int i = 0; i < dh; i++) {
				for (int j = 0; j < dw; j++)
					if (inRGBMode) {
						int index = i * dw * 3 + j * 3;
						output[i] += (data.get(index) + data.get(index + 1) + data
								.get(index + 2)) / 3;
					} else
						output[i] += data.get(i * dw + j);
				output[i] /= dw;
			}
		}
		return output;
	}

	public void dispose(){
		if(bufferedImage != null){
			bufferedImage.dispose();
			bufferedImage = null;
		}
	}
	
	/**Calculate the image data from source RGB data array [RGBRGBRGB...].
	 * @param dataArray the source data in RGB mode.
	 * @param dataWidth number of columns of dataArray; This will be the width of image data.
	 * @param dataHeight number of rows of dataArray; This will be the height of image data.
	 * @param max the upper limit of the data in dataArray
	 * @param min the lower limit of the data in dataArray
	 * @param imageData the imageData to be filled. null if a new instance should be created.
	 * @param shrink true if area size of image data is smaller than dataWidth*dataHeight. If this is true, it will use
	 * the nearest neighbor iamge scaling algorithm as described at http://tech-algorithm.com/articles/nearest-neighbor-image-scaling/.
	 * @return the image data. null if dataWidth or dataHeight is less than 1 or larger than the data array.
	 */
	private ImageData drawRGBImage(IPrimaryArrayWrapper dataArray,
			int dataWidth, int dataHeight, double max, double min,
			ImageData imageData, boolean shrink) {
		if (dataWidth < 1 || dataHeight < 1
				|| dataWidth * dataHeight * 3 > dataArray.getSize()
				|| dataWidth * dataHeight < 0)
			return null;
		if (imageData == null)
			imageData = new ImageData(dataWidth, dataHeight, 24, palette);

		if (shrink) {
			int height = imageData.height;
			int width = imageData.width;
			// EDIT: added +1 to account for an early rounding problem
			int x_ratio = (int) ((dataWidth << 16) / width) + 1;
			int y_ratio = (int) ((dataHeight << 16) / height) + 1;
			// int x_ratio = (int)((w1<<16)/w2) ;
			// int y_ratio = (int)((h1<<16)/h2) ;
			int x2, y2;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					x2 = ((j * x_ratio) >> 16);
					y2 = ((i * y_ratio) >> 16);
					int index = y2 * dataWidth * 3 + x2 * 3;
					int pixel = calcRGBPixel(dataArray, max, min, index);
					imageData.setPixel(j, i, pixel);
					
				}
			}

		} else {
			for (int y = 0; y < dataHeight; y++) {
				for (int x = 0; x < dataWidth; x++) {
					// the index of the value in the color table array
					int index = y * dataWidth * 3 + x * 3;					
					int pixel = calcRGBPixel(dataArray, max, min, index);
					imageData.setPixel(x, y, pixel);
				}
			}
		}
		return imageData;
	}
	/**
	 * @param dataArray
	 * @param max
	 * @param min
	 * @param index
	 * @return
	 */
	protected int calcRGBPixel(IPrimaryArrayWrapper dataArray, double max,
			double min, int index) {
		int r = (int) dataArray.get(index);
		int g = (int) dataArray.get(index + 1);
		int b = (int) dataArray.get(index + 2);
		switch (colorDepth) {
		case BIT16:
			r = r >> 8;
			g = g >> 8;
			b = b >> 8;
			break;
		case BIT24:
			r = r >> 16;
			g = g >> 16;
			b = b >> 16;
			break;
		case BIT30:
			r = r >> 22;
			g = g >> 22;
			b = b >> 22;
			break;
		case LOWER8BIT:
			r = r & 0xFF;
			b = b & 0xFF;
			g = g & 0xFF;
			break;
		case SCALE:
			r = (int) ((dataArray.get(index) - min) / (max - min) * 255);
			g = (int) ((dataArray.get(index + 1) - min)	/ (max - min) * 255);
			b = (int) ((dataArray.get(index + 2) - min)	/ (max - min) * 255);
			break;
		case BIT8:
		default:
			break;
		}
//		if(r>255) r=255; else if(r<0) r=0;
//		if(g>255) g=255; else if(g<0) g=0;
//		if(b>255) b=255; else if(b<0) b=0;
		int pixel = palette.getPixel(new RGB(r, g, b));
		return pixel;
	}

	private synchronized void fireProfileDataChanged(final IPrimaryArrayWrapper data,
			final int dw, final int dh) {
		if (profileListeners.size() <= 0)
			return;

		double[] xProfileData = calculateXProfileData(data, dw, dh);
		double[] yProfileData = calculateYProfileData(data, dw, dh);
		for (IProfileDataChangeLisenter lisenter : profileListeners)
			lisenter.profileDataChanged(xProfileData, yProfileData,
					xAxis.getRange(), yAxis.getRange());
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
		double[] data = new double[dataArray.getSize()];
		for(int i=0; i<dataArray.getSize(); i++){
			data[i]=dataArray.get(i);
		}
		return data;
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

	public GraphArea getGraphArea() {
		return graphArea;
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
		//This is a temporary fix of cursor value problem when the axes are invisible
		boolean yVisible = true;// yAxis.isVisible();
		boolean xVisible = true;// xAxis.isVisible();
		if(yVisible){
			width += yAxis.getPreferredSize(getClientArea().width, getClientArea().height).width;
			height += yAxis.getMargin();
			if(!xVisible)
				height += yAxis.getMargin();
		}
		if(xVisible){
			height += xAxis.getPreferredSize(getClientArea().width, getClientArea().height).height;
			if(!colorMapRamp.isVisible())
				width += xAxis.getMargin();
			if(!yVisible)
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
	 * @return true if the input data is in RGB mode. For example, the input data is a 1D array of
	 * [RGBRGBRGBRGB...]
	 */
	public boolean isInRGBMode() {
		return inRGBMode;
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
		//This is a temporary fix of cursor value problem when the axes are invisible
		boolean xVisible = true;// xAxis.isVisible();
		boolean yVisible = true;//yAxis.isVisible();
		if(yVisible){
			Dimension yAxisSize = yAxis.getPreferredSize(clientArea.width, clientArea.height);			
			yAxisBounds = new Rectangle(clientArea.x, clientArea.y, 
					yAxisSize.width, yAxisSize.height); // the height is not correct for now
			clientArea.x += yAxisSize.width;
			clientArea.y += yAxis.getMargin();	
			clientArea.height -= xVisible? yAxis.getMargin() : 2*yAxis.getMargin()-1;				
			clientArea.width -= yAxisSize.width;			
		}
		if(xVisible){
			Dimension xAxisSize = xAxis.getPreferredSize(clientArea.width, clientArea.height);			
			
			xAxisBounds = new Rectangle((yVisible ? yAxisBounds.x + yAxisBounds.width - xAxis.getMargin()-1 : clientArea.x), 
				clientArea.y + clientArea.height - xAxisSize.height,
				xAxisSize.width, xAxisSize.height); // the width is not correct for now
			clientArea.height -= xAxisSize.height;	
			//re-adjust yAxis height					
			if(yVisible){
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
			if(xVisible)
				if(yVisible)
					xAxisBounds.width -=(rampSize.width + GAP - 2*xAxis.getMargin());
				else	
					xAxisBounds.width -= (rampSize.width + GAP - xAxis.getMargin());
		}else{
			//re-adjust xAxis width
			if(xVisible){
				if(yVisible)
					xAxisBounds.width += xAxis.getMargin();
				clientArea.width -= xAxis.getMargin();
			}
				
		}
		
		if(yVisible){
			yAxis.setBounds(yAxisBounds);
		}
		
		if(xVisible){
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
		updateCroppedDataSize();
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
		updateCroppedDataSize();
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
		updateCroppedDataSize();
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
		updateCroppedDataSize();
		repaint();
	}


	/**Set the double[] data array for the intensity graph. It must be called in UI thread.
	 * Warning: for big image for example 1024*768, it may takes several milliseconds (10-50ms)
	 *  to paint the image. If this is called too fast that exceeds the painting capability, 
	 *  it may cause memory leaking.
	 * @param data the dataArray to set
	 * 
	 */
	public final void setDataArray(double[] data) {
		if(dataArray instanceof DoubleArrayWrapper){
			((DoubleArrayWrapper)dataArray).setData(data);
		}else
			dataArray = new DoubleArrayWrapper(data);
		setDataArray(dataArray);
	}
	
	
	/**Set the short[] data array for the intensity graph. It must be called in UI thread.
	 * Warning: for big image for example 1024*768, it may takes several milliseconds (10-50ms)
	 *  to paint the image. If this is called too fast that exceeds the painting capability, 
	 *  it may cause memory leaking.
	 * @param data the dataArray to set
	 * 
	 */
	public final void setDataArray(short[] data) {
		if(dataArray instanceof ShortArrayWrapper){
			((ShortArrayWrapper)dataArray).setData(data);
		}else
			dataArray = new ShortArrayWrapper(data);
		setDataArray(dataArray);
	}
	
	/**Set the byte[] data array for the intensity graph. It must be called in UI thread.
	 * Warning: for big image for example 1024*768, it may takes several milliseconds (10-50ms)
	 *  to paint the image. If this is called too fast that exceeds the painting capability, 
	 *  it may cause memory leaking.
	 * @param data the dataArray to set
	 * 
	 */
	public final void setDataArray(byte[] data) {
		if(dataArray instanceof ByteArrayWrapper){
			((ByteArrayWrapper)dataArray).setData(data);
		}else
			dataArray = new ByteArrayWrapper(data);
		setDataArray(dataArray);
	}
	
	/**Set the int[] data array for the intensity graph. It must be called in UI thread.
	 * Warning: for big image for example 1024*768, it may takes several milliseconds (10-50ms)
	 *  to paint the image. If this is called too fast that exceeds the painting capability, 
	 *  it may cause memory leaking.
	 * @param data the dataArray to set
	 * 
	 */
	public final void setDataArray(int[] data) {
		if(dataArray instanceof IntArrayWrapper){
			((IntArrayWrapper)dataArray).setData(data);
		}else
			dataArray = new IntArrayWrapper(data);
		setDataArray(dataArray);
	}
	
	/**Set the long[] data array for the intensity graph. It must be called in UI thread.
	 * Warning: for big image for example 1024*768, it may takes several milliseconds (10-50ms)
	 *  to paint the image. If this is called too fast that exceeds the painting capability, 
	 *  it may cause memory leaking.
	 * @param data the dataArray to set
	 * 
	 */
	public final void setDataArray(long[] data) {
		if(dataArray instanceof LongArrayWrapper){
			((LongArrayWrapper)dataArray).setData(data);
		}else
			dataArray = new LongArrayWrapper(data);
		setDataArray(dataArray);
	}
	
	/**Set the float[] data array for the intensity graph. It must be called in UI thread.
	 * Warning: for big image for example 1024*768, it may takes several milliseconds (10-50ms)
	 *  to paint the image. If this is called too fast that exceeds the painting capability, 
	 *  it may cause memory leaking.
	 * @param data the dataArray to set
	 * 
	 */
	public final void setDataArray(float[] data) {
		if(dataArray instanceof FloatArrayWrapper){
			((FloatArrayWrapper)dataArray).setData(data);
		}else
			dataArray = new FloatArrayWrapper(data);
		setDataArray(dataArray);
	}
	
	
	
	/**Set the data array wrapper for the intensity graph. It must be called in UI thread.
	 * Warning: for big image for example 1024*768, it may takes several milliseconds (10-50ms)
	 *  to paint the image. If this is called too fast that exceeds the painting capability, 
	 *  it may cause memory leaking.
	 * @param data the dataArray to set
	 * 
	 */
	public synchronized final void setDataArray(IPrimaryArrayWrapper dataWrapper){
		dataArray = dataWrapper;
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
		updateCroppedDataSize();
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
		updateCroppedDataSize();
		dataDirty = true;
		repaint();
	}

	/**Set if the input data is in RGB mode. For example, the input data is a 1D array of
	 * [RGBRGBRGBRGB...]. If it is true, the color of the pixel will come from the 
	 * data directly and the color map will be ignored.
	 * 
	 * @param inRGBMode true if the input data in RGB mode.
	 */
	public synchronized void setInRGBMode(boolean inRGBMode) {
		if(isInRGBMode() == inRGBMode)
			return;
		if(!isInRGBMode()){
			if(savedShowRamp == null)
				savedShowRamp = isShowRamp();
			colorMapRamp.setVisible(false);
		}else if(savedShowRamp != null)
			colorMapRamp.setVisible(savedShowRamp);
		
		this.inRGBMode = inRGBMode;
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
		dataDirty = true;
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
		dataDirty = true;
		repaint();
	}

	/**Set color of ROI figures.
	 * @param roiColor
	 */
	public void setROIColor(Color roiColor) {
		this.roiColor = roiColor;
		for(ROIFigure f : roiMap.values())
			f.setROIColor(roiColor);
	}
	
	public Color getRoiColor() {
		return roiColor;
	};
	
	public void setROIDataBounds(String name, int xIndex, int yIndex, int width, int height){
		if(roiMap.containsKey(name))
			roiMap.get(name).setROIDataBounds(xIndex, yIndex, width, height);
		else
			throw new IllegalArgumentException(name + " is not an existing ROI");
	}
	
	public ROIFigure getROI(String name){
		return roiMap.get(name);
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
		if(!isInRGBMode()){
			colorMapRamp.setVisible(show);
		}
		savedShowRamp = show;
		dataDirty = true;
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
	/**
	 * @return the colorDepth
	 */
	public ColorDepth getColorDepth() {
		return colorDepth;
	}
	/**Set Color depth of the image. 
	 * See http://en.wikipedia.org/wiki/Color_depth
	 * @param colorDepth the colorDepth to set
	 */
	public void setColorDepth(ColorDepth colorDepth) {
		this.colorDepth = colorDepth;
		dataDirty = true;
		repaint();
	}
	/**If it is profiling on single pixel.
	 * @return the isSinglePixelProfiling
	 */
	public boolean isSingleLineProfiling() {
		return isSingleLineProfiling;
	}
	/**Profile on single pixel.
	 * @param isSingleLineProfiling the isSinglePixelProfiling to set
	 */
	public void setSingleLineProfiling(boolean isSingleLineProfiling) {
		if(isSingleLineProfiling() == isSingleLineProfiling)
			return;
		this.isSingleLineProfiling = isSingleLineProfiling;
		graphArea.setSinglePixelProfiling(isSingleLineProfiling);
	}
	
	public String getPixelInfo(int xIndex, int yIndex,
			double xCoordinate, double yCoordinate, double pixelValue){
		String result = "";
		if(pixelInfoProviders == null)
			return result;
		for(IPixelInfoProvider p: pixelInfoProviders){
			result += " " + p.getPixelInfo(xIndex, yIndex, xCoordinate, yCoordinate, pixelValue);			
		}
		return result;
	}

	/**
	 * 
	 */
	protected void updateCroppedDataSize() {
		croppedDataWidth = dataWidth - cropLeft - cropRight;
		croppedDataHeight = dataHeight - cropTop - cropBottom;
		if(croppedDataSizeListeners != null){
			for(ICroppedDataSizeListener listener : croppedDataSizeListeners){
				listener.croppedDataSizeChanged(croppedDataWidth, croppedDataHeight);
			}
		}
	}
	
	
}
