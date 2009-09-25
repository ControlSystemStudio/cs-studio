package org.csstudio.diag.diles.figures;

import org.csstudio.diag.diles.model.HardwareOut;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class HardwareOutFigure extends ActivityFigure {

	FixedAnchor inAnchor;

	private Color bgColor = new Color(null, 0, 0, 0);

	public HardwareOutFigure() {
		inAnchor = new FixedAnchor(this);
		inAnchor.vertical = 12;
		targetAnchors.put(HardwareOut.TERMINAL_IN, inAnchor);

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
		r.translate(6, 4);
		r.setSize(22, 18);

		g.setForegroundColor(ColorConstants.black);

		// draw main area
		r.width++;
		r.height++;
		g.fillOval(r);
		r.width--;
		r.height--;

		g.drawOval(r);

		Font font = new Font(Display.getCurrent(), "Verdana", 5, SWT.BOLD);
		g.setFont(font);
		g.setTextAntialias(1);
		g.drawText("DOC", r.x + 3, r.y + 5);

		g.drawLine(r.x, r.y + 8, r.x - 8, r.y + 8);
	}
}
