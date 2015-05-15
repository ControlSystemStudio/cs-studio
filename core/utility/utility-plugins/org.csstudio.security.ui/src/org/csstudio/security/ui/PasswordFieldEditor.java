/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/** Preference editor for 'passwords'
 *
 *  <p>If no secure preference is set, read the 'normal' preference.
 *  The 'default' value is also fetched from the normal preference store,
 *  to allow setting a default in plugin_custommization.ini etc.
 *  Entered passwords, however, are always writes to the secure preference store.
 *
 *  <p>Corollary: You should use {@link SecurePreferences} resp.
 *  {@link ISecurePreferences} to read preferences,
 *  falling back to the normal preference store when
 *  nothing found in the secure store.
 *
 *  <p>Hides the actual text, uses secure preference store.
 *
 *  TODO Check on Linux if copy/paste allows users to peek passwords.
 *
 *  @author Kay Kasemir
 *  @author Xihui Chen - Original org.csstudio.auth.ui.security.PasswordFieldEditor
 */
@SuppressWarnings("nls")
public class PasswordFieldEditor extends FieldEditor
{
    // Based on Eclipse 3.7.2 StringFieldEditor

    private ISecurePreferences preferences;

    private Text textField;

    private String oldValue;

    /** Initialize
     *  @param plugin_id Plugin ID used to locate preferences
     *  @param key Preference key
     *  @param label GUI Label
     *  @param parent Parent widget
     */
    public PasswordFieldEditor(final String plugin_id, final String key,
            final String label, final Composite parent)
    {
        super(key, label, parent);
        try
        {
            preferences = SecurePreferences.getSecurePreferences().node(plugin_id);
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName())
                .log(Level.WARNING, "Cannot access preferences", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getNumberOfControls()
    {
        return 2;
    }

    /** {@inheritDoc} */
    @Override
    protected void adjustForNumColumns(final int numColumns)
    {
        final GridData gd = (GridData) textField.getLayoutData();
        gd.horizontalSpan = numColumns - 1;
        // We only grab excess space if we have to
        // If another field editor has more columns then
        // we assume it is setting the width.
        gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
    }

    /** {@inheritDoc} */
    @Override
    protected void doFillIntoGrid(final Composite parent, final int numColumns)
    {
        getLabelControl(parent);

        textField = new Text(parent, SWT.BORDER | SWT.PASSWORD);
        textField.setFont(parent.getFont());

        final GridData gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        textField.setLayoutData(gd);
        textField.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                textField = null;
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected void doLoad()
    {
        if (textField == null)
            return;
        try
        {
            oldValue = preferences.get(getPreferenceName(), null);
            if (oldValue == null)
                oldValue = getPreferenceStore().getString(getPreferenceName());
            if (oldValue == null)
                oldValue = "";
            textField.setText(oldValue);
        }
        catch (Throwable ex)
        {
            getPage().setErrorMessage("Cannot read " + getPreferenceName());
            Logger.getLogger(getClass().getName())
                .log(Level.WARNING, "Cannot read preferences", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doLoadDefault()
    {
        if (textField == null)
            return;
        // Load default from ordinary preferences
        oldValue = getPreferenceStore().getDefaultString(getPreferenceName());
        if (oldValue == null)
            oldValue = "";
        textField.setText(oldValue);
        // Remove what might be in secure preferences,
        // since that would be used instead of the default that
        // was just requested
        try
        {
            preferences.put(getPreferenceName(), null, false);
            preferences.flush();
        }
        catch (Throwable ex)
        {
            getPage().setErrorMessage("Cannot clear value for " + getPreferenceName());
            Logger.getLogger(getClass().getName())
                .log(Level.WARNING, "Cannot clear value for " + getPreferenceName(), ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doStore()
    {
        if (textField.getText().equals(oldValue))
            return;
        try
        {
            preferences.put(getPreferenceName(), textField.getText(), true);
            preferences.flush();
        }
        catch (Throwable ex)
        {
            getPage().setErrorMessage("Cannot write " + getPreferenceName());
            Logger.getLogger(getClass().getName())
                .log(Level.WARNING, "Cannot write preferences", ex);
        }
    }
}
