package org.csstudio.graphene;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.GridData;
import org.epics.graphene.AxisRange;
import org.epics.graphene.AxisRanges;

public class AxisRangeEditorComposite extends Composite {
	private Button btnData;
	private Button btnDisplay;
	private Button btnAbsolute;
	private Spinner minAbsolute;
	private Spinner maxAbsolute;
	private Button btnIntegrated;
	private Spinner minUsedRange;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AxisRangeEditorComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		btnData = new Button(this, SWT.RADIO);
		FormData fd_btnData = new FormData();
		fd_btnData.left = new FormAttachment(0, 10);
		btnData.setLayoutData(fd_btnData);
		btnData.setText("Data: range of the current data");
		
		btnDisplay = new Button(this, SWT.RADIO);
		fd_btnData.top = new FormAttachment(0, 36);
		FormData fd_btnDisplay = new FormData();
		fd_btnDisplay.bottom = new FormAttachment(btnData, -6);
		fd_btnDisplay.left = new FormAttachment(0, 10);
		fd_btnDisplay.right = new FormAttachment(0, 440);
		btnDisplay.setLayoutData(fd_btnDisplay);
		btnDisplay.setText("Display: suggested range of the current data");
		
		btnAbsolute = new Button(this, SWT.RADIO);
		fd_btnData.right = new FormAttachment(btnAbsolute, 0, SWT.RIGHT);
		FormData fd_btnAbsolute = new FormData();
		fd_btnAbsolute.top = new FormAttachment(btnData, 6);
		fd_btnAbsolute.left = new FormAttachment(0, 10);
		fd_btnAbsolute.right = new FormAttachment(0, 440);
		btnAbsolute.setLayoutData(fd_btnAbsolute);
		btnAbsolute.setText("Absolute: range specified by the following values");
		
		Composite composite = new Composite(this, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(100, -176);
		fd_composite.top = new FormAttachment(btnAbsolute, 6);
		fd_composite.right = new FormAttachment(100, -70);
		fd_composite.left = new FormAttachment(0, 20);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new GridLayout(4, false));
		
		Label lblMin = new Label(composite, SWT.NONE);
		lblMin.setText("Min:");
		
		minAbsolute = new Spinner(composite, SWT.BORDER);
		GridData gd_minAbsolute = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_minAbsolute.widthHint = 100;
		minAbsolute.setLayoutData(gd_minAbsolute);
		
		Label lblMax = new Label(composite, SWT.NONE);
		lblMax.setText("Max:");
		
		maxAbsolute = new Spinner(composite, SWT.BORDER);
		GridData gd_maxAbsolute = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_maxAbsolute.widthHint = 100;
		maxAbsolute.setLayoutData(gd_maxAbsolute);
		
		btnIntegrated = new Button(this, SWT.RADIO);
		FormData fd_btnIntegrated = new FormData();
		fd_btnIntegrated.right = new FormAttachment(btnData, 0, SWT.RIGHT);
		fd_btnIntegrated.top = new FormAttachment(composite, 6);
		fd_btnIntegrated.left = new FormAttachment(0, 10);
		btnIntegrated.setLayoutData(fd_btnIntegrated);
		btnIntegrated.setText("Integrated: range of all past data");
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		FormData fd_composite_1 = new FormData();
		fd_composite_1.bottom = new FormAttachment(btnIntegrated, 42, SWT.BOTTOM);
		fd_composite_1.top = new FormAttachment(btnIntegrated, 6);
		fd_composite_1.right = new FormAttachment(composite, -111, SWT.RIGHT);
		fd_composite_1.left = new FormAttachment(0, 20);
		composite_1.setLayoutData(fd_composite_1);
		
		Label lblMinUsedRange = new Label(composite_1, SWT.NONE);
		lblMinUsedRange.setText("Min used range:");
		
		minUsedRange = new Spinner(composite_1, SWT.BORDER);
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setText("%");

	}
	
	private AxisRange axisRange;
	
	public AxisRange getAxisRange() {
		return axisRange;
	}
	
	public void setAxisRange(AxisRange range) {
		this.axisRange = range;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
