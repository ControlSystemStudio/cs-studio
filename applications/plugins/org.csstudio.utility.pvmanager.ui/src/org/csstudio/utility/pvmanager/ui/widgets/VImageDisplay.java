package org.csstudio.utility.pvmanager.ui.widgets;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.epics.pvmanager.data.VImage;

public class VImageDisplay extends Canvas {

	public VImageDisplay(Composite parent) {
		super(parent, SWT.NO_BACKGROUND);
		addPaintListener(paintListener);
	}
	
	private VImage vImage;
	
	public void setVImage(VImage vImage) {
		this.vImage = vImage;
		setSize(vImage.getWidth(), vImage.getHeight());
		redraw();
	}
	
	public VImage getVImage() {
		return vImage;
	}
	
	private PaintListener paintListener = new PaintListener() {
		
		@Override
		public void paintControl(PaintEvent e) {
			GC gc = e.gc;
			if (vImage != null) {
				gc.drawImage(SWTUtil.toImage(gc, vImage), 0, 0);
			} else {
				drawBackground(gc, 0, 0, getSize().x, getSize().y);
			}
		}
	};

}
