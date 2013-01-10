/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.ui.util.swt.stringtable.RowEditDialog;
import org.csstudio.ui.util.swt.stringtable.StringTableEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/** Field Editor for scripts' list of description and command.
 *
 *  <p>The preference is internally stored as a string,
 *  but we encode/decode a list of {@link ScriptInfo} entries
 *  in that list and edit it with a {@link StringTableEditor}
 *  @author Kay Kasemir
 */
public class ScriptInfoFieldEditor extends FieldEditor
{
	final private List<String[]> texts = new ArrayList<String[]>();
	private StringTableEditor table_editor;
	
	public ScriptInfoFieldEditor(final Composite parent)
	{
		init(Preferences.PREF_SCRIPTS, Messages.PrefEdit_Scripts);
		createControl(parent);
	}

    /** {@inheritDoc} */
    @Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns)
	{
        final Label label = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        label.setLayoutData(gd);

        final RowEditDialog editor = new ScriptInfoEditor(parent.getShell());
        table_editor = new StringTableEditor(parent,
        	new String[] { Messages.PrefEdit_Description, Messages.PrefEdit_Command },
        	new boolean[] { true, true },
        	texts,
        	editor,
        	new int[] { 100, 200 });
        table_editor.setLayoutData(
        		new GridData(SWT.FILL, SWT.FILL, true, true, numColumns, 1));
	}

    /** {@inheritDoc} */
	@Override
	public int getNumberOfControls()
	{
		return 1;
	}

	/** {@inheritDoc} */
    @Override
    protected void adjustForNumColumns(final int numColumns)
    {
        GridData gd = (GridData)table_editor.getLayoutData();
        gd.horizontalSpan = numColumns;
        gd = (GridData)getLabelControl().getLayoutData();
        gd.horizontalSpan = numColumns;
    }

    /** {@inheritDoc} */
    @Override
	protected void doLoad()
    {
		final String encoded = getPreferenceStore().getString(Preferences.PREF_SCRIPTS);
    	loadSettings(encoded);
	}

    /** {@inheritDoc} */
    @Override
	protected void doLoadDefault()
    {
		final String encoded = getPreferenceStore().getDefaultString(Preferences.PREF_SCRIPTS);
    	loadSettings(encoded);
	}

    /** Load preference settings into GUI
     *  @param encoded Encoded settings
     */
	private void loadSettings(final String encoded)
    {
    	if (table_editor == null)
    		return;
	    try
    	{
			final ScriptInfo[] infos = Preferences.decode(encoded);
			texts.clear();
			for (ScriptInfo info : infos)
				texts.add(new String[] { info.getDescription(), info.getScript() });
			table_editor.updateInput(texts);
    	}
    	catch (Exception ex)
    	{
    		MessageDialog.openError(table_editor.getShell(),
    				Messages.Error,
    				NLS.bind(Messages.PreferenceErrorFmt, ex.getMessage()));
    	}
    }


    /** {@inheritDoc} */
    @Override
	protected void doStore()
    {
    	final ScriptInfo[] infos = new ScriptInfo[texts.size()];
    	for (int i=0; i<infos.length; ++i)
    		infos[i] = new ScriptInfo(texts.get(i)[0], texts.get(i)[1]);
    	final String encoded = Preferences.encode(infos);
    	getPreferenceStore().setValue(Preferences.PREF_SCRIPTS, encoded);
	}
}
