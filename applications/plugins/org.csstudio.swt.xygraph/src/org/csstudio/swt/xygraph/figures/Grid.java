package org.csstudio.swt.xygraph.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;

/**The grid in the plot area.
 * @author Xihui Chen
 *
 */
public class Grid extends Figure implements IAxisListener{

	private Axis axis;
	
	public Grid(Axis axis) {
		axis.addListener(this);
		this.axis = axis;
		
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {		
		super.paintFigure(graphics);
		graphics.pushState();
		if(axis.isShowMajorGrid()){
			graphics.setLineStyle(axis.isDashGridLine()? SWT.LINE_DASH : SWT.LINE_SOLID);
			graphics.setForegroundColor(axis.getMajorGridColor());
			graphics.setLineWidth(1);
			for(int pos: axis.getScaleTickLabels().getTickLabelPositions()){
				if(axis.isHorizontal())
					graphics.drawLine(axis.getBounds().x + pos, bounds.y + bounds.height,
							axis.getBounds().x + pos, bounds.y);
				else
					graphics.drawLine(bounds.x, axis.getBounds().y + axis.getBounds().height - pos, bounds.x + bounds.width,
							axis.getBounds().y + axis.getBounds().height - pos);
			}
		}
		graphics.popState();
	}

	public void axisRevalidated(Axis axis) {
		if(axis.isShowMajorGrid())
			repaint();	
	}
	
}
