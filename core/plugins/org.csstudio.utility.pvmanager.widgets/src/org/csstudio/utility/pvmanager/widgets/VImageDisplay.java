package org.csstudio.utility.pvmanager.widgets;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
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
	// Whether the image should be stretched to the full size of the display
	private boolean stretched;
	
	/**
	 * True if the image is stretched to fit the size of the display.
	 * 
	 * @return the current property value
	 */
	public boolean isStretched() {
		return stretched;
	}
	
	/**
	 * Changes whether the image is streatched to fit the size of the display.
	 * 
	 * @param stretched the new property value
	 */
	public void setStretched(boolean stretched) {
		this.stretched = stretched;
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
				if (stretched) {
					// Stretch the image to the whole client area
					gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height,
							0, 0, getClientArea().width, getClientArea().height);
				} else {
					// Draw the image, then draw the background at the right of the image
					// and then below the image
					gc.drawImage(image, 0, 0);
					drawBackground(gc, image.getBounds().width, 0,
							Math.max(0, getClientArea().width - image.getBounds().width), image.getBounds().height);
					drawBackground(gc, 0, image.getBounds().height,
							getClientArea().width, Math.max(0, getClientArea().height - image.getBounds().height));
				}
			} else {
				// If image is not set, just paint the background
				drawBackground(gc, 0, 0, getSize().x, getSize().y);
			}
		}
	};

}
