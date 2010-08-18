package org.remotercp.provisioning.dialogs;

import java.net.MalformedURLException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.remotercp.provisioning.UpdateActivator;
import org.remotercp.provisioning.images.ImageKeys;

public class AcceptUpdateDialog {

	private int dialogStatus = 0;
	private Shell shell;
	private Display display;

	public int open() {
		display = Display.getDefault();
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setSize(450, 250);
		shell.setText("Restart required");
		createContent(shell);

		// center the Dialog
		Rectangle bounds = Display.getCurrent().getBounds();
		Point size = shell.getSize();
		int xPosition = (bounds.width - size.x) / 2;
		int yPosition = (bounds.height - size.y) / 2;
		shell.setLocation(xPosition, yPosition);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return dialogStatus;
	}

	private void createContent(final Shell shell) {
		shell.setLayout(new GridLayout(1, false));

		Composite main = new Composite(shell, SWT.None);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		{
			Composite labels = new Composite(main, SWT.None);
			labels.setLayout(new GridLayout(2, false));
			{

				ImageDescriptor image = UpdateActivator
						.getImageDescriptor(ImageKeys.WARNING);

				Image warning = image.createImage();

				Label imageLabel = new Label(labels, SWT.None);
				imageLabel.setImage(warning);

				Label label = new Label(labels, SWT.None);

				label.setText("Your application has to be updated...");
			}
		}

		Composite messageComp = new Composite(main, SWT.None);
		messageComp.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(messageComp);
		{
			final Text message = new Text(messageComp, SWT.MULTI | SWT.WRAP
					| SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(message);

			new Thread() {

				public void run() {

					for (int wait = 20; wait >= 0; wait--) {
						// System.out.println("thread wait: " + wait);
						final int timeToLive = wait;
						display.asyncExec(new Runnable() {
							public void run() {
								if (!message.isDisposed()) {

									message
											.setText("Your application will be updated within the next "
													+ timeToLive
													+ " sec. "
													+ "\n\n"
													+ "After the update a restart is necessary. Please save your work and press OK");
								}
							}
						});
						try {
							// sleep for 1 sec
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					timedOut();
				}
			}.start();

		}

		Composite buttons = new Composite(main, SWT.None);
		buttons.setLayout(new GridLayout(2, false));
		GridData griddata = new GridData();
		griddata.horizontalAlignment = GridData.END;
		buttons.setLayoutData(griddata);
		{

			Button ok = new Button(buttons, SWT.PUSH | SWT.RIGHT);
			ok.setText("OK");
			ok.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					okPressed();
				}
			});

			Button cancel = new Button(buttons, SWT.PUSH | SWT.RIGHT);
			cancel.setText("Cancel");
			cancel.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					cancelPressed();
				}
			});
		}
	}

	private void okPressed() {
		dialogStatus = SWT.OK;
		shell.dispose();
	}

	private void timedOut() {
		dialogStatus = SWT.OK;
		display.syncExec(new Runnable() {
			public void run() {
				shell.dispose();
			}
		});
	}

	private void cancelPressed() {
		dialogStatus = SWT.CANCEL;
		shell.dispose();
	}
}
