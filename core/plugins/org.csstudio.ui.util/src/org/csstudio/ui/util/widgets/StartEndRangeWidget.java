/**
 * 
 */
package org.csstudio.ui.util.widgets;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
 * A widget with which allows you to select a range from a predefine range of
 * values.
 * 
 * @author shroffk
 * 
 */
public class StartEndRangeWidget extends Canvas {

    // Set Range
    private volatile double min = Double.NaN;
    private volatile double max = Double.NaN;
    private volatile boolean rangeSet;
    
    // Selected range
    private volatile double selectedMin = Double.NaN;
    private volatile double selectedMax = Double.NaN;

    // Mark is the selected min and max nodes are being actively moved
    private boolean followMin = true;
    private boolean followMax = true;

    private double distancePerPx;

    public enum ORIENTATION {
	HORIZONTAL, VERTICAL
    }

    private enum MOVE {
	SELECTEDMIN, SELECTEDMAX, RANGE, NONE, SELECTED
    }

    private List<RangeListener> listeners = new CopyOnWriteArrayList<RangeListener>();

    /**
     * Adds a listener, notified if the range or the selected range changes.
     * 
     * @param listener
     *            a new listener.
     */
    public void addRangeListener(RangeListener listener) {
	listeners.add(listener);
    }

    /**
     * Removes a listener.
     * 
     * @param listener
     *            listener to be removed.
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

    /**
     * Create a the StartEndRangeWidget widget.
     * 
     * @param parent
     * @param style
     */
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
	rangeSet = false;
	redraw();
    }

    /**
     * Get the current minimum value of the range.
     * 
     * @return
     */
    public double getMin() {
	return min;
    }

    @Deprecated    
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

    /**
     * Get the current maximum value of the range.
     * 
     * @return
     */
    public double getMax() {
	return max;
    }

    @Deprecated
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

    /**
     * Returns if the widget has a valid range set.
     * @return
     */
    public boolean isRangeSet() {
        return rangeSet;
    }
    
    /**
     * Reset the widget.
     */
    public synchronized void resetRange() {
	// Set Range
	min = Double.NaN;
	max = Double.NaN;
	rangeSet = false;

	// Selected range
	selectedMin = Double.NaN;
	selectedMax = Double.NaN;
	
	recalculateDistancePerPx();
    }

    /**
     * get the minimum value of the selected range.
     * 
     * @return
     */
    public double getSelectedMin() {
	return selectedMin;
    }

    /**
     * set the minimum value of the selected range.
     * @param selectedMin
     */
    public void setSelectedMin(double selectedMin) {
	if (rangeSet) {
	    if (this.selectedMin != selectedMin) {
		if (!(selectedMin < this.min)
			&& (selectedMin <= this.selectedMax)) {
		    this.selectedMin = selectedMin;
		    followMin = false;
		    if (selectedMin == this.min) {
			followMin = true;
		    }
		    fireRangeChanged();
		} else {
		    throw new IllegalArgumentException(
			    "Invalid value for selectedMin," + selectedMin
				    + " must be within the range " + this.min
				    + "-" + this.selectedMax);
		}
	    }
	} else {
	    throw new IllegalArgumentException(
		    "Widget Range is not set, thus selected range cannot be set.");
	}
    }

    /**
     * Get the maximum value of the selected range.
     * 
     * @return
     */
    public double getSelectedMax() {
	return selectedMax;
    }

    /**
     * Set the maximum value of the selected range.
     * 
     * @param selectedMax
     */
    public void setSelectedMax(double selectedMax) {
	if (rangeSet) {
	    if (this.selectedMax != selectedMax) {
		if (!(selectedMax > this.max)
			&& (selectedMax >= this.selectedMin)) {
		    this.selectedMax = selectedMax;
		    followMax = false;
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
	} else {
	    throw new IllegalArgumentException(
		    "Widget Range is not set, thus selected range cannot be set.");
	}
    }

    /**
     * Set the selected range for the widget.
     * 
     * @param selectedMin
     * @param selectedMax
     */
    public synchronized void setSelectedRange(double selectedMin, double selectedMax) {
	if (rangeSet) {
	    if (selectedMax <= this.max && selectedMin >= this.min && selectedMax >= selectedMin) {
		this.selectedMin = selectedMin;
		this.selectedMax = selectedMax;
		fireRangeChanged();
	    } else {
		throw new IllegalArgumentException("Invalid range values.");
	    }
	} else {
	    throw new IllegalArgumentException(
		    "Widget Range is not set, thus selected range cannot be set.");
	}
    }

    /**
     * Set the new min and max for the widget. The widget will resize while also
     * attempting to retain the previous selected range if possible.
     * 
     * @param min
     * @param max
     */
    public synchronized void setRange(double min, double max) {
	if (min != this.min || max != this.max) {
	    if (min <= max) {
		this.min = min;
		this.max = max;
		// there was no prior range set 
		// or the old selected range no longer fits in the current range 
		// reset the selected range
		if (!rangeSet ||
		     selectedMax <= this.min ||
		     selectedMin >= this.max) {
		    this.selectedMin = min;
		    followMin = true;
		    this.selectedMax = max;
		    followMax = true;
		} else {
		    // calculate the new selected range based on the previous selected range
		    if (followMin || selectedMin <= this.min) {
			this.selectedMin = this.min;
			followMin = true;
		    }
		    if (followMax || selectedMax >= this.max) {
			this.selectedMax = this.max;
			followMax = true;
		    }
		    
		}
		rangeSet = true;
		recalculateDistancePerPx();
	    } else {
		throw new IllegalArgumentException(
			"Invalid range values, minimum cannot be greater than maximum");
	    }
	}
    }

    /**
     * Set both the range and the selected range of the widget.
     * 
     * @param min  
     * @param max
     * @param selectedMin
     * @param selectedMax
     */
    public synchronized void setRanges(double min, double max, double selectedMin, double selectedMax) {
	if (min <= selectedMin && selectedMin <= selectedMax
		&& selectedMax <= max) {
	    this.min = min;
	    this.max = max;
	    this.rangeSet = true;
	    this.selectedMin = selectedMin;
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

    /**
     * Set the orientation of the widget (either horizontal or vertical) {@link ORIENTATION}.
     * 
     * @param orientation
     */
    public void setOrientation(ORIENTATION orientation) {
	if (this.orientation != orientation) {
	    this.orientation = orientation;
	    fireRangeChanged();
	}
    }

    /**
     * Get the size of the current range.
     * 
     * @return
     */
    public double getRange() {
	return this.max - this.min;
    }

    /**
     * Get the size of the selected range
     * 
     * @return
     */
    public double getSelectedRange() {
	return this.selectedMax - this.selectedMin;
    }

    private synchronized void recalculateDistancePerPx() {
	switch (orientation) {
	case HORIZONTAL:
	    setDistancePerPx((getClientArea().width - 11) / Math.abs(max - min));
	    break;
	case VERTICAL:
	    setDistancePerPx((getClientArea().height - 11) / Math.abs(max - min));
	    break;
	default:
	    break;
	}

    }

    private final MouseRescale mouseListener = new MouseRescale();

    // Listener that implements the re-scaling through a drag operation
    private class MouseRescale extends MouseAdapter implements MouseMoveListener {

	private volatile int rangeX;

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	    if(rangeSet){
		
		setRanges(min, max, min, max);
	    }
	}
	
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
	    // Only if range is set and it is a left click drag
	    if (rangeSet) {
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
			setSelectedRange(getMin(), getMin()
				+ getSelectedRange());
		    } else if (getSelectedMax() + increment > getMax()) {
			setSelectedRange(getMax() - getSelectedRange(),
				getMax());
		    }
		    break;
		default:
		    break;
		}
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
	    // selected min max arcs
	    int arcRadius = 5;
	    int startAngle;

	    Point minArc = new Point(0, 0);
	    Point maxArc;

	    // selection Rectangle
	    // rectangleHeight = 2*arcRadius
	    int rectangleHeight = 10;
	    Point topLeft;
	    Point bottomRight;
	    Color rectangleFill = new Color(getDisplay(), 255, 255, 255);
	    Color arcFill = new Color(getDisplay(), 255, 255, 255);

	    // Range line
	    Point origin = new Point(arcRadius, arcRadius);
	    Point end;

	    if (Double.isInfinite(distancePerPx) || Double.isNaN(distancePerPx)) {
		if (orientation.equals(ORIENTATION.HORIZONTAL)) {
		    end = new Point(getClientArea().width - (arcRadius), arcRadius);

		    startAngle = 90;
		    maxArc = new Point((getClientArea().width) - (2 * arcRadius), 0);

		    topLeft = new Point(minArc.x + arcRadius, 0);
		    bottomRight = new Point(maxArc.x + arcRadius, rectangleHeight);

		} else {
		    end = new Point(arcRadius, getClientArea().height - arcRadius);

		    startAngle = 360;
		    maxArc = new Point(0, getClientArea().height - (2 * arcRadius));

		    topLeft = new Point(minArc.x, minArc.y + arcRadius);
		    bottomRight = new Point(maxArc.x + rectangleHeight,
			    maxArc.y + arcRadius);
		}
	    } else {
		double zero = (0 - min) * distancePerPx;
		if (orientation.equals(ORIENTATION.HORIZONTAL)) {
		    end = new Point(getClientArea().width - (arcRadius + 1), arcRadius);

		    startAngle = 90;
		    minArc = new Point(
			    (int) (zero + (selectedMin * distancePerPx)), 0);
		    maxArc = new Point(
			    (int) (zero + (selectedMax * distancePerPx)), 0);

		    topLeft = new Point(minArc.x + arcRadius, minArc.y);
		    bottomRight = new Point(maxArc.x + arcRadius, maxArc.y
			    + rectangleHeight);
		} else {
		    end = new Point(arcRadius, getClientArea().height - (arcRadius + 1));

		    startAngle = 360;
		    minArc = new Point(0,
			    (int) (zero + (selectedMin * distancePerPx)));
		    maxArc = new Point(0,
			    (int) (zero + (selectedMax * distancePerPx)));

		    topLeft = new Point(minArc.x, minArc.y + arcRadius);
		    bottomRight = new Point(maxArc.x + (2 * arcRadius),
			    maxArc.y + arcRadius);
		}
	    }

	    // Draw the line of appropriate size
	    e.gc.drawLine(origin.x, origin.y, end.x, end.y);

	    e.gc.setBackground(arcFill);
	    // arc for min selected
	    e.gc.fillArc(minArc.x, minArc.y, 10, 10, startAngle, 180);
	    e.gc.drawArc(minArc.x, minArc.y, 10, 10, startAngle, 180);

	    // arc for max selected
	    e.gc.fillArc(maxArc.x, maxArc.y, 10, 10, startAngle, -180);
	    e.gc.drawArc(maxArc.x, maxArc.y, 10, 10, startAngle, -180);

	    e.gc.setBackground(rectangleFill);
	    Rectangle rectangle = new Rectangle(topLeft.x, topLeft.y,
		    bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
	    e.gc.fillRectangle(rectangle.x, rectangle.y, rectangle.width,
		    rectangle.height);
	    e.gc.drawRectangle(rectangle);
	}
    };
}
