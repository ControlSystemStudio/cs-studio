package org.csstudio.graphene;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;

public class LineGraph2DConfigurationPanel extends
		AbstractPointDatasetGraph2DConfigurationPanel {

	public LineGraph2DConfigurationPanel(Composite parent,
			int style) {
		super(parent, style);
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setText("New Label");
		new Label(this, SWT.NONE);
	}
}
