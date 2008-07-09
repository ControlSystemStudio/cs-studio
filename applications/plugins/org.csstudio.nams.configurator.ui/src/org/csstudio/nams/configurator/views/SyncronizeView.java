package org.csstudio.nams.configurator.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class SyncronizeView extends ViewPart {

	public SyncronizeView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		new Label(parent, SWT.NONE).setText("Hallo Welt! Hier wird bald was passieren... ;-)");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
