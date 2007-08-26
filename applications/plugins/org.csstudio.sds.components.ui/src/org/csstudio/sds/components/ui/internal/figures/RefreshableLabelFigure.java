package org.csstudio.sds.components.ui.internal.figures;

import java.text.NumberFormat;

import org.csstudio.sds.components.ui.internal.utils.TextPainter;
import org.csstudio.sds.util.AntialiasingUtil;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;

/**
 * A label figure.
 * 
 * @author jbercic
 * 
 */
public final class RefreshableLabelFigure extends Shape {
	
	/**
	 * Types of values to be displayed.
	 */
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_DOUBLE = 1;
	
	/**
	 * Type of the label.
	 */
	private int value_type=TYPE_DOUBLE;
	
	/**
	 * The potenz for the precision.
	 */
	private int _decimalPlaces = 2;
	
	/**
	 * Default label font.
	 */
	public Font font = CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE);
	
	/**
	 * An Array, which contains the PositionConstants for Center, Top, Bottom, Left, Right.
	 */
	private final int[] alignments = new int[] {PositionConstants.CENTER, PositionConstants.TOP, PositionConstants.BOTTOM, PositionConstants.LEFT, PositionConstants.RIGHT};
	
	/**
	 * Things to do with the displayed text:
	 *   alignment, rotation angle, coordinate offsets (if rotation goes out of bounds).
	 */
	private int alignment=0;
	private double rotation=90.0;
	private int x_off=0,y_off=0;
	
	/**
	 * Value fields. Currently only text and double values;
	 */
	private String text_value="";
	
	private double double_value=0.0;
	//private String double_value_format="%.3f";
	
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
	 * Fills the image. Nothing to do here.
	 */
	protected void fillShape(Graphics gfx) {}
	
	/**
	 * Draws the outline of the image. Nothing to do here.
	 */
	protected void outlineShape(Graphics gfx) {}
	
	/**
	 * The main drawing routine.
	 */
	public void paintFigure(Graphics gfx) {
		
		Rectangle bound=getBounds();
		gfx.translate(bound.x,bound.y);
		
		if (transparent==false) {
			gfx.setBackgroundColor(getBackgroundColor());
			gfx.fillRectangle(0,0,bound.width,bound.height);
		}
		gfx.setFont(font);
		gfx.setForegroundColor(getForegroundColor());
		AntialiasingUtil.getInstance().enableAntialiasing(gfx);
		
		String toprint="none";
		switch (value_type) {
		case TYPE_TEXT:
			toprint=text_value;
			break;
		case TYPE_DOUBLE:
			try {
				double d = Double.parseDouble(text_value);
				NumberFormat format = NumberFormat.getInstance();
				format.setMaximumFractionDigits(_decimalPlaces);
				toprint = format.format(d);
			} catch (Exception e) {
				toprint = text_value;
			}
//			try {
//				toprint=String.format(double_value_format,double_value);
//			} catch (IllegalFormatException e) {
//				toprint=Double.toString(double_value);
//			}
			break;
		default:
			toprint="unknown value type";
		}
		
		switch (alignment) {
		case 0: //center
			if (Math.round(rotation)==90) {
				TextPainter.drawText(gfx,toprint,bound.width/2+x_off,bound.height/2+y_off,TextPainter.CENTER);
			}
			else {
				TextPainter.drawRotatedText(gfx,toprint,90.0-rotation,
						bound.width/2+x_off,bound.height/2+y_off,TextPainter.CENTER);
			}
			break;
		case 1: //top
			if (Math.round(rotation)==90) {
				TextPainter.drawText(gfx,toprint,bound.width/2+x_off,y_off,TextPainter.TOP_CENTER);
			}
			else {
				TextPainter.drawRotatedText(gfx,toprint,90.0-rotation,
						bound.width/2+x_off,y_off,TextPainter.TOP_CENTER);
			}
			break;
		case 2: //bottom
			if (Math.round(rotation)==90) {
				TextPainter.drawText(gfx,toprint,bound.width/2+x_off,bound.height+y_off,TextPainter.BOTTOM_CENTER);
			} 
			else {
				TextPainter.drawRotatedText(gfx,toprint,90.0-rotation,
						bound.width/2+x_off,bound.height+y_off,TextPainter.BOTTOM_CENTER);
			}
			break;
		case 3: //left
			if (Math.round(rotation)==90) {
				TextPainter.drawText(gfx,toprint,x_off,bound.height/2+y_off,TextPainter.LEFT);
			}
			else {
				TextPainter.drawRotatedText(gfx,toprint,90.0-rotation,
						x_off,bound.height/2+y_off,TextPainter.LEFT);
			}
			break;
		case 4: //right
			if (Math.round(rotation)==90) {
				TextPainter.drawText(gfx,toprint,bound.width+x_off,bound.height/2+y_off,TextPainter.RIGHT);
			}
			else {
				TextPainter.drawRotatedText(gfx,toprint,90.0-rotation,
						bound.width+x_off,bound.height/2+y_off,TextPainter.RIGHT);
			}
			break;
		}
	}
	
	public void setFont(final Font newval) {
		font=newval;
	}
	public Font getFont() {
		return font;
	}
	
	/**
	 * Sets the count of decimal places for this Figure.
	 * 
	 * @param decimalPlaces
	 *            The precision
	 */
	public void setDecimalPlaces(final int decimalPlaces) {
		_decimalPlaces = decimalPlaces;
	}

	/**
	 * Gets the precision of this Figure.
	 * 
	 * @return The precision
	 */
	public int getDecimalPlaces() {
		return _decimalPlaces;
	}
	
	public void setTextAlignment(final int newval) {
		if (newval>=0 && newval<alignments.length) {
			alignment=newval;
		}
	}
	public int getTextAlignment() {
		return alignment;
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
	
	public void setRotation(final double newval) {
		rotation=newval;
	}
	public double getRotation() {
		return rotation;
	}
	
	public void setXOff(final int newval) {
		x_off=newval;
	}
	public int getXOff() {
		return x_off;
	}
	
	public void setYOff(final int newval) {
		y_off=newval;
	}
	public int getYOff() {
		return y_off;
	}
	
	public void setType(final int newval) {
		value_type=newval;
	}
	public int getType() {
		return value_type;
	}
	
	public void setTextValue(final String newval) {
		text_value=newval;
	}
	public String getTextValue() {
		return text_value;
	}
	
	public void setDoubleValue(final double newval) {
		double_value=newval;
	}
	public double getDoubleValue() {
		return double_value;
	}
	
//	public void setDoubleValueFormat(final String newval) {
//		double_value_format=newval;
//	}
//	public String getDoubleValueFormat() {
//		return double_value_format;
//	}
}
