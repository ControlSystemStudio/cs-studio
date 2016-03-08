/*******************************************************************************
 * Copyright (c) 2010, 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.clock.preferences;

import org.csstudio.utility.clock.Messages;
import org.csstudio.utility.clock.Plugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Clock prefs.
 *  @author Kay Kasemir
 */
public class PreferencePage extends FieldEditorPreferencePage
                implements IWorkbenchPreferencePage
{
    /** Preference ID (also used in preferences.ini) */
    final private static  String P_HOURS = "hours"; //$NON-NLS-1$

    final public static int DEFAULT_HOURS = 25;

    /** Minimum value */
    final private static int min = 24;

    /** Maximum value */
    final private static int max = 35;

    /** Initialize */
    public PreferencePage()
    {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Plugin.ID));
        setMessage(Messages.PreferencePage_Title);
    }

    /** {@inheritDoc} */
    @Override
    public void init(IWorkbench workbench)
    { /* NOP */ }

    /** Creates the field editors.
     *  Each one knows how to save and restore itself.
     */
    @Override
    public void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();
        final IntegerFieldEditor hour_editor =
            new IntegerFieldEditor(P_HOURS, Messages.PreferencePage_Hours, parent);
        hour_editor.setErrorMessage(NLS.bind(Messages.PreferencePage_ErrorMsg,
                                             min, max));
        hour_editor.setValidRange(min, max);
        addField(hour_editor);
    }
}
