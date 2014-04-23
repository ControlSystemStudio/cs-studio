/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.figures;

import org.csstudio.swt.xygraph.Preferences;
import org.csstudio.swt.xygraph.linearscale.LinearScale;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;

/**
 * Linear Scale tick labels.
 * 
 * @author Xihui Chen
 */
public class AxisMousePosition extends Figure implements MouseMotionListener {

	private static final String MINUS = "-";

	private static final int TEXT_MARGIN = 2;

	/**
	 * Use advanced graphics?
	 */
	private final boolean use_advanced_graphics = Preferences
			.useAdvancedGraphics();

	/** the mouse position in pixels */
	private Integer mousePosition;
	private Integer mousePositionRelativeToAxis;

	private LinearScale scale;

	/**
	 * Constructor.
	 * 
	 * @param linearScale
	 *            the scale
	 */
	public AxisMousePosition(LinearScale linearScale) {

		this.scale = linearScale;

		setFont(this.scale.getFont());
		setForegroundColor(this.scale.getForegroundColor());

		this.mousePosition = 0;
		if (scale.isHorizontal()) {
			mousePositionRelativeToAxis = mousePosition - scale.getBounds().x;
		} else {
			mousePositionRelativeToAxis = scale.getBounds().height
					+ scale.getBounds().y - mousePosition;
		}
		setVisible(false);
	}

	/**
	 * Draw the X tick.
	 * 
	 * @param grahics
	 *            the graphics context
	 */
	private void drawXTick(Graphics graphics) {
		// draw tick labels
		graphics.setFont(scale.getFont());
		String label = scale.format(scale
				.getPositionValue(mousePosition, false));
		Dimension fontDimension = FigureUtilities.getTextExtents(label,
				getFont());
		int fontWidth = (int) (fontDimension.width * 1.08);
		int fontHeight = fontDimension.height;
		int x = (int) Math.ceil(mousePositionRelativeToAxis - fontWidth / 2.0)
				+ TEXT_MARGIN;
		int y = TEXT_MARGIN;

		PointList points = new PointList();
		points.addPoint(x - TEXT_MARGIN, y + fontHeight + TEXT_MARGIN);
		points.addPoint(x + fontWidth + TEXT_MARGIN, y + fontHeight
				+ TEXT_MARGIN);
		points.addPoint(x + fontWidth + TEXT_MARGIN, y - TEXT_MARGIN);
		points.addPoint(x - TEXT_MARGIN, y - TEXT_MARGIN);

		graphics.fillPolygon(points);
		graphics.drawPolygon(points);
		graphics.drawText(label, x, TEXT_MARGIN);

		bounds = getBounds();
		bounds.height = Math.max(bounds.height, y + fontHeight + TEXT_MARGIN
				* 2);
	}

	/**
	 * Draw the Y tick.
	 * 
	 * @param grahpics
	 *            the graphics context
	 */
	private void drawYTick(Graphics graphics) {
		// draw tick labels
		graphics.setFont(scale.getFont());
		String label = scale.format(scale
				.getPositionValue(mousePosition, false));
		Dimension fontDimension = FigureUtilities.getTextExtents(label,
				getFont());
		int fontWidth = (int) (fontDimension.width * 1.08);
		int fontHeight = fontDimension.height;

		int x = TEXT_MARGIN;
		if (label.startsWith(MINUS) && !label.startsWith(MINUS)) {
			x += FigureUtilities.getTextExtents(MINUS, getFont()).width;
		}
		int y = (int) Math.ceil(scale.getLength() - mousePositionRelativeToAxis
				- fontHeight / 2.0)
				+ TEXT_MARGIN;

		PointList points = new PointList();
		points.addPoint(x - TEXT_MARGIN, y + fontHeight + TEXT_MARGIN);
		points.addPoint(x + fontWidth + TEXT_MARGIN, y + fontHeight
				+ TEXT_MARGIN);
		points.addPoint(x + fontWidth + TEXT_MARGIN, y - TEXT_MARGIN);
		points.addPoint(x - TEXT_MARGIN, y - TEXT_MARGIN);

		graphics.fillPolygon(points);
		graphics.drawPolygon(points);
		graphics.drawText(label, x, y);
		bounds = getBounds();
		bounds.width = Math.max(bounds.width, x + fontWidth + TEXT_MARGIN * 2);
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		if (!isVisible()) return;
		graphics.translate(bounds.x, bounds.y);

		graphics.setAdvanced(use_advanced_graphics);
		if (use_advanced_graphics) {
			graphics.setAntialias(SWT.ON);
			graphics.setTextAntialias(SWT.ON);
		}
		graphics.setForegroundColor(scale.getForegroundColor());
		graphics.setBackgroundColor(scale.getBackgroundColor());
		if (scale.isHorizontal()) {
			drawXTick(graphics);
		} else {
			drawYTick(graphics);
		}

		super.paintClientArea(graphics);
	}

	public void mouseDragged(MouseEvent me) {
		// NOP
	}

	public void mouseEntered(MouseEvent me) {
		// NOP
	}

	public void mouseExited(MouseEvent me) {
		// NOP
	}

	public void mouseHover(MouseEvent me) {
		// NOP
	}

	public void mouseMoved(MouseEvent me) {
		if (isVisible()) {
			if (scale.isHorizontal()) {
				mousePosition = me.getLocation().x;
				mousePositionRelativeToAxis = mousePosition
						- scale.getBounds().x;
			} else {
				mousePosition = me.getLocation().y;
				mousePositionRelativeToAxis = scale.getBounds().height
						+ scale.getBounds().y - mousePosition;
			}
			repaint();
		}
	}

}
