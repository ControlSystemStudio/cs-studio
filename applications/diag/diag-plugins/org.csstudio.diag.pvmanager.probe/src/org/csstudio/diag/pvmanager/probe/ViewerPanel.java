package org.csstudio.diag.pvmanager.probe;

import org.csstudio.ui.util.widgets.MeterWidget;
import org.csstudio.utility.pvmanager.widgets.VTableDisplay;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.epics.vtype.Display;
import org.epics.vtype.VNumber;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueUtil;

/**
 * Probe panel that allows to show the value.
 *
 * @author carcassi
 *
 */
public class ViewerPanel extends Composite {

    private MeterWidget numericScalarSection;
    private VTableDisplay tableSection;

    /**
     * Create the composite.
     *
     * @param parent
     * @param style
     */
    public ViewerPanel(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);

        numericScalarSection = new MeterWidget(this, SWT.NONE);
        GridLayout gl_valueSection = new GridLayout(1, false);
        gl_valueSection.marginBottom = 5;
        gl_valueSection.marginWidth = 0;
        gl_valueSection.marginHeight = 0;
        numericScalarSection.setLayout(gl_valueSection);
        numericScalarSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 1, 1));

        tableSection = new VTableDisplay(this);
        tableSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        GridLayout gl_timestampSection = new GridLayout(1, false);
        gl_timestampSection.marginBottom = 5;
        gl_timestampSection.marginWidth = 0;
        gl_timestampSection.marginHeight = 0;
        tableSection.setLayout(gl_timestampSection);

        changeValue(null);
    }

    private boolean needsDoLayout;

    public void changeValue(Object value) {
        needsDoLayout = false;

        if (value instanceof VNumber) {
            needsDoLayout = needsDoLayout
                    || ShowHideForGridLayout.show(numericScalarSection);
            setMeter(ValueUtil.numericValueOf(value),
                    ValueUtil.displayOf(value));
        } else {
            needsDoLayout = needsDoLayout
                    || ShowHideForGridLayout.hide(numericScalarSection);
        }

        if (value instanceof VTable) {
            needsDoLayout = needsDoLayout
                    || ShowHideForGridLayout.show(tableSection);
            tableSection.setVTable((VTable) value);
        } else {
            needsDoLayout = needsDoLayout
                    || ShowHideForGridLayout.hide(tableSection);
        }

        if (needsDoLayout) {
            this.getParent().layout();
        }
    }

    private void setMeter(Double value, Display display) {
        if (value == null || display == null
                || !ValueUtil.displayHasValidDisplayLimits(display)) {
            numericScalarSection.setEnabled(false);
        } else if (display.getUpperDisplayLimit() <= display
                .getLowerDisplayLimit()) {
            numericScalarSection.setEnabled(false);
        } else {
            numericScalarSection.setEnabled(true);
            numericScalarSection.setLimits(display.getLowerDisplayLimit(),
                    display.getLowerAlarmLimit(),
                    display.getLowerWarningLimit(),
                    display.getUpperWarningLimit(),
                    display.getUpperAlarmLimit(),
                    display.getUpperDisplayLimit(), 1);
            numericScalarSection.setValue(value);
        }
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
