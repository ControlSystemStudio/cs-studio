/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.figures;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.swt.xygraph.Preferences;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.figures.Trace.ErrorBarType;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.linearscale.LinearScale;
import org.csstudio.swt.xygraph.util.SWTConstants;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * The hover label layer in the plot area.
 * 
 * @author Davy Dequidt
 * 
 */
public class HoverLabels extends Figure implements MouseMotionListener {

	private static final int TEXT_MARGIN = 3;
	private static final int GAP_BEETWEEN_LABEL = 1;
	private static final int ARROW_LENGTH = 20;
	private static final double ARROW_HEIGHT_RATIO = 0.6;

	private final List<Trace> traceList = new ArrayList<Trace>();
	private final List<HoverLabel> labelList = new ArrayList<HoverLabel>();

	/**
	 * Use advanced graphics?
	 */
	private final boolean use_advanced_graphics = Preferences.useAdvancedGraphics();

	private final Font textFont;
	private final int textHeight;
	private final int labelHeight;
	private final int arrowHeight;

	private int cursor_x;
	private Color backColor;

	public HoverLabels(PlotArea plotArea) {
		textFont = XYGraphMediaFactory.getInstance().getDefaultFont(SWT.NORMAL);
		final Dimension textSize = FigureUtilities.getTextExtents("Test",
				textFont);
		if (textSize.height % 2 == 0) {
			textHeight = textSize.height;
		} else {
			textHeight = textSize.height + 1;
		}
		labelHeight = textHeight + TEXT_MARGIN * 2;
		arrowHeight = (int) (labelHeight * ARROW_HEIGHT_RATIO) * 2 / 2;
		backColor = plotArea.getBackgroundColor();
	}

	/**
	 * Describe a label
	 * 
	 */
	private class HoverLabel implements Comparable<HoverLabel> {

		private int xPos;

		private int yPos;

		private String text;

		private Color foregrounColor;

		private boolean optional;

		public HoverLabel(int xPos, int yPos, String text,
				Color foregrounColor, boolean optional) {
			this.xPos = xPos;
			this.yPos = yPos;
			this.text = text;
			this.foregrounColor = foregrounColor;
			this.optional = optional;
		}

		public int getXPos() {
			return xPos;
		}

		public int getYPos() {
			return yPos;
		}

		public String getText() {
			return text;
		}

		public Color getForegrounColor() {
			return foregrounColor;
		}

		public boolean isOptional() {
			return optional;
		}

		public int compareTo(HoverLabel o) {
			if (o == null) {
				return 1;
			}
			return Double.compare(getYPos(), o.getYPos());
		}

	}

	@Override
	protected void paintFigure(Graphics graphics) {
		if (!isVisible()) {
			return;
		}
		super.paintFigure(graphics);

		computeHoverLabels();

		graphics.pushState();
		graphics.setFont(textFont);
		graphics.setAdvanced(use_advanced_graphics);
		if (use_advanced_graphics) {
			graphics.setAntialias(SWT.ON);
			graphics.setTextAntialias(SWT.ON);
		}
		drawLabels(graphics);
		graphics.popState();
	}

	private void drawLabels(Graphics graphics) {
		graphics.setBackgroundColor(backColor);
		graphics.setLineStyle(SWTConstants.LINE_SOLID);
		graphics.setLineWidth(1);

		final int plotHeight = bounds.y + bounds.height;

		final List<HoverLabel> labelListToDisplay;
		if ((labelHeight + GAP_BEETWEEN_LABEL) * labelList.size() > bounds.height) {
			// If not enough space, don't displays optional values
			labelListToDisplay = new ArrayList<HoverLabel>();
			for (HoverLabel label : labelList) {
				if (!label.isOptional()) {
					labelListToDisplay.add(label);
				}
			}
		} else {
			labelListToDisplay = labelList;
		}

		int nextYPos = bounds.y + GAP_BEETWEEN_LABEL + labelHeight / 2;
		for (int i = 0; i < labelListToDisplay.size(); i++) {
			final HoverLabel label = labelListToDisplay.get(i);
			graphics.setForegroundColor(label.getForegrounColor());

			int yLabelPos = label.getYPos();
			if (yLabelPos < nextYPos) {
				yLabelPos = nextYPos;
			}
			final int maxYLabelPos = (int) (plotHeight - ((labelListToDisplay
					.size() - i - 0.5) * (labelHeight + GAP_BEETWEEN_LABEL)));
			if (yLabelPos > maxYLabelPos) {
				yLabelPos = maxYLabelPos;
			}
			drawLabel(graphics, label.getText(), label.getXPos(),
					label.getYPos(), label.getXPos(), yLabelPos);
			nextYPos = yLabelPos + labelHeight + GAP_BEETWEEN_LABEL;
		}
	}

