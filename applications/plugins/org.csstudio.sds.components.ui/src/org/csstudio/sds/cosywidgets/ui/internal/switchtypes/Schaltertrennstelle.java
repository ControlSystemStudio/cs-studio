package org.csstudio.sds.cosywidgets.ui.internal.switchtypes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Figure;
import org.eclipse.swt.SWT;

import org.csstudio.sds.cosywidgets.common.ICosySwitch;
import org.csstudio.sds.cosywidgets.ui.internal.figures.RefreshableSwitchFigure;
import org.csstudio.sds.cosywidgets.ui.internal.utils.Trigonometry;

/**
 * The Schaltertrennstelle switch type.
 * 
 * @author jbercic
 *
 */
public final class Schaltertrennstelle implements ICosySwitch {
	/**
	 * The owning figure.
	 */
	private RefreshableSwitchFigure figure;
	
	/**
	 * Current width and height of this switch.
	 */
	private int width,height;
	
	/**
	 * The displacement of the switch because the arcs are not 180 degrees long but 151.
	 */
	int delta=0;
	
	/**
	 * {@inheritDoc}
	 */
	public void construct(Figure fig, int w, int h) {
		width=w;
		height=h;
		figure=(RefreshableSwitchFigure)fig;
		delta=(int)Math.round((double)w/4.0*Trigonometry.sin(13.0));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void resize(int neww, int newh) {
		width=neww;
		height=newh;
		delta=(int)Math.round((double)neww/4.0*Trigonometry.sin(13.0));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Schaltertrennstelle() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void paintSwitch(Graphics gfx, int state) {
		paintBase(gfx);
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
	private void paintBase(Graphics gfx) {
		int disp=gfx.getLineWidth()/2+gfx.getLineWidth()%2;
		gfx.drawRectangle(disp,4*disp+width/4,width-2*disp,height-8*disp-width/2);
		gfx.drawLine(width/2,width/4-delta,width/2,height/3);
		gfx.drawLine(width/2,2*height/3,width/2,height-width/4+delta);
		gfx.drawArc(width/4,-width/4-delta,width/2,width/2,193,151);
		gfx.drawArc(width/4,height-width/4+delta,width/2,width/2,13,151);
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
