package org.csstudio.sds.cosywidgets.ui.internal.switchtypes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Figure;

import org.csstudio.sds.cosywidgets.common.ICosySwitch;
import org.csstudio.sds.cosywidgets.ui.internal.figures.RefreshableSwitchFigure;

/**
 * Draws the image representing an unknown switch type.
 * 
 * @author jbercic
 *
 */
public final class UnknownSwitch implements ICosySwitch {
	/**
	 * Current width and height of this switch.
	 */
	private int width,height;
	
	/**
	 * The parent figure.
	 */
	private RefreshableSwitchFigure figure;
	
	/**
	 * {@inheritDoc}
	 */
	public void construct(Figure fig, int w, int h) {
		width=w;
		height=h;
		figure=(RefreshableSwitchFigure)fig;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void resize(int neww, int newh) {
		width=neww;
		height=newh;
	}
	
	/**
	 * Default constructor.
	 */
	public UnknownSwitch() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void paintSwitch(Graphics gfx, int state) {
		gfx.drawLine(0,0,width,height);
		gfx.drawLine(0,height,width,0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void paintUnknown(Graphics gfx) {
		paintSwitch(gfx,0);
	}
}
