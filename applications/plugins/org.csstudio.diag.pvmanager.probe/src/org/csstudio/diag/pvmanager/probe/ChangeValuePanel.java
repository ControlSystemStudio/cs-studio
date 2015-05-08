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
        newValueLabel.setText(Messages.Probe_newValueLabelText);

        newValueField = new Text(valueSection, SWT.BORDER);
        newValueField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        newValueField.setEditable(false);
        newValueField.setToolTipText(Messages.Probe_newValueFieldToolTipText);

        errorBar = new ErrorBar(this, SWT.NONE);
        errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        newValueField.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                pvWriter.write(newValueField.getText());
            }
        });
    }

    PVReaderListener<Object> readerListener = new PVReaderListener<Object>() {

        @Override
        public void pvChanged(PVReaderEvent<Object> event) {
            setValue(event.getPvReader().getValue());
        }
    };

    PVWriterListener<Object> writerListener = new PVWriterListener<Object>() {

        @Override
        public void pvChanged(PVWriterEvent<Object> event) {
            Exception ex = event.getPvWriter().lastWriteException();
            errorBar.setException(ex);
            newValueField.setEditable(event.getPvWriter().isWriteConnected());
            getParent().layout();
        }

    };

    public PVReaderListener<Object> getReaderListener() {
        return readerListener;
    }

    public PVWriterListener<Object> getWriterListener() {
        return writerListener;
    }

    public void reset() {
        errorBar.setException(null);
        setValue(null);
        newValueField.setEditable(false);
    }

    public void setPvWriter(PVWriter<Object> pvWriter) {
        this.pvWriter = pvWriter;
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
