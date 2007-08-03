package org.csstudio.sds.cosywidgets.ui.internal.utils;

import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.graphics.GC;

import org.csstudio.sds.cosywidgets.ui.internal.utils.Trigonometry;

/**
 * Utility class for drawing nice 3D-looking things.
 * 
 * @author jbercic
 *
 */
public final class ShadedDrawing {
	/**
	 * Draws a line at the given angle, relative to the given center.
	 *  
	 * @param gfx the graphics context
	 * @param minrad the radius at which the line should begin
	 * @param maxrad the radius at which the ine should end
	 * @param angl the angle at which the line is to be drawn
	 * @param x x coordinate of the circle's center
	 * @param y y coordinate of the circle's center
	 */
	public static void drawLineAtAngle(Graphics gfx, double minrad, double maxrad, double angl, int x, int y) {
		int x1,y1,x2,y2;
		
		x1=x+(int)(minrad*Trigonometry.cos(angl));
		y1=y-(int)(minrad*Trigonometry.sin(angl));
		x2=x+(int)(maxrad*Trigonometry.cos(angl));
		y2=y-(int)(maxrad*Trigonometry.sin(angl));
		gfx.drawLine(x1, y1, x2, y2);
	}
}
