/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A Sash Container figure.
 * 
 * @author Xihui
 * 
 */
public class SashContainerFigure extends Figure implements Introspectable {

	private class Sash extends Figure {

		class SashDragger extends MouseMotionListener.Stub implements
				MouseListener {

			private Point start;

			private double startSashPosition;

			private Rectangle containerClientArea;
			private int startSashLoc;
			private boolean armed;

			public void mouseDoubleClicked(MouseEvent me) {

			}

			@Override
			public void mouseDragged(MouseEvent me) {
				if (!armed)
					return;
				Dimension locDiff = me.getLocation().getDifference(start);

				if (horizontal) {
					int newLoc = startSashLoc + locDiff.width;
					int rightEdge = containerClientArea.x
							+ containerClientArea.width - sashWidth / 2;
					if (newLoc > rightEdge)
						newLoc = rightEdge;
					else {
						int leftEdge = containerClientArea.x + sashWidth / 2;
						if (newLoc < leftEdge)
							newLoc = leftEdge;
					}
					setSashPosition((newLoc - containerClientArea.x)
							/ (double) containerClientArea.width);
				} else {
					int newLoc = startSashLoc + locDiff.height;
					int downEdge = containerClientArea.y
							+ containerClientArea.height - sashWidth / 2;
					if (newLoc > downEdge)
						newLoc = downEdge;
					else {
						int upEdge = containerClientArea.y + sashWidth / 2;
						if (newLoc < upEdge)
							newLoc = upEdge;
					}
					setSashPosition((newLoc - containerClientArea.y)
							/ (double) containerClientArea.height);
				}
				me.consume();
			}

			public void mousePressed(MouseEvent me) {
				if (me.button != 1)
					return;
				armed = true;
				start = me.getLocation();
				startSashPosition = sashPosition;

				containerClientArea = SashContainerFigure.this.getClientArea();
				if (horizontal)
					startSashLoc = containerClientArea.x
							+ (int) (containerClientArea.width * startSashPosition);
				else
					startSashLoc = containerClientArea.y
							+ (int) (containerClientArea.height * startSashPosition);

				me.consume();
			}

			public void mouseReleased(MouseEvent me) {
				if (me.button != 1)
					return;
				if (!armed)
					return;
				armed = false;
				me.consume();
			}

		}

		public Sash() {
			SashDragger dragger = new SashDragger();
			addMouseListener(dragger);
			addMouseMotionListener(dragger);
			if (horizontal)
				setCursor(Cursors.SIZEWE);
			else
				setCursor(Cursors.SIZENS);
		}

