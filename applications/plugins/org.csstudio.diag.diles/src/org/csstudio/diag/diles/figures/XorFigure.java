package org.csstudio.diag.diles.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public class XorFigure extends LogicFigure {

	private Color bgColor = ColorConstants.lightGreen;

	public XorFigure() {
		super();

		setBackgroundColor(getBgColor());
	}

	public Color getBgColor() {
		return bgColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	public void paintFigure(Graphics g) {
		g.setAntialias(1);

		Rectangle r = getBounds().getCopy();
		r.translate(8, 6);
		r.setSize(17, 29);

		// draw main area
		r.width++;
		r.height++;
		g.fillRectangle(r);
		r.width--;
		r.height--;

		// outline main area
		g.drawLine(r.x, r.y, r.x, r.bottom());
		g.drawLine(r.x, r.y, r.right() - 1, r.y);
		g.drawLine(r.x, r.bottom(), r.right(), r.bottom());

		r.width = 29;
		r.x += 0;

		r.width++;
		r.height++;
		g.fillArc(r, 270, 180);
		r.width--;
		r.height--;

		g.drawArc(r, 270, 180);
		// g.drawLine(r.x + r.width / 2, r.bottom(), r.x + r.width / 2 + 2,
		// r.bottom());

		// Draw terminals, 2 at left, extends into the figure
		g.drawLine(r.x + 21, r.y + 3, r.x - 8, r.y + 3);
		g.drawLine(r.x + 21, r.bottom() - 3, r.x - 8, r.bottom() - 3);

		// draw line that crosses two left terminals
		g.drawLine(r.x - 4, r.y, r.x - 4, r.bottom());

		// draw right terminal
		g.drawLine(r.right(), r.y + r.height / 2, r.right() + 8, r.y + r.height
				/ 2);

	}
}