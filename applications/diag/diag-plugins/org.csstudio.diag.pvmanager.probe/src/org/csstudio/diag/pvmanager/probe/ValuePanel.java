package org.csstudio.diag.pvmanager.probe;

import java.time.format.DateTimeFormatter;

import org.csstudio.java.time.TimestampFormats;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Enum;
import org.diirt.vtype.SimpleValueFormat;
import org.diirt.vtype.Time;
import org.diirt.vtype.ValueFormat;
import org.diirt.vtype.ValueUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Probe panel that allows to show the value.
 *
 * @author carcassi
 *
 */
public class ValuePanel extends Composite {

    // TODO: we should take these from a default place
    private ValueFormat valueFormat = new SimpleValueFormat(3);
    private DateTimeFormatter timeFormat = TimestampFormats.FULL_FORMAT;

    private Text valueField;
    private Text timestampField;
    private Text labelsField;
    private Text displayField;
    private Label valueLabel;
    private Label timestampLabel;
    private Label labelsLabel;
    private Label displayLabel;
    private Composite valueSection;
    private Composite timestampSection;
    private Composite labelsSection;
    private Composite displaySection;

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public ValuePanel(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);

        valueSection = new Composite(this, SWT.NONE);
        GridLayout gl_valueSection = new GridLayout(2, false);
        gl_valueSection.marginBottom = 5;
        gl_valueSection.marginWidth = 0;
        gl_valueSection.marginHeight = 0;
        valueSection.setLayout(gl_valueSection);
        valueSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        valueLabel = new Label(valueSection, SWT.NONE);
        valueLabel.setText(Messages.Probe_infoValue);

        valueField = new Text(valueSection, SWT.BORDER);
        valueField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        valueField.setEditable(false);

        timestampSection = new Composite(this, SWT.NONE);
        timestampSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_timestampSection = new GridLayout(2, false);
        gl_timestampSection.marginBottom = 5;
        gl_timestampSection.marginWidth = 0;
        gl_timestampSection.marginHeight = 0;
        timestampSection.setLayout(gl_timestampSection);

        timestampLabel = new Label(timestampSection, SWT.NONE);
        timestampLabel.setText(Messages.Probe_infoTimestamp);

        timestampField = new Text(timestampSection, SWT.BORDER);
        timestampField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        timestampField.setEditable(false);

        labelsSection = new Composite(this, SWT.NONE);
        labelsSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_labelsSection = new GridLayout(2, false);
        gl_labelsSection.marginBottom = 5;
        gl_labelsSection.marginWidth = 0;
        gl_labelsSection.marginHeight = 0;
        labelsSection.setLayout(gl_labelsSection);

        labelsLabel = new Label(labelsSection, SWT.NONE);
        labelsLabel.setText(Messages.Probe_infoLabels);

        labelsField = new Text(labelsSection, SWT.BORDER);
        labelsField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        labelsField.setEditable(false);

        displaySection = new Composite(this, SWT.NONE);
        GridLayout gl_displaySection = new GridLayout(2, false);
        gl_displaySection.marginBottom = 5;
        gl_displaySection.marginHeight = 0;
        gl_displaySection.marginWidth = 0;
        displaySection.setLayout(gl_displaySection);
        displaySection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        displayLabel = new Label(displaySection, SWT.NONE);
        displayLabel.setText(Messages.Probe_infoDisplay);

        displayField = new Text(displaySection, SWT.BORDER);
        displayField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        displayField.setEditable(false);

    }

    private boolean needsDoLayout;

    public void changeValue(Object value, boolean connection) {
        needsDoLayout = false;

        setValue(value, connection);
        setTime(ValueUtil.timeOf(value));

        if (value instanceof Enum) {
            setLabels((Enum) value);
        } else {
            setLabels(null);
        }

        hideSection(displaySection);

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

    private void setTime(Time time) {
        if (time != null) {
            timestampField.setText(timeFormat.format(time.getTimestamp()));
            showSection(timestampSection);
        } else {
            timestampField.setText(""); //$NON-NLS-1$
            hideSection(timestampSection);
        }
    }

    private void hideSection(Composite section) {
        needsDoLayout = ShowHideForGridLayout.hide(section) || needsDoLayout;
    }

    private void showSection(Composite section) {
        needsDoLayout = ShowHideForGridLayout.show(section) || needsDoLayout;
    }

    private void appendAlarm(StringBuilder builder, Alarm alarm) {
        if (alarm == null || alarm.getAlarmSeverity().equals(AlarmSeverity.NONE)) {
            return; //$NON-NLS-1$
        } else {
            if (builder.length() != 0) {
                builder.append(' ');
            }
            builder.append('[')
                   .append(alarm.getAlarmSeverity())
                   .append(" - ") //$NON-NLS-1$
                   .append(alarm.getAlarmName())
                   .append(']');
        }
    }

    private void setValue(Object value, boolean connection) {
        StringBuilder formattedValue = new StringBuilder();

        if (value != null) {
            String valueString = valueFormat.format(value);
            if (valueString != null) {
                formattedValue.append(valueString);
            }
        }

        appendAlarm(formattedValue, ValueUtil.alarmOf(value, connection));

        valueField.setText(formattedValue.toString());
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
