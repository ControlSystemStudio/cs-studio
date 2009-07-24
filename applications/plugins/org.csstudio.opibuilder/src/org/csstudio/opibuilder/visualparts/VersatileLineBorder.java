package org.csstudio.opibuilder.visualparts;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;

/**The line border which allows versatile line style:
 * SWT.LINE_SOLID, SWT.LINE_DASH, SWT.LINE_DOT, SWT.LINE_DASHDOT or
 * SWT.LINE_DASHDOTDOT.
 * @author Xihui Chen
 *
 */
public class VersatileLineBorder extends LineBorder {

	private int lineStyle;
	
	/**
	 * @param lineStyle the line style, which must be one of the constants
	 * SWT.LINE_SOLID, SWT.LINE_DASH, SWT.LINE_DOT, SWT.LINE_DASHDOT or
	 * SWT.LINE_DASHDOTDOT.
	 * @param borderColor the border color
	 * @param lineWidth the line width in pixels
	 */
	public VersatileLineBorder(int lineStyle, Color borderColor, int lineWidth) {
		super(borderColor, lineWidth);
		this.lineStyle = lineStyle;
	}
	
	@Override
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		graphics.setLineStyle(lineStyle);
		super.paint(figure, graphics, insets);
	}
	
	
}
