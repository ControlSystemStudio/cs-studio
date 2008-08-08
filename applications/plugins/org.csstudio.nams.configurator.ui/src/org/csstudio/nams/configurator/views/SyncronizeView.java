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

	/**
	 * Injiziert den {@link SynchronizeService} für diese View. Must be called
	 * before View is used.
	 */
	public static void staticInjectSynchronizeService(
			final SynchronizeService synchronizeService) {
		SyncronizeView.synchronizeService = synchronizeService;
	}

	private Text statusText;

	private Button syncButton;

	public SyncronizeView() {
		if (SyncronizeView.synchronizeService == null) {
			throw new RuntimeException(
					"View class is not probably initialised, missing: SynchronizeService!");
		}
	}

	private void appendStatusText(final String text) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				SyncronizeView.this.statusText.append(text);
			}
		});
	}

	private void appendStatusText(final String text, final Throwable t) {
		this.appendStatusText(text);
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);
		printWriter.flush();
		stringWriter.flush();
		this.appendStatusText(stringWriter.toString());
		printWriter.close();
		try {
			stringWriter.close();
		} catch (final IOException e) { // Ignored!
		}
	}

	@Override
	public void createPartControl(final Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL | SWT.HORIZONTAL));

		final Composite innerComposite = new Composite(composite, SWT.NONE);
		innerComposite.setLayout(new GridLayout(1, false));

		this.syncButton = new Button(innerComposite, SWT.TOGGLE);
		this.syncButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		this.syncButton.setText("Perform synchronization of background-system");
		this.syncButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
				// will never be called by a button.
			}

			public void widgetSelected(final SelectionEvent e) {
				final Button source = (Button) e.getSource();
				if (source.getSelection()) {
					SyncronizeView.this.statusText.setText("");
					SyncronizeView.this.doSynchronize();
				} else {
					// deselect nur aus dem code erlaubt!
					source.setSelection(true);
				}
			}

		});

		this.statusText = new Text(innerComposite, SWT.SCROLL_LINE
				| SWT.V_SCROLL | SWT.H_SCROLL);
		this.statusText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		this.statusText.setEditable(false);
		this.statusText
				.setText("Push \"Perform synchronization of background-system\" to begin syncronization.");
	}

	protected synchronized void doSynchronize() {
		this.appendStatusText("Beginning synchronization...\n");

		try {
			SyncronizeView.synchronizeService
					.sychronizeAlarmSystem(new SynchronizeService.Callback() {
						volatile Boolean result = null;

						public void bereiteSynchronisationVor() {
							SyncronizeView.this
									.appendStatusText("Preparing synchronization...\n");
						}

						private void buttonFreigben() {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									SyncronizeView.this.syncButton
											.setSelection(false);
								}
							});
						}

						public void fehlerBeimVorbereitenDerSynchronisation(
								final Throwable t) {
							SyncronizeView.this
									.appendStatusText(
											"Failed to prepare synchronization.\nReason:",
											t);
							this.buttonFreigben();
						}

						public boolean pruefeObSynchronisationAusgefuehrtWerdenDarf() {
							SyncronizeView.this
									.appendStatusText("Looking for unsaved changes...");
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									result = SyncronizeView.this
											.lookForUnsavedChangesAndDecideAboutSynchrinsationProceeding();
								}
							});
							while (this.result == null) {
								Thread.yield();
							}
							SyncronizeView.this.appendStatusText("Done.\n");
							return this.result.booleanValue();
						}

						public void sendeNachrichtAnHintergrundSystem() {
							SyncronizeView.this
									.appendStatusText("Sending sychronization request to back-end-system...\n");
						}

						public void sendenDerNachrichtAnHintergrundSystemFehlgeschalgen(
								final Throwable t) {
							SyncronizeView.this
									.appendStatusText(
											"Failed to send sychronization request to back-end-system.\nReason:",
											t);
							this.buttonFreigben();
						}

						public void synchronisationAbgebrochen() {
							SyncronizeView.this
									.appendStatusText("Sychronization has been canceled.\n");
							this.buttonFreigben();
						}

						public void synchronisationsDurchHintergrundsystemsErfolgreich() {
							SyncronizeView.this
									.appendStatusText("\n\nSynchronization successfully finished!");
							this.buttonFreigben();
						}

						public void synchronisationsDurchHintergrundsystemsFehlgeschalgen(
								final String fehlertext) {
							SyncronizeView.this
									.appendStatusText("Back-end-system reporting failure of sychronization.\nReported reason: "
											+ fehlertext);
							this.buttonFreigben();
						}

						public void wartetAufAntowrtDesHintergrundSystems() {
							SyncronizeView.this
									.appendStatusText("Waiting for sychronization commitment to back-end-system...\n");
						}

					});
		} catch (final Throwable t) {
			this.appendStatusText("\n\nSynchronization failed!\nReason: ", t);
		}
	}

	protected boolean lookForUnsavedChangesAndDecideAboutSynchrinsationProceeding() {
		boolean hasADirtyNamsEditor = false;
		final IWorkbenchPage[] pages = this.getSite().getWorkbenchWindow()
				.getPages();
		for (final IWorkbenchPage page : pages) {
			final IEditorPart[] dirtyEditors = page.getDirtyEditors();
			for (final IEditorPart editor : dirtyEditors) {
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
			final MessageBox messageBox = new MessageBox(this.getSite()
					.getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
			messageBox.setText("Unsaved NAMS-Editors!");
			messageBox
					.setMessage("You still have unsaved changes in NAMS-Editors."
							+ " This changes will be ignored by synchronization process.\n"
							+ "Do you want to review your changes before continuing?");
			if (messageBox.open() == SWT.YES) {
				// The User likes to check his changes.
				return false;
			}
		}
		return true;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
