package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.util.AntialiasingUtil;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.RGB;

/**
 * An arc figure.
 * 
 * @author jbercic
 * 
 */
public final class RefreshableArcFigure extends Shape {
	/**
	 * start angle and length (in degrees) of the arc
	 * should it be drawn filled? (using fill_color)
	 */
	private int start_angle=0,angle=90;
	private RGB fill_color=new RGB(255,0,0);
	
	/**
	 * Is the background transparent or not?
	 */
	private boolean transparent=true;
	
	/**
	 * Border properties.
	 */
	private int border_width;
	private RGB border_color = new RGB(0,0,0);
	
	/**
	 * {@inheritDoc}
	 */
	protected boolean useLocalCoordinates() {
		return true;
	}
	
	/**
	 * Fills the arc.
	 */
	protected void fillShape(Graphics gfx) {
		if (transparent==false) {
			gfx.setBackgroundColor(getBackgroundColor());
			gfx.fillRectangle(getBounds());
		}
		gfx.setBackgroundColor(CustomMediaFactory.getInstance().getColor(fill_color));
		gfx.fillArc(getBounds().getCropped(new Insets(lineWidth/2+lineWidth%2+border_width)),
				start_angle,angle);
	}
	
	/**
	 * Draws the arc.
	 */
	protected void outlineShape(Graphics gfx) {
		if (lineWidth>0) {
			gfx.setLineWidth(lineWidth);
			gfx.drawArc(getBounds().getCropped(new Insets(lineWidth/2+lineWidth%2+border_width)),
					start_angle,angle);
		}
	}
	
	/**
	 * The main drawing routine.
	 */
	public void paintFigure(Graphics gfx) {
		AntialiasingUtil.getInstance().enableAntialiasing(gfx);
		super.paintFigure(gfx);
	}
	
	public void setTransparent(final boolean newval) {
		transparent=newval;
	}
	public boolean getTransparent() {
		return transparent;
	}
	
	public void setBorderWidth(final int newval) {
		border_width=newval;
		if (newval>0) {
			setBorder(new LineBorder(CustomMediaFactory.getInstance().getColor(border_color),border_width));
		} else {
			setBorder(null);
		}
	}
	public int getBorderWidth() {
		return border_width;
	}
	
	public void setBorderColor(final RGB newval) {
		border_color=newval;
		if (border_width>0) {
			setBorder(new LineBorder(CustomMediaFactory.getInstance().getColor(border_color),border_width));
		} else {
			setBorder(null);
		}
	}
	public RGB getBorderColor() {
		return border_color;
	}
	
	public void setStartAngle(final int newval) {
		start_angle=newval;
	}
	public int getStartAngle() {
		return start_angle;
	}
	
	public void setAngle(final int newval) {
		angle=newval;
	}
	public int getAngle() {
		return angle;
	}
	
	public void setFillColor(final RGB newval) {
		fill_color=newval;
	}
	public RGB getFillColor() {
		return fill_color;
	}
}
