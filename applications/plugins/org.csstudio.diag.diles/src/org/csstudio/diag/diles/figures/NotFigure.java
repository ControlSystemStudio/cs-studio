package org.csstudio.diag.diles.figures;

import org.csstudio.diag.diles.model.Not;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public class NotFigure extends ActivityFigure {

	protected static final PointList GATE_OUTLINE = new PointList();

	static {
		GATE_OUTLINE.addPoint(6, 2);
		GATE_OUTLINE.addPoint(24, 19);
		GATE_OUTLINE.addPoint(6, 35);
		GATE_OUTLINE.addPoint(6, 2);
	}

	FixedAnchor inAnchor, outAnchor;

	private Color bgColor = ColorConstants.red;

	public NotFigure() {
		inAnchor = new FixedAnchor(this);
		inAnchor.vertical = 19;
		targetAnchors.put(Not.TERMINAL_IN, inAnchor);

		outAnchor = new FixedAnchor(this);
		outAnchor.vertical = 19;
		outAnchor.horizontal = 39;
		sourceAnchors.put(Not.TERMINAL_OUT, outAnchor);

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
		r.setSize(25, 19);

		// draw in terminal
		g.drawLine(r.x, r.y + 13, r.x - 8, r.y + 13);

		// draw the triangle
		g.translate(getLocation());
		g.fillPolygon(GATE_OUTLINE);
		g.drawPolyline(GATE_OUTLINE);
		g.translate(getLocation().getNegated());

		g.setBackgroundColor(ColorConstants.blue);

		// draw the circle
		r.x += 15;
		r.y += 9;
		r.setSize(9, 9);
		g.fillOval(r);
		r.width--;
		r.height--;
		g.drawOval(r);

		// draw out terminal
		g.drawLine(r.right(), r.y + r.height / 2, r.right() + 8, r.y + r.height
				/ 2);
	}
}
