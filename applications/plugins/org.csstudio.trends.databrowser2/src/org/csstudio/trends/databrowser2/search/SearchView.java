/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.search;

import java.util.ArrayList;
import java.util.Arrays;

import org.csstudio.apputil.ui.swt.TableColumnSortHelper;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.autocomplete.ui.AutoCompleteUIHelper;
import org.csstudio.autocomplete.ui.AutoCompleteTypes;
import org.csstudio.autocomplete.ui.AutoCompleteWidget;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.archive.SearchJob;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.ChannelInfo;
import org.csstudio.trends.databrowser2.ui.TableHelper;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for searching the archive
 *  @author Kay Kasemir
 */
public class SearchView extends ViewPart
{
    /** View ID (same ID as original Data Browser) registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.archiveview.ArchiveView"; //$NON-NLS-1$

    /** Memento tags */
	private static final String TAG_REGEX = "regex", //$NON-NLS-1$
			TAG_REPLACE = "replace"; //$NON-NLS-1$
	// TAG_CHANNELS = "channels"; //$NON-NLS-1$

    /** Archive URL and list of archives */
    private ArchiveListGUI archive_gui;

    // GUI elements
    private Text pattern;
    private Button search, result_replace, regex;
    private TableViewer channel_table;

    /** Memento that might store previous state */
    private IMemento memento;

    @Override
    public void init(final IViewSite site, final IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.memento = memento;
    }

