package org.csstudio.sds.ui.internal.preferences;

import org.csstudio.sds.ui.internal.localization.Messages;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class RulesPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected final Control createContents(final Composite parent) {
        Label label = new Label(parent, SWT.NULL);
        label.setText(Messages.CSSApplicationsPreferencePage_MESSAGE);
        return label;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void init(final IWorkbench workbench) {
        // nothing to do here
    }
}
