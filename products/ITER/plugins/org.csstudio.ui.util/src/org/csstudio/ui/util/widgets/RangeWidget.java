package org.csstudio.ui.util.widgets;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * Widget that display a range (currently only time, and only vertical) which can be modified
 * using a mouse drag.
 * 
 * @author carcassi
 */
public class RangeWidget extends Canvas {
	
	private double distancePerPx = 0.5;
	private int startPosition = SWT.TOP;
	private double pxPerTick = 2.0;
	private boolean editable = true;
	
	// The tick sizes for the first few ticks (loop around after that)
	private int[] sizes = new int[] {20, 10, 10, 10, 10, 15, 10, 10, 10, 10};
	private Set<RangeListener> listeners = new HashSet<RangeListener>();
	
	/**
	 * Adds a listener, notified if the range resolution changes.
	 * 
	 * @param listener a new listener
	 */
	public void addRangeListener(RangeListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes a listener.
	 * 
	 * @param listener listener to be removed
	 */
	public void removeRangeListener(RangeListener listener) {
		listeners.remove(listener);
	}
	
	private void fireRangeChanged() {
		for (RangeListener listener : listeners) {
			listener.rangeChanged();
		}
	}
	
	/**
	 * Determines whether the range start at the top (and goes down)
	 * or at the bottom (and goes up).
	 * 
	 * @param startPosition SWT.TOP or SWT.BOTTOM
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
		redraw();
	}
	
	/**
	 * Whether the range starts at the top or at the bottom.
	 * 
	 * @return SWT.TOP or SWT.BOTTOM
	 */
	public int getStartPosition() {
		return startPosition;
	}
	
	/**
	 * Whether the use can use the mouse to change the resolution.
	 * 
	 * @return true if user changes are allowed
	 */
	public boolean isEditable() {
		return editable;
	}
	
	/**
	 * Changes whether the use can use the mouse to change the resolution.
	 * 
	 * @param editable true if user changes are allowed
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * A new range widget.
	 * 
	 * @param parent parent component
	 * @param style SWT style
	 */
	public RangeWidget(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		addPaintListener(paintListener);
		addMouseListener(mouseListener);
		addMouseMoveListener(mouseListener);
	}
	
	private NumberFormat numberFormat = new DecimalFormat("0.#");
	
	private String calculateLabel(double distance) {
		// Calculate the label
		if (distance == 0)
			return "now";
		if (distance >= 0.999) {
			return numberFormat.format(distance) + " s";
		} else if (distance >= 0.000999) {
			return numberFormat.format(distance * 1000) + " ms";			
		} else if (distance >= 0.000000999) {
			return numberFormat.format(distance * 1000000) + " us";			
		} else {
			return numberFormat.format(distance * 1000000000) + " ns";			
		}
	}
	
	private final MouseRescale mouseListener = new MouseRescale();
	
	// Listener that implements the re-scaling through a drag operation
	private class MouseRescale extends MouseAdapter implements MouseMoveListener {
		
		private double startY;
		private double startDistancePerPx;

		@Override
		public void mouseDown(MouseEvent e) {
			// Save the starting point
			startY = e.y;
			startDistancePerPx = distancePerPx;
		}
		
		@Override
		public void mouseMove(MouseEvent e) {
			// Only if editable and it is a left click drag
			if (editable && (e.stateMask & SWT.BUTTON1) != 0) {
				// Re-scale based on how much the mouse is dragged
				if ((startPosition & SWT.DOWN) != 0) {
					// Calculate the coordinates starting from the bottom
					int height = getClientArea().height;
					if (e.y < height) {
						setDistancePerPx(startDistancePerPx * (height - startY) / (height - e.y));
					}
				} else {
					if (e.y > 0) {
						setDistancePerPx(startDistancePerPx * startY / e.y);
					}
				}
			}
		}
		
	}
	
	/**
	 * Changes how much distance is represented by each pixel. For example,
	 * 1 ms per pixel or 20 seconds per pixel. The distance is expressed in
	 * seconds.
	 * 
	 * @param distancePerPx seconds (or fraction) represented by each pixel
	 */
	public void setDistancePerPx(double distancePerPx) {
		this.distancePerPx = distancePerPx;
		// Calculate the distance in pixels between ticks.
		// The distance should between 2 to 20 pixels to make the range look
		// reasonable. Find the appropriate order of magnitute to get to that.
		this.pxPerTick = 1.0 / distancePerPx;
		if (pxPerTick > 0.0) {
			while (pxPerTick < 2.0 || pxPerTick > 20.0) {
				if (pxPerTick < 2.0)
					pxPerTick *= 10;
				if (pxPerTick > 20.0)
					pxPerTick /= 10;
			}
		}
		
		// New range: need to redraw and notify listeners
		redraw();
		fireRangeChanged();
	}
	
	/**
	 * Distance represented by each pixel (e.g. 10 ms per pixel).
	 * 
	 * @return seconds (or fraction) represented by each pixel
	 */
	public double getDistancePerPx() {
		return distancePerPx;
	}
	
	// Drawing function
	private PaintListener paintListener = new PaintListener() {
		
		@Override
		public void paintControl(PaintEvent e) {
			double height = getClientArea().height;
			int width = getClientArea().width;
			double currentPx = 0.0;
			int sizeIndex = 0;
			while (currentPx < height) {
				// Calculate new tick screen position (if from bottom, invert it)
				int tickPosition = (int) currentPx;
				if ((startPosition & SWT.BOTTOM) != 0) {
					tickPosition = getClientArea().height - tickPosition - 1;
				}
				
				// If we are at the beginning of a new ruler, we need
				// to print the label
				if (sizeIndex == 0) {
					// Invert screen position if start from bottom
					if ((startPosition & SWT.BOTTOM) != 0) {
						e.gc.drawText(calculateLabel(distancePerPx * currentPx), 0, tickPosition - e.gc.getFontMetrics().getHeight());
					} else {
						e.gc.drawText(calculateLabel(distancePerPx * currentPx), 0, tickPosition);
					}
				}
				
				// Draw the line of appropriate size
				e.gc.drawLine(width - sizes[sizeIndex], tickPosition, width, tickPosition);
				
				// If there are more than 10 pixels between ticks,
				// draw another smaller tick between them
				if (pxPerTick >= 10.0) {
					tickPosition = (int) (currentPx + pxPerTick / 2.0);
					if ((startPosition & SWT.BOTTOM) != 0) {
						// Invert screen position if start from bottom
						tickPosition = getClientArea().height - tickPosition - 1;
					}
					e.gc.drawLine(width - 5, tickPosition, width, tickPosition);
				}
				
				// Increase screen position to next tick
				currentPx += pxPerTick;
				
				// Increment the pointer for the tick size, and look around if necessary
				sizeIndex++;
				if (sizeIndex == sizes.length)
					sizeIndex = 0;
			}
			
		}
	};

}
