package org.csstudio.channel.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Line2DPlotConfigurationPanel extends
		AbstractConfigurationComposite {
	private Text XChannelQuery;

	public Line2DPlotConfigurationPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblXChannelQuery = new Label(this, SWT.NONE);
		lblXChannelQuery.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblXChannelQuery.setText("XChannelQuery:");

		XChannelQuery = new Text(this, SWT.BORDER);
		XChannelQuery.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				changeSupport.firePropertyChange("xChannelQuery", null,
						getXChannelQuery());
			}
		});
		XChannelQuery.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
	}

	public String getXChannelQuery() {
		return XChannelQuery.getText();
	}

	public void setXChannelQuery(String xChannelQuery) {
		XChannelQuery.setText(xChannelQuery);
	}

}
