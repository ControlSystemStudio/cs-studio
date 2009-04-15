package org.csstudio.sds.components.ui.internal.figureparts;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * A bulb figure, which could be used for knob, LED etc.
 * @author Xihui Chen
 *
 */
public class Bulb extends Figure {
	
	private RGB bulbColor;
	private boolean dirty;	
	private static final RGB TRANSPARENT_PIXEL = CustomMediaFactory.COLOR_WHITE;; 
	private boolean effect3D;
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	private ImageData imageData;
	
	public Bulb() {
		bulbColor = new RGB(240,240,240);
		dirty = true;	
		setEffect3D(true);
	}
	
	@Override
	public void setBounds(Rectangle rect) {
		if(!bounds.equals(rect))
    		setDirty(true);    	
    	//get the square in the rect
    	rect.width = Math.min(rect.width, rect.height);
    	if(rect.width < 3)
    		rect.width =3;
    	rect.height = rect.width;
    	super.setBounds(rect);  

	}
	@Override
	protected void paintClientArea(Graphics graphics) {		
		
		if(dirty) {
			if(effect3D) {
				BufferedImage awtImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = (Graphics2D) awtImage.getGraphics();				
				g2d.setColor(new java.awt.Color(TRANSPARENT_PIXEL.red, TRANSPARENT_PIXEL.green, TRANSPARENT_PIXEL.blue));
				g2d.fillRect(0, 0, bounds.width, bounds.height);
				drawJava2DBulb(g2d);
				imageData = AWT2SWTImageConverter.convertToSWT(awtImage);				
				imageData.transparentPixel = 
					new RGB(TRANSPARENT_PIXEL.blue, TRANSPARENT_PIXEL.green, TRANSPARENT_PIXEL.red).hashCode();		
			}			
			dirty =false;
		}
		if(effect3D) {
			Image swtImage = new Image(Display.getCurrent(), imageData);	
			graphics.setAntialias(SWT.ON);
			graphics.drawImage(swtImage, bounds.x, bounds.y);
			swtImage.dispose();
		} else {
			graphics.setBackgroundColor(CustomMediaFactory.getInstance().getColor(bulbColor));
			graphics.fillOval(bounds);
		}		
		
		super.paintClientArea(graphics);
	}
	
	/**
	 * @param bulbColor the bulbColor to set
	 */
	public void setBulbColor(RGB color) {
		this.bulbColor = color;
		setDirty(true);
	}

	/**
	 * @param dirty the dirty to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	/**
	 * @return the dirty
	 */
	public boolean isDirty() {
		return dirty;
	}
	
	
	private void drawJava2DBulb(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
	        // Retains the previous state
	        Paint oldPaint = g2.getPaint();
	        // Fills the circle with solid blue color
	        g2.setColor(new Color(bulbColor.red, bulbColor.green, bulbColor.blue));
	        g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
	        
	        Paint p;
	        
	        //diagonal linear gradient
	        p = new GradientPaint((float) (getWidth() / 2 *(1-Math.cos(Math.PI/4.0))-4),
	        		(float) (getHeight() / 2 *(1-Math.cos(Math.PI/4.0))-4), new Color(0,0,0, 0), 
	        		getWidth(), getHeight(), new Color(0, 0, 0, 150));
	        g2.setPaint(p);
	        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
	      
	        // Adds oval specular highlight at the top left
//	        p = new RadialGradientPaint(new Point2D.Double(getWidth() / 2.0,
//	                getHeight() / 2.0), getWidth() / 2f,
//	                new Point2D.Double(getWidth() / 2 *(1-Math.cos(Math.PI/4.0)*4.0/5.0),  getWidth() / 2 *(1-Math.cos(Math.PI/4.0)*4.0/5.0)),
//	                new float[] { 0.0f, 1.0f },
//	                new Color[] { new Color(1.0f, 1.0f, 1.0f, 0.8f),
//	                    new Color(bulbColor.red, bulbColor.green, bulbColor.blue, 0) },
//	                RadialGradientPaint.CycleMethod.NO_CYCLE);
//	        g2.setPaint(p);
//	        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
	        
	        // Restores the previous state
	        g2.setPaint(oldPaint);
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
		setDirty(true);
	}




	
	
}
