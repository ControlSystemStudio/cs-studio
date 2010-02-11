package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IMinMaxDoubleValue;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.ui.swt.AutoSizeColumn;
import org.csstudio.platform.ui.swt.AutoSizeControlListener;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.editor.DataBrowserAwareView;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.csstudio.trends.databrowser.model.PlotSample;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

/** A View that shows all the current Model Samples in a list.
 *        
 *  @author Kay Kasemir
 *  @author Helge Rickens contributed to the previous Data Browser SampleView
 *  @author Albert Kagarmanov changed the previous Data Browser's
 *              SampleTableLabelProvider to show numbers with 4 trailing digits.
 *              This implementation uses tooltips to show the Double.toString(number)
 */
public class SampleView extends DataBrowserAwareView
{
    /** View ID registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.sample_view"; //$NON-NLS-1$

    /** Model of the currently active Data Browser plot or <code>null</code> */
    private Model model;
    
    /** GUI elements */
    private Combo items;
    private TableViewer sample_table;
    
    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite parent)
    {
        final GridLayout layout = new GridLayout(3, false);
        parent.setLayout(layout);
        
        // Item: pvs [Refresh]
        Label l = new Label(parent, 0);
        l.setText(Messages.SampleView_Item);
        l.setLayoutData(new GridData());
        
        items = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        items.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        items.addSelectionListener(new SelectionListener()
        {
            public void widgetSelected(final SelectionEvent e)
            {
                widgetDefaultSelected(e);
            }
            
            public void widgetDefaultSelected(final SelectionEvent e)
            {   // Configure table to display samples of the selected model item
                if (items.getSelectionIndex() == 0)
                {
                    sample_table.setInput(null);
                    return;
                }
                final ModelItem item = model.getItem(items.getText());
                if (item == null)
                    return;
                sample_table.setInput(item);
            }
        });
        
        final Button refresh = new Button(parent, SWT.PUSH);
        refresh.setText(Messages.SampleView_Refresh);
        refresh.setToolTipText(Messages.SampleView_RefreshTT);
        refresh.setLayoutData(new GridData());
        refresh.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {   // Trigger GUI update by switching to current model
                updateModel(model, model);
            }
        });
        
        // Sample Table
        sample_table = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
        sample_table.setContentProvider(new SampleTableContentProvider());
        final Table table = sample_table.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        // Time column
        TableViewerColumn col = AutoSizeColumn.make(sample_table, Messages.TimeColumn, 90, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                cell.setText(sample.getTime().toString());
            }
        });
        // Value column
        col = AutoSizeColumn.make(sample_table, Messages.ValueColumn, 50, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                cell.setText(sample.getValue().format());
            }

            @Override
            public String getToolTipText(Object element)
            {
                final PlotSample sample = (PlotSample) element;
                final IValue value = sample.getValue();
                if (value instanceof IMinMaxDoubleValue)
                {
                    final IMinMaxDoubleValue mmd = (IMinMaxDoubleValue) value;
                    return NLS.bind(Messages.SampleView_MinMaxValueTT,
                        new String[]
                        {
                            Double.toString(mmd.getValue()),
                            Double.toString(mmd.getMinimum()),
                            Double.toString(mmd.getMaximum())
                        }); 
                }
                else if (value instanceof IDoubleValue)
                {
                    final IDoubleValue dbl = (IDoubleValue) value;
                    return Double.toString(dbl.getValue()); 
                }
                else
                    return value.toString();
            }
        });
        // Severity column
        col = AutoSizeColumn.make(sample_table, Messages.SeverityColumn, 90, 50);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                final IValue value = sample.getValue();
                final ISeverity severity = value.getSeverity();
                cell.setText(severity.toString());
                if (severity.isOK())
                {
                    cell.setBackground(null);
                    return;
                }
                final Display display = cell.getControl().getDisplay();
                if (severity.isMajor())
                    cell.setBackground(display.getSystemColor(SWT.COLOR_RED));
                else if (severity.isMinor())
                    cell.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
                else
                    cell.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
            }
        });
        // Status column
        col = AutoSizeColumn.make(sample_table, Messages.StatusColumn, 90, 50);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                final IValue value = sample.getValue();
                cell.setText(value.getStatus());
            }
        });
        // Sample Source column
        col = AutoSizeColumn.make(sample_table, Messages.SampleView_Source, 90, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                cell.setText(sample.getSource());
            }
        });
        // Data Quality column
        col = AutoSizeColumn.make(sample_table, Messages.SampleView_Quality, 90, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                cell.setText(sample.getValue().getQuality().toString());
            }
        });
        ColumnViewerToolTipSupport.enableFor(sample_table, ToolTip.NO_RECREATE);

        new AutoSizeControlListener(table);
    }

    /** {@inheritDoc} */
    @Override
    protected void updateModel(Model oldModel, Model model)
    {
        this.model = model;
        if (model == null)
        {   // Clear/disable GUI
            items.setItems(new String[] { Messages.SampleView_NoPlot});
            items.select(0);
            items.setEnabled(false);
            sample_table.setInput(null);
            return;
        }
        
        // Show PV names
        final String names[] = new String[model.getItemCount()+1];
        names[0] = Messages.SampleView_SelectItem;
        for (int i=1; i<names.length; ++i)
            names[i] = model.getItem(i-1).getName();
        if (oldModel == model  &&  items.getSelectionIndex() > 0)
        {
            // Is the previously selected item still valid?
            final String old_name = items.getText();
            final ModelItem item = model.getItem(old_name);
            if (item == sample_table.getInput())
            {   // Show same PV name again in combo box
                items.setItems(names);
                items.setText(item.getName());
                // Update sample table size
                sample_table.setItemCount(item.getSamples().getSize());
                return;
            }
        }
        // Previously selected item no longer valid.
        // Show new items, clear rest
        items.setItems(names);
        items.select(0);
        items.setEnabled(true);
        sample_table.setInput(null);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        items.setFocus();        
    }
}