		@Override
		protected void paintFigure(Graphics graphics) {
			super.paintFigure(graphics);
			Rectangle bounds = getBounds();
			graphics.pushState();
			PointList line1 = null, line2 = null;
			switch (sashStyle) {
			case DOUBLE_LINES:
			case ETCHED:
			case RIDGED:
				if (horizontal) {
					line1 = new PointList(new int[] { bounds.x, bounds.y,
							bounds.x, bounds.y + bounds.height });
					line2 = new PointList(new int[] {
							bounds.x + bounds.width - 1, bounds.y,
							bounds.x + bounds.width - 1,
							bounds.y + bounds.height });
				} else {
					line1 = new PointList(new int[] { bounds.x, bounds.y,
							bounds.x + bounds.width, bounds.y });
					line2 = new PointList(new int[] { bounds.x,
							bounds.y + bounds.height - 1,
							bounds.x + bounds.width,
							bounds.y + bounds.height - 1 });
				}
				break;

			default:
				break;
			}

			switch (sashStyle) {
			case DOUBLE_LINES:
				graphics.drawLine(line1.getPoint(0), line1.getPoint(1));
				graphics.drawLine(line2.getPoint(0), line2.getPoint(1));
				break;
			case RIDGED:
				graphics.setForegroundColor(ColorConstants.buttonLightest);
				graphics.drawLine(line1.getPoint(0), line1.getPoint(1));
				graphics.setBackgroundColor(ColorConstants.button);
				if (horizontal)
					graphics.fillRectangle(bounds.x + 1, bounds.y,
							bounds.width - 1, bounds.height);
				else
					graphics.fillRectangle(bounds.x, bounds.y + 1,
							bounds.width, bounds.height - 1);
				graphics.setForegroundColor(ColorConstants.buttonDarker);
				graphics.drawLine(line2.getPoint(0), line2.getPoint(1));
				break;
			case LINE:
				graphics.setLineWidth(sashWidth);
				if (horizontal)
					graphics.drawLine(bounds.x + bounds.width / 2, bounds.y,
							bounds.x + bounds.width / 2, bounds.y
									+ bounds.height);
				else
					graphics.drawLine(bounds.x, bounds.y + bounds.height / 2,
							bounds.x + bounds.width, bounds.y + bounds.height
									/ 2);
				break;
			case ETCHED:
				graphics.setForegroundColor(ColorConstants.buttonDarker);
				graphics.drawLine(line1.getPoint(0), line1.getPoint(1));
				graphics.setBackgroundColor(ColorConstants.button);
				if (horizontal)
					graphics.fillRectangle(bounds.x + 1, bounds.y,
							bounds.width - 1, bounds.height);
				else
					graphics.fillRectangle(bounds.x, bounds.y + 1,
							bounds.width, bounds.height - 1);
				graphics.setForegroundColor(ColorConstants.buttonLightest);
				graphics.drawLine(line2.getPoint(0), line2.getPoint(1));
				break;
			default:
				break;
			}
			graphics.popState();
		}

	}

	/**
	 * style of the sash.
	 * 
	 * @author Xihui Chen
	 * 
	 */
	public enum SashStyle {

		/**
		 * Invisible.
		 */
		NONE("None"),

		ROUNDED("Rounded"),

		/**
		 * A ridged sash.
		 */
		RIDGED("Ridged"),
		/**
		 * An etched sash.
		 */
		ETCHED("Etched"),

		/**
		 * A line.
		 */
		LINE("Line"),

		DOUBLE_LINES("Double Lines");

		public static String[] stringValues() {
			String[] sv = new String[values().length];
			int i = 0;
			for (SashStyle p : values())
				sv[i++] = p.toString();
			return sv;
		}

		private String description;

		private SashStyle(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}

	}

	private IFigure pane;

	private ScrollPane scrollPane;

	private boolean transparent = false;

	/**
	 * Position of the splitter in percentage.
	 */
	private double sashPosition = 0.5;

	private int sashWidth = 3;

	private boolean horizontal = true;

	/**
	 * true if sash position has been changed but figure has not layout yet.
	 */
	private boolean sashPositionDirty = false;

	private SashStyle sashStyle = SashStyle.RIDGED;

	private Sash sash;

