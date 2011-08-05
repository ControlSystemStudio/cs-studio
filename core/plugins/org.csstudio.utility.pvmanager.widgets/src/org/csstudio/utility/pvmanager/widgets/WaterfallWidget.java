package org.csstudio.utility.pvmanager.widgets;

import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.data.ExpressionLanguage.*;
import static org.epics.pvmanager.extra.ExpressionLanguage.waterfallPlotOf;
import static org.epics.pvmanager.extra.WaterfallPlotParameters.pixelDuration;
import static org.epics.pvmanager.util.TimeDuration.*;

import java.util.List;

import org.csstudio.ui.util.widgets.RangeListener;
import org.csstudio.ui.util.widgets.RangeWidget;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.extra.WaterfallPlot;
import org.epics.pvmanager.extra.WaterfallPlotParameters;
import org.epics.pvmanager.util.TimeDuration;

import com.swtdesigner.ResourceManager;

/**
 * A widget that connects to an array and display a waterfall plot based on it.
 * 
 * @author carcassi
 */
public class WaterfallWidget extends Composite {
	
	private VImageDisplay imageDisplay;
	private RangeWidget rangeWidget;
	private WaterfallPlotParameters parameters = WaterfallPlotParameters.defaults();
	private WaterfallPlot plot;
	private CLabel errorLabel;
	private Label errorImage;
	private GridData gd_rangeWidget;
	private boolean editable = true;
	
	private String sortProperty;
	
	public String getSortProperty() {
		return sortProperty;
	}
	
	public void setSortProperty(String sortProperty) {
		this.sortProperty = sortProperty;
	}
	
	public void openConfigurationDialog(int x, int y) {
		WaterfallParametersDialog dialog = new WaterfallParametersDialog(getShell(), SWT.NORMAL);
		dialog.open(this, x, y);
	}

	/**
	 * Creates a new widget.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public WaterfallWidget(Composite parent, int style) {
		super(parent, style);
		
		// Close PV on dispose
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (pv != null) {
					pv.close();
					pv = null;
				}
			}
		});
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		rangeWidget = new RangeWidget(this, SWT.NONE);
		rangeWidget.addRangeListener(new RangeListener() {
			
			@Override
			public void rangeChanged() {
				parameters = parameters.with(pixelDuration(TimeDuration.nanos((long) (rangeWidget.getDistancePerPx() * 1000000000))));
				if (plot != null) {
					plot.with(parameters);
				}
			}
		});
		gd_rangeWidget = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_rangeWidget.widthHint = 61;
		rangeWidget.setLayoutData(gd_rangeWidget);
		
		imageDisplay = new VImageDisplay(this);
		imageDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		imageDisplay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (editable && e.button == 3) {
					Point position = new Point(e.x, e.y);
					position = getDisplay().map(WaterfallWidget.this, null, position);
					openConfigurationDialog(position.x, position.y);
				}
			}
		});
		imageDisplay.setStretched(SWT.HORIZONTAL);
		GridLayout gl_imageDisplay = new GridLayout(2, false);
		gl_imageDisplay.marginLeft = 1;
		gl_imageDisplay.marginWidth = 0;
		gl_imageDisplay.marginHeight = 0;
		imageDisplay.setLayout(gl_imageDisplay);
		imageDisplay.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				if (imageDisplay.getSize().y != 0) {
					changePlotHeight(imageDisplay.getSize().y);
				}
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				// Nothing to do
			}
		});
		
		errorImage = new Label(imageDisplay, SWT.NONE);
		errorImage.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/warn_tsk.gif"));
		errorImage.setVisible(false);
		
		errorLabel = new CLabel(imageDisplay, SWT.NONE);
		GridData gd_errorLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_errorLabel.widthHint = 221;
		errorLabel.setLayoutData(gd_errorLabel);
		errorLabel.setText("");
		errorLabel.setVisible(false);
		
		// Set the parameters to the default
		parametersChanged();
	}
	
	public void setInputText(String name) {
		setWaveformPVName(name);
	}
	
	public String getInputText() {
		return getWaveformPVName();
	}
	
	// The pv name for waveform
	private String waveformPVName;
	// The pv names for multiple channels
	private List<String> scalarPVNames;
	// The pv created by pvmanager
	private PVReader<VImage> pv;
	
	/**
	 * The pv name to connect to.
	 * 
	 * @return the current property value
	 */
	public String getWaveformPVName() {
		return waveformPVName;
	}
	
	/**
	 * Changes the pv name to connect to. Triggers a reconnection.
	 * 
	 * @param pvName the new property value
	 */
	public void setWaveformPVName(String pvName) {
		// Guard from double calls
		if (this.waveformPVName != null && this.waveformPVName.equals(pvName)) {
			return;
		}
		
		this.scalarPVNames = null;
		this.waveformPVName = pvName;
		reconnect();
	}
	
	public List<String> getScalarPVNames() {
		return scalarPVNames;
	}
	
