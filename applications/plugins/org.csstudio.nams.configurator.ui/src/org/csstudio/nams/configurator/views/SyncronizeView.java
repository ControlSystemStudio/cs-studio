package org.csstudio.nams.configurator.views;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.csstudio.nams.configurator.editor.AbstractEditor;
import org.csstudio.nams.configurator.service.synchronize.SynchronizeService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;

/**
 * View um die Synchronisation mit dem Hintergrundsystem auszuführen.
 * Verwenendet den {@link SynchronizeService}.
 */
public class SyncronizeView extends ViewPart {

	private static SynchronizeService synchronizeService = null;
	private Text statusText;
	private Button syncButton;

	/**
	 * Injiziert den {@link SynchronizeService} für diese View. Must be called
	 * before View is used.
	 */
	public static void staticInjectSynchronizeService(
			SynchronizeService synchronizeService) {
		SyncronizeView.synchronizeService = synchronizeService;
	}

	public SyncronizeView() {
		if (synchronizeService == null) {
			throw new RuntimeException(
					"View class is not probably initialised, missing: SynchronizeService!");
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL | SWT.HORIZONTAL));

		Composite innerComposite = new Composite(composite, SWT.NONE);
		innerComposite.setLayout(new GridLayout(1, false));

		syncButton = new Button(innerComposite, SWT.TOGGLE);
		syncButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		syncButton.setText("Perform synchronization of background-system");
		syncButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				if (source.getSelection()) {
					statusText.setText("");
					SyncronizeView.this.doSynchronize();
				} else {
					// deselect nur aus dem code erlaubt!
					source.setSelection(true);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// will never be called by a button.
			}

		});

		statusText = new Text(innerComposite, SWT.SCROLL_LINE | SWT.V_SCROLL
				| SWT.H_SCROLL);
		statusText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		statusText.setEditable(false);
		statusText
				.setText("Push \"Perform synchronization of background-system\" to begin syncronization.");
	}

	protected boolean lookForUnsavedChangesAndDecideAboutSynchrinsationProceeding() {
		boolean hasADirtyNamsEditor = false;
		IWorkbenchPage[] pages = this.getSite().getWorkbenchWindow().getPages();
		for (IWorkbenchPage page : pages) {
			IEditorPart[] dirtyEditors = page.getDirtyEditors();
			for (IEditorPart editor : dirtyEditors) {
				if (editor instanceof AbstractEditor<?>) {
					hasADirtyNamsEditor = true;
				}
			}
		}

		if (hasADirtyNamsEditor) {
			// XXX Besser wöre ein Dialog der Art: "Ungesciherte Änderungen. Wie
			// möchten
			// Sie verfahren?" mit Aktionen: "Anderungen sichten" vs. "trozdem
			// fortfahren".
			MessageBox messageBox = new MessageBox(this.getSite().getShell(),
					SWT.ICON_WARNING | SWT.YES | SWT.NO);
			messageBox.setText("Unsaved NAMS-Editors!");
			messageBox
					.setMessage("You still have unsaved changes in NAMS-Editors."
							+ " This changes will be ignored by synchrnoisation process.\n"
							+ "Do you want to review your changes before continuing?");
			if (messageBox.open() == SWT.YES) {
				// The User likes to check his changes.
//				System.out
//						.println("SyncronizeView.lookForUnsavedChangesAndDecideAboutSynchrinsationProceeding(): false");
				return false;
			}
		}
//		System.out
//				.println("SyncronizeView.lookForUnsavedChangesAndDecideAboutSynchrinsationProceeding(): true");
		return true;
	}

	protected synchronized void doSynchronize() {
		appendStatusText("Beginning syncronization...");

		try {
			synchronizeService
					.sychronizeAlarmSystem(new SynchronizeService.Callback() {
						public void bereiteSynchronisationVor() {
							appendStatusText("Preparing syncronisation...\n");
						}

						public void fehlerBeimVorbereitenDerSynchronisation(
								Throwable t) {
							appendStatusText(
									"Failed to prepare syncronisation.\nReason:",
									t);
							buttonFreigben();
						}

						volatile Boolean result = null;

						public boolean pruefeObSynchronisationAusgefuehrtWerdenDarf() {
							appendStatusText("Looking for unsaved changes...");
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									result = lookForUnsavedChangesAndDecideAboutSynchrinsationProceeding();
								}
							});
							while (result == null)
								Thread.yield();
							appendStatusText("Done.\n");
							return result.booleanValue();
						}

						public void sendeNachrichtAnHintergrundSystem() {
							appendStatusText("Sending sychronisation request to back-end-system...\n");
						}

						public void sendenDerNachrichtAnHintergrundSystemFehlgeschalgen(
								Throwable t) {
							appendStatusText(
									"Failed to send sychronisation request to back-end-system.\nReason:",
									t);
							buttonFreigben();
						}

						public void synchronisationAbgebrochen() {
							appendStatusText("Sychronisation has been canceled.\n");
							buttonFreigben();
						}

						public void synchronisationsDurchHintergrundsystemsErfolgreich() {
							appendStatusText("\n\nSyncronisation successfully finished!");
							buttonFreigben();
						}

						public void synchronisationsDurchHintergrundsystemsFehlgeschalgen(
								String fehlertext) {
							appendStatusText("Back-end-system reporting failure of sychronisation.\nReported reason: "
									+ fehlertext);
							buttonFreigben();
						}

						private void buttonFreigben() {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									syncButton.setSelection(false);
								}
							});
						}

						public void wartetAufAntowrtDesHintergrundSystems() {
							appendStatusText("Waiting for sychronisation commitment to back-end-system...\n");
						}

					});
		} catch (Throwable t) {
			appendStatusText("\n\nSyncronisation failed!\nReason: ", t);
		}

		// Display.getDefault().asyncExec(new Runnable() {
		// public void run() {
		// syncButton.setSelection(false);
		// }
		// });
	}

	private void appendStatusText(final String text) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String statusTextContent = SyncronizeView.this.statusText
						.getText();
				statusTextContent += text;
				SyncronizeView.this.statusText.setText(statusTextContent);
			}
		});
	}

	private void appendStatusText(final String text, final Throwable t) {
		appendStatusText(text);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);
		printWriter.flush();
		stringWriter.flush();
		appendStatusText(stringWriter.toString());
		printWriter.close();
		try {
			stringWriter.close();
		} catch (IOException e) { // Ignored!
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
