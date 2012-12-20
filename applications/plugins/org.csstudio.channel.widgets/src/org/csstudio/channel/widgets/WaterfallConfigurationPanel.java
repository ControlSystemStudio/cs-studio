package org.csstudio.channel.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.epics.pvmanager.util.TimeDuration;

/**
 * Popup dialog used by the waterfall widget to modify the WaterfallWidget.
 * 
 * @author carcassi
 */
public class WaterfallConfigurationPanel extends AbstractConfigurationComposite {
	
	private Button btnMetadata;
	private Button btnAutoRange;
	private Button btnUp;
	private Button btnDown;

	private Spinner spPixelDuration;
	private Button btnShowTimeAxis;
	private Label lblProperty;
	private Group grpChannels;
	private Text propertyField;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public WaterfallConfigurationPanel(Composite parent, int style) {
		super(parent, style);
		
		this.setLayout(new FormLayout());
		
		Group grpRange = new Group(this, SWT.NONE);
		grpRange.setText("Color range:");
		grpRange.setLayout(new GridLayout(2, false));
		FormData fd_grpRange = new FormData();
		fd_grpRange.left = new FormAttachment(0, 10);
		fd_grpRange.right = new FormAttachment(100, -10);
		fd_grpRange.top = new FormAttachment(0, 10);
		fd_grpRange.bottom = new FormAttachment(0, 63);
		grpRange.setLayoutData(fd_grpRange);
		
		btnMetadata = new Button(grpRange, SWT.RADIO);
		btnMetadata.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("adaptiveRange", !isAdaptiveRange(), isAdaptiveRange());
			}
		});
		btnMetadata.setText("Metadata");
		
		btnAutoRange = new Button(grpRange, SWT.RADIO);
		btnAutoRange.setText("Auto");
		btnAutoRange.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("adaptiveRange", !isAdaptiveRange(), isAdaptiveRange());
			}
		});
		
		Group grpScroll = new Group(this, SWT.NONE);
		grpScroll.setText("Scroll:");
		grpScroll.setLayout(new GridLayout(2, false));
		FormData fd_grpScroll = new FormData();
		fd_grpScroll.left = new FormAttachment(0, 10);
		fd_grpScroll.right = new FormAttachment(100, -10);
		fd_grpScroll.top = new FormAttachment(grpRange, 6);
		grpScroll.setLayoutData(fd_grpScroll);
		
		btnUp = new Button(grpScroll, SWT.RADIO);
		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("scrollDirection", 0, getScrollDirection());
			}
		});
		btnUp.setText("Up");
		
		btnDown = new Button(grpScroll, SWT.RADIO);
		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("scrollDirection", 0, getScrollDirection());
			}
		});
		btnDown.setText("Down");
		
		Label lblResolution = new Label(this, SWT.NONE);
		FormData fd_lblResolution = new FormData();
		fd_lblResolution.left = new FormAttachment(grpRange, 0, SWT.LEFT);
		lblResolution.setLayoutData(fd_lblResolution);
		lblResolution.setText("Resolution:");
		
		spPixelDuration = new Spinner(this, SWT.BORDER);
		spPixelDuration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("resolution", null, getResolution());
			}
		});
		FormData fd_spPixelDuration = new FormData();
		fd_spPixelDuration.top = new FormAttachment(lblResolution, -3, SWT.TOP);
		fd_spPixelDuration.left = new FormAttachment(lblResolution, 6);
		spPixelDuration.setLayoutData(fd_spPixelDuration);
		spPixelDuration.setMaximum(10000);
		spPixelDuration.setMinimum(1);
		
		Label lblMsPerPixel = new Label(this, SWT.NONE);
		FormData fd_lblMsPerPixel = new FormData();
		fd_lblMsPerPixel.top = new FormAttachment(lblResolution, 0, SWT.TOP);
		fd_lblMsPerPixel.left = new FormAttachment(spPixelDuration, 6);
		lblMsPerPixel.setLayoutData(fd_lblMsPerPixel);
		lblMsPerPixel.setText("ms per pixel");
		
		btnShowTimeAxis = new Button(this, SWT.CHECK);
		FormData fd_btnShowTimeAxis = new FormData();
		fd_btnShowTimeAxis.top = new FormAttachment(lblResolution, 6);
		fd_btnShowTimeAxis.left = new FormAttachment(grpRange, 0, SWT.LEFT);
		btnShowTimeAxis.setLayoutData(fd_btnShowTimeAxis);
		btnShowTimeAxis.setText("Show Time Axis");
		
		grpChannels = new Group(this, SWT.NONE);
		fd_lblResolution.top = new FormAttachment(grpChannels, 6);
		grpChannels.setText("Channels:");
		grpChannels.setLayout(new GridLayout(2, false));
		FormData fd_grpChannels = new FormData();
		fd_grpChannels.left = new FormAttachment(0, 10);
		fd_grpChannels.right = new FormAttachment(100, -10);
		fd_grpChannels.bottom = new FormAttachment(grpScroll, 65, SWT.BOTTOM);
		fd_grpChannels.top = new FormAttachment(grpScroll, 6);
		grpChannels.setLayoutData(fd_grpChannels);
		
		lblProperty = new Label(grpChannels, SWT.NONE);
		lblProperty.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProperty.setText("Order by prop:");
		
		propertyField = new Text(grpChannels, SWT.BORDER);
		propertyField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		propertyField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				changeSupport.firePropertyChange("sortProperty", "", getSortProperty());
			}
		});
		btnShowTimeAxis.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("showTimeAxis", !isShowTimeAxis(), isShowTimeAxis());
			}
		});

	}
	
	public String getSortProperty() {
		return propertyField.getText();
	}
	
	public void setSortProperty(String sortProperty) {
		if (sortProperty != null) {
			propertyField.setText(sortProperty);
		} else {
			propertyField.setText("");
		}
	}
	
	public boolean isShowTimeAxis() {
		return btnShowTimeAxis.getSelection();
	}
	
	public void setShowTimeAxis(boolean showTimeAxis) {
		btnShowTimeAxis.setSelection(showTimeAxis);
	}
	
	public TimeDuration getResolution() {
		return TimeDuration.ms(spPixelDuration.getSelection());
	}
	
	public void setResolution(TimeDuration duration) {
		spPixelDuration.setSelection((int) (duration.getNanoSec() / 1000000));
	}
	
	public int getScrollDirection() {
		if (btnUp.getSelection()) {
			return SWT.UP;
		}
		if (btnDown.getSelection()) {
			return SWT.DOWN;
		}
		throw new IllegalStateException("Neither up or down is selected");
	}
	
	public void setScrollDirection(int direction) {
		if (direction == SWT.UP) {
			btnUp.setSelection(true);
		}
		if (direction == SWT.DOWN) {
			btnDown.setSelection(true);
		}
	}
	
	public boolean isAdaptiveRange() {
		if (btnAutoRange.getSelection()) {
			return true;
		}
		if (btnMetadata.getSelection()) {
			return false;
		}
		throw new IllegalStateException("Neither auto or metadata is selected");
	}
	
	public void setAdaptiveRange(boolean adaptiveRange) {
		if (adaptiveRange) {
			btnAutoRange.setSelection(true);
		} else {
			btnMetadata.setSelection(true);
		}
	}
}
