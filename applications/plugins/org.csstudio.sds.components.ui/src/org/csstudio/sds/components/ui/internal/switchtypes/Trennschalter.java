package org.csstudio.sds.components.ui.internal.switchtypes;

import org.csstudio.sds.components.common.CosySwitch;
import org.csstudio.sds.components.ui.internal.utils.Trigonometry;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;

/**
 * The Trennschalter switch type.
 * 
 * @author jbercic
 *
 */
public final class Trennschalter extends CosySwitch {
	
	/**
	 * The default constructor.
	 */
	public Trennschalter() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void paintBase(final Graphics gfx, final int width, final int height) {
		gfx.drawLine(width/2,0,width/2,height/3);
		gfx.drawLine(width/2,2*height/3,width/2,height);
		gfx.drawLine(width/4+width/8,height/3,
				3*width/4-width/8,height/3);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void paintOpenState(final Graphics gfx, final int width, final int height) {
		gfx.drawLine(width/2,2*height/3,
				width/2+(int)(height/3*Trigonometry.cos(120.0)),
				2*height/3-(int)(height/3*Trigonometry.sin(120.0)));
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void paintDashedOpenState(final Graphics gfx, final int width, final int height) {
		gfx.setLineStyle(SWT.LINE_DOT);
		gfx.drawLine(width/2,2*height/3,
				width/2+(int)(height/3*Trigonometry.cos(120.0)),
				2*height/3-(int)(height/3*Trigonometry.sin(120.0)));
		gfx.setLineStyle(SWT.LINE_SOLID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void paintClosedState(final Graphics gfx, final int width, final int height) {
		gfx.drawLine(width/2,2*height/3,width/2,height/3);
		gfx.fillOval(width/2-this.getLineWidth()*2,2*height/3-this.getLineWidth()*2,
				this.getLineWidth()*4,this.getLineWidth()*4);
	}
	
}
