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
	
	// Whether the image should be horizontally or vertically stretched to the full size of the display.
	// Any combination of SWT.HORIZONTAL or SWT.VERTICAL are allowed	
	private int stretched = SWT.NONE;
	
	// How the image should be allowed. Any combination of SWT.LEFT, SWT.RIGHT, SWT.TOP, SWT.BOTTOM
	// is allowed
	private int alignment = SWT.LEFT | SWT.TOP;

	/**
	 * In which direction the image should be stretched.
	 * 
	 * @return the current property value
	 */
	public int getStretched() {
		return stretched;
	}
	
	/**
	 * Changes in which direction the image should be stretched. Possible values are SWT.NONE,
	 * SWT.HORIZONTAL, SWT.VERTICAL, SWT.HORIZONTAL | SWT.VERTICAL
	 * 
	 * @param stretched the new property value
	 */
	public void setStretched(int stretched) {
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
	 * Where the image is positioned in the widget.
	 * 
	 * @return current alignment
	 */
	public int getAlignment() {
		return alignment;
	}

	/**
	 * Changes where the image is position in the widget. Possible values are
	 * SWT.CENTER,
	 * SWT.TOP, SWT.BOTTOM, SWT.LEFT, SWT.RIGHT, the four corners (i.e. SWT.TOP | SWT.LEFT).
	 * 
	 * @param alignment
	 */
	public void setAlignment(int alignment) {
		if (!isDisposed()) {
			this.alignment = alignment;
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
				int width;
				
				if ((stretched & SWT.HORIZONTAL) != 0) {
					width = getClientArea().width;
					x = 0;
				} else {
					width = image.getBounds().width;
					if ((alignment & SWT.LEFT) != 0) {
						x = 0;
					} else if ((alignment & SWT.RIGHT) != 0) {
						x = getClientArea().width - image.getBounds().width;
					} else {
						x = (getClientArea().width - image.getBounds().width)/2;
					}
				}
				
				int y;
				int height;
				if ((stretched & SWT.VERTICAL) != 0) {
					height = getClientArea().height;
					y = 0;
				} else {
					height = image.getBounds().height;
					if ((alignment & SWT.TOP) != 0) {
						y = 0;
					} else if ((alignment & SWT.BOTTOM) != 0) {
						y = getClientArea().height - image.getBounds().height;
					} else {
						y = (getClientArea().height - image.getBounds().height)/2;
					}
				}
				
				

				gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height,
						x, y, width, height);
				drawBackground(gc, 0, 0, x, getClientArea().height);
				drawBackground(gc, x + width, 0, getClientArea().width, getClientArea().height);
				drawBackground(gc, 0, 0, getClientArea().width, y);
				drawBackground(gc, 0, y + height, getClientArea().width, getClientArea().height);
				
				image.dispose();
			} else {
				// If image is not set, just paint the background
				drawBackground(gc, 0, 0, getSize().x, getSize().y);
			}
		}
	};

}
