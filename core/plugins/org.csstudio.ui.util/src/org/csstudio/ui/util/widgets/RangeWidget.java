package org.csstudio.ui.util.widgets;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
	private double pxPerTick = 2.0;
	private int[] sizes = new int[] {20, 10, 10, 10, 10, 15, 10, 10, 10, 10};

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
				if (sizeIndex == 0) {
					e.gc.drawText(label(distancePerPx * currentPx), 0, tickPosition);
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
