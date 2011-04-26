package org.csstudio.utility.pvmanager.widgets;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.epics.pvmanager.extra.WaterfallPlotParameters;
import org.epics.pvmanager.util.TimeDuration;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class WaterfallParametersDialog extends Dialog {

	protected WaterfallPlotParameters result;
	protected WaterfallPlotParameters oldParameters;
	protected Shell shell;
	
	private Button btnMetadata;
	private Button btnAutoRange;
	private Button btnUp;
	private Button btnDown;

	private Spinner spPixelDuration;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public WaterfallParametersDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
		createContents();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public WaterfallPlotParameters open(WaterfallPlotParameters oldParameters, int x, int y) {
		this.oldParameters = oldParameters;
		if (oldParameters.isAdaptiveRange()) {
			btnAutoRange.setSelection(true);
			btnMetadata.setSelection(false);
		} else {
			btnAutoRange.setSelection(false);
			btnMetadata.setSelection(true);
		}
		if (oldParameters.isScrollDown()) {
			btnDown.setSelection(true);
			btnUp.setSelection(false);
		} else {
			btnDown.setSelection(false);
			btnUp.setSelection(true);
		}
		spPixelDuration.setSelection((int) (oldParameters.getPixelDuration().getNanoSec() / 1000000));
		
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
	
	private void prepareResult() {
		result = oldParameters.with(WaterfallPlotParameters.adaptiveRange(btnAutoRange.getSelection()),
				WaterfallPlotParameters.scrollDown(btnDown.getSelection()),
				WaterfallPlotParameters.pixelDuration(TimeDuration.ms(spPixelDuration.getSelection())));
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.APPLICATION_MODAL);
		shell.setSize(271, 225);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Button btnCancel = new Button(shell, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shell.close();
			}
		});
		
		Button btnApply = new Button(shell, SWT.NONE);
		FormData fd_btnApply = new FormData();
		fd_btnApply.top = new FormAttachment(btnCancel, 0, SWT.TOP);
		fd_btnApply.right = new FormAttachment(btnCancel, -6);
		btnApply.setLayoutData(fd_btnApply);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				prepareResult();
				shell.close();
			}
		});
		btnApply.setText("Apply");
		shell.setDefaultButton(btnApply);
		
		Group grpRange = new Group(shell, SWT.NONE);
		grpRange.setText("Range:");
		grpRange.setLayout(new GridLayout(2, false));
		FormData fd_grpRange = new FormData();
		fd_grpRange.top = new FormAttachment(0, 10);
		fd_grpRange.right = new FormAttachment(btnCancel, 0, SWT.RIGHT);
		fd_grpRange.left = new FormAttachment(0, 10);
		fd_grpRange.bottom = new FormAttachment(0, 63);
		grpRange.setLayoutData(fd_grpRange);
		
		btnMetadata = new Button(grpRange, SWT.RADIO);
		btnMetadata.setSelection(true);
		btnMetadata.setText("Metadata");
		
		btnAutoRange = new Button(grpRange, SWT.RADIO);
		btnAutoRange.setText("Auto");
		
		Group grpScroll = new Group(shell, SWT.NONE);
		grpScroll.setText("Scroll:");
		grpScroll.setLayout(new GridLayout(2, false));
		FormData fd_grpScroll = new FormData();
		fd_grpScroll.right = new FormAttachment(btnCancel, 0, SWT.RIGHT);
		fd_grpScroll.top = new FormAttachment(grpRange, 6);
		fd_grpScroll.left = new FormAttachment(grpRange, 0, SWT.LEFT);
		grpScroll.setLayoutData(fd_grpScroll);
		
		btnUp = new Button(grpScroll, SWT.RADIO);
		btnUp.setText("Up");
		
		btnDown = new Button(grpScroll, SWT.RADIO);
		btnDown.setText("Down");
		
		Label lblResolution = new Label(shell, SWT.NONE);
		FormData fd_lblResolution = new FormData();
		fd_lblResolution.top = new FormAttachment(grpScroll, 9);
		fd_lblResolution.left = new FormAttachment(grpRange, 0, SWT.LEFT);
		lblResolution.setLayoutData(fd_lblResolution);
		lblResolution.setText("Resolution:");
		
		spPixelDuration = new Spinner(shell, SWT.BORDER);
		FormData fd_spPixelDuration = new FormData();
		fd_spPixelDuration.top = new FormAttachment(lblResolution, -3, SWT.TOP);
		fd_spPixelDuration.left = new FormAttachment(lblResolution, 6);
		spPixelDuration.setLayoutData(fd_spPixelDuration);
		
		Label lblMsPerPixel = new Label(shell, SWT.NONE);
		FormData fd_lblMsPerPixel = new FormData();
		fd_lblMsPerPixel.bottom = new FormAttachment(lblResolution, 0, SWT.BOTTOM);
		fd_lblMsPerPixel.left = new FormAttachment(spPixelDuration, 6);
		lblMsPerPixel.setLayoutData(fd_lblMsPerPixel);
		lblMsPerPixel.setText("ms per pixel");

	}
}
