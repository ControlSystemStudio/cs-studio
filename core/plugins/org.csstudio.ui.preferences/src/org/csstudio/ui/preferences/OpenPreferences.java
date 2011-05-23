/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.preferences;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;

/** Open preference GUI
 *  @author Kay Kasemir
 */
public class OpenPreferences extends AbstractHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        final Shell shell = HandlerUtil.getActiveShell(event);
        final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(shell, null, null, null);
        dialog.open();
        return null;
    }
}
