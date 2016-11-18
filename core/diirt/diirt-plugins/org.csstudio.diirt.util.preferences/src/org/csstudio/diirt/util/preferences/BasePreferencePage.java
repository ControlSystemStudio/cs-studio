/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.SWTResourceManager;

import lombok.Data;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 16 Nov 2016
 */
abstract class BasePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final Set<BasePreferencePage> DIIRT_PAGES = new HashSet<>();

    private final Map<FieldEditor, Editor> editors = new HashMap<>();

    public BasePreferencePage ( ) {
        DIIRT_PAGES.add(this);
    }

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
     * @param canBeDefaulted {@code true} if the given {@code editor} can be
     *            restored to its default value.
     */
    protected void addField ( FieldEditor editor, Composite parent, boolean canBeDefaulted ) {
        addField(editor, parent, canBeDefaulted, null, null);
    }

    /**
     * Add the given {@code editor} field to this page. The editor's
     * caption foreground will be updated when the editor's value changes.
     *
     * @param fieldEditor The {@link FieldEditor} to be added and updated.
     * @param parent The {@link Composite} owning the given {@code editor}.
     * @param canBeDefaulted {@code true} if the given {@code editor} can be
     *            restored to its default value.
     * @param defaultGetter The {@link Supplier} of the editor's default value.
     *            Can be {@code null} if the editor's caption foreground
     *            should not be updated when the editor's value changes.
     * @param storedGetter The {@link Supplier} of the editor's stored value.
     *            Can be {@code null} if the editor's caption foreground
     *            should not be initially updated.
     */
    protected void addField ( FieldEditor fieldEditor, Composite parent, boolean canBeDefaulted, Supplier<Object> defaultGetter, Supplier<Object> storedGetter ) {

        final IPreferenceStore store = getPreferenceStore();
        final Editor editor = new Editor(fieldEditor, parent, canBeDefaulted, defaultGetter, storedGetter);

        fieldEditor.getLabelControl(parent).setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        fieldEditor.setPage(this);
        fieldEditor.setPreferenceStore(store);
        fieldEditor.load();
        fieldEditor.setPropertyChangeListener(e -> {
            if ( FieldEditor.VALUE.equals(e.getProperty()) ) {
                editor.updateCaptionColor(e.getNewValue());
            }
        });

        editor.updateCaptionColor();
        editors.put(fieldEditor, editor);

    }

    /**
     * Clear the warning message.
     */
    protected void clearWarning ( ) {
        Display.getDefault().syncExec( ( ) -> setMessage(null, NONE));
    }

    @Override
    public void dispose ( ) {

        DIIRT_PAGES.remove(this);

        super.dispose();

    }

    @Override
    public boolean performOk ( ) {

        boolean restart = MessageDialog.openConfirm(getShell(), Messages.BPP_performOk_title, Messages.BPP_performOk_message);

        if ( restart ) {

            editors.keySet().stream().forEach(e -> e.store());
            Job.createSystem(m -> Display.getDefault().asyncExec(() -> PlatformUI.getWorkbench().restart())).schedule(500L);

            return super.performOk();

        } else {
            return false;
        }

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
        Display.getDefault().syncExec( ( ) -> setMessage(message, message != null ? WARNING : NONE));
    }

    @Override
    protected void performApply ( ) {
        editors.keySet().stream().forEach(e -> e.store());
    }

    @Override
    protected void performDefaults ( ) {

        super.performDefaults();

        editors.entrySet().stream().filter(e -> e.getValue().isCanBeDefaulted()).forEach(e -> {

            e.getKey().loadDefault();

            Editor editor = e.getValue();
            Supplier<Object> defaultGetter = editor.getDefaultGetter();

            if ( defaultGetter != null ) {
                editor.updateCaptionColor(defaultGetter.get());
            }

        });

    }

    /**
     * Updates the colors of all editors in the page.
     */
    protected void reloadEditors ( ) {
        editors.entrySet().stream().forEach(e -> {
            e.getKey().load();
            e.getValue().updateCaptionColor();
        });
    }

    /**
     * Updates the colors of all editors inside all DIIRT pages.
     */
    protected final void reloadEditorsForAllPages ( ) {
        DIIRT_PAGES.stream().forEach(p -> p.reloadEditors());
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

        Color captionColor = Objects.equals(defaultValue, currentValue)
                ? SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND)
                : SWTResourceManager.getColor(SWT.COLOR_BLUE);

        Display.getDefault().asyncExec(() -> {

            Label l = editor.getLabelControl(parent);

            if ( !l.isDisposed() ) {
                l.setForeground(captionColor);
            }

        });

    }

    @Data
    private class Editor {

        private final FieldEditor editor;
        private final Composite parent;
        private final boolean canBeDefaulted;
        private final Supplier<Object> defaultGetter;
        private final Supplier<Object> storedGetter;

        void updateCaptionColor ( ) {
            if ( storedGetter != null ) {
                updateCaptionColor(storedGetter.get());
            }
        }

        void updateCaptionColor ( final Object value ) {
            if ( defaultGetter != null ) {
                BasePreferencePage.this.updateCaptionColor(editor, parent, defaultGetter.get(), value);
            }
        }

    }

}
