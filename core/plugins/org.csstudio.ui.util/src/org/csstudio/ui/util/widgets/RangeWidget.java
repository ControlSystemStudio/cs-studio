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

public class RangeWidget extends Canvas {
	
	private double distancePerPx = 0.5;
	private int alignment = SWT.TOP;
	private double pxPerTick = 2.0;
	private int[] sizes = new int[] {20, 10, 10, 10, 10, 15, 10, 10, 10, 10};
	private Set<RangeListener> listeners = new HashSet<RangeListener>();
	
	public void addRangeListener(RangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeRangeListener(RangeListener listener) {
		listeners.remove(listener);
	}
	
	private void fireRangeChanged() {
		for (RangeListener listener : listeners) {
			listener.rangeChanged();
		}
	}
	
	public void setAlignment(int alignment) {
		this.alignment = alignment;
		redraw();
	}
	
	public int getAlignment() {
		return alignment;
	}

	public RangeWidget(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		addPaintListener(paintListener);
		addMouseListener(mouseListener);
		addMouseMoveListener(mouseListener);
	}
	
	private NumberFormat numberFormat = new DecimalFormat("0.#");
	
	public String label(double distance) {
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
	
	private class MouseRescale extends MouseAdapter implements MouseMoveListener {
		
		private double startY;
		private double startDistancePerPx;

		@Override
		public void mouseDown(MouseEvent e) {
			startY = e.y;
			startDistancePerPx = distancePerPx;
		}
		
		@Override
		public void mouseMove(MouseEvent e) {
			if ((e.stateMask & SWT.BUTTON1) != 0 && e.y > 0) {
				setDistancePerPx(startDistancePerPx * startY / e.y);
			}
		}
		
	}
	
	public void setDistancePerPx(double distancePerPx) {
		this.distancePerPx = distancePerPx;
		this.pxPerTick = 1.0 / distancePerPx;
		if (pxPerTick > 0.0) {
			while (pxPerTick < 2.0 || pxPerTick > 20.0) {
				if (pxPerTick < 2.0)
					pxPerTick *= 10;
				if (pxPerTick > 20.0)
					pxPerTick /= 10;
			}
		}
		redraw();
		fireRangeChanged();
	}
	
	public double getDistancePerPx() {
		return distancePerPx;
	}
	
	private PaintListener paintListener = new PaintListener() {
		
		@Override
		public void paintControl(PaintEvent e) {
			double height = getClientArea().height;
			int width = getClientArea().width;
			double currentPx = 0.0;
			int sizeIndex = 0;
			while (currentPx < height) {
				int tickPosition = (int) currentPx;
				if ((alignment & SWT.BOTTOM) != 0) {
					tickPosition = getClientArea().height - tickPosition - 1;
				}
				if (sizeIndex == 0) {
					if ((alignment & SWT.BOTTOM) != 0) {
						e.gc.drawText(label(distancePerPx * currentPx), 0, tickPosition - e.gc.getFontMetrics().getHeight());
					} else {
						e.gc.drawText(label(distancePerPx * currentPx), 0, tickPosition);
					}
				}
				e.gc.drawLine(width - sizes[sizeIndex], tickPosition, width, tickPosition);
				if (pxPerTick >= 10.0) {
					tickPosition = (int) (currentPx + pxPerTick / 2.0);
					e.gc.drawLine(width - 5, tickPosition, width, tickPosition);
				}
				
				currentPx += pxPerTick;
				sizeIndex++;
				if (sizeIndex == sizes.length)
					sizeIndex = 0;
			}
			
		}
	};

}
