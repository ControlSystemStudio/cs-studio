package org.csstudio.sds.components.ui.internal.figures;

import java.text.NumberFormat;

import org.csstudio.sds.components.ui.internal.utils.TextPainter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.AntialiasingUtil;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

/**
 * A label figure.
 * 
 * @author jbercic
 * 
 */
public final class RefreshableLabelFigure extends Shape implements IAdaptable {
	
	/**
	 * The ID for the <i>text</i> type.
	 */
	public static final int TYPE_TEXT = 0;
	/**
	 * The ID for the <i>double</i> type.
	 */
	public static final int TYPE_DOUBLE = 1;
	
	/**
	 * Type of the label.
	 */
	private int _valueType=TYPE_DOUBLE;
	
	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	
	/**
	 * The potenz for the precision.
	 */
	private int _decimalPlaces = 2;
	
	/**
	 * Default label font.
	 */
	private Font _font = CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE);
	
	/**
	 * An Array, which contains the PositionConstants for Center, Top, Bottom, Left, Right.
	 */
	private final int[] _alignments = new int[] {PositionConstants.CENTER, PositionConstants.TOP, PositionConstants.BOTTOM, PositionConstants.LEFT, PositionConstants.RIGHT};
	
	/**
	 * The alignment of the text.
	 */
	private int _alignment=0;
	/**
	 * The rotation of the text.
	 */
	private double _rotation=90.0;
	/**
	 * The x offset of the text.
	 */
	private int _xOff=0;
	/**
	 * The x offset of the text.
	 */
	private int _yOff=0;
	
	/**
	 * Value fields.
	 */
	private String _textValue="";
	
	/**
	 * Is the background transparent or not?
	 */
	private boolean _transparent=true;
	
	/**
	 * The width of the border.
	 */
	private int _borderWidth;
	
	/**
	 * Fills the image. Nothing to do here.
	 * @param gfx The {@link Graphics} to use.
	 */
	protected void fillShape(final Graphics gfx) {}
	
	/**
	 * Draws the outline of the image. Nothing to do here.
	 * @param gfx The {@link Graphics} to use.
	 */
	protected void outlineShape(final Graphics gfx) {}
	
	/**
	 * The main drawing routine.
	 * @param gfx The {@link Graphics} to use.
	 */
	public void paintFigure(final Graphics gfx) {
		
		Rectangle bound=getBounds();
		gfx.translate(bound.x,bound.y);
		
		if (!_transparent) {
			gfx.setBackgroundColor(getBackgroundColor());
			gfx.fillRectangle(0,0,bound.width,bound.height);
		}
		gfx.setFont(_font);
		gfx.setForegroundColor(getForegroundColor());
		AntialiasingUtil.getInstance().enableAntialiasing(gfx);
		
		String toprint="none";
		switch (_valueType) {
		case TYPE_TEXT:
			toprint=_textValue;
			break;
		case TYPE_DOUBLE:
			try {
				double d = Double.parseDouble(_textValue);
				NumberFormat format = NumberFormat.getInstance();
				format.setMaximumFractionDigits(_decimalPlaces);
				toprint = format.format(d);
			} catch (Exception e) {
				toprint = _textValue;
			}
			break;
		default:
			toprint="unknown value type";
		}
		
		Point textPoint;
		int alignment;
		switch (_alignment) {
		case 0: //center
			textPoint = new Point(bound.width/2+_xOff,bound.height/2+_yOff);
			alignment = TextPainter.CENTER;
			break;
		case 1: //top
			textPoint = new Point(bound.width/2+_xOff,_yOff);
			alignment = TextPainter.TOP_CENTER;
			break;
		case 2: //bottom
			textPoint = new Point(bound.width/2+_xOff,bound.height+_yOff);
			alignment = TextPainter.BOTTOM_CENTER;
			break;
		case 3: //left
			textPoint = new Point(_xOff,bound.height/2+_yOff);
			alignment = TextPainter.LEFT;
			break;
		case 4: //right
			textPoint = new Point(bound.width+_xOff,bound.height/2+_yOff);
			alignment = TextPainter.RIGHT;
			break;
		default : //default
			textPoint = new Point(bound.width/2+_xOff,bound.height/2+_yOff);
			alignment = TextPainter.CENTER;
			break;
		}
		if (Math.round(_rotation)==90) {
			TextPainter.drawText(gfx,toprint,textPoint.x,textPoint.y,alignment);
		} else {
			TextPainter.drawRotatedText(gfx,toprint,90.0-_rotation,textPoint.x,textPoint.y,alignment);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setFont(final Font newval) {
		_font=newval;
	}
	
	/**
	 * {@inheritDoc} 
	 */
	public Font getFont() {
		return _font;
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
	
	/**
	 * Sets the alignment for the text.
	 * @param newval The alignment for the text
	 */
	public void setTextAlignment(final int newval) {
		if (newval>=0 && newval<_alignments.length) {
			_alignment=newval;
		}
	}
	
	/**
	 * Returns the alignment of the text.
	 * @return The alignment of the text
	 */
	public int getTextAlignment() {
		return _alignment;
	}
	
	/**
	 * Sets the transparent state of the background.
	 * @param newval The transparent state
	 */
	public void setTransparent(final boolean newval) {
		_transparent=newval;
	}
	
	/**
	 * Returns the transparent state of the background.
	 * @return True, if the background is transparent, false otherwise
	 */
	public boolean getTransparent() {
		return _transparent;
	}
	
	/**
	 * Sets the width of the border.
	 * @param newval The width of the border
	 */
	public void setBorderWidth(final int newval) {
		_borderWidth=newval;
	}
	
	/**
	 * returns the width of the border.
	 * @return The width of the border
	 */
	public int getBorderWidth() {
		return _borderWidth;
	}
	
	/**
	 * Sets the rotation for the text.
	 * @param newval The rotation for the text
	 */
	public void setRotation(final double newval) {
		_rotation=newval;
	}
	
	/**
	 * Returns the rotation of the text.
	 * @return The rotation of the text
	 */
	public double getRotation() {
		return _rotation;
	}
	
	/**
	 * Sets the x offset for the text. 
	 * @param newval The x offset
	 */
	public void setXOff(final int newval) {
		_xOff=newval;
	}
	
	/**
	 * Returns the x offset of the text.
	 * @return The x offset of the text
	 */
	public int getXOff() {
		return _xOff;
	}
	
	/**
	 * Sets the y offset for the text. 
	 * @param newval The y offset
	 */
	public void setYOff(final int newval) {
		_yOff=newval;
	}
	
	/**
	 * Returns the y offset of the text.
	 * @return The y offset of the text
	 */
	public int getYOff() {
		return _yOff;
	}
	
	/**
	 * Sets the type of the displayed text.
	 * @param newval The type of the displayed text
	 */
	public void setType(final int newval) {
		_valueType=newval;
	}
	
	/**
	 * returns the type of the displayed text.
	 * @return The type of the displayed text
	 */
	public int getType() {
		return _valueType;
	}
	
	/**
	 * Sets the value for the text.
	 * @param newval The value for the text
	 */
	public void setTextValue(final String newval) {
		_textValue=newval;
	}
	
	/**
	 * Returns the value for the text.
	 * @return The value for the text
	 */
	public String getTextValue() {
		return _textValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if (_borderAdapter == null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}

}
