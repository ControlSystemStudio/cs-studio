package org.csstudio.diag.pvmanager.probe;

import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.vtype.SimpleValueFormat;
import org.epics.vtype.ValueFormat;

/**
 * Probe panel that allows to change values.
 * 
 * @author carcassi
 *
 */
class ChangeValuePanel extends Composite {
	
	// TODO: we should take these from a default place
	private ValueFormat valueFormat = new SimpleValueFormat(3);
	
	private Text newValueField;
	private Label newValueLabel;
	private Composite valueSection;
	private ErrorBar errorBar;
	
	private PVWriter<Object> pvWriter;

	/**
	 * Create the panel.
	 * 
	 * @param parent parent composite
	 * @param style widget style
	 */
	ChangeValuePanel(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		valueSection = new Composite(this, SWT.NONE);
		GridLayout gl_valueSection = new GridLayout(2, false);
		gl_valueSection.marginWidth = 0;
		gl_valueSection.marginHeight = 0;
		valueSection.setLayout(gl_valueSection);
		valueSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		newValueLabel = new Label(valueSection, SWT.NONE);
		newValueLabel.setText("New value:");
		
		newValueField = new Text(valueSection, SWT.BORDER);
		newValueField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		newValueField.setEditable(false);
		
		errorBar = new ErrorBar(this, SWT.NONE);

		newValueField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				pvWriter.write(newValueField.getText());
			}
		});
	}

	/**
	 * Changes the pv used to read and write value.
	 * 
	 * @param pv the pv to be used
	 */
	void setPV(PV<?, Object> pv) {
		if (pv == null) {
			errorBar.setException(null);
			setValue(null);
			newValueField.setEditable(false);
		} else {
			pv.addPVReaderListener(new PVReaderListener<Object>() {
	
				@Override
				public void pvChanged(PVReaderEvent<Object> event) {
					setValue(event.getPvReader().getValue());
				}
			});
			pv.addPVWriterListener(new PVWriterListener<Object>() {
	
				@Override
				public void pvChanged(PVWriterEvent<Object> event) {
					errorBar.setException(event.getPvWriter().lastWriteException());
					newValueField.setEditable(event.getPvWriter().isWriteConnected());
				}
				
			});
			errorBar.setException(pv.lastWriteException());
			setValue(pv.getValue());
			newValueField.setEditable(pv.isWriteConnected());
			
			this.pvWriter = pv;
		}
	}
	
	/**
	 * Changes the values, making sure not to change it while editing.
	 * 
	 * @param value the new value
	 */
	private void setValue(Object value) {
		if (newValueField.isVisible() && !newValueField.isFocusControl()) {
			StringBuilder formattedValue = new StringBuilder();
			
			if (value != null) {
				String valueString = valueFormat.format(value);
				if (valueString != null) {
					formattedValue.append(valueString);
				}
			}
	
			newValueField.setText(formattedValue.toString());
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
