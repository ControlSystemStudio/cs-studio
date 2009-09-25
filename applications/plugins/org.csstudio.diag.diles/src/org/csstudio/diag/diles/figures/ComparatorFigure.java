package org.csstudio.diag.diles.figures;

import org.csstudio.diag.diles.model.Logic;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class ComparatorFigure extends ActivityFigure {

	protected static final PointList GATE_OUTLINE = new PointList();

	static {
		GATE_OUTLINE.addPoint(6, 2);
		GATE_OUTLINE.addPoint(35, 19);
		GATE_OUTLINE.addPoint(6, 35);
		GATE_OUTLINE.addPoint(6, 2);
	}

	FixedAnchor leftAnchor, rightAnchor, outAnchor;

	private Color bgColor = new Color(null, 225, 240, 240);

	public ComparatorFigure() {
		leftAnchor = new FixedAnchor(this);
		leftAnchor.vertical = 9;
		targetAnchors.put(Logic.TERMINAL_A, leftAnchor);

		rightAnchor = new FixedAnchor(this);
		rightAnchor.vertical = 28;
		targetAnchors.put(Logic.TERMINAL_B, rightAnchor);

		outAnchor = new FixedAnchor(this);
		outAnchor.vertical = 19;
		outAnchor.horizontal = 42;
		sourceAnchors.put(Logic.TERMINAL_OUT, outAnchor);

		setBackgroundColor(getBgColor());
		setForegroundColor(ColorConstants.black);
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
		r.setSize(25, 19);

		// Draw terminals, 2 at left
		g.drawLine(r.x, r.y + 3, r.x - 8, r.y + 3);
		g.drawLine(r.x, r.y + 22, r.x - 8, r.y + 22);

		// draw the triangle
		g.translate(getLocation());

		r.width++;
		r.height++;
		g.fillPolygon(GATE_OUTLINE);
		r.width--;
		r.height--;

		g.drawPolyline(GATE_OUTLINE);
		g.translate(getLocation().getNegated());

		Font font = new Font(Display.getCurrent(), "Verdana", 9, SWT.BOLD);
		g.setFont(font);
		g.setTextAntialias(1);
		g.drawText("+", r.x + 0, r.y + 1);
		g.drawText("-", r.x + 1, r.y + 13);

		// draw out terminal
		g.drawLine(r.right() + 2, r.y + 13, r.right() + 8, r.y + 13);
	}
}
