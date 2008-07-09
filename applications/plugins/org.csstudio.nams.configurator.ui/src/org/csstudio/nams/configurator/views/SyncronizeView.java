package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.service.synchronize.SynchronizeService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * View um die Synchronisation mit dem Hintergrundsystem auszuführen.
 * Verwenendet den {@link SynchronizeService}.
 */
public class SyncronizeView extends ViewPart {

	private static SynchronizeService synchronizeService = null;
	private Text statusText;

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
		composite.setLayout(new FillLayout(SWT.VERTICAL|SWT.HORIZONTAL));
		
		Composite innerComposite = new Composite(composite, SWT.NONE);
		innerComposite.setLayout(new GridLayout(1, false));
		
		Button syncButton = new Button(innerComposite, SWT.TOGGLE);
		syncButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		syncButton.setText("Perform synchronization of background-system");
		syncButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Button source = (Button)e.getSource();
				if( source.getSelection() ) {
					statusText.setText("Beginning syncronization...");
				} else {
					// deselect nur aus dem code erlaubt!
					source.setSelection(true);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// will never be called by a button.
			}
			
		});
//		
//		Composite innerTextComposite = new Composite(innerComposite, SWT.NONE);
//		innerTextComposite.setLayout(new FillLayout(SWT.VERTICAL|SWT.HORIZONTAL));
		
		
		statusText = new Text(innerComposite, SWT.SCROLL_LINE | SWT.V_SCROLL | SWT.H_SCROLL);
		statusText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		statusText.setEditable(false);
		statusText.setText("Push \"Perform synchronization of background-system\" to begin syncronization.");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
