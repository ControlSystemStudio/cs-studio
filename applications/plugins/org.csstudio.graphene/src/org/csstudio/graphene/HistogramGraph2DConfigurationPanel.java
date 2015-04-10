package org.csstudio.graphene;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class HistogramGraph2DConfigurationPanel extends
		AbstractGraph2DConfigurationPanel {
	private Button btnHighlightFocusValue;

	public HistogramGraph2DConfigurationPanel(Composite parent,
			int style) {
		super(parent, style);
		
		btnHighlightFocusValue = new Button(this, SWT.CHECK);
		btnHighlightFocusValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnHighlightFocusValue.setText("Highlight Focus Value");
		forwardCheckBoxEvents(btnHighlightFocusValue, "highlightFocusValue");
	}

	public boolean isHighlightFocusValue() {
		return this.btnHighlightFocusValue.getSelection();
	}

	public void setHighlightFocusValue(boolean highlightFocusValue) {
		this.btnHighlightFocusValue.setSelection(highlightFocusValue);
		changeSupport.firePropertyChange("highlightFocusValue", null, isHighlightFocusValue());
	}
	
}
