/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.waveformview;

import org.csstudio.data.values.IValue;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.figures.XYGraphFlags;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.editor.DataBrowserAwareView;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.ModelListener;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.PlotSamples;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
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
 *  @author Takashi Nakamoto changed WaveformView to handle multiple itesm with
 *                           the same name.
 */
public class WaveformView extends DataBrowserAwareView
	implements ModelListener
{
    /** View ID registered in plugin.xml */
    final public static String ID =
        "org.csstudio.trends.databrowser.waveformview.WaveformView"; //$NON-NLS-1$

    /** PV Name selector */
    private Combo pv_name;

    /** XY Graph */
    private XYGraph xygraph;

    /** Selector for model_item's current sample */
    private Slider sample_index;

    /** Timestamp of current sample. */
    private Text timestamp;

    /** Status/severity of current sample. */
    private Text status;

    /** Model of the currently active Data Browser plot or <code>null</code> */
    private Model model;

    /** Selected model item in model, or <code>null</code> */
    private ModelItem model_item = null;

    /** Color for trace of model_item's current sample */
    private Color color = null;

    /** Waveform for the currently selected sample */
    private WaveformValueDataProvider waveform = null;

    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite parent)
    {
        // Arrange disposal
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                if (color != null)
                    color.dispose();

                // Be ignorant of any change of the current model after
                // this view is disposed.
                if (model != null)
                	model.removeListener(WaveformView.this);
            }
        });

        final GridLayout layout = new GridLayout(4, false);
        parent.setLayout(layout);

        // PV: .pvs..... [Refresh]
        // =====================
        // ======= Plot ========
        // =====================
        // <<<<<< Slider >>>>>>
        // Timestamp: __________ Sevr./Status: __________

        // PV: .pvs..... [Refresh]
        Label l = new Label(parent, 0);
        l.setText(Messages.SampleView_Item);
        l.setLayoutData(new GridData());

        pv_name = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        pv_name.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns-2, 1));
        pv_name.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                widgetDefaultSelected(e);
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {   // First item is "--select PV name--"
                 if (pv_name.getSelectionIndex() == 0)
                    selectPV(null);
                else
                	selectPV(model.getItem(pv_name.getSelectionIndex() - 1));
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

        // =====================
        // ======= Plot ========
        // =====================
        final Canvas canvas = new Canvas(parent, 0);
        canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        // Create plot with basic configuration
        final LightweightSystem lws = new LightweightSystem(canvas);
        final ToolbarArmedXYGraph plot =
            new ToolbarArmedXYGraph(new XYGraph(), XYGraphFlags.COMBINED_ZOOM);
        xygraph = plot.getXYGraph();
        // Configure axes
        xygraph.primaryXAxis.setTitle(Messages.WaveformIndex);
        xygraph.primaryYAxis.setTitle(Messages.WaveformAmplitude);
        lws.setContents(plot);

        // <<<<<< Slider >>>>>>
        sample_index = new Slider(parent, SWT.HORIZONTAL);
        sample_index.setToolTipText(Messages.WaveformTimeSelector);
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
        l.setText(Messages.WaveformTimestamp);
        l.setLayoutData(new GridData());
        timestamp = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
        timestamp.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        l = new Label(parent, 0);
        l.setText(Messages.WaveformStatus);
        l.setLayoutData(new GridData());
        status = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
        status.setLayoutData(new GridData(SWT.FILL, 0, true, false));
    }

	/** {@inheritDoc} */
	// Remove Override annotation for RAP
	// @Override
	public void setFocus() {
        pv_name.setFocus();
    }

    /** {@inheritDoc} */
    @Override
    protected void updateModel(final Model old_model, final Model model)
    {
    	this.model = model;
    	if (old_model != model) {
    		if (old_model != null)
    			old_model.removeListener(this);

    		if (model != null)
    			model.addListener(this);
    	}
    	update(old_model != model);
    }

    /** Update combo box of this view.
     * @param model_changed set true if the model was changed
     */
    private void update(final boolean model_changed)
    {
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
        if (!model_changed  &&  pv_name.getSelectionIndex() > 0)
        {
        	// Is the previously selected item still valid?
        	if (model.indexOf(model_item) != -1)
        	{	// Show same PV name again in combo box
        		pv_name.setItems(names);
        		pv_name.select(model.indexOf(model_item) + 1);
        		pv_name.setEnabled(true);
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

    /** Select given PV item (or <code>null</code>). */
    private void selectPV(final ModelItem new_item)
    {
        if (new_item == null)
            model_item = null;
        else
            model_item = new_item;

        // Delete all existing traces
        int N = xygraph.getPlotArea().getTraceList().size();
        while (N > 0)
            xygraph.removeTrace(xygraph.getPlotArea().getTraceList().get(--N));

        // No or unknown PV name?
        if (model_item == null)
        {
            pv_name.setText(""); //$NON-NLS-1$
            sample_index.setEnabled(false);
            return;
        }

        // Prepare to show waveforms of model item in plot
        waveform = new WaveformValueDataProvider();

        // Create trace for waveform
        final Trace trace = new Trace(model_item.getDisplayName(),
                xygraph.primaryXAxis, xygraph.primaryYAxis, waveform);
        // Configure color, ...
        final Color old_color = color;
        color = new Color(pv_name.getDisplay(), model_item.getColor());
        trace.setTraceColor(color);
        trace.setLineWidth(model_item.getLineWidth());
        trace.setPointStyle(PointStyle.POINT);
        trace.setPointSize(5);
        // Add to graph
        xygraph.addTrace(trace);
        // Dispose previous color
        if (old_color != null)
            old_color.dispose();
        // Enable waveform selection and update slider's range
        sample_index.setEnabled(true);
        showSelectedSample();
    }

    /** Show the current sample of the current model item. */
    private void showSelectedSample()
    {
        // Get selected sample (= one waveform)
        final PlotSamples samples = model_item.getSamples();
        final IValue value;
        synchronized (samples)
        {
            sample_index.setMaximum(samples.getSize());
            final int idx = sample_index.getSelection();
            value = samples.getSample(idx).getValue();
        }
        waveform.setValue(value);
        if (value == null)
            clearInfo();
        else
        {
            timestamp.setText(value.getTime().toString());
            status.setText(NLS.bind(Messages.SeverityStatusFmt, value.getSeverity().toString(), value.getStatus()));
        }
    }

    /** Clear all the info fields. */
    private void clearInfo()
    {
        timestamp.setText(""); //$NON-NLS-1$
        status.setText(""); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
	@Override
	public void itemAdded(ModelItem item) {
	    // Be aware of the addition of a new item to update combo box.
		update(false);
	}

	/** {@inheritDoc} */
	@Override
	public void itemRemoved(ModelItem item) {
	    // Be aware of the addition of a new item to update combo box.
		update(false);
	}

	/** {@inheritDoc} */
	@Override
	public void changedItemLook(ModelItem item) {
		// Be aware of the change of the item name.
		update(false);
	}

	/** {@inheritDoc} */
	@Override
	public void changedColors() {
		// Be aware of the change of the item color.
		// TODO: this update does not trigger color change. Fix it.
		update(false);
	}

	// Following methods are defined as they are mandatory to fulfill
	// ModelListener interface, but they are not used at all to update
	// this sample view.
	@Override
	public void changedUpdatePeriod() {}

	@Override
	public void changedArchiveRescale() {}

	@Override
	public void changedTimerange() {}

	@Override
	public void changedAxis(AxisConfig axis) {}

	@Override
	public void changedItemVisibility(ModelItem item) {}

	@Override
	public void changedItemDataConfig(PVItem item) {}

	@Override
	public void scrollEnabled(boolean scroll_enabled) {}

	@Override
	public void changedAnnotations() {}

	@Override
	public void changedXYGraphConfig() {}
}
