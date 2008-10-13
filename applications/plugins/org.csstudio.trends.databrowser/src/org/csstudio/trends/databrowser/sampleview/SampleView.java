package org.csstudio.trends.databrowser.sampleview;

import org.apache.log4j.Logger;
import org.csstudio.apputil.ui.swt.AutoSizeColumn;
import org.csstudio.apputil.ui.swt.AutoSizeControlListener;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableWithSamplesDragSource;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelListener;
import org.csstudio.trends.databrowser.ploteditor.PlotAwareView;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;

/** A View that shows all the current Model Samples in a list.
 *  <p>
 *  The table has a context menu, and table items implement
 *  ProcessVariableWithSample.
 *  <p>
 *  TODO: Sort of works, but doesn't refresh automatically when samples
 *        are added, and might have some performance issue:
 *        Especially when switching PVs, or closing after looking
 *        at many samples, Eclipse freezes for a while.
 *        
 *  @author Kay Kasemir
 *  @author Helge Rickens
 */
public class SampleView extends PlotAwareView
{
    public static final String ID = SampleView.class.getName();
    static Logger log;
    private Model model = null;
    private Combo pv_name;
    private TableViewer table_viewer;
    private IModelItem model_item = null;
    
    /** Listen to model changes: Whenever the PVs change, update pv_name */
    private ModelListener model_listener = new ModelListener()
    {
		public void entriesChanged()
        {
            refreshPVs();
        }

        public void entryAdded(IModelItem new_item)
        {
            refreshPVs();
        }

        public void entryRemoved(IModelItem removed_item)
        {
            refreshPVs();
        }

        // Ignore the rest
        public void plotColorsChangedChanged()            { /* NOP */ }
        public void entryArchivesChanged(IModelItem item) { /* NOP */ }
        public void entryConfigChanged(IModelItem item)   { /* NOP */ }
        public void entryMetaDataChanged(IModelItem item) { /* NOP */ }
        public void samplingChanged()                     { /* NOP */ }
        public void timeRangeChanged()                    { /* NOP */ }
        public void timeSpecificationsChanged()           { /* NOP */ }
    };
    
    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite parent)
    {
        log = CentralLogger.getInstance().getLogger(this);

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);
        GridData gd;

        // GUI:
        // Drop-down list of channels [Refresh]
        // Table of samples

        // The drop-down list
        Label l = new Label(parent, 0);
        l.setText(Messages.PVLabel);
        gd = new GridData();
        l.setLayoutData(gd);

        pv_name = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        pv_name.setToolTipText(Messages.PV_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        pv_name.setLayoutData(gd);
        pv_name.setEnabled(false);
        pv_name.addSelectionListener(new SelectionListener()
        {
            // Called after <Return> was pressed
            public void widgetDefaultSelected(SelectionEvent e)
            {   selectPV(pv_name.getText());  }

            // Called after existing entry was picked from list
            public void widgetSelected(SelectionEvent e)
            {   selectPV(pv_name.getText());  }
        });

        Button refresh = new Button(parent, SWT.PUSH);
        refresh.setText(Messages.Refesh);
        refresh.setToolTipText(Messages.Refresh_TT);
        gd = new GridData();
        refresh.setLayoutData(gd);
        refresh.addSelectionListener(new SelectionAdapter()
        {   @Override
            public void widgetSelected(SelectionEvent e)
            {   selectPV(pv_name.getText()); }
        });

        // The table
        Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL
                                | SWT.FULL_SELECTION | SWT.VIRTUAL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        table.setLayoutData(gd);
        AutoSizeColumn.make(table, Messages.TimeCol,      80, 100);
        AutoSizeColumn.make(table, Messages.ValueCol,     70,  90);
        AutoSizeColumn.make(table, Messages.StatSevrCol, 100,  50);
        AutoSizeColumn.make(table, Messages.QualityCol,   85,  50);
        AutoSizeColumn.make(table, Messages.SourceCol,    50,  90);
        // Configure table to auto-size the columns
        new AutoSizeControlListener(parent, table);

        table_viewer = new TableViewer(table);
        table_viewer.setLabelProvider(new SampleTableLabelProvider());
        table_viewer.setContentProvider(
                 new SampleTableLazyContentProvider(this, table_viewer));
        
        // Context menu
        makeContextMenu();
        // Drag and Drop
        new ProcessVariableWithSamplesDragSource(table_viewer.getControl(), table_viewer);
    }

    /** Set the initial focus. */
    @Override
    public void setFocus()
    {
        table_viewer.getTable().setFocus();
    }

    /** @return Current model item or <code>null</code>. */
    IModelItem getModelItem()
    {
        return model_item;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    protected void updateModel(final Model old_model, final Model new_model)
    {
        if (log.isDebugEnabled())
        {
            final String o = old_model == null ? "<null>" : old_model.toString();
            final String n = new_model == null ? "<null>" : new_model.toString();
            log.debug("Changing model from " + o + " to " + n);
        }
        // Any change at all?
        if (new_model == model)
            return;
        // Model changed 
        if (model != null)
        {   // Disconnect from our current model
            model.removeListener(model_listener);
            model = null;
            // Clear everything
            pv_name.setText(Messages.NoPlot);
            pv_name.setEnabled(false);
            selectPV(null);
        }
        if (new_model == null)
            return;
        // Display PV names of new model?
        model = new_model;
        selectPV(null);
        refreshPVs();
        // Learn when PVs get added/removed...
        model.addListener(model_listener);
    }

    /** Update PV name combo from the current model. */
    private void refreshPVs()
    {
        final String old_pv_name = pv_name.getText();
        if (model == null)
        {
            pv_name.removeAll();
            pv_name.setEnabled(false);
            return;
        }
        final String pvs[] = new String[model.getNumItems()];
        for (int i=0; i<pvs.length; ++i)
            pvs[i] = model.getItem(i).getName();
        pv_name.setItems(pvs);
        pv_name.setEnabled(true);
        // If there is a PV selected, select it again.
        // In case it's no longer a valid PV name,
        // this will clear the data.
        if (old_pv_name.length() > 0)
            selectPV(old_pv_name);
    }

    /** A PV name was entered or selected.
     *  <p>
     *  Find it in the model, and display its samples.
     *  @param PV Name or <code>null</code> to reset everything.
     */
    @SuppressWarnings("nls")
    private void selectPV(final String name)
    {
        if (name != null)
        {
            for (int i=0; i<model.getNumItems(); ++i)
            {
                IModelItem item = model.getItem(i);
                if (item.getName().equals(name))
                {
                    log.debug("Updating table for " + item.getName());
                    model_item = item;
                    table_viewer.setItemCount(model_item.size());
                    table_viewer.refresh();
                    return;
                }
            }
        }
        // Name was null or not found in model
        if (name == null)
            log.debug("Clearing table");
        else
            log.debug("Clearing table, " + name + " not found");
        pv_name.setText(""); //$NON-NLS-1$
        table_viewer.setItemCount(0);
        model_item = null;
        table_viewer.refresh();
    }

    /** Create context menu for table.
     *  <p>
     *  Basically empty, meant for object contributions.
     */
    @SuppressWarnings("nls")
    private void makeContextMenu()
    {   MenuManager manager = new MenuManager("#PopupMenu");
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        Control contr = table_viewer.getControl();
        Menu menu = manager.createContextMenu(contr);
        contr.setMenu(menu);
        getSite().registerContextMenu(manager, table_viewer);
    }
}
