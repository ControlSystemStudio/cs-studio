/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.rdbshell;

import java.util.ArrayList;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/** View for RDB shell.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBShellView extends ViewPart
{
    /** View ID registered in plugin.xml */
    public static final String ID = "org.csstudio.debugging.rdbshell.view";
    
    // Memento tags
    final private static String MEMENTO_QUERY = "QUERY";
    final private static String MEMENTO_USER = "USER";
    final private static String MEMENTO_URL = "URL";
    private IMemento memento;

    // GUI elements
    private Text url;
    private Text user;
    private Text password;
    private Text query;
    private Button run;
    private Table result;


    /** {@inheritDoc} */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.memento = memento;
    }

    /** {@inheritDoc} */
    @Override
    public void saveState(IMemento memento)
    {
        super.saveState(memento);
        memento.putString(MEMENTO_URL, url.getText().trim());
        memento.putString(MEMENTO_USER, user.getText().trim());
        memento.putString(MEMENTO_QUERY, query.getText().trim());
    }

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        createGUI(parent);

        // Initialize from preferences (Thanks to Lana.Abadie@iter.org)
        final IPreferencesService prefs = Platform.getPreferencesService();
        url.setText(prefs.getString(Activator.ID, MEMENTO_URL, "jdbc:oracle:thin:@//HOST:1521/DB", null));
        user.setText(prefs.getString(Activator.ID, MEMENTO_USER, "user", null));
        query.setText(prefs.getString(Activator.ID, MEMENTO_QUERY, "select * from dual", null));
        
        // Override with values from last invocation
        if (memento != null)
        {
            String saved = memento.getString(MEMENTO_URL);
            if (saved != null)
                url.setText(saved);
            saved = memento.getString(MEMENTO_USER);
            if (saved != null)
                user.setText(saved);
            saved = memento.getString(MEMENTO_QUERY);
            if (saved != null)
                query.setText(saved);
        }
        
        run.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                runQuery();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        query.setFocus();
    }

    /** Create GUI elements
     *  @param parent Parent widget
     */
    private void createGUI(final Composite parent)
    {
        parent.setLayout(new FillLayout());

        SashForm sash = new SashForm(parent, SWT.VERTICAL | SWT.SMOOTH);
        sash.setLayout(new FillLayout());

        addQuerySashElement(sash);
        addResultSashElement(sash);

        sash.setWeights(new int[] { 30, 70 });
    }
        
    /** Create sash elements for query
     *  @param parent Parent sash
     */
    private void addQuerySashElement(SashForm sash)
    {
        final Composite parent = new Composite(sash, SWT.BORDER);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 7;
        parent.setLayout(layout);

        // URL: ____ User: ____ Password: ____ [Run]
        // Query:                        
        // -----------------------------------------
        // -----------------------------------------
        Label l = new Label(parent, 0);
        l.setText("URL:");
        l.setLayoutData(new GridData());
        
        url = new Text(parent, SWT.BORDER);
        url.setToolTipText("Enter database URL");
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.minimumWidth = 200;
        url.setLayoutData(gd);
        
        l = new Label(parent, 0);
        l.setText("User:");
        l.setLayoutData(new GridData());
        
        user = new Text(parent, SWT.BORDER);
        user.setToolTipText("Enter database user name");
        user.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        l = new Label(parent, 0);
        l.setText("Password:");
        l.setLayoutData(new GridData());
        
        password = new Text(parent, SWT.BORDER | SWT.PASSWORD);
        password.setToolTipText("Enter database user password");
        password.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        run = new Button(parent, SWT.PUSH);
        run.setText("Run");
        run.setLayoutData(new GridData());

        // New row
        l = new Label(parent, 0);
        l.setText("Query:");
        l.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));
        
        // New row
        query = new Text(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
        query.setToolTipText("Enter SQL commands");
        query.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
    }

    /** Create sash elements for result
     *  @param parent Parent sash
     */
    private void addResultSashElement(SashForm sash)
    {
        final Composite parent = new Composite(sash, SWT.BORDER);
        parent.setLayout(new FillLayout());
        
        result = new Table(parent,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        result.setHeaderVisible(true);
        result.setLinesVisible(true);
    }

    /** Execute the query */
    private void runQuery()
    {
        // Clear result table
        result.removeAll();
        while (result.getColumnCount() > 0)
            result.getColumn(0).dispose();

        // Run query
        SQLExecutor rdb;
        try
        {
            rdb = new SQLExecutor(url.getText().trim(), user.getText().trim(),
                    password.getText().trim());
        }
        catch (Exception ex)
        {
            MessageDialog.openError(getSite().getShell(),
                "Connection Error",
                NLS.bind("Cannot connect to database:\n{0}", ex.getMessage()));
           return;
        }
        
        ArrayList<String[]> rows;
        try
        {
            rows = rdb.execute(query.getText());
        }
        catch (Exception ex)
        {
            MessageDialog.openError(getSite().getShell(),
                "SQL Error",
                NLS.bind("Error in SQL statement:\n{0}", ex.getMessage()));
           return;
        }
        finally
        {
            rdb.close();
        }
            
        if (rows.size() < 1)
            return;
            
        // Display: Create columns for each header
        final String[] headers = rows.get(0);
        for (String header : headers)
        {
            final TableColumn col = new TableColumn(result, 0);
            col.setText(header);
            col.setMoveable(true);
            col.setResizable(true);
            col.setWidth(result.getBounds().width / headers.length);
        }
        // Populate rows
        for (int r = 1; r < rows.size(); r++)
        {
            final String row[] = rows.get(r);
            final TableItem item = new TableItem(result, 0);
            for (int i = 0; i < row.length; i++)
                item.setText(i, row[i]);
        }
    }
}
