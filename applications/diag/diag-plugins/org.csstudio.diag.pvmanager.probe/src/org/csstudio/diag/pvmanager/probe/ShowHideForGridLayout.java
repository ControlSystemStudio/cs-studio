package org.csstudio.diag.pvmanager.probe;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ShowHideForGridLayout {

    public static boolean setShow(Composite section, boolean show) {
        if (show) {
            return show(section);
        } else {
            return hide(section);
        }
    }

    public static boolean hide(Composite section) {
        GridData data = (GridData) section.getLayoutData();
        if (data.exclude == false || data.heightHint != 0 || section.getVisible()) {
            data.heightHint = 0;
            data.exclude = true;
            section.setVisible(false);
            return true;
        }
        return false;
    }

    public static boolean show(Composite section) {
        GridData data = (GridData) section.getLayoutData();
        if (data.exclude == true || data.heightHint != -1 || !section.getVisible()) {
            data.heightHint = -1;
            data.exclude = false;
            section.setVisible(true);
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
                composite.layout();
                composite.getParent().layout();
            }
        });
        return menuItem;
    }


}
