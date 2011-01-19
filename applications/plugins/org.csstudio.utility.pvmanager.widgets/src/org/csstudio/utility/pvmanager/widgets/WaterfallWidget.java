package org.csstudio.utility.pvmanager.widgets;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.widgets.Composite;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.extra.WaterfallPlotParameters;

import static org.epics.pvmanager.extra.ExpressionLanguage.*;
import static org.epics.pvmanager.data.ExpressionLanguage.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

/**
 * A widget that connects to an array and display a waterfall plot based on it.
 * 
 * @author carcassi
 */
public class WaterfallWidget extends Composite {
	
	private VImageDisplay imageDisplay;
	private WaterfallPlotParameters parameters = new WaterfallPlotParameters();

	/**
	 * Creates a new widget.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public WaterfallWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		imageDisplay = new VImageDisplay(this);
		imageDisplay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					WaterfallParametersDialog dialog = new WaterfallParametersDialog(getShell(), SWT.NORMAL);
					Point position = new Point(e.x, e.y);
					position = getDisplay().map(WaterfallWidget.this, null, position);
					WaterfallPlotParameters newParameters = dialog.open(parameters, position.x, position.y);
					if (newParameters != null) {
						parameters = newParameters;
						reconnect();
					}
				}
			}
		});
		imageDisplay.setStretched(true);
		FormData fd_imageDisplay = new FormData();
		fd_imageDisplay.bottom = new FormAttachment(100);
		fd_imageDisplay.right = new FormAttachment(100);
		fd_imageDisplay.top = new FormAttachment(0);
		fd_imageDisplay.left = new FormAttachment(0);
		imageDisplay.setLayoutData(fd_imageDisplay);

	}
	
	// The pv name for connection
	private String pvName;
	// The pv created by pvmanager
	private PV<VImage> pv;
	
	/**
	 * The pv name to connect to.
	 * 
	 * @return the current property value
	 */
	public String getPvName() {
		return pvName;
	}
	
	/**
	 * Changes the pv name to connect to. Triggers a reconnection.
	 * 
	 * @param pvName the new property value
	 */
	public void setPvName(String pvName) {
		this.pvName = pvName;
		reconnect();
	}
	
	private void reconnect() {
		// First de-allocate current pv if any
		if (pv != null) {
			pv.close();
			pv = null;
		}
		
		if (pvName != null) {
			pv = PVManager.read(waterfallPlotOf(vDoubleArray(pvName), parameters))
				.andNotify(SWTUtil.onSWTThread()).atHz(30);
			pv.addPVValueChangeListener(new PVValueChangeListener() {
				
				@Override
				public void pvValueChanged() {
					imageDisplay.setVImage(pv.getValue());
				}
			});
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
