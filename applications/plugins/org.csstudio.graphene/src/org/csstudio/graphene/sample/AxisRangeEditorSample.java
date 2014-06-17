package org.csstudio.graphene.sample;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.csstudio.graphene.AxisRangeEditorComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.epics.graphene.AxisRanges;

public class AxisRangeEditorSample {

	protected Shell shell;
	private AxisRangeEditorComposite axisRangeEditorComposite;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AxisRangeEditorSample window = new AxisRangeEditorSample();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(592, 393);
		shell.setText("SWT Application");
		
		axisRangeEditorComposite = new AxisRangeEditorComposite(shell, SWT.NONE);
		axisRangeEditorComposite.setBounds(0, 0, 574, 187);
		
		Button btnNull = new Button(shell, SWT.NONE);
		btnNull.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				axisRangeEditorComposite.setAxisRange(null);
			}
		});
		btnNull.setBounds(10, 308, 90, 30);
		btnNull.setText("Null");
		
		Button btnDisplay = new Button(shell, SWT.NONE);
		btnDisplay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				axisRangeEditorComposite.setAxisRange(AxisRanges.display());
			}
		});
		btnDisplay.setBounds(106, 308, 90, 30);
		btnDisplay.setText("Display");
		
		Button btnData = new Button(shell, SWT.NONE);
		btnData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				axisRangeEditorComposite.setAxisRange(AxisRanges.data());
			}
		});
		btnData.setBounds(202, 308, 90, 30);
		btnData.setText("Data");
		
		Button btnAbsolute1 = new Button(shell, SWT.NONE);
		btnAbsolute1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				axisRangeEditorComposite.setAxisRange(AxisRanges.absolute(-10, 10));
			}
		});
		btnAbsolute1.setBounds(298, 308, 128, 30);
		btnAbsolute1.setText("Absolute -10/10");
		
		Button btnAbsolute2 = new Button(shell, SWT.NONE);
		btnAbsolute2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				axisRangeEditorComposite.setAxisRange(AxisRanges.absolute(0, 25));
			}
		});
		btnAbsolute2.setText("Absolute 0/25");
		btnAbsolute2.setBounds(298, 272, 128, 30);
		
		Button btnIntegrated = new Button(shell, SWT.NONE);
		btnIntegrated.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				axisRangeEditorComposite.setAxisRange(AxisRanges.integrated());
			}
		});
		btnIntegrated.setBounds(432, 308, 105, 30);
		btnIntegrated.setText("Integrated");
		
		Button btnIntegrated50 = new Button(shell, SWT.NONE);
		btnIntegrated50.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				axisRangeEditorComposite.setAxisRange(AxisRanges.integrated(0.5));
			}
		});
		btnIntegrated50.setBounds(432, 272, 105, 30);
		btnIntegrated50.setText("Integrated 50");

	}
}
