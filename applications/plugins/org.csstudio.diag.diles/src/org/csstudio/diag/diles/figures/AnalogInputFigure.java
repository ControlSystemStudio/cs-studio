package org.csstudio.diag.diles.figures;

import org.csstudio.diag.diles.model.Logic;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class AnalogInputFigure extends ActivityFigure {
	FixedAnchor outAnchor;

	private String text = "Test";

	private Color bgColor = new Color(null, 0, 0, 0);

	public AnalogInputFigure() {
		outAnchor = new FixedAnchor(this);
		outAnchor.horizontal = 37;
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
		r.setSize(27, 19);

		g.setForegroundColor(ColorConstants.black);

		// draw main area
		r.width++;
		r.height++;
		g.fillRectangle(r);
		r.width--;
		r.height--;

		g.drawRectangle(r);

		Font font = new Font(Display.getCurrent(), "Verdana", 5, SWT.BOLD);
		g.setFont(font);
		g.setTextAntialias(1);
		g.drawText(text, r.x + 2, r.y + 6);

		// g.drawLine(r.x + r.width / 2, r.bottom(), r.x + r.width / 2,
		// r.bottom() + 4);
		g.drawLine(r.right(), r.y + r.height / 2, r.right() + 8, r.y + r.height
				/ 2);
	}

	public void setText(String s) {
		text = s;
	}
}
