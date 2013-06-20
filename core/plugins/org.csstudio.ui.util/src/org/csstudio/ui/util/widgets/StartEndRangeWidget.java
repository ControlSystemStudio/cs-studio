/**
 * 
 */
package org.csstudio.ui.util.widgets;

import java.util.HashSet;
import java.util.Set;

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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

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
	SELECTEDMIN, SELECTEDMAX, RANGE, NONE, SELECTED
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
	if (min != this.min || max != this.max) {
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
    }

    public void setRanges(double min, double selectedMin, double selectedMax,
	    double max) {
	if (min != this.min || max != this.max
		|| selectedMin != this.selectedMin
		|| selectedMax != this.selectedMax) {
	    if (min <= selectedMin && selectedMin <= selectedMax
		    && selectedMax <= max) {
		this.min = min;
		this.selectedMin = selectedMin;
		this.max = max;
		this.selectedMax = selectedMax;
		if (selectedMin == min) {
		    followMin = true;
		}
		if (selectedMax == max) {
		    followMax = true;
		}
		recalculateDistancePerPx();
	    } else {
		throw new IllegalArgumentException();
	    }
	}
    }

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

	    moveControl = MOVE.NONE;
	    if ((valueAlongOrientationAxis >= minSelectedOval && valueAlongOrientationAxis <= minSelectedOval + 5)
		    && (valueAlongNonOrientationAxis >= 0 && valueAlongNonOrientationAxis <= 10)) {
		moveControl = MOVE.SELECTEDMIN;
		followMin = false;
	    }
	    if ((valueAlongOrientationAxis >= maxSelectedOval + 5 && valueAlongOrientationAxis <= maxSelectedOval + 10)
		    && (valueAlongNonOrientationAxis >= 0 && valueAlongNonOrientationAxis <= 10)) {
		moveControl = MOVE.SELECTEDMAX;
		followMax = false;
	    }
	    if ((valueAlongOrientationAxis >= minSelectedOval + 10 && valueAlongOrientationAxis <= maxSelectedOval)) {
		moveControl = MOVE.RANGE;
		followMin = false;
		followMax = false;
		rangeX = valueAlongOrientationAxis;
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
		if (newSelectedMin < getSelectedMax()) {
		    setSelectedMin(newSelectedMin);
		}
		break;
	    case SELECTEDMAX:
		double newSelectedMax = Math.min(
			(valueAlongOrientationAxis - zero) / distancePerPx,
			getMax());
		if (newSelectedMax > getSelectedMin()) {
		    setSelectedMax(newSelectedMax);
		}
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

    /**
     * Drawing function
     * 
     * The things that need to be painted are 2 arcs, fills for the arcs and
     * binding lines.
     */
    private PaintListener paintListener = new PaintListener() {

	@Override
	public void paintControl(PaintEvent e) {
	    Point origin = new Point(5, 5);
	    Point end;

	    int startAngle;

	    Point minArc;
	    Point maxArc;

	    Point topLeft;
	    Point bottomRight;

	    e.gc.setBackground(new Color(getDisplay(), 100, 100, 100));
	    // e.gc.setBackground(new Color(getDisplay(), 234, 246, 253));

	    if (Double.isInfinite(distancePerPx)) {
		if (orientation.equals(ORIENTATION.HORIZONTAL)) {
		    startAngle = 90;
		    end = new Point(getClientArea().width - 5, 5);
		    minArc = new Point(0, 0);
		    topLeft = new Point(minArc.x, 0);
		    maxArc = new Point(getClientArea().width - 11, 0);
		    bottomRight = new Point(maxArc.x + 5, 10);

		} else {
		    startAngle = 360;
		    end = new Point(5, getClientArea().height - 5);
		    minArc = new Point(0, 0);
		    topLeft = new Point(minArc.x, 5);
		    maxArc = new Point(0, getClientArea().height - 11);
		    bottomRight = new Point(maxArc.x, maxArc.y + 5);
		}
	    } else {
		double zero = (0 - min) * distancePerPx;
		if (orientation.equals(ORIENTATION.HORIZONTAL)) {
		    startAngle = 90;
		    end = new Point(getClientArea().width - 5, 5);
		    minArc = new Point(
			    (int) (zero + (selectedMin * distancePerPx)), 0);
		    topLeft = new Point(minArc.x, minArc.y);
		    maxArc = new Point(
			    (int) (zero + (selectedMax * distancePerPx)) - 1, 0);
		    bottomRight = new Point(maxArc.x, maxArc.y + 10);
		} else {
		    startAngle = 360;
		    end = new Point(5, getClientArea().height - 5);
		    minArc = new Point(0,
			    (int) (zero + (selectedMin * distancePerPx)));
		    topLeft = new Point(minArc.x - 5, minArc.y + 5);
		    maxArc = new Point(0,
			    (int) (zero + (selectedMax * distancePerPx)) - 1);
		    bottomRight = new Point(maxArc.x + 5, maxArc.y + 5);
		}
	    }

	    // Draw the line of appropriate size
	    e.gc.drawLine(origin.x, origin.y, end.x, end.y);

	    // arc for min selected
	    e.gc.drawArc(minArc.x, minArc.y, 10, 10, startAngle, 180);
	    e.gc.fillArc(minArc.x + 1, minArc.y + 1, 9, 9, startAngle, 180);
	    // arc for max selected
	    e.gc.drawArc(maxArc.x, maxArc.y, 10, 10, startAngle, -180);
	    e.gc.fillArc(maxArc.x + 1, maxArc.y + 1, 9, 9, startAngle, -180);

	    e.gc.setBackground(new Color(getDisplay(), 220, 220, 220));

	    Rectangle rectangle = new Rectangle(topLeft.x + 5, topLeft.y,
		    bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
	    e.gc.drawRectangle(rectangle);
	    e.gc.fillRectangle(rectangle.x + 1, rectangle.y + 1,
		    rectangle.width - 1, rectangle.height - 1);
	}
    };
}
