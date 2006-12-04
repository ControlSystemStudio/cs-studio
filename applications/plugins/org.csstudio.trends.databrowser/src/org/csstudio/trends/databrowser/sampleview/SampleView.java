package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelSamples;
import org.csstudio.trends.databrowser.ploteditor.PlotAwareView;
import org.csstudio.util.swt.AutoSizeColumn;
import org.csstudio.util.swt.AutoSizeControlListener;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

/** A View that shows all the current Model Samples in a list.
 *  <p>
 *  TODO: Sort of works, but doesn't refresh automatically when samples
 *        are added, and might have some performance issue:
 *        Especially when switching PVs, or closing after looking
 *        at many samples, Eclipse freezes for a while.
 *  @author Kay Kasemir
 */
public class SampleView extends PlotAwareView
{
    public static final String ID = SampleView.class.getName();
    private Model model = null;
    private Combo pv_name;
    private TableViewer table_viewer;
    private ModelSamples samples = null;
    
    /** Create the GUI elements. */
    @Override
    public void createPartControl(Composite parent)
    {
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

        pv_name = new Combo(parent, SWT.DROP_DOWN);
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
        Table table = new Table(parent,
                        SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
                        | SWT.VIRTUAL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        table.setLayoutData(gd);
        AutoSizeColumn.make(table, Messages.TimeCol, 80, 100);
        AutoSizeColumn.make(table, Messages.ValueCol, 70, 50);
        AutoSizeColumn.make(table, Messages.InfoCol, 100, 100);
        // Configure table to auto-size the columns
        new AutoSizeControlListener(parent, table);
        
        table_viewer = new TableViewer(table);
        table_viewer.setLabelProvider(new SampleTableLabelProvider());
        table_viewer.setContentProvider(
                 new SampleTableLazyContentProvider(this, table_viewer));
        
        // Invoke PlotAwareView's createPartControl to enable updateModel()
        super.createPartControl(parent);
    }
    
    /** Set the initial focus. */
    @Override
    public void setFocus()
    {
        table_viewer.getTable().setFocus();
    }
    
    // @see PlotAwareView
    @Override
    protected void updateModel(Model old_model, Model new_model)
    {
        boolean change = old_model != new_model;
        model = new_model;
        if (model == null)
        {
            pv_name.setText(Messages.NoPlot);
            pv_name.setEnabled(false);
            selectPV(null);
        }
        else
        {
            String pvs[] = new String[model.getNumItems()];
            for (int i=0; i<pvs.length; ++i)
                pvs[i] = model.getItem(i).getName();
            pv_name.setItems(pvs);
            pv_name.setEnabled(true);
            if (change)
            {
                pv_name.setText(""); //$NON-NLS-1$
                selectPV(null);
            }
        }
    }
    
    /** A PV name was entered or selected.
     *  <p>
     *  Find it in the model, and display its samples.
     *  @param PV Name or <code>null</code> to reset everything.
     */
    private void selectPV(String name)
    {
        if (name == null)
        {
            table_viewer.setItemCount(0);
            samples = null;
            return;
        }
        int i, N = model.getNumItems();
        IModelItem item;
        for (i=0; i<N; ++i)
        {
            item = model.getItem(i);
            if (item.getName().equals(name))
            {
                samples = item.getSamples();
                synchronized (samples)
                {
                    table_viewer.setItemCount(samples.size());
                    table_viewer.refresh();
                }
                return;
            }
        }
        // Invalid PV name, not in model
        selectPV(null);
    }
    
    /** Get the samples of the current plot and selected PV.
     *  Remember to synchronize on them!
     *  @return the samples or <code>null</code>.
     */
    ModelSamples getSamples()
    {
        return samples;
    }
}
