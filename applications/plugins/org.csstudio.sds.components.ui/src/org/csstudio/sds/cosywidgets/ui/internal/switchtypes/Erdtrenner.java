package org.csstudio.sds.cosywidgets.ui.internal.switchtypes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Figure;
import org.eclipse.swt.SWT;

import org.csstudio.sds.cosywidgets.common.ICosySwitch;
import org.csstudio.sds.cosywidgets.ui.internal.figures.RefreshableSwitchFigure;
import org.csstudio.sds.cosywidgets.ui.internal.utils.Trigonometry;

/**
 * The Erdtrenner switch type.
 * 
 * @author jbercic
 *
 */
public final class Erdtrenner implements ICosySwitch {
	/**
	 * Height of the line upper three lines.
	 */
	private int wire_height=0;
	
	/**
	 * Current width and height of this switch.
	 */
	private int width,height;
	
	/**
	 * The owning figure.
	 */
	private RefreshableSwitchFigure figure;
	
	/**
	 * {@inheritDoc}
	 */
	public void construct(Figure fig, int w, int h) {
		width=w;
		height=h;
		figure=(RefreshableSwitchFigure)fig;
		wire_height=(int)Math.round((double)h*0.82);
	}
	
	/**
	 * The default constructor.
	 */
	public Erdtrenner() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void resize(int neww, int newh) {
		width=neww;
		height=newh;
		wire_height=(int)Math.round((double)newh*0.82);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void paintSwitch(Graphics gfx, int state) {
		height-=figure.getLineWidth()/2+figure.getLineWidth()%2;
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
		height+=figure.getLineWidth()/2+figure.getLineWidth()%2;
	}
	
	/**
	 * Paints the base of the switch, which is always the same.
	 */
	private void paintBase(Graphics gfx) {
		gfx.drawLine(width/2,0,width/2,wire_height/3);
		gfx.drawLine(width/2,2*wire_height/3,width/2,wire_height);
		//base three lines
		gfx.drawLine(width/4,wire_height,3*width/4,wire_height);
		gfx.drawLine(width/4+width/16,wire_height+(height-wire_height)/2,
				3*width/4-width/16,wire_height+(height-wire_height)/2);
		gfx.drawLine(width/4+width/8,height,
				3*width/4-width/8,height);
	}
	
	/**
	 * wires not connected
	 */
	private void paintOpen(Graphics gfx) {
		gfx.drawLine(width/2,2*wire_height/3,
				width/2+(int)(wire_height/3*Trigonometry.cos(120.0)),
				2*wire_height/3-(int)(wire_height/3*Trigonometry.sin(120.0)));
	}
	
	/**
	 * wires not connected, dashed connecting wire
	 */
	private void paintDashedOpen(Graphics gfx) {
		gfx.setLineStyle(SWT.LINE_DOT);
		gfx.drawLine(width/2,2*wire_height/3,
				width/2+(int)(wire_height/3*Trigonometry.cos(120.0)),
				2*wire_height/3-(int)(wire_height/3*Trigonometry.sin(120.0)));
		gfx.setLineStyle(SWT.LINE_SOLID);
	}
	
	/**
	 * wires connected
	 */
	private void paintClosed(Graphics gfx) {
		gfx.drawLine(width/2,2*wire_height/3,width/2,wire_height/3);
		gfx.drawLine(width/4+width/8,wire_height/3,
				3*width/4-width/8,wire_height/3);
		gfx.fillOval(width/2-figure.getLineWidth()*2,2*wire_height/3-figure.getLineWidth()*2,
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
