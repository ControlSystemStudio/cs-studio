package org.csstudio.opibuilder.widgets.figureparts;

import org.csstudio.opibuilder.datadefinition.ColorMap;
import org.csstudio.opibuilder.datadefinition.ColorMap.PredefinedColorMap;
import org.csstudio.swt.xygraph.linearscale.LinearScale;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.linearscale.LinearScale.Orientation;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**The color map figure which can be used as the ramp of intensity graph.
 * @author Xihui Chen
 *
 */
public class ColorMapRamp extends Figure {

	private double min, max;
	
	private double[] mapData;
	
	private ColorMap colorMap;
	private LinearScale scale;
	private ColorMapFigure colorMapFigure;
	private final static int RAMP_WIDTH = 25;
	public ColorMapRamp() {
		mapData = new double[256];
		for(int j=0; j<256; j++)
			mapData[j] = 1-j/255.0;
		min = 0;
		max = 1;
		colorMap = new ColorMap(PredefinedColorMap.GrayScale, true, true);
		
		scale = new LinearScale();
		scale.setOrientation(Orientation.VERTICAL);
		scale.setScaleLineVisible(false);
		scale.setTickLableSide(LabelSide.Secondary);
		scale.setMinorTicksVisible(false);
		scale.setRange(min, max);
		scale.setMajorTickMarkStepHint(50);
		colorMapFigure = new ColorMapFigure();
		add(colorMapFigure);
		add(scale);	
	}
	
	
	@Override
	protected void layout() {			
		Rectangle clientArea = getClientArea();
		Dimension scaleSize = scale.getPreferredSize(clientArea.width, clientArea.height);		
		scale.setBounds(new Rectangle(clientArea.x + clientArea.width - scaleSize.width, clientArea.y,
				scaleSize.width, clientArea.height));
		
		colorMapFigure.setBounds(new Rectangle(clientArea.x, scale.getValuePosition(max, false),
				clientArea.width - scaleSize.width, scale.getTickLength()));
		super.layout();
		
	}
	
	@Override
	public Dimension getPreferredSize(int hint, int hint2) {
		Dimension result = super.getPreferredSize(hint, hint2);		
		result.width = RAMP_WIDTH + scale.getPreferredSize(hint, hint2).width;
		return result;
		
	}

	/**
	 * @param min the min to set
	 */
	public final void setMin(double min) {
		this.min = min;
		scale.setRange(min, max);
	}

	/**
	 * @param max the max to set
	 */
	public final void setMax(double max) {
		this.max = max;
		scale.setRange(min, max);
	}

	/**
	 * @param colorMap the colorMap to set
	 */
	public final void setColorMap(ColorMap colorMap) {
		this.colorMap = colorMap;
	}
	
	
	class ColorMapFigure extends Figure{
		
		@Override
		protected void paintClientArea(Graphics graphics) {
			super.paintClientArea(graphics);
			Rectangle clientArea = getClientArea();
			Image image = new Image(Display.getCurrent(), colorMap.drawImage(mapData, 1, 256, 1, 0));
			graphics.drawImage(image, new Rectangle(image.getBounds()), clientArea);
			image.dispose();
		}		
		
	}
	
	
	
	
}
