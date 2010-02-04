package org.csstudio.opibuilder.widgets.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.datadefinition.ColorMap;
import org.csstudio.opibuilder.datadefinition.ColorMap.PredefinedColorMap;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.widgets.figureparts.ColorMapRamp;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**An intensity graph figure.
 * @author Xihui Chen
 *
 */
public class IntensityGraphFigure extends Figure {
	
	private int dataWidth, dataHeight;
	private int cropLeft, cropRigth, cropTop, cropBottom;
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
	
	private ExecutionMode executionMode = ExecutionMode.EDIT_MODE;
	
	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE); 
	private final static Color BLACK_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_BLACK); 
	private final static Color TRANSPARENT_COLOR = CustomMediaFactory.getInstance().getColor(
			new RGB(123,0,23)); 
	public IntensityGraphFigure(ExecutionMode executionMode) {
		this.executionMode = executionMode;
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
	
	
//	@Override
	protected void paintClientArea2(Graphics graphics) {
		super.paintClientArea(graphics);		
		PaletteData palette = new PaletteData(0xff, 0xff00, 0xff0000);
		if(dataWidth == 0 || dataHeight == 0)
			return;
		ImageData imageData = new ImageData(dataWidth,dataHeight, 24, palette);
		
		//padding with zero if the array length is not long enough
		if(dataArray.length < dataWidth * dataHeight){
			double[] originalData = dataArray;			
			dataArray = new double[dataWidth*dataHeight];
		    System.arraycopy(originalData, 0, dataArray, 0,originalData.length);
		}
			
		for(int y = 0; y < dataHeight; y++){
			for(int x = 0; x<dataWidth; x++){
				float brightness = (float) ((dataArray[y*dataWidth + x]-min)/(max -min));
				if(brightness > 1) brightness = 1;
				if(brightness < 0) brightness =0;
				int pixel = palette.getPixel(new RGB(0f, 0f, brightness));
				imageData.setPixel(x, y, pixel);
			}
		}
		
		/*for(int x = 0; x < dataWidth; x++){
			for(int y = 0; y<dataHeight; y++){
				float bright = (float) ((dataArray[x*dataHeight + y]-min)/(max -min));
				if(bright > 1) bright = 1;
				if(bright < 0) bright =0;
				int pixel = palette.getPixel(new RGB(360f, 0f, bright));
				imageData.setPixel(x, y, pixel);
			}
		}*/
		
		Image image = new Image(null, imageData);
		try {			
			Rectangle clientArea = getClientArea();
			graphics.drawImage(image, 0, 0, dataWidth, dataHeight,
					clientArea.x, clientArea.y, clientArea.width,
					clientArea.height);
		}finally{
			image.dispose();
		}
		
	}


	/**
	 * @param dataWidth the dataWidth to set
	 */
	public final void setDataWidth(int dataWidth) {
		this.dataWidth = dataWidth;
		dataDirty = true;
	}


	/**
	 * @param dataHeight the dataHeight to set
	 */
	public final void setDataHeight(int dataHeight) {
		this.dataHeight = dataHeight;
		dataDirty = true;
	}


	/**
	 * @param dataArray the dataArray to set
	 */
	public final void setDataArray(double[] dataArray) {
		this.dataArray = dataArray;
		dataDirty = true;
		graphArea.repaint();
	}

	public double[] getDataArray() {
		return dataArray;
	}

	/**
	 * @param max the max to set
	 */
	public final void setMax(double max) {
		this.max = max;
		colorMapRamp.setMax(max);
	}


	/**
	 * @param min the min to set
	 */
	public final void setMin(double min) {
		this.min = min;
		colorMapRamp.setMin(min);
	}
	
	
	/**
	 * @param colorMap the colorMap to set
	 */
	public final void setColorMap(ColorMap colorMap) {
		this.colorMap = colorMap;
		colorMapRamp.setColorMap(colorMap);
		dataDirty = true;
	}
	
	public void setShowRamp(boolean show){
		dataDirty = true;
		colorMapRamp.setVisible(show);
		revalidate();
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
	 * @param cropLeft the cropLeft to set
	 */
	public final void setCropLeft(int cropLeft) {
		this.cropLeft = cropLeft;
		dataDirty = true;
	}


	/**
	 * @param cropRigth the cropRigth to set
	 */
	public final void setCropRigth(int cropRigth) {
		this.cropRigth = cropRigth;
		dataDirty = true;
	}


	/**
	 * @param cropTop the cropTop to set
	 */
	public final void setCropTop(int cropTop) {
		this.cropTop = cropTop;
		dataDirty = true;
	}


	/**
	 * @param cropBottom the cropBottom to set
	 */
	public final void setCropBottom(int cropBottom) {
		this.cropBottom = cropBottom;
		dataDirty = true;
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
	
	
	public void addProfileDataListener(IProfileDataChangeLisenter listener){
		if(listener != null)
			listeners.add(listener);
	}
	
	
	
	private void fireProfileDataChanged(double[] data, int dw, int dh){
		if(listeners.size() <= 0)
			return;
		double[] xProfileData = calculateXProfileData(data, dw, dh);
		double[] yProfileData = calculateYProfileData(data, dw, dh);
		for(IProfileDataChangeLisenter lisenter : listeners)
			lisenter.profileDataChanged(xProfileData, yProfileData, xAxis.getRange(), yAxis.getRange());
	}
	
	
	public interface IProfileDataChangeLisenter {
		void profileDataChanged(double[] xProfileData, double[] yProfileData, 
				Range xAxisRange, Range yAxisRange);
	}
	
	private void zoom(){
		double t1, t2;
		if(xAxisRange == null || yAxisRange == null){
			xAxisRange = xAxis.getRange();
			yAxisRange = yAxis.getRange();	
		}
		if(originalCrop == null){
			originalCrop = new Rectangle(cropLeft, cropTop, cropRigth, cropBottom);
		}
			
				Point leftTop = graphArea.getDataLocation(
					Math.min(start.x, end.x), Math.min(start.y, end.y));
			Point rightBottom = graphArea.getDataLocation(
					Math.max(start.x, end.x), Math.max(start.y, end.y));
			if(leftTop == null || rightBottom == null || leftTop.equals(rightBottom))
				return;
			int toBeCropLeft = cropLeft + leftTop.x;
			int toBeCropTop = cropTop + leftTop.y;
			int toBeCropRight = cropRigth + croppedDataWidth - rightBottom.x;
			int toBeCropBottom = cropBottom + croppedDataHeight - rightBottom.y;
			if(toBeCropLeft + toBeCropRight >= dataWidth || 
					toBeCropBottom + toBeCropTop >=dataHeight)
				return;
			setCropLeft(toBeCropLeft);
			setCropTop(toBeCropTop);
			
			setCropRigth(toBeCropRight);
			setCropBottom(toBeCropBottom);
			graphArea.repaint();
			t1 = xAxis.getPositionValue(start.x, false);
			t2 = xAxis.getPositionValue(end.x, false);
			xAxis.setRange(t1, t2);			
			t1 = yAxis.getPositionValue(start.y, false);
			t2 = yAxis.getPositionValue(end.y, false);
			yAxis.setRange(t1, t2);			
	
		
	}

	public void dispose(){
		if(bufferedImage != null){
			bufferedImage.dispose();
			bufferedImage = null;
		}
	}

	class GraphArea extends Figure{
		private final static int CURSOR_SIZE = 14;
		
		public GraphArea() {
			if(executionMode == ExecutionMode.RUN_MODE){
				setCursor(null);
				GraphAreaZoomer zoomer = new GraphAreaZoomer();
				addMouseMotionListener(zoomer);
				addMouseListener(zoomer);
			}
		}
		private void updateTextCursor(MouseEvent me) {
					if(croppedDataArray == null)
						return;
					if(getCursor() != null)
						getCursor().dispose();
					double xCordinate = xAxis.getPositionValue(me.x, false);
					double yCordinate = yAxis.getPositionValue(me.y, false);
					
					Point dataLocation = getDataLocation(me.x, me.y);		
					if(dataLocation == null)
						return;
					String text = "(" + xAxis.format(xCordinate) + ", " + yAxis.format(yCordinate) + ", "+ 
						yAxis.format(croppedDataArray[(dataLocation.y)*croppedDataWidth + dataLocation.x]) + ")";
					GC mgc = new GC(new Label(Display.getCurrent().getActiveShell(), SWT.None));
					Point size = mgc.textExtent(text);
					mgc.dispose();
					Image image = new Image(Display.getCurrent(),
							size.x + CURSOR_SIZE, size.y + CURSOR_SIZE);
					
					GC gc = new GC(image);
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
					setCursor(new Cursor(null, imageData, CURSOR_SIZE/2 ,CURSOR_SIZE/2));
					gc.dispose();
					image.dispose();
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
			
				croppedDataArray = cropDataArray(cropLeft, cropRigth, cropTop, cropBottom);
				croppedDataWidth = dataWidth - cropLeft - cropRigth;
				croppedDataHeight = dataHeight - cropTop - cropBottom;
				fireProfileDataChanged(croppedDataArray, croppedDataWidth, croppedDataHeight);
				
				bufferedImage = new Image(Display.getCurrent(), 
						colorMap.drawImage(croppedDataArray,
								croppedDataWidth, croppedDataHeight,
								max, min));
				
			}
		
			graphics.drawImage(bufferedImage, new Rectangle(bufferedImage.getBounds()), clientArea);
		
				
			if(armed && end != null && start != null){
				graphics.setLineStyle(SWT.LINE_DOT);
				graphics.setLineWidth(1);				
				graphics.setForegroundColor(BLACK_COLOR);
				graphics.drawRectangle(start.x, start.y, end.x - start.x, end.y - start.y);
			}
		
		}
		
		private double[] cropDataArray(int left, int right, int top, int bottom){
			if(left != 0 || right != 0 || top != 0 || bottom != 0){
				int i=0;
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
	}

	class GraphAreaZoomer extends MouseMotionListener.Stub implements MouseListener{	
				
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
				setCropRigth(originalCrop.width);
				setCropBottom(originalCrop.height);
				graphArea.repaint();
			}
				
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
		
		@Override
		public void mouseMoved(MouseEvent me) {
			graphArea.updateTextCursor(me);
		}
		
	}
	
}
