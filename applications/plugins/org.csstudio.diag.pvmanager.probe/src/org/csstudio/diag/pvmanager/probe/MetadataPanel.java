package org.csstudio.diag.pvmanager.probe;

import java.text.NumberFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.epics.pvmanager.util.NumberFormats;
import org.epics.util.time.TimestampFormat;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.Enum;
import org.epics.vtype.SimpleValueFormat;
import org.epics.vtype.Time;
import org.epics.vtype.ValueFormat;
import org.epics.vtype.ValueUtil;

/**
 * Probe panel that allows to show the value.
 * 
 * @author carcassi
 *
 */
public class MetadataPanel extends Composite {
	
	private Text displayLimitsField;
	private Text alarmLimitsField;
	private Text labelsField;
	private Text warningLimitsField;
	private Label labelsLabel;
	private Composite displaySection;
	private Composite labelsSection;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MetadataPanel(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		labelsSection = new Composite(this, SWT.NONE);
		labelsSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_labelsSection = new GridLayout(2, false);
		gl_labelsSection.marginBottom = 5;
		gl_labelsSection.marginWidth = 0;
		gl_labelsSection.marginHeight = 0;
		labelsSection.setLayout(gl_labelsSection);
		
		labelsLabel = new Label(labelsSection, SWT.NONE);
		labelsLabel.setText("Labels:");
		
		labelsField = new Text(labelsSection, SWT.BORDER);
		labelsField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelsField.setEditable(false);
		
		displaySection = new Composite(this, SWT.NONE);
		GridLayout gl_displaySection = new GridLayout(2, false);
		gl_displaySection.marginBottom = 5;
		gl_displaySection.marginWidth = 0;
		gl_displaySection.marginHeight = 0;
		displaySection.setLayout(gl_displaySection);
		displaySection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label displayLimitsLabel = new Label(displaySection, SWT.NONE);
		displayLimitsLabel.setText("Display limits:");
		
		displayLimitsField = new Text(displaySection, SWT.BORDER);
		displayLimitsField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		displayLimitsField.setEditable(false);
		
		Label alarmLimitsLabel = new Label(displaySection, SWT.NONE);
		alarmLimitsLabel.setText("Alarm limits:");
		
		alarmLimitsField = new Text(displaySection, SWT.BORDER);
		alarmLimitsField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		alarmLimitsField.setEditable(false);
		
		Label warningLimitsLabel = new Label(displaySection, SWT.NONE);
		warningLimitsLabel.setText("Warning limits:");
		
		warningLimitsField = new Text(displaySection, SWT.BORDER);
		warningLimitsField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		warningLimitsField.setEditable(false);
		
		Label controlLimitsLabel = new Label(displaySection, SWT.NONE);
		controlLimitsLabel.setText("Control limits:");
		
		controlLimitsField = new Text(displaySection, SWT.BORDER);
		controlLimitsField.setEditable(false);
		controlLimitsField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		unitLabel = new Label(displaySection, SWT.NONE);
		unitLabel.setText("Unit:");
		
		unitField = new Text(displaySection, SWT.BORDER);
		unitField.setEditable(false);
		unitField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}
	
	private boolean needsDoLayout;
	private Text controlLimitsField;
	private Label unitLabel;
	private Text unitField;
	
	public void changeValue(Object value) {
		needsDoLayout = false;
		
		setDisplay(ValueUtil.displayOf(value));
		
		if (value instanceof Enum) {
			setLabels((Enum) value);
		} else {
			setLabels(null);
		}
		
		if (needsDoLayout) {
			this.getParent().layout();
		}
	}
	
	private void setLabels(Enum vEnum) {
		if (vEnum != null) {
			labelsField.setText(vEnum.getLabels().toString());
			showSection(labelsSection);
		} else {
			labelsField.setText(""); //$NON-NLS-1$
			hideSection(labelsSection);
		}
	}
	
	private void setDisplay(Display display) {
		if (display != null) {
			NumberFormat format = display.getFormat();
			if (format == null) {
				format = ValueUtil.getDefaultNumberFormat();
			}
			displayLimitsField.setText(format.format(display.getLowerDisplayLimit()) + " - " + format.format(display.getUpperDisplayLimit()));
			alarmLimitsField.setText(format.format(display.getLowerAlarmLimit()) + " - " + format.format(display.getUpperAlarmLimit()));
			warningLimitsField.setText(format.format(display.getLowerWarningLimit()) + " - " + format.format(display.getUpperWarningLimit()));
			controlLimitsField.setText(format.format(display.getLowerCtrlLimit()) + " - " + format.format(display.getUpperCtrlLimit()));
			unitField.setText(String.valueOf(display.getUnits()));
			showSection(displaySection);
		} else {
			displayLimitsField.setText(""); //$NON-NLS-1$
			alarmLimitsField.setText(""); //$NON-NLS-1$
			warningLimitsField.setText(""); //$NON-NLS-1$
			controlLimitsField.setText(""); //$NON-NLS-1$
			unitField.setText(""); //$NON-NLS-1$
			hideSection(displaySection);
		}
	}
	
	private void hideSection(Composite section) {
		needsDoLayout = ShowHideForGridLayout.hide(section) || needsDoLayout;
	}
	
	private void showSection(Composite section) {
		needsDoLayout = ShowHideForGridLayout.show(section) || needsDoLayout;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
