/**
 * 
 */
package org.csstudio.utility.pvmanager.jfreechart.widgets;

import static org.epics.pvmanager.data.ExpressionLanguage.synchronizedArrayOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubles;
import static org.epics.pvmanager.util.TimeDuration.ms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.data.VMultiDouble;

/**
 * @author shroffk
 * 
 */
public class XYChartWidget extends Composite {

	private VMultiChannelChartDisplay multiDoubleDisplay;

	public XYChartWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		multiDoubleDisplay = new VMultiChannelChartDisplay(this, SWT.NO_BACKGROUND);
		FormData fd_chartDisplay = new FormData();
		fd_chartDisplay.bottom = new FormAttachment(100);
		fd_chartDisplay.right = new FormAttachment(100);
		fd_chartDisplay.top = new FormAttachment(0);
		fd_chartDisplay.left = new FormAttachment(0, 0);
		multiDoubleDisplay.setLayoutData(fd_chartDisplay);
	}

	// The pv name for connection
	private List<String> pvNames;
	// The pv created by pvmanager
	private PV<VMultiDouble> pv;

	public List<String> getPvName() {
		return pvNames;
	}

	public void setPvName(List<String> pvNames) {
		this.pvNames = pvNames;
		reconnect();
	}

	private void reconnect() {
		// First de-allocate current pv if any
		if (pv != null) {
			pv.close();
			pv = null;
		}

		// Clean up old chart if present
		multiDoubleDisplay.setPVData(null);
		// Create an empty chart

		if (pvNames != null) {
			pv = PVManager.read(
					synchronizedArrayOf(ms(75),
							vDoubles(Collections.unmodifiableList(pvNames))))
					.andNotify(SWTUtil.onSWTThread()).atHz(10);
			pv.addPVValueChangeListener(new PVValueChangeListener() {

				@Override
				public void pvValueChanged() {
					// TODO try avoiding the creation of the arraylist
					ArrayList<VMultiDouble> value = new ArrayList<VMultiDouble>();
					value.add(pv.getValue());
					multiDoubleDisplay.setPVData(value);
				}
			});
		}
	}

	/** {@inheritDoc} */
	@Override
	public void dispose() {
		if (pv != null) {
			pv.close();
		}
		super.dispose();
	}
}
