package org.csstudio.opibuilder.widgets.figureparts;

import org.csstudio.opibuilder.widgets.util.GraphicsUtil;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * A bulb figure, which could be used for knob, LED etc.
 * @author Xihui Chen
 *
 */
public class Bulb extends Figure{
	
	private Color bulbColor;

	private final static int SQURE_BORDER_WIDTH = 3;
	private final static Color DARK_GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_DARK_GRAY); 
	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE); 
	private final static Color BLACK_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_BLACK); 
	private static final Color COLOR_WHITE = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	private boolean effect3D;

	private boolean squareLED;
	
	public Bulb() {
		setBulbColor(new RGB(150,150,150));
		setEffect3D(true);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public void setBounds(Rectangle rect) {
    	//get the square in the rect
    	rect.width = Math.min(rect.width, rect.height);
    	if(rect.width < 3)
    		rect.width =3;
    	rect.height = rect.width;
    	super.setBounds(rect);  

	}
	@Override
	protected void paintClientArea(Graphics graphics) {		
		graphics.setAntialias(SWT.ON);
		Rectangle clientArea = getClientArea().getCopy();
		boolean support3D = GraphicsUtil.testPatternSupported(graphics);
		
		if (squareLED){
			if(effect3D && support3D){
				//draw up border			
				Pattern pattern = new Pattern(Display.getCurrent(), clientArea.x, clientArea.y, 
					clientArea.x, clientArea.y+SQURE_BORDER_WIDTH, BLACK_COLOR, 20, BLACK_COLOR, 100);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x, clientArea.y, 
					clientArea.x+SQURE_BORDER_WIDTH,clientArea.y + SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, clientArea.y + SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width, clientArea.y});
				pattern.dispose();
				
				//draw left border
				pattern = new Pattern(Display.getCurrent(), clientArea.x, clientArea.y, 
					clientArea.x + SQURE_BORDER_WIDTH, clientArea.y, BLACK_COLOR, 20, BLACK_COLOR, 100);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x, clientArea.y, 
						clientArea.x+SQURE_BORDER_WIDTH,clientArea.y + SQURE_BORDER_WIDTH,
						clientArea.x+SQURE_BORDER_WIDTH, clientArea.y + clientArea.height - SQURE_BORDER_WIDTH,
						clientArea.x, clientArea.y + clientArea.height});
				pattern.dispose();				
				
				//draw bottom border			
				pattern = new Pattern(Display.getCurrent(), clientArea.x, 
					clientArea.y+ clientArea.height - SQURE_BORDER_WIDTH, 
					clientArea.x, clientArea.y+clientArea.height, 
					WHITE_COLOR, 20, WHITE_COLOR, 30);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x, clientArea.y + clientArea.height, 
					clientArea.x+SQURE_BORDER_WIDTH,clientArea.y +clientArea.height - SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, 
					clientArea.y + clientArea.height - SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width, clientArea.y + clientArea.height});
				pattern.dispose();
				
				//draw right border			
				pattern = new Pattern(Display.getCurrent(), clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, 
					clientArea.y, 
					clientArea.x + clientArea.width, clientArea.y, 
					WHITE_COLOR, 20, WHITE_COLOR, 30);			
				graphics.setBackgroundPattern(pattern);
				graphics.fillPolygon(new int[]{clientArea.x + clientArea.width, clientArea.y, 
					clientArea.x+ clientArea.width - SQURE_BORDER_WIDTH,clientArea.y + SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width - SQURE_BORDER_WIDTH, 
					clientArea.y + clientArea.height - SQURE_BORDER_WIDTH,
					clientArea.x + clientArea.width, clientArea.y + clientArea.height});
				pattern.dispose();		
				
				//draw light
				clientArea.shrink(SQURE_BORDER_WIDTH, SQURE_BORDER_WIDTH);
		        graphics.setBackgroundColor(bulbColor);
		        graphics.fillRectangle(clientArea);
				pattern = new Pattern(Display.getCurrent(), clientArea.x,	clientArea.y,
		        		clientArea.x + clientArea.width, clientArea.y + clientArea.height,
		        		WHITE_COLOR, 200, bulbColor, 0);
		        graphics.setBackgroundPattern(pattern);
		       	graphics.fillRectangle(clientArea);		
		       	pattern.dispose();
				
			}else { //if not 3D
				clientArea.shrink(SQURE_BORDER_WIDTH/2, SQURE_BORDER_WIDTH/2);
				graphics.setForegroundColor(DARK_GRAY_COLOR);
				graphics.setLineWidth(SQURE_BORDER_WIDTH);
				graphics.drawRectangle(clientArea);
				
				clientArea.shrink(SQURE_BORDER_WIDTH/2, SQURE_BORDER_WIDTH/2);
		        graphics.setBackgroundColor(bulbColor);
		        graphics.fillRectangle(clientArea);
			}
			
		}
		else {
			if(effect3D && GraphicsUtil.testPatternSupported(graphics)) {			
				// Fills the circle with solid bulb color
		        graphics.setBackgroundColor(bulbColor);
		        graphics.fillOval(bounds);
		        
				//diagonal linear gradient
					Pattern p = new Pattern(Display.getCurrent(), bounds.x,	bounds.y,
							bounds.x + getWidth(), bounds.y + getHeight(),
							COLOR_WHITE, 255, bulbColor, 0);
		        try {				
					graphics.setBackgroundPattern(p);
					graphics.fillOval(bounds);		
					p.dispose();
				} catch (Exception e) {
					p.dispose();				
				}
				
			} else {			
				graphics.setBackgroundColor(bulbColor);
				graphics.fillOval(bounds);
			}		
		}
		super.paintClientArea(graphics);
	}
	
	/**
	 * @param bulbColor the bulbColor to set
	 */
	public void setBulbColor(RGB color) {
		this.bulbColor = CustomMediaFactory.getInstance().getColor(color);
	}
	
	private int getHeight() {
		return bounds.height;
	}

	private int getWidth() {
		return bounds.width;
	}
	
	/**
	 * @param effect3D the effect3D to set
	 */
	public void setEffect3D(boolean effect3D) {
		this.effect3D = effect3D;
	}

	public void setSquareLED(boolean squareLED){
		this.squareLED = squareLED;
	}


	
	
}
