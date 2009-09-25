package org.csstudio.diag.diles.figures;

import org.csstudio.diag.diles.model.FlipFlop;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class FlipFlopFigure extends ActivityFigure {

	FixedAnchor jAnchor, kAnchor, qAnchor, notqAnchor;

	public FlipFlopFigure() {
		jAnchor = new FixedAnchor(this);
		jAnchor.vertical = 7;
		targetAnchors.put(FlipFlop.TERMINAL_J, jAnchor);

		kAnchor = new FixedAnchor(this);
		kAnchor.vertical = 32;
		targetAnchors.put(FlipFlop.TERMINAL_K, kAnchor);

		qAnchor = new FixedAnchor(this);
		qAnchor.vertical = 7;
		qAnchor.horizontal = 57;
		sourceAnchors.put(FlipFlop.TERMINAL_Q, qAnchor);

		setBackgroundColor(new Color(null, 253, 238, 204));

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
		r.setSize(40, 30);

		// Draw terminals, 2 at left
		g.drawLine(r.x, r.y + 2, r.x - 8, r.y + 2);
		g.drawLine(r.x, r.bottom() - 3, r.x - 8, r.bottom() - 3);

		// draw main area
		g.fillRectangle(r);
		g.drawRectangle(r);

		// draw text
		Font font = new Font(Display.getCurrent(), "Ariel", 7, SWT.BOLD);
		g.setFont(font);
		g.setTextAntialias(1);
		g.drawText("J", r.x + 5, r.y + 2);
		g.drawText("K", r.x + 5, r.y + 19);
		g.drawText("Q", r.x + 29, r.y + 2);

		// draw right terminal
		g.drawLine(r.right(), r.y + 2, r.right() + 8, r.y + 2);
	}
}
