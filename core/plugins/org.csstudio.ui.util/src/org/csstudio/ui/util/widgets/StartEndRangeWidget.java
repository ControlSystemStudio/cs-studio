/**
 * 
 */
package org.csstudio.ui.util.widgets;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

/**
 * @author shroffk
 * 
 */
public class StartEndRangeWidget extends Canvas {

	private double min;
	private double max;
	private double selectedMin;
	private double selectedMax;

	private double distancePerPx;

	public enum ORIENTATION {
		HORIZONTAL, VERTICAL
	}

	private enum MOVE {
		SELECTEDMIN, SELECTEDMAX, RANGE, NONE
	}

	private ORIENTATION orientation;
	private MOVE moveControl = MOVE.NONE;

	public StartEndRangeWidget(Composite parent, int style) {
		super(parent, SWT.DOUBLE_BUFFERED);

		addPaintListener(paintListener);
		addMouseListener(mouseListener);
		addMouseMoveListener(mouseListener);
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
		calculateDistancePerPx();
		redraw();
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
		calculateDistancePerPx();
		redraw();
	}

	public double getSelectedMin() {
		return selectedMin;
	}

	public void setSelectedMin(double selectedMin) {
		this.selectedMin = selectedMin;
		redraw();
	}

	public double getSelectedMax() {
		return selectedMax;
	}

	public void setSelectedMax(double selectedMax) {
		this.selectedMax = selectedMax;
		redraw();
	}

	// public ORIENTATION getOrientation() {
	// return orientation;
	// }

	public void setOrientation(ORIENTATION orientation) {
		this.orientation = orientation;
		redraw();
	}

	private void calculateDistancePerPx() {
		distancePerPx = getClientArea().width / Math.abs(max - min);
	}

	private final MouseRescale mouseListener = new MouseRescale();

	// Listener that implements the re-scaling through a drag operation
	private class MouseRescale extends MouseAdapter implements
			MouseMoveListener {

		private int rangeX;

		@Override
		public void mouseDown(MouseEvent e) {
			// Save the starting point
			// System.out.println("x:" + e.x + " y:" + e.y);
			double minSelectedOval = selectedMin * distancePerPx;
			double maxSelectedOval = selectedMax * distancePerPx;
			if ((e.x >= minSelectedOval && e.x <= minSelectedOval + 10)
					&& (e.y >= 0 && e.y <= 10)) {
				moveControl = MOVE.SELECTEDMIN;
			} else if ((e.x >= maxSelectedOval && e.x <= maxSelectedOval + 10)
					&& (e.y >= 0 && e.y <= 10)) {
				moveControl = MOVE.SELECTEDMAX;
			} else if ((e.x >= minSelectedOval + 10 && e.x <= maxSelectedOval)) {
				moveControl = MOVE.RANGE;
				rangeX = e.x;
				System.out.println(e.x);
			} else {
				moveControl = MOVE.NONE;
			}
		}

		@Override
		public void mouseUp(MouseEvent e) {
			moveControl = MOVE.NONE;
		}

		@Override
		public void mouseMove(MouseEvent e) {
			// Only if editable and it is a left click drag
//			System.out.println("M x:" + e.x + " M y:" + e.y);
			switch (moveControl) {
			case SELECTEDMIN:
				setSelectedMin(e.x / distancePerPx);
				break;
			case SELECTEDMAX:
				setSelectedMax(e.x / distancePerPx);
				break;
			case RANGE:
				System.out.println(e.x + " " + rangeX + " " + (e.x - rangeX)
						/ distancePerPx);
				double increment = ((e.x - rangeX) / distancePerPx);
				setSelectedMin(getSelectedMin() + increment);
				setSelectedMax(getSelectedMax() + increment);
				rangeX = e.x;
				break;
			default:
				break;
			}
		}
	}

	public void setDistancePerPx(double distancePerPx) {
		this.distancePerPx = distancePerPx;
		// New range: need to redraw and notify listeners
		redraw();
	}

	// Drawing function
	private PaintListener paintListener = new PaintListener() {

		@Override
		public void paintControl(PaintEvent e) {
			System.out.println("distance per " + distancePerPx);
			double height = getClientArea().height;
			int width = getClientArea().width;
			double currentPx = 0.0;
			int sizeIndex = 0;

			// Draw the line of appropriate size
			e.gc.drawLine(5, 5, width, 5);
			e.gc.setBackground(new Color(getDisplay(), 234, 246, 253));
			// min selected
			e.gc.drawOval((int) (selectedMin * distancePerPx), 0, 10, 10);
			e.gc.fillOval((int) ((selectedMin * distancePerPx) + 1), 1, 9, 9);
			// max selected
			e.gc.drawOval((int) (selectedMax * distancePerPx), 0, 10, 10);
			e.gc.fillOval((int) ((selectedMax * distancePerPx) + 1), 1, 9, 9);

		}
	};
}
