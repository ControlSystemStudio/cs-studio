package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.widgets.figureparts.Bulb;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

/**
 * LED figure
 * @author Xihui Chen
 *
 */
public class LEDFigure extends AbstractBoolFigure {

	Bulb bulb; 
	private final static int OUTLINE_WIDTH = 2;
	private boolean effect3D = true;
	private boolean squareLED = false;
	public LEDFigure() {
		super();
		bulb = new Bulb();		
		setLayoutManager(new XYLayout());
		add(bulb);
		add(boolLabel);
		bulb.setBulbColor(boolValue ? onColor.getRGB() : offColor.getRGB());		
	}
	
	@Override
	protected void layout() {	
		Rectangle bulbBounds = getClientArea().getCopy();
		if(bulb.isVisible() ){			
			bulbBounds.shrink(OUTLINE_WIDTH, OUTLINE_WIDTH);
			bulb.setBounds(bulbBounds);
		}		
		if(boolLabel.isVisible()){
			Dimension labelSize = boolLabel.getPreferredSize();				
			boolLabel.setBounds(new Rectangle(bulbBounds.x + bulbBounds.width/2 - labelSize.width/2,
					bulbBounds.y + bulbBounds.height/2 - labelSize.height/2,
					labelSize.width, labelSize.height));
		}
		super.layout();
	}
	@Override
	public void setOnColor(RGB onColor) {
		super.setOnColor(onColor);
		if(boolValue && bulb.isVisible())
			bulb.setBulbColor(onColor);
	}
	
	@Override
	public void setOffColor(RGB offColor) {
		super.setOffColor(offColor);
		if(!boolValue  && bulb.isVisible())
			bulb.setBulbColor(offColor);
	}
	
	@Override
	protected void updateBoolValue() {
		super.updateBoolValue();
		bulb.setBulbColor(boolValue ? onColor.getRGB() : offColor.getRGB());
		
	}
	
	/**
	 * @param effect3D the effect3D to set
	 */
	public void setEffect3D(boolean effect3D) {
		this.effect3D = effect3D;
		bulb.setEffect3D(this.effect3D);
	}

	/**
	 * @param squareLED the squareLED to set
	 */
	public void setSquareLED(boolean squareLED) {
		this.squareLED = squareLED;
		bulb.setSquareLED(this.squareLED);
		}
}
