/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.jmsmonitor;

import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.csstudio.ui.util.MinSizeTableColumnLayout;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPartSite;

/** JMS monitor GUI
 *  @author Kay Kasemir
 */
public class GUI implements ModelListener
{
    /** JMS Connection parameters */
    final private String url, user, password;

    private Model model = null;

    private TableViewer table_viewer;

    private Text topic;

    private Button clear;

    private Label server_name;

    /** Initialize
     *  @param url JMS server URL
     *  @param user JMS user name or <code>null</code>
     *  @param password JMS password or <code>null</code>
     *  @param parent Parent widget
     */
    public GUI(IWorkbenchPartSite site, final String url, final String user, final String password,
               final Composite parent)
    {
        this.url = url;
        this.user = user;
        this.password = password;

        createGUI(parent);

        topic.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                setTopic(getTopic());
            }
        });

        clear.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (model != null)
                    model.clear();
            }
        });

        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                if (model != null)
                    model.close();
            }
        });

        // Publish the current selection to the site
        // (to allow context menu extensions based on the selection)
        if (site != null)
            site.setSelectionProvider(table_viewer);
    }

    /** Create the GUI elements
     *  @param parent Parent widget
     */
    private void createGUI(final Composite parent)
    {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);

        // URL:   ____url ____
        Label l = new Label(parent, 0);
        l.setText(Messages.URLLabel);
        l.setLayoutData(new GridData());

        l = new Label(parent, 0);
        l.setText(NLS.bind(Messages.URLLabelFmt, new Object[] { url, user, password }));
        l.setLayoutData(new GridData(SWT.LEFT, 0, true, false, 2, 1));

        // Topic: ____topic ____ [Clear]
        l = new Label(parent, 0);
        l.setText(Messages.TopicLabel);
        l.setLayoutData(new GridData());

        topic = new Text(parent, SWT.BORDER);
        topic.setToolTipText(Messages.Topic_TT);
        topic.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        clear = new Button(parent, SWT.PUSH);
        clear.setText(Messages.Clear);
        clear.setToolTipText(Messages.ClearTT);
        clear.setLayoutData(new GridData());

        // Server: ____server_name____
        l = new Label(parent, 0);
        l.setText(Messages.Server);
        l.setLayoutData(new GridData());

        server_name = new Label(parent, 0);
        server_name.setText(Messages.Disconnected);
        server_name.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));

        // Message table
        // TableColumnLayout requires table to be only child of its parent
        final Composite table_parent = new Composite(parent, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        final TableColumnLayout table_layout = new MinSizeTableColumnLayout(10);
        table_parent.setLayout(table_layout);

        table_viewer = new TableViewer(table_parent,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        // Some tweaks to the underlying table widget
        final Table table = table_viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
        TableColumn col = view_col.getColumn();
        col.setText(Messages.DateColumn);
        col.setMoveable(true);
        table_layout.setColumnData(col, new ColumnWeightData(5, 200));
        view_col.setLabelProvider(new DateLabelProvider());

        view_col = new TableViewerColumn(table_viewer, 0);
        col = view_col.getColumn();
        col.setText(Messages.TypeColumn);
        col.setMoveable(true);
        table_layout.setColumnData(col, new ColumnWeightData(5, 50));
        view_col.setLabelProvider(new TypeLabelProvider());

        view_col = new TableViewerColumn(table_viewer, 0);
        col = view_col.getColumn();
        col.setText(Messages.ContentColumn);
        col.setMoveable(true);
        table_layout.setColumnData(col, new ColumnWeightData(100, 400));
        view_col.setLabelProvider(new ContentLabelProvider());

        table_viewer.setContentProvider(new ReceivedMessageProvider());

        ColumnViewerToolTipSupport.enableFor(table_viewer, ToolTip.NO_RECREATE);

        // Double-click on message opens detail
        table_viewer.getTable().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                new OpenViewAction(IPageLayout.ID_PROP_SHEET).run();
            }
        });

        // Context menu
        final MenuManager manager = new MenuManager();
        manager.add(new OpenViewAction(IPageLayout.ID_PROP_SHEET, Messages.ShowProperties));
        table.setMenu(manager.createContextMenu(table));

        clear();
    }

    /** Set initial focus */
    public void setFocus()
    {
        topic.setFocus();
    }

    /** @return SelectionProvider (TableViewer) for selected messages */
    public ISelectionProvider getSelectionProvider()
    {
        return table_viewer;
    }

    /** @return Currently selected topic */
    public String getTopic()
    {
        return topic.getText().trim();
    }

    /** Select topic: Connect to JMS, subscribe to topic, ...
     *  @param topic_name Name of topic
     */
    public void setTopic(final String topic_name)
    {
        try
        {
            if (! topic.getText().equals(topic_name))
                topic.setText(topic_name);
            if (model != null)
                model.close();
            clear();
            if (topic_name.length() <= 0  ||  Messages.EnterTopicMsg.equals(topic_name))
                return;
            model = new Model(url, user, password, topic_name);
            modelChanged(model);
            model.addListener(this);
        }
        catch (Exception ex)
        {
            showError(ex.getMessage());
        }
    }

    /** Set messages to something that indicates "no messages" */
    private void clear()
    {
        table_viewer.setInput(new ReceivedMessage[0]);
    }

    /** Set messages to something that show error message
     *  @param message Error message text
     */
    private void showError(final String message)
    {
        table_viewer.setInput(new ReceivedMessage[]
        {
            ReceivedMessage.createErrorMessage(message)
        });
    }

    /** @see ModelListener */
    @Override
    public void modelChanged(final Model model)
    {
        table_viewer.getTable().getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (server_name.isDisposed())
                    return;
                server_name.setText(model.getServerName());
                table_viewer.setInput(model.getMessages());
                table_viewer.refresh();
            }
        });
    }
}
