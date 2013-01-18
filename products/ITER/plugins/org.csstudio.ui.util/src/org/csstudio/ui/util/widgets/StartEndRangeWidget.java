/**
 * 
 */
package org.csstudio.ui.util.widgets;

import java.util.HashSet;
import java.util.Set;

import javax.activity.InvalidActivityException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author shroffk
 * 
 */
public class StartEndRangeWidget extends Canvas {

	private double min = 0;
	private double max = 1;
	private double selectedMin;
	private double selectedMax;

	private boolean followMin = true;
	private boolean followMax = true;

	private boolean isEditable = true;

	private double distancePerPx;

	public enum ORIENTATION {
		HORIZONTAL, VERTICAL
	}

	private enum MOVE {
		SELECTEDMIN, SELECTEDMAX, RANGE, NONE
	}

	private Set<RangeListener> listeners = new HashSet<RangeListener>();

	/**
	 * Adds a listener, notified if the range resolution changes.
	 * 
	 * @param listener
	 *            a new listener
	 */
	public void addRangeListener(RangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            listener to be removed
	 */
	public void removeRangeListener(RangeListener listener) {
		listeners.remove(listener);
	}

	private void fireRangeChanged() {
		for (RangeListener listener : listeners) {
			listener.rangeChanged();
		}
	}

	private ORIENTATION orientation = ORIENTATION.HORIZONTAL;
	private MOVE moveControl = MOVE.NONE;

	public StartEndRangeWidget(Composite parent, int style) {
		super(parent, SWT.DOUBLE_BUFFERED);

		addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				recalculateDistancePerPx();
			}

			@Override
			public void controlMoved(ControlEvent e) {

			}
		});
		addPaintListener(paintListener);
		addMouseListener(mouseListener);
		addMouseMoveListener(mouseListener);
		addRangeListener(new RangeListener() {

			@Override
			public void rangeChanged() {
				redraw();
			}
		});
		if (followMin) {
			selectedMin = min;
		}
		if (followMax) {
			selectedMax = max;
		}
		redraw();
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		if (this.min != min) {
			if (min <= this.max) {
				this.min = min;
				double oldSelectedRange = getSelectedRange();
				if (followMin || this.selectedMin < min) {
					this.selectedMin = min;
				}
				if (this.selectedMax < min) {
					this.selectedMax = min + oldSelectedRange > this.max ? this.max
							: min + oldSelectedRange;
				}
				recalculateDistancePerPx();
			} else {
				throw new IllegalArgumentException(
						"Invalid argument, min value " + min
								+ " must be smaller than max " + this.max);
			}
		}
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		if (this.max != max) {
			if (max >= this.min) {
				this.max = max;
				if (followMax || this.selectedMax > max) {
					this.selectedMax = max;
				}
				recalculateDistancePerPx();
			} else {
				throw new IllegalArgumentException(
						"Invalid argument, max value " + max
								+ " must be larger than minimum " + this.min);
			}
		}
	}

	public double getSelectedMin() {
		return selectedMin;
	}

	public void setSelectedMin(double selectedMin) {
		if (this.selectedMin != selectedMin) {
			if (!(selectedMin < this.min) && (selectedMin <= this.selectedMax)) {
				this.selectedMin = selectedMin;
				if (selectedMin == this.min) {
					followMin = true;
				}
				fireRangeChanged();
			} else {
				throw new IllegalArgumentException(
						"Invalid value for selectedMin," + selectedMin
								+ " must be within the range " + this.min + "-"
								+ this.selectedMax);
			}
		}
	}

	public double getSelectedMax() {
		return selectedMax;
	}

	public void setSelectedMax(double selectedMax) {
		if (this.selectedMax != selectedMax) {
			if (!(selectedMax > this.max) && (selectedMax >= this.selectedMin)) {
				this.selectedMax = selectedMax;
				if (selectedMax == this.max) {
					followMax = true;
				}
				fireRangeChanged();
			} else {
				throw new IllegalArgumentException(
						"Invalid value for selectedMax," + selectedMax
								+ " must be within the range "
								+ this.selectedMin + "-" + this.max);
			}
		}
	}

	public void setSelectedRange(double selectedMin, double selectedMax) {
		if (selectedMax <= this.max && selectedMin >= this.min
				&& selectedMax >= selectedMin) {
			this.selectedMin = selectedMin;
			this.selectedMax = selectedMax;
			fireRangeChanged();
		} else {
			throw new IllegalArgumentException("Invalid range values.");
		}
	}

	public void setRange(double min, double max) {
		if (min <= max) {
			this.min = min;
			if (selectedMin < min || followMin) {
				this.selectedMin = min;
			}
			this.max = max;
			if (selectedMax > max || followMax) {
				this.selectedMax = max;
			}
			recalculateDistancePerPx();
		} else {
			throw new IllegalArgumentException(
					"Invalid range values, minimum cannot be greater than maximum");
		}
	}

	public void setRanges(double min, double selectedMin, double selectedMax,
			double max) {
		if (min <= selectedMin && selectedMin <= selectedMax
				&& selectedMax <= max) {
			this.min = min;
			this.selectedMin = selectedMin;
			if (selectedMin == this.min) {
				followMin = true;
			}
			this.selectedMax = selectedMax;
			if (selectedMax == this.max) {
				followMax = true;
			}
			this.max = max;
			recalculateDistancePerPx();
			// fireRangeChanged();
		} else {
			throw new IllegalArgumentException();
		}
	}

	// public ORIENTATION getOrientation() {
	// return orientation;
	// }

	public void setOrientation(ORIENTATION orientation) {
		if (this.orientation != orientation) {
			this.orientation = orientation;
			fireRangeChanged();
		}
	}

	public double getRange() {
		return this.max - this.min;
	}

	public double getSelectedRange() {
		return this.selectedMax - this.selectedMin;
	}

	private void recalculateDistancePerPx() {
		switch (orientation) {
		case HORIZONTAL:
			setDistancePerPx((getClientArea().width - 10) / Math.abs(max - min));
			break;
		case VERTICAL:
			setDistancePerPx((getClientArea().height - 10)
					/ Math.abs(max - min));
			break;
		default:
			break;
		}

	}

	private final MouseRescale mouseListener = new MouseRescale();

	// Listener that implements the re-scaling through a drag operation
	private class MouseRescale extends MouseAdapter implements
			MouseMoveListener {

		private volatile int rangeX;

		@Override
		public void mouseDown(MouseEvent e) {
			// Save the starting point
			double zero = (0 - min) * distancePerPx;

			double minSelectedOval = zero + (selectedMin * distancePerPx);
			double maxSelectedOval = zero + (selectedMax * distancePerPx);

			int valueAlongOrientationAxis;
			int valueAlongNonOrientationAxis;
			if (orientation.equals(ORIENTATION.HORIZONTAL)) {
				valueAlongOrientationAxis = e.x;
				valueAlongNonOrientationAxis = e.y;
			} else {
				valueAlongOrientationAxis = e.y;
				valueAlongNonOrientationAxis = e.x;
			}
			if ((valueAlongOrientationAxis >= minSelectedOval && valueAlongOrientationAxis <= minSelectedOval + 10)
					&& (valueAlongNonOrientationAxis >= 0 && valueAlongNonOrientationAxis <= 10)) {
				moveControl = MOVE.SELECTEDMIN;
				followMin = false;
			} else if ((valueAlongOrientationAxis >= maxSelectedOval && valueAlongOrientationAxis <= maxSelectedOval + 10)
					&& (valueAlongNonOrientationAxis >= 0 && valueAlongNonOrientationAxis <= 10)) {
				moveControl = MOVE.SELECTEDMAX;
				followMax = false;
			} else if ((valueAlongOrientationAxis >= minSelectedOval + 10 && valueAlongOrientationAxis <= maxSelectedOval)) {
				moveControl = MOVE.RANGE;
				followMin = false;
				followMax = false;
				rangeX = valueAlongOrientationAxis;
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
			// System.out.println(e.x + " " + e.y);
			int valueAlongOrientationAxis;
			double zero = (0 - min * distancePerPx);
			if (orientation.equals(ORIENTATION.HORIZONTAL)) {
				valueAlongOrientationAxis = e.x;
			} else {
				valueAlongOrientationAxis = e.y;
			}
			switch (moveControl) {
			case SELECTEDMIN:
				double newSelectedMin = Math.max(
						(valueAlongOrientationAxis - zero) / distancePerPx,
						getMin());
				setSelectedMin(newSelectedMin);
//				setToolTipText(String.valueOf(newSelectedMin));
				break;
			case SELECTEDMAX:
				double newSelectedMax = Math.min(
						(valueAlongOrientationAxis - zero) / distancePerPx,
						getMax());
				setSelectedMax(newSelectedMax);
//				setToolTipText(String.valueOf(newSelectedMax));
				break;
			case RANGE:
				double increment = ((valueAlongOrientationAxis - rangeX) / distancePerPx);
				if ((getSelectedMin() + increment) >= getMin()
						&& (getSelectedMax() + increment) <= getMax()) {
					setSelectedRange(getSelectedMin() + increment,
							getSelectedMax() + increment);
					rangeX = valueAlongOrientationAxis;
				} else if (getSelectedMin() + increment < getMin()) {
					setSelectedRange(getMin(), getMin() + getSelectedRange());
				} else if (getSelectedMax() + increment > getMax()) {
					setSelectedRange(getMax() - getSelectedRange(), getMax());
				}
				break;
			default:
				break;
			}
		}
	}

	private void setDistancePerPx(double distancePerPx) {
		this.distancePerPx = distancePerPx;
		// New range: need to redraw and notify listeners
		fireRangeChanged();
	}

	// Drawing function
	private PaintListener paintListener = new PaintListener() {

		@Override
		public void paintControl(PaintEvent e) {
			Point origin = new Point(5, 5);
			Point end;
			Point minOval;
			Point maxOval;
			double zero = (0 - min) * distancePerPx;
			if (orientation.equals(ORIENTATION.HORIZONTAL)) {
				end = new Point(getClientArea().width - 5, 5);
				minOval = new Point(
						(int) (zero + (selectedMin * distancePerPx)), 0);
				maxOval = new Point(
						(int) (zero + (selectedMax * distancePerPx)) - 1, 0);
			} else {
				end = new Point(5, getClientArea().height - 5);
				minOval = new Point(0,
						(int) (zero + (selectedMin * distancePerPx)));
				maxOval = new Point(0,
						(int) (zero + (selectedMax * distancePerPx)) - 1);
			}

			// Draw the line of appropriate size
			e.gc.drawLine(origin.x, origin.y, end.x, end.y);

			e.gc.setBackground(new Color(getDisplay(), 234, 246, 253));
			// min selected
			e.gc.drawOval(minOval.x, minOval.y, 10, 10);
			e.gc.fillOval(minOval.x + 1, minOval.y + 1, 9, 9);
			// max selected
			e.gc.drawOval(maxOval.x, maxOval.y, 10, 10);
			e.gc.fillOval(maxOval.x + 1, maxOval.y + 1, 9, 9);
		}
	};
}
