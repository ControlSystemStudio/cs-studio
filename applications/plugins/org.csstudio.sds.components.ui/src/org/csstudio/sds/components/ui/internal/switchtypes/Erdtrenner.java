package org.csstudio.sds.components.ui.internal.switchtypes;

import org.csstudio.sds.components.common.CosySwitch;
import org.csstudio.sds.components.ui.internal.utils.Trigonometry;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;

/**
 * The Erdtrenner switch type.
 * 
 * @author jbercic
 *
 */
public final class Erdtrenner extends CosySwitch {
	
	/**
	 * The default constructor.
	 */
	public Erdtrenner() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void paintBase(final Graphics gfx, final int width, final int height) {
		int wireHeight=(int)Math.round((double)height*0.82);
		gfx.drawLine(width/2,0,width/2,wireHeight/3);
		gfx.drawLine(width/2,2*wireHeight/3,width/2,wireHeight);
		//base three lines
		gfx.drawLine(width/4,wireHeight,3*width/4,wireHeight);
		gfx.drawLine(width/4+width/16,wireHeight+(height-wireHeight)/2,
				3*width/4-width/16,wireHeight+(height-wireHeight)/2);
		gfx.drawLine(width/4+width/8,height,
				3*width/4-width/8,height);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void paintOpenState(final Graphics gfx, final int width, final int height) {
		int wireHeight=(int)Math.round((double)height*0.82);
		gfx.drawLine(width/2,2*wireHeight/3,
				width/2+(int)(wireHeight/3*Trigonometry.cos(120.0)),
				2*wireHeight/3-(int)(wireHeight/3*Trigonometry.sin(120.0)));
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void paintDashedOpenState(final Graphics gfx, final int width, final int height) {
		int wireHeight=(int)Math.round((double)height*0.82);
		gfx.setLineStyle(SWT.LINE_DOT);
		gfx.drawLine(width/2,2*wireHeight/3,
				width/2+(int)(wireHeight/3*Trigonometry.cos(120.0)),
				2*wireHeight/3-(int)(wireHeight/3*Trigonometry.sin(120.0)));
		gfx.setLineStyle(SWT.LINE_SOLID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void paintClosedState(final Graphics gfx, final int width, final int height) {
		int wireHeight=(int)Math.round((double)height*0.82);
		gfx.drawLine(width/2,2*wireHeight/3,width/2,wireHeight/3);
		gfx.drawLine(width/4+width/8,wireHeight/3,
				3*width/4-width/8,wireHeight/3);
		gfx.fillOval(width/2-this.getLineWidth()*2,2*wireHeight/3-this.getLineWidth()*2,
				this.getLineWidth()*4,this.getLineWidth()*4);
	}
	
}
