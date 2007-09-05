package org.csstudio.sds.components.ui.internal.switchtypes;

import org.csstudio.sds.components.common.CosySwitch;
import org.csstudio.sds.components.ui.internal.utils.Trigonometry;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;

/**
 * The Schaltertrennstelle switch type.
 * 
 * @author jbercic
 *
 */
public final class Schaltertrennstelle extends CosySwitch {
	
	/**
	 * Constructor.
	 */
	public Schaltertrennstelle() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void paintBase(final Graphics gfx, final int width, final int height) {
		int disp=gfx.getLineWidth()/2+gfx.getLineWidth()%2;
		int delta=(int)Math.round((double)width/4.0*Trigonometry.sin(13.0));
		gfx.drawRectangle(disp,4*disp+width/4,width-2*disp,height-8*disp-width/2);
		gfx.drawLine(width/2,width/4-delta,width/2,height/3);
		gfx.drawLine(width/2,2*height/3,width/2,height-width/4+delta);
		gfx.drawArc(width/4,-width/4-delta,width/2,width/2,193,151);
		gfx.drawArc(width/4,height-width/4+delta,width/2,width/2,13,151);
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
		gfx.drawLine(width/4+width/8,height/3,
				3*width/4-width/8,height/3);
		gfx.fillOval(width/2-this.getLineWidth()*2,2*height/3-this.getLineWidth()*2,
				this.getLineWidth()*4,this.getLineWidth()*4);
	}
	
}
