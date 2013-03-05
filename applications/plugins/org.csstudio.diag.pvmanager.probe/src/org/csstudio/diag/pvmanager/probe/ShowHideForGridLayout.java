package org.csstudio.diag.pvmanager.probe;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ShowHideForGridLayout {
	
	public static boolean hide(Composite section) {
		GridData data = (GridData) section.getLayoutData();
		if (data.heightHint != 0) {
			data.heightHint = 0;
			return true;
		}
		return false;
	}
	
	public static boolean show(Composite section) {
		GridData data = (GridData) section.getLayoutData();
		if (data.heightHint != -1) {
			data.heightHint = -1;
			return true;
		}
		return false;
	}

	public static MenuItem createShowHideMenuItem(Menu menu, final Composite composite) {
		final MenuItem menuItem = new MenuItem(menu, SWT.CHECK);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (menuItem.getSelection()) {
					show(composite);
				} else {
					hide(composite);
				}
				composite.getParent().layout();
			}
		});
		return menuItem;
	}
	
	
}
