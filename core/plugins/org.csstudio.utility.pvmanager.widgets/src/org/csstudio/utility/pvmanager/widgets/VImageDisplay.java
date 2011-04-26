package org.csstudio.utility.pvmanager.widgets;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.epics.pvmanager.data.VImage;

/**
 * Basic ui component that can display a VImage on screen.
 * 
 * @author carcassi
 */
public class VImageDisplay extends Canvas {

	/**
	 * Creates a new display.
	 * 
	 * @param parent
	 */
	public VImageDisplay(Composite parent) {
		// Use no background so that image does not flicker
		super(parent, SWT.NO_BACKGROUND);
		addPaintListener(paintListener);
	}
	
	// The current image being displayed
	private VImage vImage;
	// Whether the image should be horizontally stretched to the full size of the display
	private boolean hStretched;
	// Whether the image should be vertically stretched to the full size of the display
	private boolean vStretched;
	
	private int hAlignment = SWT.LEFT;
	private int vAlignment = SWT.TOP;

	/**
	 * True if the image is horizontally stretched to fit the size of the display.
	 * 
	 * @return the current property value
	 */
	public boolean isHStretched() {
		return hStretched;
	}
	
	/**
	 * Changes whether the image is horizontally stretched to fit the size of the display.
	 * 
	 * @param stretched the new property value
	 */
	public void setHStretched(boolean hStretched) {
		this.hStretched = hStretched;
	}
	
	/**
	 * True if the image is vertically stretched to fit the size of the display.
	 * 
	 * @return the current property value
	 */
	public boolean isVStretched() {
		return vStretched;
	}
	
	/**
	 * Changes whether the image is vertically stretched to fit the size of the display.
	 * 
	 * @param stretched the new property value
	 */
	public void setVStretched(boolean vStretched) {
		this.vStretched = vStretched;
	}
	
	/**
	 * Changes the current image being displayed. Triggers a redraw.
	 * 
	 * @param vImage the new property value
	 */
	public void setVImage(VImage vImage) {
		if (!isDisposed()) {
			this.vImage = vImage;
			redraw();
		}
	}
	
	public int getHAlignment() {
		return hAlignment;
	}

	public void setHAlignment(int hAlignment) {
		if (!isDisposed()) {
			this.hAlignment = hAlignment;
			redraw();
		}
	}

	public int getVAlignment() {
		return vAlignment;
	}

	public void setVAlignment(int vAlignment) {
		if (!isDisposed()) {
			this.vAlignment = vAlignment;
			redraw();
		}
	}

	/**
	 * Returns the current image being displayed.
	 * 
	 * @return the current property value
	 */
	public VImage getVImage() {
		return vImage;
	}
	
	private PaintListener paintListener = new PaintListener() {
		
		@Override
		public void paintControl(PaintEvent e) {
			GC gc = e.gc;
			
			if (vImage != null) {
				Image image = SWTUtil.toImage(gc, vImage);
				int x;
				switch (hAlignment) {
				case SWT.LEFT:
					x = 0;
					break;
				case SWT.CENTER:
					x = (getClientArea().width - image.getBounds().width)/2;
					break;
				case SWT.RIGHT:
					x = getClientArea().width - image.getBounds().width;
					break;
				default:
					x = 0;
					break;
				}
				
				int y;
				switch (vAlignment) {
				case SWT.TOP:
					y = 0;
					break;
				case SWT.CENTER:
					y = (getClientArea().height - image.getBounds().height)/2;
					break;
				case SWT.BOTTOM:
					y = getClientArea().height - image.getBounds().height;
					break;
				default:
					y = 0;
					break;
				}
				
				int width;
				if (hStretched) {
					width = getClientArea().width;
				} else {
					width = image.getBounds().width;
				}
				
				int height;
				if (vStretched) {
					height = getClientArea().height;
				} else {
					height = image.getBounds().height;
				}

				gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height,
						x, y, width, height);
//				drawBackground(gc, image.getBounds().width, 0,
//						Math.max(0, getClientArea().width - image.getBounds().width), image.getBounds().height);
//				drawBackground(gc, 0, image.getBounds().height,
//						getClientArea().width, Math.max(0, getClientArea().height - image.getBounds().height));
				
//				if (stretched) {
//					// Stretch the image to the whole client area
//					gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height,
//							0, 0, getClientArea().width, getClientArea().height);
//				} else {
//					// Draw the image, then draw the background at the right of the image
//					// and then below the image
//					gc.drawImage(image, 0, 0);
//					drawBackground(gc, image.getBounds().width, 0,
//							Math.max(0, getClientArea().width - image.getBounds().width), image.getBounds().height);
//					drawBackground(gc, 0, image.getBounds().height,
//							getClientArea().width, Math.max(0, getClientArea().height - image.getBounds().height));
//				}
				image.dispose();
			} else {
				// If image is not set, just paint the background
				drawBackground(gc, 0, 0, getSize().x, getSize().y);
			}
		}
	};

}
