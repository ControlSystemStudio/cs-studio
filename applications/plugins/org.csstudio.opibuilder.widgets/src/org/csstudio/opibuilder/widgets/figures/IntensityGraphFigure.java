package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.datadefinition.ColorMap;
import org.csstudio.opibuilder.datadefinition.ColorMap.PredefinedColorMap;
import org.csstudio.opibuilder.widgets.figureparts.ColorMapRamp;
import org.csstudio.swt.xygraph.figures.Axis;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**An intensity graph figure.
 * @author Xihui Chen
 *
 */
public class IntensityGraphFigure extends Figure {
	
	private int dataWidth, dataHeight;
	private double[] dataArray;
	private double max, min;
	
	private ColorMapRamp colorMapRamp;
	private GraphArea graphArea;
	private ColorMap colorMap;
	
	private final Axis xAxis;
	private final Axis yAxis;
	
	private final static int GAP = 3;


	public IntensityGraphFigure() {
		dataArray = new double[0];
		max = 255;
		min = 0;
		dataWidth = 0;
		dataHeight = 0;
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
	}


	/**
	 * @param dataHeight the dataHeight to set
	 */
	public final void setDataHeight(int dataHeight) {
		this.dataHeight = dataHeight;
	}


	/**
	 * @param dataArray the dataArray to set
	 */
	public final void setDataArray(double[] dataArray) {
		this.dataArray = dataArray;
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
	}
	
	public void setShowRamp(boolean show){
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


	class GraphArea extends Figure{
		
		@Override
		protected void paintClientArea(Graphics graphics) {
			super.paintClientArea(graphics);
			Rectangle clientArea = getClientArea();		
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
	
			Image image = new Image(Display.getCurrent(), 
					colorMap.drawImage(dataArray, dataWidth, dataHeight, max, min));
			graphics.drawImage(image, new Rectangle(image.getBounds()), clientArea);
			image.dispose();			
		}
		
		
		
		
	}
	
}