	public void setScalarPVNames(List<String> scalarPVNames) {
		// Guard from double calls
		if (this.scalarPVNames != null && this.scalarPVNames.equals(scalarPVNames)) {
			return;
		}
		
		this.waveformPVName = null;
		this.scalarPVNames = scalarPVNames;
		reconnect();
	}
	
	/**
	 * Whether the user is able to customize the widget.
	 * 
	 * @return true if it can be customized
	 */
	public boolean isEditable() {
		return editable;
	}
	
	/**
	 * Changes whether the user is able to customize the widget.
	 * 
	 * @param editable true if it can be customized
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	// Displays the last error generated
	private void setLastError(Exception ex) {
		if (!isDisposed()) {
			if (ex == null) {
				errorImage.setVisible(false);
				errorLabel.setVisible(false);
			} else {
				errorImage.setVisible(true);
				errorLabel.setVisible(true);
				errorLabel.setToolTipText(ex.getMessage());
				errorLabel.setText(ex.getMessage());
			}
		}
	}
	
	private void parametersChanged() {
		// Make sure image alignment and the range direction
		// are consistent with the scroll direction
		if (parameters.isScrollDown()) {
			imageDisplay.setAlignment(SWT.LEFT | SWT.TOP);
			rangeWidget.setStartPosition(SWT.UP);
		} else {
			imageDisplay.setAlignment(SWT.LEFT | SWT.BOTTOM);
			rangeWidget.setStartPosition(SWT.DOWN);
		}
		
		// Make sure the range is consistent with the image resolution
		rangeWidget.setDistancePerPx(parameters.getPixelDuration().getNanoSec() / 1000000000.0);
	}
	
	// Reconnects the pv
	private void reconnect() {
		// First de-allocate current pv if any
		if (pv != null) {
			pv.close();
			pv = null;
		}
		
		// Clean up old image and previous error
		imageDisplay.setVImage(null);
		setLastError(null);
		
		if (waveformPVName != null && !waveformPVName.trim().isEmpty()) {
			int color = (getBackground().getRed() << 16) + (getBackground().getGreen() << 8) + getBackground().getBlue();
			plot = waterfallPlotOf(vDoubleArrayOf(channel(waveformPVName))).with(parameters, WaterfallPlotParameters.backgroundColor(color));
			parameters = plot.getParameters();
			pv = PVManager.read(plot)
				.notifyOn(SWTUtil.swtThread()).every(hz(50));
			pv.addPVReaderListener(new PVReaderListener() {
				
				@Override
				public void pvChanged() {
					setLastError(pv.lastException());
					imageDisplay.setVImage(pv.getValue());
				}
			});
			return;
		}
		
		if (scalarPVNames != null && !scalarPVNames.isEmpty()) {
			int color = (getBackground().getRed() << 16) + (getBackground().getGreen() << 8) + getBackground().getBlue();
			plot = waterfallPlotOf(vDoubles(scalarPVNames)).with(parameters, WaterfallPlotParameters.backgroundColor(color));
			parameters = plot.getParameters();
			pv = PVManager.read(plot)
				.notifyOn(SWTUtil.swtThread()).every(hz(50));
			pv.addPVReaderListener(new PVReaderListener() {
				
				@Override
				public void pvChanged() {
					setLastError(pv.lastException());
					imageDisplay.setVImage(pv.getValue());
				}
			});
			return;
		}
	}
	
	private void changePlotHeight(int newHeight) {
		parameters = parameters.with(WaterfallPlotParameters.height(newHeight));
		if (plot != null) {
			plot.with(parameters);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	/**
	 * Changes the parameters used for the waterfall plot.
	 * 
	 * @param parameters a set of waterfall plot parameter
	 */
	public void setWaterfallPlotParameters(WaterfallPlotParameters parameters) {
		this.parameters = parameters;
		parametersChanged();
		if (plot != null) {
			plot.with(parameters);
		}
	}

	/**
	 * The parameters used for the waterfall plot.
	 * 
	 * @return waterfall plot parameters
	 */
	public WaterfallPlotParameters getWaterfallPlotParameters() {
		return parameters;
	}
	
	/**
	 * Changes whether the range should be displayed.
	 * 
	 * @param showRange true if range should be displayed
	 */
	public void setShowRange(boolean showRange) {
		rangeWidget.setVisible(showRange);
		
		// Making the range invisible is not enough to not show it.
		// We have to change the layout so that the width is
		// zero and redo the layout
		if (showRange) {
			gd_rangeWidget.widthHint = 61;
			rangeWidget.setLayoutData(gd_rangeWidget);
		} else {
			gd_rangeWidget.widthHint = 0;
			rangeWidget.setLayoutData(gd_rangeWidget);
		}
		layout();
	}
	
	/**
	 * Whether the range should be displayed.
	 * 
	 * @return true if the range is displayed
	 */
	public boolean isShowRange() {
		return rangeWidget.isVisible();
	}

}
