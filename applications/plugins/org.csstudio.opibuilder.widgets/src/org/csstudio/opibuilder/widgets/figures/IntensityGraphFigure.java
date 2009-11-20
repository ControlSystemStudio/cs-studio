package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.datadefinition.ColorMap;
import org.csstudio.opibuilder.datadefinition.ColorMap.PredefinedColorMap;
import org.csstudio.opibuilder.widgets.figureparts.ColorMapRamp;
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
		
		add(colorMapRamp);
		add(graphArea);
	}
	
	
	@Override
	protected void layout() {		
		Rectangle clientArea = getClientArea();		
		
		if(colorMapRamp.isVisible()){
			Dimension rampSize = colorMapRamp.getPreferredSize(clientArea.width, clientArea.height);
			colorMapRamp.setBounds(new Rectangle(clientArea.x + clientArea.width - rampSize.width, clientArea.y,
					rampSize.width, clientArea.height));
			graphArea.setBounds(new Rectangle(clientArea.x, clientArea.y, 
					clientArea.width - rampSize.width - GAP, clientArea.height));
		}else
			graphArea.setBounds(clientArea);
		
		super.layout();
	}
	
	/**
	 * @return the insets for graph area
	 */
	public Dimension getGraphAreaInsets() {
		if(colorMapRamp.isVisible())
			return new Dimension(getInsets().left + getInsets().right + colorMapRamp.getPreferredSize(
				getClientArea().width, getClientArea().height).width + GAP,
				getInsets().top + getInsets().bottom);
		else
			return new Dimension(getInsets().getWidth(), getInsets().getHeight());
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
	
	
	class GraphArea extends Figure{
		
		@Override
		protected void paintClientArea(Graphics graphics) {
			super.paintClientArea(graphics);
			Rectangle clientArea = getClientArea();		
			
			if(dataWidth == 0 || dataHeight == 0){
				graphics.drawRectangle(clientArea.getCopy().shrink(1, 1));
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
