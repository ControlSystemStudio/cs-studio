package org.csstudio.utility.pvmanager.jfreechart.widgets;

import static org.csstudio.utility.pvmanager.jfreechart.widgets.VMultiChannelChartDisplay.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.plot.PlotUtilities;

public class SelectChartDomainAxis extends Dialog {

	private Button btnChannelName;
	private Button btnChannelPosition;
	private int currentSelection;
	
	protected int result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 * @param plotUsing 
	 */
	public SelectChartDomainAxis(Shell parent, int style, int plotUsing) {
		super(parent, style);		
		this.currentSelection = plotUsing;
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public int open(int x, int y) {
		createContents();
		shell.open();
		shell.layout();
		shell.setBounds(Math.min(x, shell.getDisplay().getClientArea().width - shell.getBounds().width),
				Math.min(y, shell.getDisplay().getClientArea().height - shell.getBounds().height),
				shell.getBounds().width, shell.getBounds().height);
		
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.APPLICATION_MODAL);
		shell.setSize(190, 185);
		shell.setText(getText());
		shell.setLayout(new FormLayout());

		btnChannelName = new Button(shell, SWT.RADIO);
		btnChannelName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
		FormData fd_btnChannelName = new FormData();
		btnChannelName.setLayoutData(fd_btnChannelName);
		btnChannelName.setText("Channel Name");

		btnChannelPosition = new Button(shell, SWT.RADIO);
		fd_btnChannelName.top = new FormAttachment(btnChannelPosition, 6);
		fd_btnChannelName.left = new FormAttachment(btnChannelPosition, 0,
				SWT.LEFT);
		FormData fd_btnChannelPosition = new FormData();
		btnChannelPosition.setLayoutData(fd_btnChannelPosition);
		btnChannelPosition.setText("Channel Position");
		if(this.currentSelection == DOMAIN_AXIS_TYPE_POSITION){
			btnChannelPosition.setSelection(true);
		}else{
			btnChannelName.setSelection(true);
		}

		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = btnChannelName.getSelection() ? DOMAIN_AXIS_TYPE_AUTO
						: DOMAIN_AXIS_TYPE_POSITION;
				shell.close();

			}
		});
		
		FormData fd_btnOk = new FormData();
		fd_btnOk.bottom = new FormAttachment(100, -10);
		fd_btnOk.left = new FormAttachment(0, 48);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("OK");
		fd_btnOk.right = new FormAttachment(100, -85);

		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.left = new FormAttachment(btnOk, 6);
		fd_btnCancel.top = new FormAttachment(btnOk, 0, SWT.TOP);
		fd_btnCancel.right = new FormAttachment(100, -13);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");

		Label lblSelectTheX = new Label(shell, SWT.NONE);
		fd_btnChannelPosition.top = new FormAttachment(lblSelectTheX, 6);
		fd_btnChannelPosition.left = new FormAttachment(lblSelectTheX, 0,
				SWT.LEFT);
		lblSelectTheX.setText("Plot using:");
		FormData fd_lblSelectTheX = new FormData();
		fd_lblSelectTheX.right = new FormAttachment(btnCancel, 0, SWT.RIGHT);
		fd_lblSelectTheX.bottom = new FormAttachment(100, -119);
		fd_lblSelectTheX.top = new FormAttachment(0, 10);
		fd_lblSelectTheX.left = new FormAttachment(0, 7);
		lblSelectTheX.setLayoutData(fd_lblSelectTheX);

	}
}
