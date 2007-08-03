package org.csstudio.sds.cosywidgets.ui.internal.switchtypes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Figure;
import org.eclipse.swt.SWT;

import org.csstudio.sds.cosywidgets.common.ICosySwitch;
import org.csstudio.sds.cosywidgets.ui.internal.figures.RefreshableSwitchFigure;
import org.csstudio.sds.cosywidgets.ui.internal.utils.Trigonometry;

/**
 * The Leistungsschalter switch type.
 * 
 * @author jbercic
 *
 */
public final class Leistungsschalter implements ICosySwitch {
	/**
	 * The owning figure.
	 */
	private RefreshableSwitchFigure figure;
	
	/**
	 * Current width and height of this switch.
	 */
	private int width,height;
	
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
	public Leistungsschalter() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void resize(int neww, int newh) {
		width=neww;
		height=newh;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void paintSwitch(Graphics gfx, int state) {
		int delta=gfx.getLineWidth()/2+gfx.getLineWidth()%2;
		paintBase(gfx,delta);
		switch (state) {
			case RefreshableSwitchFigure.STATE_GESTOERT:
			case RefreshableSwitchFigure.STATE_AUS:
				paintOpen(gfx);
				break;
			case RefreshableSwitchFigure.STATE_EIN:
				paintClosed(gfx);
				break;
			case RefreshableSwitchFigure.STATE_SCHALTET:
				paintDashedOpen(gfx);
				break;
			default:
				paintUnknown(gfx);
				break;
		}
	}
	
	/**
	 * Paints the base of the switch, which is always the same.
	 */
	private void paintBase(Graphics gfx, int delta) {
		gfx.drawRectangle(delta,4*delta,width-2*delta,height-8*delta);
		gfx.drawLine(width/2,0,width/2,height/3);
		gfx.drawLine(width/2,2*height/3,width/2,height);
	}
	
	/**
	 * wires not connected
	 */
	private void paintOpen(Graphics gfx) {
		gfx.drawLine(width/2,2*height/3,
				width/2+(int)(height/3*Trigonometry.cos(120.0)),
				2*height/3-(int)(height/3*Trigonometry.sin(120.0)));
	}
	
	/**
	 * wires not connected, dashed connecting wire
	 */
	private void paintDashedOpen(Graphics gfx) {
		gfx.setLineStyle(SWT.LINE_DOT);
		gfx.drawLine(width/2,2*height/3,
				width/2+(int)(height/3*Trigonometry.cos(120.0)),
				2*height/3-(int)(height/3*Trigonometry.sin(120.0)));
		gfx.setLineStyle(SWT.LINE_SOLID);
	}
	
	/**
	 * wires connected
	 */
	private void paintClosed(Graphics gfx) {
		gfx.drawLine(width/2,2*height/3,width/2,height/3);
		gfx.drawLine(width/4+width/8,height/3,
				3*width/4-width/8,height/3);
		gfx.fillOval(width/2-figure.getLineWidth()*2,2*height/3-figure.getLineWidth()*2,
				figure.getLineWidth()*4,figure.getLineWidth()*4);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void paintUnknown(Graphics gfx) {
		gfx.drawLine(0,0,width,height);
		gfx.drawLine(0,height,width,0);
	}
}
