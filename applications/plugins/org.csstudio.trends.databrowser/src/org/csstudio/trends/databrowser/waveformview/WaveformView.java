package org.csstudio.trends.databrowser.waveformview;


import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.ShowButtonBarAction;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.ploteditor.PlotAwareView;
import org.csstudio.trends.databrowser.sampleview.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;

/** View for inspecting Waveform (Array) Samples
 *  of the current PlotEditor
 *  @author Kay Kasemir
 */
public class WaveformView extends PlotAwareView
{
    final public static String ID = 
            "org.csstudio.trends.databrowser.waveformview.WaveformView"; //$NON-NLS-1$
    
    // GUI
    private Combo pv_name;
    private InteractiveChart chart;
    
    private Model model = null;
    
    public WaveformView()
    {
        // TODO Auto-generated constructor stub
    }

    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite parent)
    {
        createGUI(parent);
        createContextMenu();
        super.createPartControl(parent);
    }

    /** Create the GUI Elements */
    private void createGUI(final Composite parent)
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        parent.setLayout(layout);
        GridData gd;
        
        // PV: .......
        // =====================
        // ======= Plot ========
        // =====================
        // Sample: 42  +-
        
        Label l = new Label(parent, 0);
        l.setText("UNDER CONSTRUCTION...");
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        l.setLayoutData(gd);
       
        // New Row
        l = new Label(parent, 0);
        l.setText("PV:");
        l.setLayoutData(new GridData());

        pv_name = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        pv_name.setToolTipText("Select PV");
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

        // New Row
        chart = new InteractiveChart(parent, Chart.USE_TRACE_NAMES);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        chart.setLayoutData(gd);
    }

    /** Add context menu to plot */
    private void createContextMenu()
    {
        Action button_bar_action = new ShowButtonBarAction(chart);

        final MenuManager context_menu = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        context_menu.add(button_bar_action);
        context_menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        final Control ctl = chart.getChart();
        final Menu menu = context_menu.createContextMenu(ctl);
        ctl.setMenu(menu);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        pv_name.setFocus();
    }
    
    /** {@inheritDoc} */
    @Override
    protected void updateModel(final Model old_model, final Model new_model)
    {
        if (model == null)
        {   // Clear everyting
            model = null;
            pv_name.setText(Messages.NoPlot);
            pv_name.setEnabled(false);
            selectPV(null);
            return;
        }
        if (new_model == model)
            return; // No change
        // Display PV names of new model
        model = new_model;
        final String pvs[] = new String[model.getNumItems()];
        for (int i=0; i<pvs.length; ++i)
            pvs[i] = model.getItem(i).getName();
        pv_name.setItems(pvs);
        pv_name.setEnabled(true);
        selectPV(null);
    }

    private void selectPV(final String new_pv_name)
    {
        if (new_pv_name == null)
        {
            showValue(null);
            pv_name.setText(""); //$NON-NLS-1$
            return;
        }
        for (int i=0; i<model.getNumItems(); ++i)
        {
            IModelItem item = model.getItem(i);
            if (item.getName().equals(new_pv_name))
            {
                // TODO remove Fake Sample
                // TODO Get sample[i] of that pv
                final double val[] = new double[] { 1, 2, 4, 8, 12, 8, 4, 2, 1 };
                final IValue value = ValueFactory.createDoubleValue(
                                TimestampFactory.now(),
                                ValueFactory.createMinorSeverity(),
                                "Fake Sample",
                                null,
                                IValue.Quality.Interpolated, val);
                showValue(value);
                return;
            }
        }
        // Invalid PV name, not in model
        selectPV(null);
    }
    
    private void showValue(final IValue value)
    {
        // chart.getChart().addTrace(name, series, color, line_width, yaxis_index, type)
    }
}