    @Override
    public void saveState(final IMemento memento)
    {
        memento.putBoolean(TAG_REGEX, regex.getSelection());
        memento.putBoolean(TAG_REPLACE, result_replace.getSelection());
    }

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        createGUI(parent);
        configureActions();
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        archive_gui.setFocus();
    }

    /** Create GUI elements
     *  @param parent Parent widget
     */
    private void createGUI(final Composite parent)
    {
        // Upper Sash: Server URL, archives on server
        // Lower Sash: PV search
        parent.setLayout(new FillLayout());
        final SashForm sashform = new SashForm(parent, SWT.VERTICAL | SWT.BORDER);
        sashform.setLayout(new FillLayout());

        createServerSash(sashform);
        createSearchSash(sashform);

        sashform.setWeights(new int[] { 15, 85 });
    }

    /** Create the 'upper' sash for selecting a server and archives
     *  @param sashform
     */
    private void createServerSash(final SashForm sashform)
    {
        final Composite parent = new Composite(sashform, SWT.BORDER);
        archive_gui = new ArchiveListGUI(parent)
        {
            @Override
            protected void handleArchiveUpdate()
            {
                pattern.setEnabled(true);
                search.setEnabled(true);
            }

            @Override
            protected void handleServerError(final String url, final Exception ex)
            {
                SearchView.this.handleServerError(url, ex);
            }
        };
    }

    /** Create the 'lower' sash for searching archives
     *  @param sashform
     */
    private void createSearchSash(final SashForm sashform)
    {
        final Composite parent = new Composite(sashform, SWT.BORDER);
        final GridLayout layout = new GridLayout(3, false);
        parent.setLayout(layout);

        // Pattern:  ___pattern___ [x] [search]
        Label l = new Label(parent, 0);
        l.setText(Messages.SearchPattern);
        l.setLayoutData(new GridData());

        // On OS X, a 'search' box might look a little better:
        // pattern = new Text(parent, SWT.SEARCH | SWT.ICON_CANCEL);
        // ... except:
        // a) only takes effect on OS X
        // b) doesn't support drop-down for recent searches
        pattern = new Text(parent, SWT.BORDER);
        pattern.setToolTipText(Messages.SearchPatternTT);
        pattern.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        pattern.setEnabled(false);
		pattern.addListener(SWT.DefaultSelection, new Listener()
		{
			@Override
            public void handleEvent(Event e) {
				searchForChannels();
			}
		});
		AutoCompleteWidget acw = new AutoCompleteWidget(pattern, AutoCompleteTypes.PV);

        search = new Button(parent, SWT.PUSH);
        search.setText(Messages.Search);
        search.setToolTipText(Messages.SearchTT);
        search.setLayoutData(new GridData());
        search.setEnabled(false);
		AutoCompleteUIHelper.handleSelectEvent(search, acw);

        // ( ) Add  (*) Replace   [ ] Reg.Exp.
        final Button result_append = new Button(parent, SWT.RADIO);
        result_append.setText(Messages.AppendSearchResults);
        result_append.setToolTipText(Messages.AppendSearchResultsTT);
        result_append.setLayoutData(new GridData());

        result_replace = new Button(parent, SWT.RADIO);
        result_replace.setText(Messages.ReplaceSearchResults);
        result_replace.setToolTipText(Messages.ReplaceSearchResultsTT);
        result_replace.setLayoutData(new GridData(SWT.LEFT, 0, true, false));
        result_replace.setSelection(true);

        regex = new Button(parent, SWT.CHECK);
        regex.setText(Messages.RegularExpression);
        regex.setToolTipText(Messages.RegularExpressionTT);
        regex.setLayoutData(new GridData());

        // Table for channel names, displaying array of ChannelInfo entries
        // TableColumnLayout requires table in its own composite
        final Composite table_parent = new Composite(parent, 0);
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

        channel_table = new TableViewer(table_parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        channel_table.setContentProvider(new ArrayContentProvider());
        TableViewerColumn col = TableHelper.createColumn(table_layout, channel_table, Messages.PVName, 200, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ChannelInfo channel = (ChannelInfo) cell.getElement();
                cell.setText(channel.getProcessVariable().getName());
            }
        });
        new TableColumnSortHelper<ChannelInfo>(channel_table, col)
        {
			@Override
            public int compare(final ChannelInfo item1, final ChannelInfo item2)
            {
				return item1.getProcessVariable().getName().compareTo(item2.getProcessVariable().getName());
            }
        };
        col = TableHelper.createColumn(table_layout, channel_table, Messages.ArchiveName, 50, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final ChannelInfo channel = (ChannelInfo) cell.getElement();
                cell.setText(channel.getArchiveDataSource().getName());
            }
        });
        new TableColumnSortHelper<ChannelInfo>(channel_table, col)
        {
			@Override
            public int compare(final ChannelInfo item1, final ChannelInfo item2)
            {
				return item1.getArchiveDataSource().getName().compareTo(item2.getArchiveDataSource().getName());
            }
        };
        final Table table = channel_table.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        // searchForChannels() relies on non-null content
        channel_table.setInput(new ChannelInfo[0]);

		// Load previously entered patterns
		// pattern_history.loadSettings();
		// Restore settings from memento
        if (memento != null)
        {
            if (memento.getBoolean(TAG_REGEX) != null)
                regex.setSelection(memento.getBoolean(TAG_REGEX));
            if (memento.getBoolean(TAG_REPLACE) != null)
            {
                final boolean replace = memento.getBoolean(TAG_REPLACE);
                result_append.setSelection(! replace);
                result_replace.setSelection(replace);
            }
        }
    }

    /** React to selections, button presses, ... */
    private void configureActions()
    {
        // Start a channel search
        search.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                searchForChannels();
            }
        });

        // Channel Table: Allow dragging of PVs with archive
        final Table table = channel_table.getTable();
        new ControlSystemDragSource(table)
        {
            @Override
            public Object getSelection()
            {
                final IStructuredSelection selection = (IStructuredSelection) channel_table.getSelection();
                // To allow transfer of array, the data must be
                // of the actual array type, not Object[]
                final Object[] objs = selection.toArray();
                final ChannelInfo[] channels = Arrays.copyOf(objs, objs.length, ChannelInfo[].class);
                return channels;
                // return selection.getFirstElement();
            }
        };

        // Add context menu for object contributions
        final MenuManager menu = new MenuManager();
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        table.setMenu(menu.createContextMenu(table));
        getSite().registerContextMenu(menu, channel_table);
    }

    /** Display error from archive server connection or channel search
     *  @param url Server URL
     *  @param ex Error
     */
    private void handleServerError(final String url, final Exception ex)
    {
        if (pattern.isDisposed())
            return;
        pattern.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (pattern.isDisposed())
                    return;
                MessageDialog.openError(pattern.getShell(),
                    Messages.Error,
                    NLS.bind(Messages.ArchiveServerErrorFmt, url, ex.getMessage()));
            }
        });
    }

    /** Search selected archives for channels, updating the
     *  <code>channel_table</code> with the result
     *  @see #channel_table
     */
    private void searchForChannels()
    {
        final ArchiveDataSource archives[] = archive_gui.getSelectedArchives();
        if (archives == null)
            return;

        final String pattern_txt = pattern.getText().trim();

        // Warn when searching ALL channels
        if (pattern_txt.length() <= 0)
    	{
            MessageDialog.openInformation(pattern.getShell(),
                    Messages.Search,
                    Messages.SearchPatternEmptyMessage);
            // Aborted, move focus to search pattern
            pattern.setFocus();
            return;
    	}

        final ArchiveReader reader = archive_gui.getArchiveReader();
        new SearchJob(reader, archives, pattern_txt, !regex.getSelection())
        {
            @Override
            protected void receivedChannelInfos(final ChannelInfo channels[])
            {
                displayChannelInfos(channels);
            }

            @Override
            protected void archiveServerError(final String url, final Exception ex)
            {
                handleServerError(url, ex);
            }
        }.schedule();
    }

    /** Update the <code>channel_table</code> with received channel list
     *  @param channels Channel infos to add/replace
     */
    private void displayChannelInfos(final ChannelInfo[] channels)
    {
        final Table table = channel_table.getTable();
        if (table.isDisposed())
            return;
        table.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (result_replace.isDisposed())
                    return;
                if (result_replace.getSelection())
                    channel_table.setInput(channels);
                else
                {   // Combine new channels with existing list
                    final ChannelInfo old[] = (ChannelInfo[]) channel_table.getInput();
                    final ArrayList<ChannelInfo> full = new ArrayList<ChannelInfo>();
                    for (ChannelInfo channel : old)
                        full.add(channel);
                    // Add new channels but avoid duplicates
                    for (ChannelInfo channel : channels)
                        if (! full.contains(channel))
                            full.add(channel);
                    channel_table.setInput((ChannelInfo[]) full.toArray(new ChannelInfo[full.size()]));
                }
            }
        });
    }
}
