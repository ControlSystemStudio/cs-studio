package org.csstudio.channel.widgets;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.channel.widgets.Line2DPlotWidget.XAxis;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Line2DPlotConfigurationPanel extends
		AbstractConfigurationComposite {

	private Text xChannelQuery;
	private Text offset;
	private Text increment;
	private CCombo combo;

	private Button btnIndex;
	private Button btnRange;
	private Button btnProperty;
	private Button btnChannelQuery;

	public Line2DPlotConfigurationPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblXAxis = new Label(this, SWT.NONE);
		lblXAxis.setText("X Axis Configuration");
		new Label(this, SWT.NONE);

		btnIndex = new Button(this, SWT.RADIO);
		btnIndex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setXOrdering(XAxis.INDEX);
			}
		});
		btnIndex.setText("Index");
		new Label(this, SWT.NONE);

		btnRange = new Button(this, SWT.RADIO);
		btnRange.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setXOrdering(XAxis.OFFSET_INCREMENT);
			}
		});
		btnRange.setText("Range");
		new Label(this, SWT.NONE);

		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel.setText("Offset:");

		offset = new Text(this, SWT.BORDER);
		offset.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				changeSupport.firePropertyChange("offset", null, getOffset());
			}
		});
		offset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel_1.setText("Increment:");

		increment = new Text(this, SWT.BORDER);
		increment.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				changeSupport.firePropertyChange("increment", null,
						getIncrement());
			}
		});
		increment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		btnProperty = new Button(this, SWT.RADIO);
		btnProperty.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setXOrdering(XAxis.PROPERTY);
			}
		});
		btnProperty.setText("Property");
		new Label(this, SWT.NONE);

		Label lblNewLabel_2 = new Label(this, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel_2.setText("Property Name:");

		combo = new CCombo(this, SWT.BORDER);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("sortProperty", null,
						getSortProperty());
			}
		});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		btnChannelQuery = new Button(this, SWT.RADIO);
		btnChannelQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setXOrdering(XAxis.CHANNELQUERY);
			}
		});
		btnChannelQuery.setText("Channel Query");
		new Label(this, SWT.NONE);

		Label lblXChannelQuery = new Label(this, SWT.NONE);
		lblXChannelQuery.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblXChannelQuery.setText("XChannelQuery:");

		xChannelQuery = new Text(this, SWT.BORDER);
		xChannelQuery.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				changeSupport.firePropertyChange("xChannelQuery", null,
						getXChannelQuery());
			}
		});
		xChannelQuery.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		initializeControls();
	}

	private final void disableInputs() {
		this.combo.setEnabled(false);
		this.xChannelQuery.setEnabled(false);
		this.increment.setEnabled(false);
		this.offset.setEnabled(false);
	}

	/**
	 * need this since setSelection on radio button does not toggle rest of the
	 * buttons in the group.
	 */
	private void initializeControls() {
		disableInputs();
		switch (xOrdering) {
		case OFFSET_INCREMENT:
			btnIndex.setSelection(false);
			btnRange.setSelection(true);
			btnProperty.setSelection(false);
			btnChannelQuery.setSelection(false);
			offset.setEnabled(true);
			increment.setEnabled(true);
			break;
		case PROPERTY:
			btnIndex.setSelection(false);
			btnRange.setSelection(false);
			btnProperty.setSelection(true);
			combo.setEnabled(true);
			btnChannelQuery.setSelection(false);
			break;
		case CHANNELQUERY:
			btnIndex.setSelection(false);
			btnRange.setSelection(false);
			btnProperty.setSelection(false);
			btnChannelQuery.setSelection(true);
			xChannelQuery.setEnabled(true);
			break;
		default:
			btnIndex.setSelection(true);
			btnRange.setSelection(false);
			btnProperty.setSelection(false);
			btnChannelQuery.setSelection(false);
			break;
		}
		changeSupport.firePropertyChange("xOrdering", null, getXOrdering());
	}

	public String getXChannelQuery() {
		return xChannelQuery.getText();
	}

	public void setXChannelQuery(String xChannelQuery) {
		this.xChannelQuery.setText(xChannelQuery);
	}

	public String getSortProperty() {
		return combo.getText();
	}

	private volatile Collection<String> properties = new ArrayList<String>();
	private XAxis xOrdering = XAxis.INDEX;

	public void setProperties(Collection<String> properties) {
		this.properties = properties;
		this.combo.setItems(properties.toArray(new String[properties.size()]));
	}

	public void setSortProperty(String sortProperty) {
		if (properties.contains(sortProperty)) {
			this.combo.setText(sortProperty);
		}
	}

	public String getOffset() {
		return this.offset.getText();
	}

	public void setOffset(String offset) {
		this.offset.setText(offset);
	}

	public String getIncrement() {
		return this.increment.getText();
	}

	public void setIncrement(String increment) {
		this.increment.setText(increment);
	}

	public void setXOrdering(XAxis xOrdering) {
		this.xOrdering = xOrdering;
		initializeControls();
	}

	public XAxis getXOrdering() {
		return this.xOrdering;
	}
}
