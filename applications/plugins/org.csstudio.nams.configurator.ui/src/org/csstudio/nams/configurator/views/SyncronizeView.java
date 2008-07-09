package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.service.synchronize.SynchronizeService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
		if( synchronizeService == null )
		{
			throw new RuntimeException("View class is not probably initialised, missing: SynchronizeService!");
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		
		
		Button syncButton = new Button(parent, SWT.TOGGLE);
		syncButton.setText("Perform synchronization of background-system");
		syncButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Button source = (Button)e.getSource();
				if( source.getSelection() ) {
					System.out.println("selected");
				} else {
					// deselect nur aus dem code erlaubt!
					source.setSelection(true);
					System.out.println("not selected");
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		// TODO Auto-generated method stub
		new Label(parent, SWT.NONE)
				.setText("Hallo Welt! Hier wird bald was passieren... ;-)");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
