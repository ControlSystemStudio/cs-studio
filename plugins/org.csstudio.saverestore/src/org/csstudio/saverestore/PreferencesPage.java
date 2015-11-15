package org.csstudio.saverestore;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 *
 * <code>PreferencesPage</code> is an empty preference page for save and restore to which other pages can be plugged in.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class PreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

    @Override
    public void init(final IWorkbench workbench) {
    }

    @Override
    protected Control createContents(final Composite parent) {
        final Label label = new Label(parent, SWT.NULL);
        label.setText("Use these pages to customize Save and Restore application");
        return label;
    }

}
