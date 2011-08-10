
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
import org.eclipse.swt.graphics.Color;
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
public class SyncronizeView extends ViewPart implements
		ConfigurationBeanServiceListener {

	private static SynchronizeService _synchronizeService = null;

	private static ConfigurationBeanService _beanService;

	/**
	 * Injiziert den {@link SynchronizeService} für diese View. Must be called
	 * before View is used.
	 */
	public static void staticInjectSynchronizeService(
			final SynchronizeService synchronizeService) {
		SyncronizeView._synchronizeService = synchronizeService;
	}

	/**
	 * Injiziert den {@link ConfigurationBeanService} für diese View. Must be
	 * called before View is used.
	 */
	public static void staticInject(final ConfigurationBeanService beanService) {
		SyncronizeView._beanService = beanService;
		if (instance != null) {
			beanService.addConfigurationBeanServiceListener(instance);
		}
	}

	private static SyncronizeView instance;

	private Text statusText;

	private Button syncButton;

	private Color buttonColor;

	public SyncronizeView() {
		if (SyncronizeView._synchronizeService == null) {
			throw new RuntimeException(
					"View class is not probably initialised, missing: SynchronizeService!"); //$NON-NLS-1$
		}
		instance = this;
		if (_beanService != null) {
			_beanService.addConfigurationBeanServiceListener(this);
		}
	}

	private void appendStatusText(final String text) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
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
		this.buttonColor = syncButton.getBackground();
		this.syncButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		this.syncButton.setText(Messages.SyncronizeView_sync_button_text);
		this.syncButton.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetDefaultSelected(final SelectionEvent e) {
				// will never be called by a button.
			}

			@Override
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
			SyncronizeView._synchronizeService
					.sychronizeAlarmSystem(new SynchronizeService.Callback() {
						volatile Boolean result = null;

						@Override
                        public void bereiteSynchronisationVor() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_prepare_sync);
						}

						private void buttonFreigben() {
							Display.getDefault().asyncExec(new Runnable() {
								@Override
                                public void run() {
									SyncronizeView.this.syncButton
											.setSelection(false);
								}
							});
						}

						@Override
                        public void fehlerBeimVorbereitenDerSynchronisation(
								final Throwable t) {
							SyncronizeView.this
									.appendStatusText(
											Messages.SyncronizeView_failed_prepare_sync,
											t);
							this.buttonFreigben();
						}

						@Override
                        public boolean pruefeObSynchronisationAusgefuehrtWerdenDarf() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_look_for_unsaved_changes);
							Display.getDefault().asyncExec(new Runnable() {
								@Override
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

						@Override
                        public void sendeNachrichtAnHintergrundSystem() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_send_sync_request);
						}

						@Override
                        public void sendenDerNachrichtAnHintergrundSystemFehlgeschalgen(
								final Throwable t) {
							SyncronizeView.this
									.appendStatusText(
											Messages.SyncronizeView_failed_send_sync_request,
											t);
							this.buttonFreigben();
						}

						@Override
                        public void synchronisationAbgebrochen() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_sync_canceled);
							this.buttonFreigben();
						}

						@Override
                        public void synchronisationsDurchHintergrundsystemsErfolgreich() {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_sync_successfull);
							this.buttonFreigben();
							SyncronizeView.this.showBusy(false);
						}

						@Override
                        public void synchronisationsDurchHintergrundsystemsFehlgeschalgen(
								final String fehlertext) {
							SyncronizeView.this
									.appendStatusText(Messages.SyncronizeView_backend_failure
											+ fehlertext);
							this.buttonFreigben();
						}

						@Override
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

	@Override
	public void showBusy(boolean busy) {
		super.showBusy(busy);

		if (busy) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
                public void run() {
					syncButton.setBackground(Display.getDefault()
							.getSystemColor(SWT.COLOR_RED));
					syncButton
							.setText(">>> "
									+ Messages.SyncronizeView_sync_button_text
									+ " <<<");
				}
			});
		} else {
			Display.getDefault().syncExec(new Runnable() {
				@Override
                public void run() {
					syncButton.setBackground(buttonColor);
					syncButton
							.setText(Messages.SyncronizeView_sync_button_text);
				}
			});

		}
	}

	@Override
	public void setFocus() {
	    // Nothing to do
	}

	@Override
    public void onBeanDeleted(IConfigurationBean bean) {
		this.showBusy(true);
	}

	@Override
    public void onBeanInsert(IConfigurationBean bean) {
		this.showBusy(true);
	}

	@Override
    public void onBeanUpdate(IConfigurationBean bean) {
		this.showBusy(true);
	}

	@Override
    public void onConfigurationReload() {
	    // Not used yet
	}
}