	private void drawLabel(Graphics graphics, String text, int xPos, int yPos,
			int xLabelPos, int yLabelPos) {
		PointList points = new PointList();
		final int yTextPos = yLabelPos - textHeight / 2;
		final int textWidth = (int) (FigureUtilities.getTextWidth(text, textFont) * 1.08);
		int xTextPos = xLabelPos - textWidth - ARROW_LENGTH;
		if (xTextPos < bounds.x) {
			// Display label on right
			xTextPos = xLabelPos + ARROW_LENGTH + TEXT_MARGIN;
			points.addPoint(xTextPos + textWidth + TEXT_MARGIN, yTextPos
					- TEXT_MARGIN);
			points.addPoint(xTextPos - TEXT_MARGIN, yTextPos - TEXT_MARGIN);
			points.addPoint(xTextPos - TEXT_MARGIN, yTextPos + textHeight
					- arrowHeight);
			points.addPoint(xPos, yPos);
			points.addPoint(xTextPos - TEXT_MARGIN, yTextPos + arrowHeight);
			points.addPoint(xTextPos - TEXT_MARGIN, yTextPos + textHeight);
			points.addPoint(xTextPos + textWidth + TEXT_MARGIN, yTextPos
					+ textHeight);
		} else {
			// Display label on left
			points.addPoint(xTextPos - TEXT_MARGIN, yTextPos - TEXT_MARGIN);
			points.addPoint(xTextPos + textWidth + TEXT_MARGIN, yTextPos
					- TEXT_MARGIN);
			points.addPoint(xTextPos + textWidth + TEXT_MARGIN, yTextPos
					+ textHeight - arrowHeight);
			points.addPoint(xPos, yPos);
			points.addPoint(xTextPos + textWidth + TEXT_MARGIN, yTextPos
					+ arrowHeight - 2);
			points.addPoint(xTextPos + textWidth + TEXT_MARGIN, yTextPos
					+ textHeight);
			points.addPoint(xTextPos - TEXT_MARGIN, yTextPos + textHeight);
		}

		if (use_advanced_graphics) {
			graphics.setAlpha(180);
		}
		graphics.fillPolygon(points);
		if (use_advanced_graphics) {
			graphics.setAlpha(255);
		}
		graphics.drawPolygon(points);
		graphics.drawText(text, xTextPos, yTextPos);
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
			cursor_x = me.getLocation().x;
			repaint();
		}
	}

	private void computeHoverLabels() {
		// Loop over trace and sample to find the nearest (left) sample to
		// the cross
		labelList.clear();
		for (Trace trace : traceList) {
			if (!trace.isVisible())
				continue;
			final Axis xAxis = trace.getXAxis();

			final double xValue = xAxis.getPositionValue(cursor_x, false);
			ISample previousSample = null;
			double previousSampleXValue = 0;
			for (ISample sample : trace.getHotSampleList()) {
				if (sample.getXValue() > xValue) {
					break;
				} else if (previousSampleXValue < sample.getXValue()) {
					previousSampleXValue = sample.getXValue();
					previousSample = sample;
				}
			}

			if (previousSample == null) {
				// Looking for the nearest sample before the plot start
				IDataProvider dp = trace.getDataProvider();
				previousSampleXValue = 0;
				for (int i = dp.getSize() - 1; i >= 0; i--) {
					ISample sample = dp.getSample(i);
					if (sample.getXValue() > previousSampleXValue
							&& sample.getXValue() <= xAxis.getRange()
									.getLower()) {
						previousSampleXValue = sample.getXValue();
						previousSample = sample;
					}
					if (dp.isChronological()
							&& sample.getXValue() < xAxis.getRange().getLower()) {
						break;
					}
				}
			}
			if (previousSample != null) {
				createHoverLabel(previousSample, trace);
			}
		}
		Collections.sort(labelList);
	}

	private void createHoverLabel(ISample sample, Trace trace) {
		final LinearScale xAxis = trace.getXAxis();
		final LinearScale yAxis = trace.getYAxis();
		int xPos = xAxis.getValuePosition(sample.getXValue(), false);
		if (xPos < bounds.x) {
			xPos = bounds.x;
		}
		final double yValue = sample.getYValue();
		if (yAxis.getRange().inRange(yValue)) {
			// Show label only if the point is in the plot area
			labelList.add(new HoverLabel(xPos, yAxis.getValuePosition(yValue,
					false), yAxis.format(yValue), yAxis.getForegroundColor(),
					false));
		}

		if (sample.getYMinusError() != 0
				&& sample.getYPlusError() != 0
				&& (trace.isErrorBarEnabled() && !trace.isDrawYErrorInArea() || trace
						.isErrorBarEnabled()
						&& trace.isDrawYErrorInArea()
						&& trace.getTraceType() != TraceType.BAR)) {
			final double yMinError = yValue - sample.getYMinusError();
			final double yPlusError = yValue + sample.getYPlusError();
			final ErrorBarType yErrorBarType = trace.getYErrorBarType();
			switch (yErrorBarType) {
			case BOTH:
			case MINUS:
				if (yAxis.getRange().inRange(yMinError)) {
					// Show label only if the point is in the plot area
					labelList.add(new HoverLabel(xPos, yAxis.getValuePosition(
							yMinError, false), "Min: "
							+ yAxis.format(yMinError), yAxis
							.getForegroundColor(), true));
				}
				if (yErrorBarType != ErrorBarType.BOTH)
					break;
			case PLUS:
				if (yAxis.getRange().inRange(yPlusError)) {
					labelList.add(new HoverLabel(xPos, yAxis.getValuePosition(
							yPlusError, false), "Max: "
							+ yAxis.format(yPlusError), yAxis
							.getForegroundColor(), true));
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Add a trace.
	 * 
	 * @param trace
	 *            the trace to be added.
	 */
	public void addTrace(final Trace trace) {
		traceList.add(trace);
	}

	/**
	 * Remove a trace.
	 * 
	 * @param trace
	 * @return true if contains the specified trace
	 */
	public boolean removeTrace(final Trace trace) {
		return traceList.remove(trace);
	}
}
