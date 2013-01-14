/**
 * 
 */
package org.csstudio.utility.pvmanager.jfreechart.widgets;

import static org.csstudio.utility.pvmanager.jfreechart.widgets.VMultiChannelChartDisplay.DOMAIN_AXIS_TYPE_POSITION;
import static org.epics.pvmanager.data.ExpressionLanguage.synchronizedArrayOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubles;
import static org.epics.pvmanager.util.TimeDuration.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VMultiDouble;

/**
 * @author shroffk
 * 
 */
public class XYChartWidget extends Composite {

	private static Logger logger = Logger.getLogger("org.csstudio.utility.pvmanager.jfreechart.widget.XYChartWidget");
	private VMultiChannelChartDisplay multiDoubleDisplay;
	private int plotUsing;

	public XYChartWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		multiDoubleDisplay = new VMultiChannelChartDisplay(this,
				SWT.NO_BACKGROUND, DOMAIN_AXIS_TYPE_POSITION);
		FormData fd_chartDisplay = new FormData();
		fd_chartDisplay.bottom = new FormAttachment(100);
		fd_chartDisplay.right = new FormAttachment(100);
		fd_chartDisplay.top = new FormAttachment(0);
		fd_chartDisplay.left = new FormAttachment(0, 0);
		multiDoubleDisplay.setLayoutData(fd_chartDisplay);
		multiDoubleDisplay.addListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					SelectChartDomainAxis dialog = new SelectChartDomainAxis(
							getShell(), SWT.NONE, plotUsing);
					Point position = new Point(e.x, e.y);
					position = getDisplay().map(XYChartWidget.this, null,
							position);
					int result = dialog.open(position.x, position.y);
					if (plotUsing != result) {
						plotUsing = result;
						reconnect();
					}
				}
			}

		});

	}

	// The channel names used in the multichannel pv connection
	private List<String> channelNames;
	// The positions corresponding to the each channel in channelNames
	private List<Double> channelPositions;

	// The pv created by pvmanager
	private PVReader<VMultiDouble> pv;

	public List<String> getChannelNames() {
		return channelNames;
	}

	/**
	 * 
	 * @param channelNames
	 *            null not permitted
	 */
	public void setChannelNames(List<String> channelNames) {
		if (channelNames != null) {
			this.channelNames = channelNames;
			this.channelPositions = null;
			reconnect();
		} else {
			throw new IllegalArgumentException("ChannelNames can not be null.");
		}
	}

	/**
	 * 
	 * @param channelPositions
	 */
	public void setChannelPositions(List<Double> channelPositions) {
		if (channelPositions != null
				&& channelPositions.size() == channelNames.size()) {
			this.channelPositions = channelPositions;
			reconnect();
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void setTitle(String title){
		multiDoubleDisplay.setTitle(title);
	}

	public void setXAxisLabel(String xAxisLabel) {
		multiDoubleDisplay.setXAxisLabel(xAxisLabel);
	}
	
	public void setYAxisLabel(String yAxisLabel) {
		multiDoubleDisplay.setYAxisLabel(yAxisLabel);
	}
	
	private void reconnect() {
		// First de-allocate current pv if any
		if (pv != null) {
			pv.close();
			pv = null;
		}

		// Clean up old chart if present
		// multiDoubleDisplay.setPVData(null);
		// multiDoubleDisplay.setNames(null);
		// multiDoubleDisplay.setPositions(null);
		multiDoubleDisplay.clear();

		if (channelNames != null) {
			multiDoubleDisplay.setNames(channelNames);
			multiDoubleDisplay.setPositions(channelPositions);
			multiDoubleDisplay.setPlotUsing(plotUsing);
			pv = PVManager
					.read(synchronizedArrayOf(
							ms(75),
							vDoubles(Collections.unmodifiableList(channelNames))))
					.notifyOn(SWTUtil.swtThread()).every(hz(10));
			pv.addPVReaderListener(new PVReaderListener() {
				
				@Override
				public void pvChanged() {
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
