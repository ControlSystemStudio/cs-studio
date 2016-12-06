/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.waveformview;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.swt.rtplot.PointType;
import org.csstudio.swt.rtplot.RTValuePlot;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.TraceType;
import org.csstudio.swt.rtplot.YAxis;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.editor.DataBrowserAwareView;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.ModelListener;
import org.csstudio.trends.databrowser2.model.ModelListenerAdapter;
import org.csstudio.trends.databrowser2.model.PlotSamples;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VType;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
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
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

/** View for inspecting Waveform (Array) Samples of the current Model
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto changed WaveformView to handle multiple items with
 *                           the same name.
 *  @author Xihui Chen (Added some work around to make it work for rap).
 */
@SuppressWarnings("nls")
public class WaveformView extends DataBrowserAwareView
{
    /** View ID registered in plugin.xml */
    final public static String ID =
        "org.csstudio.trends.databrowser.waveformview.WaveformView"; //$NON-NLS-1$

    /** PV Name selector */
    private Combo pv_name;

    /** Plot */
    private RTValuePlot plot;

    /** Selector for model_item's current sample */
    private Slider sample_index;

    /** Timestamp of current sample. */
    private Text timestamp;

    /** Status/severity of current sample. */
    private Text status;

    /** Model of the currently active Data Browser plot or <code>null</code> */
    private Model model;

    final private ModelListener model_listener = new ModelListenerAdapter()
    {
        @Override
        public void itemAdded(final ModelItem item)
        {
            update(false);
        }

        @Override
        public void itemRemoved(final ModelItem item)
        {
            if (item == model_item) {
                model_item = null;
            }
            update(false);
        }

        @Override
        public void changedItemLook(final ModelItem item)
        {
            update(false);
        }
    };

    /** Selected model item in model, or <code>null</code> */
    private ModelItem model_item = null;

    /** Waveform for the currently selected sample */
    private WaveformValueDataProvider waveform = null;

    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite parent)
    {
        // Arrange disposal
        parent.addDisposeListener((DisposeEvent e) ->
        {   // Ignore current model after this view is disposed.
            if (model != null)
                model.removeListener(model_listener);
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
                    selectPV(model.getItem(pv_name.getText()));
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
        plot = new RTValuePlot(parent);
        plot.getXAxis().setName(Messages.WaveformIndex);
        plot.getYAxes().get(0).setName(Messages.WaveformAmplitude);
        plot.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

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
        if (old_model != model)
        {
            if (old_model != null)
                old_model.removeListener(model_listener);

            if (model != null)
                model.addListener(model_listener);
        }
        update(old_model != model);
    }

    /** Update combo box of this view.
     *  Since it interacts with the UI run on the UI thread.
     *  @param model_changed Is this a different model?
     */
    private void update(final boolean model_changed)
    {
        Display.getDefault().asyncExec( () ->
        {
            if (pv_name.isDisposed())
            {
                return;
            }
            if (model == null)
            {   // Clear/disable GUI
                pv_name.setItems(new String[] { Messages.SampleView_NoPlot});
                pv_name.select(0);
                pv_name.setEnabled(false);
                selectPV(null);
                return;
            }

            // Show PV names
            final List<String> names_list = new ArrayList<>();
            names_list.add(Messages.SampleView_SelectItem);
            for (ModelItem item : model.getItems())
                names_list.add(item.getName());
            final String[] names = names_list.toArray(new String[names_list.size()]);

            // Is the previously selected item still valid?
            final int selected = pv_name.getSelectionIndex();
            if (!model_changed  &&  selected > 0  &&  model_item != null  &&  pv_name.getText().equals(model_item.getName()))
            {
                // Show same PV name again in combo box
                pv_name.setItems(names);
                pv_name.select(selected);
                pv_name.setEnabled(true);
                return;
            }
            // Previously selected item no longer valid.
            // Show new items, clear rest
            pv_name.setItems(names);
            pv_name.select(0);
            pv_name.setEnabled(true);
            selectPV(null);
        });
    }

    /** Select given PV item (or <code>null</code>). */
    private void selectPV(final ModelItem new_item)
    {
        model_item = new_item;

        // Delete all existing traces
        for (Trace<Double> trace : plot.getTraces())
            plot.removeTrace(trace);

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
        plot.addTrace(model_item.getResolvedDisplayName(), model_item.getUnits(), waveform, model_item.getColor(), TraceType.NONE, 1, PointType.CIRCLES, 5, 0);
        // Enable waveform selection and update slider's range
        sample_index.setEnabled(true);
        showSelectedSample();
        // Autoscale Y axis by default.  If the user tries to move the axis this will automatically turn off.
        for (YAxis<Double> yaxis : plot.getYAxes()) {
            yaxis.setAutoscale(true);
        }
    }

    /** Show the current sample of the current model item. */
    private void showSelectedSample()
    {
        // Get selected sample (= one waveform)
        final PlotSamples samples = model_item.getSamples();
        final VType value;
        samples.getLock().lock();
        try
        {
            sample_index.setMaximum(samples.size());
            final int idx = sample_index.getSelection();
            value = samples.get(idx).getVType();
        }
        finally
        {
            samples.getLock().unlock();
        }
        // Setting the value can be delayed while the plot is being updated
        Activator.getThreadPool().execute(() -> waveform.setValue(value));
        if (value == null)
            clearInfo();
        else
        {
            int size = value instanceof VNumberArray ? ((VNumberArray)value).getData().size() : 1;
            plot.getXAxis().setValueRange(0.0, (double)size);
            timestamp.setText(TimestampHelper.format(VTypeHelper.getTimestamp(value)));
            status.setText(NLS.bind(Messages.SeverityStatusFmt, VTypeHelper.getSeverity(value).toString(), VTypeHelper.getMessage(value)));
        }
        plot.requestUpdate();
    }

    /** Clear all the info fields. */
    private void clearInfo()
    {
        timestamp.setText("");
        status.setText("");
    }
}
