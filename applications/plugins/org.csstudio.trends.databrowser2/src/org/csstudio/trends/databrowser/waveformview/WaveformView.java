package org.csstudio.trends.databrowser.waveformview;

import org.csstudio.platform.data.IValue;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.figures.XYGraphFlags;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.editor.DataBrowserAwareView;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.csstudio.trends.databrowser.model.PlotSamples;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

/** View for inspecting Waveform (Array) Samples of the current Model
 *  @author Kay Kasemir
 */
public class WaveformView extends DataBrowserAwareView
{
    /** View ID registered in plugin.xml */
    final public static String ID = 
        "org.csstudio.trends.databrowser.waveformview.WaveformView"; //$NON-NLS-1$

    /** Model of the currently active Data Browser plot or <code>null</code> */
    private Model model;
    
    /** Selected model item in model, or <code>null</code> */
    private ModelItem model_item = null;
    
    /** PV Name selector */
    private Combo pv_name;

    /** Plot */
    private ToolbarArmedXYGraph plot;
    private XYGraph xygraph;

    /** Timestamp of current sample. */
    private Text timestamp;

    /** Status/severity of current sample. */
    private Text status;

    private Slider sample_index;
    
    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite parent)
    {
        final GridLayout layout = new GridLayout(4, false);
        parent.setLayout(layout);
        
        // PV: ....... [Refresh]
        // =====================
        // ======= Plot ========
        // =====================
        // <<<<<< Slider >>>>>>
        // Timestamp: __________ Sevr./Status: __________
        
        // Item: pvs [Refresh]
        Label l = new Label(parent, 0);
        l.setText(Messages.SampleView_Item);
        l.setLayoutData(new GridData());
        
        pv_name = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        pv_name.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));
        pv_name.addSelectionListener(new SelectionListener()
        {
            public void widgetSelected(final SelectionEvent e)
            {
                widgetDefaultSelected(e);
            }
            
            public void widgetDefaultSelected(final SelectionEvent e)
            {   // Configure table to display samples of the selected model item
                if (pv_name.getSelectionIndex() == 0)
                {
                    selectPV(null);
                    return;
                }
                selectPV(pv_name.getText());
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

        // Plot of currently selected waveform sample
        final Canvas canvas = new Canvas(parent, 0);
        canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        // Create plot with basic configuration
        final LightweightSystem lws = new LightweightSystem(canvas);
        plot = new ToolbarArmedXYGraph(new XYGraph(), XYGraphFlags.COMBINED_ZOOM);
        xygraph = plot.getXYGraph();
        // Configure axes
        xygraph.primaryXAxis.setTitle("Waveform Index");
        xygraph.primaryYAxis.setTitle("Amplitude");
        lws.setContents(plot);
        
        sample_index = new Slider(parent, SWT.HORIZONTAL);
        sample_index.setToolTipText("Select waveform by time");
        sample_index.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));
        sample_index.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                showSelectedSample();
            }
        });
        
        // Timestamp: __________ Sevr./Status: __________
        l = new Label(parent, 0);
        l.setText("Timestamp:");
        l.setLayoutData(new GridData());

        timestamp = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
        timestamp.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        l = new Label(parent, 0);
        l.setText("Sevr./Status:");
        l.setLayoutData(new GridData());

        status = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
        status.setLayoutData(new GridData(SWT.FILL, 0, true, false));
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        pv_name.setFocus();        
    }

    /** {@inheritDoc} */
    @Override
    protected void updateModel(final Model old_model, final Model model)
    {
        this.model = model;
        if (model == null)
        {   // Clear/disable GUI
            pv_name.setItems(new String[] { Messages.SampleView_NoPlot});
            pv_name.select(0);
            pv_name.setEnabled(false);
            selectPV(null);
            return;
        }
        
        // Show PV names
        final String names[] = new String[model.getItemCount()+1];
        names[0] = Messages.SampleView_SelectItem;
        for (int i=1; i<names.length; ++i)
            names[i] = model.getItem(i-1).getName();
        if (old_model == model  &&  pv_name.getSelectionIndex() > 0)
        {
            // Is the previously selected item still valid?
            final String old_name = pv_name.getText();
            final ModelItem item = model.getItem(old_name);
            if (item == model_item)
            {   // Show same PV name again in combo box
                pv_name.setItems(names);
                pv_name.setText(item.getName());
                showSelectedSample();
                return;
            }
        }
        // Previously selected item no longer valid.
        // Show new items, clear rest
        pv_name.setItems(names);
        pv_name.select(0);
        pv_name.setEnabled(true);
        selectPV(null);
    }
    
    /** Select given PV name (or <code>null</code>). */
    private void selectPV(final String new_pv_name)
    {
        if (new_pv_name != null)
        {
            for (int i=0; i<model.getItemCount(); ++i)
            {
                final ModelItem item = model.getItem(i);
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
    private void setModelItem(final ModelItem new_item)
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
        int N = xygraph.getPlotArea().getTraceList().size();
        while (N > 0)
            xygraph.removeTrace(xygraph.getPlotArea().getTraceList().get(--N));
        
        // Anything to show?
        if (model_item == null)
        {
            clearInfo();
            return;
        }

        // Get selected sample (= one waveform)
        final PlotSamples samples = model_item.getSamples();
        final IValue value;
        synchronized (samples)
        {
            sample_index.setMaximum(samples.getSize());
            final int idx = sample_index.getSelection();
            value = samples.getSample(idx).getValue();
        }
        if (value == null)
        {
            clearInfo();
            return;
        }

        // Convert IValue into input for plot
        xygraph.addTrace(new Trace(model_item.getDisplayName(),
                xygraph.primaryXAxis, xygraph.primaryYAxis,
                new WaveformValueDataProvider(value)));
    }

    /** Clear all the info fields. */
    @SuppressWarnings("nls")
    private void clearInfo()
    {
        timestamp.setText("");
        status.setText("");
    }
}
