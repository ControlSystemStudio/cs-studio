package org.csstudio.diag.diles.figures;

import org.csstudio.diag.diles.model.Status;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class StatusFigure extends ActivityFigure {

	FixedAnchor inAnchor;

	private Color bgColor = new Color(null, 128, 185, 255);

	private int status = 0;

	public StatusFigure() {
		inAnchor = new FixedAnchor(this);
		inAnchor.vertical = 12;
		targetAnchors.put(Status.TERMINAL_IN, inAnchor);

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
		r.translate(4, 4);
		r.setSize(16, 16);

		g.setForegroundColor(ColorConstants.black);

		// draw main area
		r.width++;
		r.height++;
		g.fillRectangle(r);
		r.width--;
		r.height--;

		Font font = new Font(Display.getCurrent(), "Verdana", 9, SWT.BOLD);
		g.setFont(font);
		g.setTextAntialias(1);
		g.drawText(String.valueOf(status), r.x + 4, r.y + 1);

		g.drawRectangle(r);

		g.drawLine(r.x, r.y + 8, r.x - 8, r.y + 8);

		/*
		 * OLD FIGURE Rectangle r = getBounds().getCopy(); r.translate(4, 4);
		 * r.setSize(16, 16);
		 * 
		 * g.setForegroundColor(ColorConstants.black);
		 * 
		 * // draw main area r.width++; r.height++; g.fillOval(r); r.width--;
		 * r.height--;
		 * 
		 * g.drawOval(r);
		 * 
		 * g.drawLine(r.x, r.y + 8, r.x - 8, r.y + 8);
		 */
	}

	public int getVisualStatus() {
		return status;
	}

	public void setVisualStatus(int status) {
		this.status = status;
	}
}
