package org.csstudio.diag.pvmanager.probe;

import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.util.time.TimestampFormat;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Enum;
import org.epics.vtype.SimpleValueFormat;
import org.epics.vtype.Time;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFormat;
import org.epics.vtype.ValueUtil;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class ChangeValueBox extends Composite {
	
	// TODO: we should take these from a default place
	private ValueFormat valueFormat = new SimpleValueFormat(3);
	private TimestampFormat timeFormat = new TimestampFormat(
			"yyyy/MM/dd HH:mm:ss.N Z"); //$NON-NLS-1$
	
	private Text newValueField;
//	private Text timestampField;
//	private Text labelsField;
//	private Text displayField;
	private Label newValueLabel;
//	private Label timestampLabel;
//	private Label labelsLabel;
//	private Label displayLabel;
	private Composite valueSection;
//	private Composite timestampSection;
//	private Composite labelsSection;
//	private Composite displaySection;
	private ErrorBar errorBar;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ChangeValueBox(Composite parent, int style) {
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
		
//		timestampSection = new Composite(this, SWT.NONE);
//		timestampSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		GridLayout gl_timestampSection = new GridLayout(2, false);
//		gl_timestampSection.marginBottom = 5;
//		gl_timestampSection.marginWidth = 0;
//		gl_timestampSection.marginHeight = 0;
//		timestampSection.setLayout(gl_timestampSection);
//		
//		timestampLabel = new Label(timestampSection, SWT.NONE);
//		timestampLabel.setText("Timestamp:");
//		
//		timestampField = new Text(timestampSection, SWT.BORDER);
//		timestampField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		timestampField.setEditable(false);
//		
//		labelsSection = new Composite(this, SWT.NONE);
//		labelsSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		GridLayout gl_labelsSection = new GridLayout(2, false);
//		gl_labelsSection.marginBottom = 5;
//		gl_labelsSection.marginWidth = 0;
//		gl_labelsSection.marginHeight = 0;
//		gl_labelsSection.horizontalSpacing = 0;
//		labelsSection.setLayout(gl_labelsSection);
//		
//		labelsLabel = new Label(labelsSection, SWT.NONE);
//		labelsLabel.setText("Labels:");
//		
//		labelsField = new Text(labelsSection, SWT.BORDER);
//		labelsField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		labelsField.setEditable(false);
//		
//		displaySection = new Composite(this, SWT.NONE);
//		GridLayout gl_displaySection = new GridLayout(2, false);
//		gl_displaySection.marginBottom = 5;
//		gl_displaySection.marginHeight = 0;
//		gl_displaySection.marginWidth = 0;
//		displaySection.setLayout(gl_displaySection);
//		displaySection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		
//		displayLabel = new Label(displaySection, SWT.NONE);
//		displayLabel.setText("Display:");
//		
//		displayField = new Text(displaySection, SWT.BORDER);
//		displayField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		displayField.setEditable(false);

		newValueField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				pvWriter.write(newValueField.getText());
			}
		});
	}
	
	private PVWriter<Object> pvWriter;

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
	
	private boolean needsDoLayout;
	
	public void changeValue(Object value) {
		needsDoLayout = false;
		
		setValue(value);
//		setTime(ValueUtil.timeOf(value));
//		
//		if (value instanceof Enum) {
//			setLabels((Enum) value);
//		} else {
//			setLabels(null);
//		}
//		
//		hideSection(displaySection);
		
		if (needsDoLayout) {
			this.getParent().layout();
		}
	}
//	
//	private void setLabels(Enum vEnum) {
//		if (vEnum != null) {
//			labelsField.setText(vEnum.getLabels().toString());
//			showSection(labelsSection);
//		} else {
//			labelsField.setText(""); //$NON-NLS-1$
//			hideSection(labelsSection);
//		}
//	}
//	
//	private void setTime(Time time) {
//		if (time != null) {
//			timestampField.setText(timeFormat.format(time.getTimestamp()));
//			showSection(timestampSection);
//		} else {
//			timestampField.setText(""); //$NON-NLS-1$
//			hideSection(timestampSection);
//		}
//	}
	
	private void hideSection(Composite section) {
		needsDoLayout = ShowHideForGridLayout.hide(section) || needsDoLayout;
	}
	
	private void showSection(Composite section) {
		needsDoLayout = ShowHideForGridLayout.show(section) || needsDoLayout;
	}
//	
//	private void appendAlarm(StringBuilder builder, Alarm alarm) {
//		if (alarm == null || alarm.getAlarmSeverity().equals(AlarmSeverity.NONE)) {
//			return; //$NON-NLS-1$
//		} else {
//			if (builder.length() != 0) {
//				builder.append(' ');
//			}
//			builder.append('[')
//			       .append(alarm.getAlarmSeverity())
//			       .append(" - ")
//			       .append(alarm.getAlarmName())
//			       .append(']');
//		}
//	}
	
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
