package org.csstudio.diag.diles.figures;

import org.csstudio.diag.diles.model.Logic;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class HardwareTrueFalseFigure extends ActivityFigure {

	FixedAnchor outAnchor;

	private String text = "TRUE";

	private Color bgColor = new Color(null, 0, 0, 0);

	public HardwareTrueFalseFigure() {
		outAnchor = new FixedAnchor(this);
		outAnchor.horizontal = 34;
		outAnchor.vertical = 13;
		sourceAnchors.put(Logic.TERMINAL_OUT, outAnchor);

		setBackgroundColor(getBgColor());

	}

	public Color getBgColor() {
		return bgColor;
	}

	public String getText() {
		return text;
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
		g.drawText("DIN", r.x + 3, r.y + 5);

		// g.drawLine(r.x + r.width / 2, r.bottom(), r.x + r.width / 2,
		// r.bottom() + 4);
		g.drawLine(r.right(), r.y + r.height / 2, r.right() + 8, r.y + r.height
				/ 2);
	}

	public void setText(String s) {
		text = s;
	}

}
