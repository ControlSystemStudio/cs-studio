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
import org.epics.graphene.AxisRanges.Absolute;
import org.epics.graphene.AxisRanges.Data;
import org.epics.graphene.AxisRanges.Display;
import org.epics.graphene.AxisRanges.Integrated;

public class AxisRangeEditorComposite extends Composite implements ISelectionProvider {
	private Button btnData;
	private Button btnDisplay;
	private Button btnAbsolute;
	private Button btnIntegrated;
	private Spinner minUsedRange;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AxisRangeEditorComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		btnDisplay = new Button(this, SWT.RADIO);
		btnDisplay.setText("Display: suggested range of the current data");
		
		btnData = new Button(this, SWT.RADIO);
		btnData.setText("Data: range of the current data");
		
		btnAbsolute = new Button(this, SWT.RADIO);
		btnAbsolute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				minAbsolute.setEnabled(btnAbsolute.getSelection());
				maxAbsolute.setEnabled(btnAbsolute.getSelection());
			}
		});
		btnAbsolute.setText("Absolute: range specified by the following values");
		
		Composite composite = new Composite(this, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite.horizontalIndent = 10;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new GridLayout(4, false));
		
		Label lblMin = new Label(composite, SWT.NONE);
		lblMin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMin.setText("Min:");
		
		minAbsolute = new Text(composite, SWT.BORDER);
		minAbsolute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblMax = new Label(composite, SWT.NONE);
		lblMax.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMax.setText("Max:");
		
		btnIntegrated = new Button(this, SWT.RADIO);
		btnIntegrated.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				minUsedRange.setEnabled(btnIntegrated.getSelection());
			}
		});
		btnIntegrated.setText("Integrated: range of all past data");
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite_1.horizontalIndent = 10;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setLayout(new GridLayout(3, false));
		
		maxAbsolute = new Text(composite, SWT.BORDER);
		maxAbsolute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblMinUsedRange = new Label(composite_1, SWT.NONE);
		lblMinUsedRange.setText("Min used range:");
		
		minUsedRange = new Spinner(composite_1, SWT.BORDER);
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setText("%");

		clear();
		setAxisRange(AxisRanges.display());
	}
	
	
	private AxisRange axisRange;
	private Text minAbsolute;
	private Text maxAbsolute;
	
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
	
	private void update(AxisRange range) 	{
		clear();
		if (range instanceof Absolute) {
			Absolute abs = (Absolute) range;
			btnAbsolute.setSelection(true);
			minAbsolute.setText(abs.getAbsoluteRange().getMinimum().toString());
			maxAbsolute.setText(abs.getAbsoluteRange().getMaximum().toString());
			minAbsolute.setEnabled(true);
			maxAbsolute.setEnabled(true);
		} else if (range instanceof Data) {
			btnData.setSelection(true);
		} else if (range instanceof Display) {
			btnDisplay.setSelection(true);
		} else if (range instanceof Integrated) {
			btnIntegrated.setSelection(true);
			Integrated integrated = (Integrated) range;
			minUsedRange.setSelection((int) (integrated.getMinUsage() * 100));
			minUsedRange.setEnabled(true);
		}
	}
	
	private void clear() {
		btnAbsolute.setSelection(false);
		btnData.setSelection(false);
		btnDisplay.setSelection(false);
		btnIntegrated.setSelection(false);
		minAbsolute.setText("");
		maxAbsolute.setText("");
		minAbsolute.setEnabled(false);
		maxAbsolute.setEnabled(false);
		minUsedRange.setSelection(80);
		minUsedRange.setEnabled(false);
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
