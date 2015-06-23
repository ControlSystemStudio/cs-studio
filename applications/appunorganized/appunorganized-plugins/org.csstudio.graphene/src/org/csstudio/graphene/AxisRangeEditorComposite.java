package org.csstudio.graphene;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.epics.graphene.AxisRange;
import org.epics.graphene.AxisRanges;
import org.epics.graphene.AxisRanges.Fixed;
import org.epics.graphene.AxisRanges.Data;
import org.epics.graphene.AxisRanges.Display;
import org.epics.graphene.AxisRanges.Auto;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class AxisRangeEditorComposite extends Composite implements ISelectionProvider {
    private Button btnData;
    private Button btnDisplay;
    private Button btnFixed;
    private Button btnAuto;
    private Spinner minUsedRange;

    private boolean updating = false;

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public AxisRangeEditorComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));

        btnDisplay = new Button(this, SWT.RADIO);
        btnDisplay.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                newValue();
            }
        });
        btnDisplay.setText("Display: suggested range of the current data");

        btnData = new Button(this, SWT.RADIO);
        btnData.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                newValue();
            }
        });
        btnData.setText("Data: range of the current data");

        btnFixed = new Button(this, SWT.RADIO);
        btnFixed.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                minFixed.setEnabled(btnFixed.getSelection());
                maxFixed.setEnabled(btnFixed.getSelection());
                newValue();
            }
        });
        btnFixed.setText("Fixed: range specified by the following values");

        Composite composite = new Composite(this, SWT.NONE);
        GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_composite.horizontalIndent = 10;
        composite.setLayoutData(gd_composite);
        composite.setLayout(new GridLayout(4, false));

        Label lblMin = new Label(composite, SWT.NONE);
        lblMin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblMin.setText("Min:");

        minFixed = new Text(composite, SWT.BORDER);
        minFixed.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                newValue();
            }
        });
        minFixed.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblMax = new Label(composite, SWT.NONE);
        lblMax.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblMax.setText("Max:");

        btnAuto = new Button(this, SWT.RADIO);
        btnAuto.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                minUsedRange.setEnabled(btnAuto.getSelection());
                newValue();
            }
        });
        btnAuto.setText("Auto: range of all past data");

        Composite composite_1 = new Composite(this, SWT.NONE);
        GridData gd_composite_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_composite_1.horizontalIndent = 10;
        composite_1.setLayoutData(gd_composite_1);
        composite_1.setLayout(new GridLayout(3, false));

        maxFixed = new Text(composite, SWT.BORDER);
        maxFixed.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                newValue();
            }
        });
        maxFixed.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblMinUsedRange = new Label(composite_1, SWT.NONE);
        lblMinUsedRange.setText("Min used range:");

        minUsedRange = new Spinner(composite_1, SWT.BORDER);
        minUsedRange.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                newValue();
            }
        });

        Label label = new Label(composite_1, SWT.NONE);
        label.setText("%");

        clear();
        setAxisRange(AxisRanges.display());
    }


    private AxisRange axisRange;
    private Text minFixed;
    private Text maxFixed;

    public AxisRange getAxisRange() {
        return axisRange;
    }

    public void setAxisRange(AxisRange range) {
        if (Objects.equals(this.axisRange, range)) {
            return;
        }
        this.axisRange = range;
        update(range);
        fireSelectionChanged();
    }

    private void update(AxisRange range)     {
        updating = true;
        clear();
        if (range instanceof Fixed) {
            Fixed abs = (Fixed) range;
            btnFixed.setSelection(true);
            minFixed.setText(abs.getFixedRange().getMinimum().toString());
            maxFixed.setText(abs.getFixedRange().getMaximum().toString());
            minFixed.setEnabled(true);
            maxFixed.setEnabled(true);
        } else if (range instanceof Data) {
            btnData.setSelection(true);
        } else if (range instanceof Display) {
            btnDisplay.setSelection(true);
        } else if (range instanceof Auto) {
            btnAuto.setSelection(true);
            Auto integrated = (Auto) range;
            minUsedRange.setSelection((int) (integrated.getMinUsage() * 100));
            minUsedRange.setEnabled(true);
        }
        updating = false;
    }

    private void clear() {
        btnFixed.setSelection(false);
        btnData.setSelection(false);
        btnDisplay.setSelection(false);
        btnAuto.setSelection(false);
        minFixed.setText("0");
        maxFixed.setText("1");
        minFixed.setEnabled(false);
        maxFixed.setEnabled(false);
        minUsedRange.setSelection(80);
        minUsedRange.setEnabled(false);
    }

    private void newValue() {
        if (!updating) {
            AxisRange newRange = createRange();
            if (newRange != null && !newRange.equals(this.axisRange)) {
                this.axisRange = newRange;
                fireSelectionChanged();
            }
        }
    }

    private AxisRange createRange() {
        if (btnDisplay.getSelection()) {
            return AxisRanges.display();
        } else if (btnData.getSelection()) {
            return AxisRanges.data();
        } else if (btnFixed.getSelection()) {
            try {
                double min = Double.parseDouble(minFixed.getText());
                double max = Double.parseDouble(maxFixed.getText());
                return AxisRanges.fixed(min, max);
            } catch (RuntimeException ex) {
                // Can't parse the double, ignore
            }
        } else if (btnAuto.getSelection()) {
            double usage = minUsedRange.getSelection() / 100.0;
            return AxisRanges.auto(usage);
        }
        return null;
    }


    private List<ISelectionChangedListener> listeners = new CopyOnWriteArrayList<ISelectionChangedListener>();

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        listeners.add(listener);
    }

    @Override
    public ISelection getSelection() {
        return new StructuredSelection(getAxisRange());
    }

    @Override
    public void removeSelectionChangedListener(
            ISelectionChangedListener listener) {
        listeners.remove(listener);
    }

    private void fireSelectionChanged() {
        ISelection selection = getSelection();
        SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
        for (ISelectionChangedListener listener : listeners) {
            listener.selectionChanged(event);
        }
    }

    @Override
    public void setSelection(ISelection selection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


}
