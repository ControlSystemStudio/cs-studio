package org.csstudio.diag.diles.figures;

import org.csstudio.diag.diles.model.Logic;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class CommandTrueFalseFigure extends ActivityFigure {

	FixedAnchor outAnchor;

	private String text = "TRUE";

	private Color bgColor = new Color(null, 128, 185, 255);

	private int status = 0;

	public CommandTrueFalseFigure() {
		outAnchor = new FixedAnchor(this);
		outAnchor.horizontal = 25;
		outAnchor.vertical = 12;
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

		g.drawLine(r.right(), r.y + r.height / 2, r.right() + 8, r.y + r.height
				/ 2);
	}

	public void setText(String s) {
		text = s;
	}

	public int getVisualStatus() {
		return status;
	}

	public void setVisualStatus(int status) {
		this.status = status;
	}

}
