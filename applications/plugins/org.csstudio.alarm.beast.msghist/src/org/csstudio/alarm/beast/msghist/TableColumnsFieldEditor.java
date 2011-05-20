/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.ui.util.swt.stringtable.StringTableEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/** Preference dialog field editor for property columns
 *  @author Kay Kasemir
 */
public class TableColumnsFieldEditor extends FieldEditor
{
	private List<String> columns;
	private Label label;
	private StringTableEditor editor;

	/** Initialize
	 *  @param parent Parent Widget
	 */
	public TableColumnsFieldEditor(Composite parent)
	{
		super(Preferences.COLUMNS, Messages.TableColumnsEditor_Title, parent);
	}

	/** Indicate that we need at least one column */
	@Override
	public int getNumberOfControls()
	{
		return 1;
	}

	/** {@inheritDoc} */
	@Override
	protected void adjustForNumColumns(int numColumns)
	{
		GridData gd = (GridData)label.getLayoutData();
		gd.horizontalSpan = numColumns;
		gd = (GridData)editor.getLayoutData();
		gd.horizontalSpan = numColumns;
	}

	/** {@inheritDoc} */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		label = new Label(parent, 0);
		label.setText(Messages.TableColumnsEditor_Columns);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		label.setLayoutData(gd);

		columns = new ArrayList<String>();
		editor = new StringTableEditor(parent, columns);
		editor.setToolTipText(Messages.TableColumnsEditor_TT);
		gd = new GridData();
		gd.horizontalSpan = numColumns;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		editor.setLayoutData(gd);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("nls")
    @Override
	protected void doLoad()
	{
		try
		{
			final String[] column_prefs =
					Preferences.getColumnPreferences();
			updateColumnPrefs(column_prefs);
		}
		catch (Exception ex)
		{
		    Activator.getLogger().log(Level.WARNING, "Preference Load error", ex);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doLoadDefault()
	{
		final String pref_text =
			getPreferenceStore().getDefaultString(getPreferenceName());
		final String[] column_prefs =
			Preferences.decodeRawColumnPreferences(pref_text);
		updateColumnPrefs(column_prefs);
	}

	/** Update the column preferences shown in the GUI
	 *  @param column_prefs Column prefs to show
	 */
	private void updateColumnPrefs(final String[] column_prefs)
	{
		columns.clear();
		for (String pref : column_prefs)
			columns.add(pref);
		editor.refresh();
	}

	/** {@inheritDoc} */
	@Override
	protected void doStore()
	{
		final String pref_text = Preferences.encodeRawColumnPrefs(
				(String[]) columns.toArray(new String[columns.size()]));
		getPreferenceStore().setValue(getPreferenceName(), pref_text);
	}
}