	public SashContainerFigure() {
		scrollPane = new ScrollPane() {
			@Override
			public boolean isOpaque() {
				return !transparent;
			}
		};
		pane = new FreeformLayer();
		pane.setLayoutManager(new FreeformLayout());
		add(scrollPane);
		scrollPane.setViewport(new FreeformViewport());
		scrollPane.setContents(pane);
		scrollPane.setScrollBarVisibility(ScrollPane.NEVER);
		sash = new Sash();
		add(sash);
	}

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}

	/**
	 * @return the content pane to host children.
	 */
	public IFigure getContentPane() {
		return pane;
	}

	/**
	 * @return sash position in percentage (0-1).
	 */
	public double getSashPosition() {
		return sashPosition;
	}

	/**
	 * @return sash style.
	 */
	public SashStyle getSashStyle() {
		return sashStyle;
	}

	/**
	 * @return sash width
	 */
	public int getSashWidth() {
		return sashWidth;
	}

	/**
	 * @return the bounds of two subpanels. The coordinate of bounds is relative
	 *         to the pane. bounds[0] is the bounds of left/up panel. bounds[1]
	 *         is the bounds of right/down panel.
	 */
	public Rectangle[] getSubPanelsBounds() {
		Rectangle boundsA = new Rectangle();
		Rectangle boundsB = new Rectangle();
		Rectangle clietArea = getClientArea();
		if (horizontal) {
			boundsA.x = 0;
			boundsA.y = 0;
			boundsA.width = (int) (sashPosition * clietArea.width - sashWidth / 2);
			boundsA.height = clietArea.height;
			boundsB.x = (int) (sashPosition * clietArea.width + sashWidth / 2);
			boundsB.y = 0;
			boundsB.width = clietArea.width - boundsA.width - sashWidth;
			boundsB.height = clietArea.height;
		} else {
			boundsA.x = 0;
			boundsA.y = 0;
			boundsA.width = clietArea.width;
			boundsA.height = (int) (sashPosition * clietArea.height - sashWidth / 2);
			;
			boundsB.x = 0;
			boundsB.y = (int) (sashPosition * clietArea.height + sashWidth / 2);
			boundsB.width = clietArea.width;
			boundsB.height = clietArea.height - boundsA.height - sashWidth;
		}
		return new Rectangle[] { boundsA, boundsB };
	}

	/**
	 * @return true if sash container is horizontal.
	 */
	public boolean isHorizontal() {
		return horizontal;
	}

	/**
	 * @return true is sash position changed but figure has not been layout yet.
	 */
	public boolean isSashPositionDirty() {
		return sashPositionDirty;
	}

	@Override
	protected void layout() {
		super.layout();
		Rectangle clientArea = getClientArea();
		scrollPane.setBounds(clientArea);
		if (horizontal)
			sash.setBounds(new Rectangle(clientArea.x
					+ (int) (sashPosition * clientArea.width - sashWidth / 2),
					clientArea.y, sashWidth, clientArea.height));
		else
			sash.setBounds(new Rectangle(clientArea.x, clientArea.y
					+ (int) (sashPosition * clientArea.height - sashWidth / 2),
					clientArea.width, sashWidth));
		sashPositionDirty = false;
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
		switch (sashStyle) {
		case ROUNDED:
			Rectangle clientArea = getClientArea();
			Rectangle[] subBounds = getSubPanelsBounds();		
			subBounds[0].translate(clientArea.getLocation());
			subBounds[1].translate(clientArea.getLocation());
			subBounds[0].width -=1;
			subBounds[1].width -=1;
			subBounds[0].height -=1;
			subBounds[1].height -=1;
			graphics.drawRoundRectangle(subBounds[0], 6, 6);
			graphics.drawRoundRectangle(subBounds[1], 6, 6);
			break;
		default:
			break;
		}
	}

	/**
	 * Set layout direction of sash container.
	 * 
	 * @param horizontal
	 */
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
		if (horizontal)
			sash.setCursor(Cursors.SIZEWE);
		else
			sash.setCursor(Cursors.SIZENS);
		revalidate();
	}

	@Override
	public void setOpaque(boolean opaque) {
		transparent = !opaque;
		pane.setOpaque(opaque);
		super.setOpaque(opaque);
	}

	/**
	 * @param sashPosition
	 *            sash position in percentage (0 - 1).
	 */
	public void setSashPosition(double sashPosition) {
		if (this.sashPosition == sashPosition)
			return;
		this.sashPosition = sashPosition;
		sashPositionDirty = true;
		revalidate();
		if (sashStyle == SashStyle.ROUNDED)
			repaint();
	}

	/**
	 * Set sash style.
	 * 
	 * @param sashStyle
	 */
	public void setSashStyle(SashStyle sashStyle) {
		this.sashStyle = sashStyle;
		repaint();
	}

	/**
	 * Set Sash width.
	 * 
	 * @param sashWidth
	 *            sash width
	 */
	public void setSashWidth(int sashWidth) {
		this.sashWidth = sashWidth;
		revalidate();
	}

}
