package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.service.synchronize.SynchronizeService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
			SynchronizeService synchronizeService) {
		SyncronizeView.synchronizeService = synchronizeService;
	}

	public SyncronizeView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		new Label(parent, SWT.NONE)
				.setText("Hallo Welt! Hier wird bald was passieren... ;-)");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
