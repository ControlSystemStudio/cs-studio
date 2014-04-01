package org.csstudio.swt.xygraph.figures;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.swt.xygraph.Preferences;
import org.csstudio.swt.xygraph.util.SWTConstants;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * The axis traces for the plot area.
 * 
 * @author Davy Dequidt
 * @author Jaka Bobnar
 * 
 */
public class AxisTrace extends Figure implements MouseMotionListener,
		PropertyChangeListener {

	/** Use advanced graphics? */
	private final boolean use_advanced_graphics = Preferences.useAdvancedGraphics();

	private int cursor_x;
	private int cursor_y;
	private Color backColor;
	private Color revertBackColor;

	public AxisTrace(PlotArea plotArea) {
		backColor = plotArea.getBackgroundColor();
		RGB backRGB = backColor.getRGB();
		revertBackColor = XYGraphMediaFactory.getInstance().getColor(
				255 - backRGB.red, 255 - backRGB.green, 255 - backRGB.blue);

		plotArea.addPropertyChangeListener(this);
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		if (!isVisible()) {
			return;
		}
		super.paintFigure(graphics);

		graphics.pushState();
		graphics.setAdvanced(use_advanced_graphics);
		if (use_advanced_graphics) {
			graphics.setAntialias(SWT.ON);
		}
	
		graphics.setForegroundColor(revertBackColor);
		graphics.setLineStyle(SWTConstants.LINE_DOT);
		graphics.setLineWidth(2);
		graphics.drawLine(cursor_x, bounds.y, cursor_x, bounds.y + bounds.height);
		graphics.setLineWidth(1);
		graphics.drawLine(bounds.x, cursor_y, bounds.x + bounds.width, cursor_y);
		
		graphics.popState();
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
		cursor_x = me.getLocation().x;
		cursor_y = me.getLocation().y;
		if (isVisible()) {
			repaint();
		}
	}
		
	/*
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (PlotArea.BACKGROUND_COLOR.equals(evt.getPropertyName())) {
			backColor = (Color) evt.getNewValue();
			RGB backRGB = backColor.getRGB();
			revertBackColor = XYGraphMediaFactory.getInstance().getColor(
					255 - backRGB.red, 255 - backRGB.green, 255 - backRGB.blue);
		}
	}
}
