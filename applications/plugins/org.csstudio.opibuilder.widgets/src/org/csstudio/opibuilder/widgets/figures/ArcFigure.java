package org.csstudio.opibuilder.widgets.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.swt.SWT;

/**The arc figure
 * 
 * @author Xihui Chen
 *
 */
public class ArcFigure extends Shape {
	
	private boolean antiAlias = true;
//	private boolean cordFill = false;
	private int startAngle = 0;	
	private int totalAngle = 90;
	

	@Override
	protected void fillShape(Graphics graphics) {
		graphics.setAntialias(antiAlias ? SWT.ON : SWT.OFF);
		graphics.fillArc(getClientArea().getCopy().shrink(
				(int)(getLineWidth()*1.5), (int)(getLineWidth()*1.5)), startAngle, totalAngle);
		
	}

	@Override
	protected void outlineShape(Graphics graphics) {
		graphics.setAntialias(antiAlias ? SWT.ON : SWT.OFF);
		graphics.drawArc(getClientArea().getCopy().shrink(		
				getLineWidth(), getLineWidth()), startAngle, totalAngle);		

	}
	
	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}
	
	public void setStartAngle(int start_angle) {
		this.startAngle = start_angle;
	}

	
	public void setTotalAngle(int total_angle) {
		this.totalAngle = total_angle;
	}
}
