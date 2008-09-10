package org.csstudio.nams.configurator.views;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.editor.AbstractEditor;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceListener;
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
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * View um die Synchronisation mit dem Hintergrundsystem auszuführen.
 * Verwenendet den {@link SynchronizeService}.
 */
public class SyncronizeView extends ViewPart implements
		ConfigurationBeanServiceListener {

	private static SynchronizeService synchronizeService = null;

	private static ConfigurationBeanService beanService;

	/**
	 * Injiziert den {@link SynchronizeService} für diese View. Must be called
	 * before View is used.
	 */
	public static void staticInjectSynchronizeService(
			final SynchronizeService synchronizeService) {
		SyncronizeView.synchronizeService = synchronizeService;
	}

	/**
	 * Injiziert den {@link ConfigurationBeanService} für diese View. Must be
	 * called before View is used.
	 */
	public static void staticInject(final ConfigurationBeanService beanService) {
		SyncronizeView.beanService = beanService;
		if (instance != null) {
			beanService.addConfigurationBeanServiceListener(instance);
		}
	}

	private static SyncronizeView instance;

	private Text statusText;

	private Button syncButton;

	public SyncronizeView() {
		if (SyncronizeView.synchronizeService == null) {
			throw new RuntimeException(
					"View class is not probably initialised, missing: SynchronizeService!"); //$NON-NLS-1$
		}
		instance = this;
		if (beanService != null) {
			beanService.addConfigurationBeanServiceListener(this);
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
		this.syncButton.setText(Messages.SyncronizeView_sync_button_text);
		this.syncButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
				// will never be called by a button.
			}

			public void widgetSelected(final SelectionEvent e) {
				final Button source = (Button) e.getSource();
				if (source.getSelection()) {
					SyncronizeView.this.statusText.setText(""); //$NON-NLS-1$
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
		this.statusText.setText(Messages.SyncronizeView_initial_status_text);
	}

	protected synchronized void doSynchronize() {
		this.appendStatusText(Messages.SyncronizeView_beginn_sync);

		try {
			SyncronizeView.synchronizeService
					.sychronizeAlarmSystem(new SynchronizeService.Callback() {
						volatile Boolean result = null;

						public void bereiteSynchronisationVor() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_prepare_sync);
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
											Messages.SyncronizeView_failed_prepare_sync,
											t);
							this.buttonFreigben();
						}

						public boolean pruefeObSynchronisationAusgefuehrtWerdenDarf() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_look_for_unsaved_changes);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									result = SyncronizeView.this
											.lookForUnsavedChangesAndDecideAboutSynchrinsationProceeding();
								}
							});
							while (this.result == null) {
								Thread.yield();
							}
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_done);
							return this.result.booleanValue();
						}

						public void sendeNachrichtAnHintergrundSystem() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_send_sync_request);
						}

						public void sendenDerNachrichtAnHintergrundSystemFehlgeschalgen(
								final Throwable t) {
							SyncronizeView.this
									.appendStatusText(
											Messages.SyncronizeView_failed_send_sync_request,
											t);
							this.buttonFreigben();
						}

						public void synchronisationAbgebrochen() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_sync_canceled);
							this.buttonFreigben();
						}

						public void synchronisationsDurchHintergrundsystemsErfolgreich() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_sync_successfull);
							this.buttonFreigben();
							SyncronizeView.this.setChanged(false);
						}

						public void synchronisationsDurchHintergrundsystemsFehlgeschalgen(
								final String fehlertext) {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_backend_failure
											+ fehlertext);
							this.buttonFreigben();
						}

						public void wartetAufAntowrtDesHintergrundSystems() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_waiting_for_backend);
						}

					});
		} catch (final Throwable t) {
			this.appendStatusText(Messages.SyncronizeView_sync_failed, t);
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
			messageBox.setText(Messages.SyncronizeView_unsaved_changes_title);
			messageBox
					.setMessage(Messages.SyncronizeView_unsaved_changes_message1
							+ Messages.SyncronizeView_unsaved_changes_message2
							+ Messages.SyncronizeView_unsaved_changes_message3);
			if (messageBox.open() == SWT.YES) {
				// The User likes to check his changes.
				return false;
			}
		}
		return true;
	}

	private void setChanged(boolean b) {
		if (b) {
			syncButton.setText(">>> "
					+ Messages.SyncronizeView_sync_button_text + " <<<");
			this.showBusy(true);
		} else {
			syncButton.setText(Messages.SyncronizeView_sync_button_text);
			this.showBusy(false);
		}
	}

	@Override
	public void showBusy(boolean busy) {
		super.showBusy(busy);

		IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) getSite()
				.getAdapter(IWorkbenchSiteProgressService.class);
		if (busy) {
			service.incrementBusy();
		} else {
			service.decrementBusy();
		}
	}

	@Override
	public void setFocus() {

	}

	public void onBeanDeleted(IConfigurationBean bean) {
		this.setChanged(true);
	}

	public void onBeanInsert(IConfigurationBean bean) {
		this.setChanged(true);
	}

	public void onBeanUpdate(IConfigurationBean bean) {
		this.setChanged(true);
	}

	public void onConfigurationReload() {

	}

}
