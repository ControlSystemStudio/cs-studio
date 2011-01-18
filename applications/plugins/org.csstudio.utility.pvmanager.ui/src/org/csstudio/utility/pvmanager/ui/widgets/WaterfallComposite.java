package org.csstudio.utility.pvmanager.ui.widgets;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.widgets.Composite;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.data.VImage;

import static org.epics.pvmanager.extra.ExpressionLanguage.*;
import static org.epics.pvmanager.data.ExpressionLanguage.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class WaterfallComposite extends Composite {
	
	private VImageDisplay imageDisplay;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public WaterfallComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		imageDisplay = new VImageDisplay(this);
		imageDisplay.setStretched(true);
		FormData fd_imageDisplay = new FormData();
		fd_imageDisplay.bottom = new FormAttachment(100);
		fd_imageDisplay.right = new FormAttachment(100);
		fd_imageDisplay.top = new FormAttachment(0);
		fd_imageDisplay.left = new FormAttachment(0);
		imageDisplay.setLayoutData(fd_imageDisplay);

	}
	
	private String pvName;
	private PV<VImage> pv;
	
	public String getPvName() {
		return pvName;
	}
	
	public void setPvName(String pvName) {
		// First de-allocate current pv if any
		if (pv != null) {
			pv.close();
			pv = null;
		}
		
		pv = PVManager.read(waterfallPlotOf(vDoubleArray(pvName)))
			.andNotify(SWTUtil.onSWTThread()).atHz(30);
		pv.addPVValueChangeListener(new PVValueChangeListener() {
			
			@Override
			public void pvValueChanged() {
				imageDisplay.setVImage(pv.getValue());
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
