package org.csstudio.opibuilder.widgets.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

/**An intensity graph figure.
 * @author Xihui Chen
 *
 */
public class IntensityGraphFigure extends Figure {
	
	private int dataWidth, dataHeight;
	private double[] dataArray;
	private double max, min;
	
	public IntensityGraphFigure() {
		dataArray = new double[0];
		max = 255;
		min = 0;
		dataWidth = 0;
		dataHeight = 0;
	}
	
	
	@Override
	protected void paintClientArea(Graphics graphics) {
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
	}


	/**
	 * @param max the max to set
	 */
	public final void setMax(double max) {
		this.max = max;
	}


	/**
	 * @param min the min to set
	 */
	public final void setMin(double min) {
		this.min = min;
	}
	
	
	
}
