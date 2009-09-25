package org.csstudio.diag.diles.figures;

import org.csstudio.diag.diles.model.Not;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class TDETimerFigure extends ActivityFigure {

	private Color bgColor = new Color(null, 164, 216, 255);

	FixedAnchor inAnchor, outAnchor;

	/**
	 * Number of delay Timer waits.
	 */
	private int delay;

	public int getDelay() {
		return delay;
	}

	public void setDelay(int secs) {
		this.delay = secs;
	}

	public TDETimerFigure() {
		inAnchor = new FixedAnchor(this);
		inAnchor.vertical = 17;
		targetAnchors.put(Not.TERMINAL_IN, inAnchor);

		outAnchor = new FixedAnchor(this);
		outAnchor.vertical = 17;
		outAnchor.horizontal = 57;
		sourceAnchors.put(Not.TERMINAL_OUT, outAnchor);

		setDelay(5);

		setBackgroundColor(bgColor);
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
		r.translate(8, 5);
		r.setSize(40, 25);

		// Draw in terminal
		g.drawLine(r.x, r.y + 12, r.x - 8, r.y + 12);

		// draw main area
		r.width++;
		r.height++;
		g.fillRectangle(r);
		r.width--;
		r.height--;

		g.drawRectangle(r);

		// draw text
		Font font = new Font(Display.getCurrent(), "Ariel", 7, SWT.BOLD);
		g.setFont(font);
		g.setTextAntialias(1);
		g.drawText("TDE", r.x + 11, r.y + 2);

		g.drawText(getDelay() + " secs", r.x + 3, r.y + 13);

		// draw out terminal
		g.drawLine(r.right(), r.y + 12, r.right() + 8, r.y + 12);
	}
}
