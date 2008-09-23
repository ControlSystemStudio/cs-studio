package org.csstudio.trends.databrowser.waveformview;

import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequenceContainer;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.swt.chart.actions.RemoveMarkersAction;
import org.csstudio.swt.chart.actions.RemoveSelectedMarkersAction;
import org.csstudio.swt.chart.actions.SaveCurrentImageAction;
import org.csstudio.swt.chart.actions.ShowButtonBarAction;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IModelSamples;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelSample;
import org.csstudio.trends.databrowser.model.QualityHelper;
import org.csstudio.trends.databrowser.ploteditor.PlotAwareView;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;

/** View for inspecting Waveform (Array) Samples
 *  of the current PlotEditor
 *  @author Kay Kasemir
 */
public class WaveformView extends PlotAwareView
{
    final public static String ID = 
            "org.csstudio.trends.databrowser.waveformview.WaveformView"; //$NON-NLS-1$
    
    /** PV Name selector */
    private Combo pv_name;
    
    /** Plot */
    private InteractiveChart plot;

    /** Selector for current sample index. */
    private Slider sample_index;
    
    /** Timestamp of current sample. */
    private Text timestamp;

    /** Status/severity of current sample. */
    private Text status;

    /** Quality of current sample. */
    private Text quality;

    /** Source of current sample. */
    private Text source;
    
    /** Model of the active editor, or <code>null</code> */
    private Model model = null;
    
