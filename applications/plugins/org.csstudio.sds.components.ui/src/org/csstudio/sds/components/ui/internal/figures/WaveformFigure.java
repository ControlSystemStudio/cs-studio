package org.csstudio.sds.components.ui.internal.figures;


import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class WaveformFigure extends Panel implements IRefreshableFigure {
	private double[] _data;

	public WaveformFigure(){
		_data = new double[0];
	}
	public void randomNoiseRefresh() {

	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void setData(double[] data) {
		_data = data;
		repaint();
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		Rectangle bounds = getBounds();
		
		double i = 0;

		Point lastPoint = null;

		for (double value : _data) {
			if (lastPoint == null) {
				lastPoint = new Point(i, value).translate(bounds.getTopLeft());
			}
			
			Point currentPoint = new Point(i, value).translate(bounds.getTopLeft());
			
			graphics.drawLine(lastPoint, currentPoint);
			
			lastPoint = currentPoint.getCopy();
			i += 5;
		}
	}
}
