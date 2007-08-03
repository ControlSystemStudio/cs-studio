package org.csstudio.sds.cosywidgets.common;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Figure;

/**
 * External interface for the various switch types.
 * 
 * @author jbercic
 *
 */
public interface ICosySwitch {
	/**
	 * Initializes a switch instance.
	 * 
	 * @param fig The parent IFigure.
	 * @param w Width of the parent.
	 * @param h Height of the parent.
	 */
	public void construct(Figure fig, int w, int h);
	
	/**
	 * Resets the width and height of this switch.
	 * 
	 * @param neww The new width.
	 * @param newh The new height.
	 */
	public void resize(int neww, int newh);
	
	/**
	 * The main drawing routine that draws the switch.
	 */
	public void paintSwitch(Graphics gfx, int state);
	
	/**
	 * Draws a cross representing an unknown state.
	 */
	public void paintUnknown(Graphics gfx);
}
