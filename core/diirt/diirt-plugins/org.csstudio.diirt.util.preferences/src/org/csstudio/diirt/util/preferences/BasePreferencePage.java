/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import java.util.Objects;
import java.util.function.Supplier;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wb.swt.SWTResourceManager;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 16 Nov 2016
 */
abstract class BasePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    /**
     * Initialize the preference page.
     */
    @Override
    public void init ( IWorkbench arg0 ) {
    }

    /**
     * Add the given {@code editor} field to this page.
     *
     * @param editor The {@link FieldEditor} to tbe added and updated.
     * @param parent The {@link Composite} owning the given {@code editor}.
     */
    protected void addField ( FieldEditor editor, Composite parent ) {
        addField(editor, parent, null, null);
    }

    /**
     * Add the given {@code editor} field to this page. The editor's
     * caption foreground will be updated when the editor's value changes.
     *
     * @param editor The {@link FieldEditor} to tbe added and updated.
     * @param parent The {@link Composite} owning the given {@code editor}.
     * @param defaultGetter The {@link Supplier} of the editor's default value.
     *            Can be {@code null} if the editor's caption foreground
     *            should not be updated when the editor's value changes.
     * @param storedGetter The {@link Supplier} of the editor's stored value.
     *            Can be {@code null} if the editor's caption foreground
     *            should not be initially updated.
     */
    protected void addField ( FieldEditor editor, Composite parent, Supplier<Object> defaultGetter, Supplier<Object> storedGetter ) {

        editor.getLabelControl(parent).setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        editor.setPage(this);
        editor.setPreferenceStore(getPreferenceStore());
        editor.load();

        if ( defaultGetter != null ) {

            editor.setPropertyChangeListener(e -> {
                if ( FieldEditor.VALUE.equals(e.getProperty()) ) {
                    updateCaptionColor(editor, parent, defaultGetter.get(), e.getNewValue());
                }
            });

            if ( storedGetter != null ) {
                updateCaptionColor(editor, parent, defaultGetter.get(), storedGetter.get());
            }

        }

    }

    /**
     * Clear the warning message.
     */
    protected void clearWarning ( ) {
        Display.getDefault().asyncExec( ( ) -> setMessage(null, NONE));
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore ( ) {
        return DIIRTPreferencesPlugin.get().getPreferenceStore();
    }

    /**
     * Displays a warning message.
     *
     * @param message The message to be displayed.
     */
    protected void notifyWarning ( final String message ) {
        Display.getDefault().asyncExec( ( ) -> setMessage(message, message != null ? WARNING : NONE));
    }

    /**
     * Updates the color of the given {@code editor}'s caption, depending on
     * the current value compared with the default one. If they are equals, the
     * caption foreground color will be {@link SWT#COLOR_WIDGET_FOREGROUND},
     * otherwise {@link SWT#COLOR_DARK_BLUE}.
     *
     * @param editor The {@link FieldEditor} whose caption's color must be
     *            updated.
     * @param parent The {@link Composite} owning the given {@code editor}.
     * @param defaultValue The {@code editor}'s default value.
     * @param currentValue The {@code editor}'s current value.
     */
    protected void updateCaptionColor ( FieldEditor editor, Composite parent, Object defaultValue, Object currentValue ) {
        editor.getLabelControl(parent).setForeground(
            !Objects.equals(defaultValue, currentValue)
          ? SWTResourceManager.getColor(SWT.COLOR_BLUE)
          : SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
    }

}