    /** Selected model item in model, or <code>null</code> */
    private IModelItem model_item = null;

    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite parent)
    {
        createGUI(parent);
        createContextMenu();
    }

    /** Create the GUI Elements */
    private void createGUI(final Composite parent)
    {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        parent.setLayout(layout);
        
        // PV: .......
        // =====================
        // ======= Plot ========
        // =====================
        // <<<<<< Slider >>>>>>
        // Timestamp: __________ Sevr./Status: __________
        // Quality:   _________  Source :      __________
        
        Label l = new Label(parent, 0);
        l.setText(Messages.WaveformView_PV);
        l.setLayoutData(new GridData());

        pv_name = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        pv_name.setToolTipText(Messages.WaveformView_PV_TT);
        GridData gd = new GridData();
        gd.horizontalSpan = layout.numColumns - 1;
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
        plot = new InteractiveChart(parent, Chart.USE_TRACE_NAMES);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        plot.setLayoutData(gd);
        plot.getChart().getXAxis().setLabel(Messages.WaveformView_XAxis);

        // New Row
        sample_index = new Slider(parent, SWT.HORIZONTAL);
        sample_index.setToolTipText(Messages.WaveformView_SampleIndex_TT);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        sample_index.setLayoutData(gd);
        sample_index.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                try
                {
                    showSelectedSample();
                }
                catch (Throwable ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        
        // New Row
        l = new Label(parent, 0);
        l.setText(Messages.WaveformView_Timestamp);
        l.setLayoutData(new GridData());

        timestamp = new Text(parent, SWT.READ_ONLY);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        timestamp.setLayoutData(gd);
        
        l = new Label(parent, 0);
        l.setText(Messages.WaveformView_SevrStat);
        l.setLayoutData(new GridData());

        status = new Text(parent, SWT.READ_ONLY);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        status.setLayoutData(gd);

        // New Row
        l = new Label(parent, 0);
        l.setText(Messages.WaveformView_Quality);
        l.setLayoutData(new GridData());

        quality = new Text(parent, SWT.READ_ONLY);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        quality.setLayoutData(gd);

        l = new Label(parent, 0);
        l.setText(Messages.WaveformView_Source);
        l.setLayoutData(new GridData());

        source = new Text(parent, SWT.READ_ONLY);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        source.setLayoutData(gd);
    }

    /** Add context menu to plot */
    private void createContextMenu()
    {
        final MenuManager context_menu = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        context_menu.add(new ShowButtonBarAction(plot));

        final Chart chart = plot.getChart();
        context_menu.add(new RemoveMarkersAction(chart));
        final RemoveSelectedMarkersAction remove_marker_action
            = new RemoveSelectedMarkersAction(chart);
        context_menu.add(remove_marker_action);
        context_menu.add(new Separator());
        context_menu.add(new SaveCurrentImageAction(chart));
        context_menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
 
        final Menu menu = context_menu.createContextMenu(chart);
        chart.setMenu(menu);

        context_menu.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                remove_marker_action.updateEnablement();
            }
        });
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
        if (new_model == null)
        {   // Clear everything
            model = null;
            pv_name.setText(Messages.WaveformView_NoPlot);
            pv_name.setEnabled(false);
            selectPV(null);
            return;
        }
        if (new_model == model  &&
            model.getNumItems() == pv_name.getItemCount())
            return; // Assume no change
        // Display PV names of model
        model = new_model;
        final String pvs[] = new String[model.getNumItems()];
        for (int i=0; i<pvs.length; ++i)
            pvs[i] = model.getItem(i).getName();
        pv_name.setItems(pvs);
        pv_name.setEnabled(true);
        selectPV(null);
    }

    /** Select given PV name (or <code>null</code>). */
    private void selectPV(final String new_pv_name)
    {
        if (new_pv_name != null)
        {
            for (int i=0; i<model.getNumItems(); ++i)
            {
                IModelItem item = model.getItem(i);
                if (item.getName().equals(new_pv_name))
                {
                    setModelItem(item);
                    return;
                }
            }
        }
        // Invalid PV name, not in model
        setModelItem(null);
        pv_name.setText(""); //$NON-NLS-1$
    }
    
    /** Select model item; display one of its samples. */
    private void setModelItem(IModelItem new_item)
    {
        model_item = new_item;
        sample_index.setEnabled(model_item != null);
        showSelectedSample();
    }
    
    /** Show the current sample of the current model item.
     *  <p>
     *  Also handles the case where current model item is <code>null</code>.
     */
    private void showSelectedSample()
    {
        // Delete all existing traces
        final Chart chart = plot.getChart();
        while (chart.getNumTraces() > 0)
            chart.removeTrace(0);
        
        // Anything to show?
        if (model_item == null)
        {
            clearInfo();
            return;
        }
        
        // Get selected sample (= one waveform)
        final IModelSamples samples = model_item.getSamples();
        ModelSample sample;
        synchronized (samples)
        {
            final int max = samples.size();
            sample_index.setMaximum(max);
            final int idx = sample_index.getSelection();
            sample = samples.get(idx);
        }
        if (sample == null)
        {
            clearInfo();
            return;
        }
        // Convert the waveform into a series for the trace
        ChartSampleSequenceContainer series = new ChartSampleSequenceContainer();
        final IValue value = sample.getSample();
        if (value instanceof IDoubleValue)
        {
            double val[] = ((IDoubleValue) value).getValues();
            for (int i = 0; i < val.length; ++i)
                series.add(ChartSample.Type.Normal, i, val[i], sample.getInfo());
        }
        else if (value instanceof ILongValue)
        {
            long val[] = ((ILongValue) value).getValues();
            for (int i = 0; i < val.length; ++i)
                series.add(ChartSample.Type.Normal, i, val[i], sample.getInfo());
        }
        else
        {
            clearInfo();
            return;
        }
        // Add to trace
        chart.addTrace(model_item.getName(),
                        series, model_item.getColor(), 1, 0, TraceType.Lines);
        final int waveform_elements = series.size();
        if (waveform_elements <= 1)
            chart.getXAxis().setValueRange(-1, 1);
        else
            chart.getXAxis().setValueRange(0, waveform_elements);
        updateInfo(sample);
    }

    /** Clear all the info fields. */
    @SuppressWarnings("nls")
    private void clearInfo()
    {
        timestamp.setText("");
        status.setText("");
        quality.setText("");
        source.setText("");
    }

    /** Update info fields for given sample. */
    private void updateInfo(final ModelSample sample)
    {
        final IValue value = sample.getSample();
        timestamp.setText(value.getTime().toString());
        status.setText(ValueUtil.getInfo(value));
        quality.setText(QualityHelper.getString(value.getQuality()));
        source.setText(sample.getSource());
    }
}
