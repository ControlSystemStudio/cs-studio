package org.csstudio.sds.components.common;

import org.eclipse.draw2d.Graphics;

/**
 * External interface for the various switch types.
 * 
 * @author jbercic, Kai Meyer
 *
 */
public class CosySwitch {
	
	/**
	 * Switch states <i>UNKNOWN</i>.
	 */
	public static final int STATE_UNKNOWN = -1;
	/**
	 * Switch states <i>AUS</i>.
	 */
	public static final int STATE_AUS = 0;
	/**
	 * Switch states <i>EIN</i>.
	 */
	public static final int STATE_EIN = 1;
	/**
	 * Switch states <i>GESTOERT</i>.
	 */
	public static final int STATE_GESTOERT = 8;
	/**
	 * Switch states <i>SCHALTET</i>.
	 */
	public static final int STATE_SCHALTET = 6;
	
	/**
	 * The line width for the switch.
	 */
	private int _lineWidth = 1;
	
	/**
	 * Sets the width of the lines.
	 * @param lineWidth The new line width
	 */
	public final void setLineWidth(final int lineWidth) {
		_lineWidth = lineWidth;
	}
	
	/**
	 * Returns the current line width.
	 * @return the current line width
	 */
	public final int getLineWidth() {
		return _lineWidth;
	}
	
	/**
	 * The main drawing routine that draws the switch.
	 * @param gfx The {@link Graphics} to use
	 * @param state The state of the switch
	 * @param width The width of the switch
	 * @param height The height of the switch
	 */
	public final void paintSwitch(final Graphics gfx, final int state, final int width, final int height) { 
		paintBase(gfx, width, height);
		switch (state) {
			case STATE_GESTOERT:
				paintDisturbedState(gfx, width, height);
				break;
			case STATE_AUS:
				paintOpenState(gfx, width, height);
				break;
			case STATE_EIN:
				paintClosedState(gfx, width, height);
				break;
			case STATE_SCHALTET:
				paintDashedOpenState(gfx, width, height);
				break;
			default:
				paintUnknownState(gfx, width, height);
				break;
		}
	}

	/**
	 * Paints the base of the switch, which is always the same.
	 * @param gfx The {@link Graphics} to use
	 * @param width The width of the switch
	 * @param height The height of the switch
	 */
	protected void paintBase(Graphics gfx, final int width, final int height) {
		gfx.drawLine(0,0,width,height);
		gfx.drawLine(0,height,width,0);
	}
	
	/**
	 * Paints a disturbed switch.
	 * @param gfx The {@link Graphics} to use
	 * @param width The width of the switch
	 * @param height The height of the switch
	 */
	protected void paintDisturbedState(final Graphics gfx, final int width, final int height) {
	}
	
	/**
	 * Paints a switch, which wires are not connected.
	 * @param gfx The {@link Graphics} to use
	 * @param width The width of the switch
	 * @param height The height of the switch
	 */
	protected void paintOpenState(final Graphics gfx, final int width, final int height) {	
	}
	
	/**
	 * Paints a switch, which wires are not connected and a dashed connection.
	 * @param gfx The {@link Graphics} to use
	 * @param width The width of the switch
	 * @param height The height of the switch
	 */
	protected void paintDashedOpenState(final Graphics gfx, final int width, final int height) {	
	}
	
	/**
	 * Paints a switch, which wires are connected.
	 * @param gfx The {@link Graphics} to use
	 * @param width The width of the switch
	 * @param height The height of the switch
	 */
	protected void paintClosedState(final Graphics gfx, final int width, final int height) {
	}
	
	/**
	 * Paints a switch, which state is unknown.
	 * @param gfx The {@link Graphics} to use
	 * @param width The width of the switch
	 * @param height The height of the switch
	 */
	protected void paintUnknownState(final Graphics gfx, final int width, final int height) {
		gfx.drawLine(0,0,width,height);
		gfx.drawLine(0,height,width,0);
	}
}
