package org.csstudio.utility.pvmanager.ui.widgets;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.epics.pvmanager.data.VImage;

public class VImageDisplay extends Canvas {

	public VImageDisplay(Composite parent) {
		super(parent, SWT.NO_BACKGROUND);
		addPaintListener(paintListener);
	}
	
	private VImage vImage;
	private boolean stretched;
	
	public boolean isStretched() {
		return stretched;
	}
	
	public void setStretched(boolean stretched) {
		this.stretched = stretched;
	}
	
	public void setVImage(VImage vImage) {
		if (!isDisposed()) {
			this.vImage = vImage;
			redraw();
		}
	}
	
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
					gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height,
							0, 0, getClientArea().width, getClientArea().height);
				} else {
					gc.drawImage(image, 0, 0);
					// draw the background on the right of the image
					drawBackground(gc, image.getBounds().width, 0,
							Math.max(0, getClientArea().width - image.getBounds().width), image.getBounds().height);
					// draw the background below the image
					drawBackground(gc, 0, image.getBounds().height,
							getClientArea().width, Math.max(0, getClientArea().height - image.getBounds().height));
				}
			} else {
				drawBackground(gc, 0, 0, getSize().x, getSize().y);
			}
		}
	};

}
