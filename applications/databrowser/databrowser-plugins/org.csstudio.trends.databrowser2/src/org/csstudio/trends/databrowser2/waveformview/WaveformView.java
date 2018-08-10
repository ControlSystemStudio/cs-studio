/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.waveformview;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.swt.rtplot.PointType;
import org.csstudio.swt.rtplot.RTPlot;
import org.csstudio.swt.rtplot.RTValuePlot;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.TraceType;
import org.csstudio.swt.rtplot.data.TimeDataSearch;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.editor.DataBrowserAwareView;
import org.csstudio.trends.databrowser2.model.AnnotationInfo;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.ModelListener;
import org.csstudio.trends.databrowser2.model.ModelListenerAdapter;
import org.csstudio.trends.databrowser2.model.PlotSample;
import org.csstudio.trends.databrowser2.model.PlotSamples;
import org.csstudio.trends.databrowser2.multiselectcombo.MultiSelectCombo;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

/** View for inspecting Waveform (Array) Samples of the current Model
 *  @author Kay Kasemir
 *  @author Will Rogers Show current waveform sample in plot, various bugfixes
 *  @author Takashi Nakamoto changed WaveformView to handle multiple items with
 *                           the same name.
 *  @author Xihui Chen (Added some work around to make it work for rap).
 */
@SuppressWarnings("nls")
public class WaveformView extends DataBrowserAwareView
{
    /** View ID registered in plugin.xml */
    final public static String ID =
        "org.csstudio.trends.databrowser.waveformview.WaveformView";

    /** Text used for the annotation that indicates waveform sample */
    final public static String ANNOTATION_TEXT = "Waveform view";

    /** PV Name(s) selector */
    private MultiSelectCombo pv_select;

    /** Plot */
    private RTValuePlot plot;

    /** Selector for first model_item current sample */
    private Slider sample_index;

    /** Timestamp of current sample(s). */
    private Text timestamp;

    /** Status/severity of current sample(s). */
    private Text status;

    /** Model of the currently active Data Browser plot or <code>null</code> */
    private Model model;

    /** Annotation(s) in data browser plot that indicate waveform sample(s) */
    private List<AnnotationInfo> waveform_annotations;

    private boolean changing_annotations = false;

    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> pending_move = null;

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
            for (ModelItem model_item : model_items) {
                if (item == model_item) {
                    model_items.remove(model_item);
                }
            }
            update(false);
        }

        @Override
        public void changedItemLook(final ModelItem item)
        {
            update(false);
        }

        @Override
        public void changedAnnotations()
        {
            if (changing_annotations)
                return;

            // Reacting as the user moves the annotation
            // would be too expensive.
            // Delay, canceling previous request, for "post-selection"
            // type update once the user stops moving the annotation for a little time
            if (pending_move != null)
                pending_move.cancel(false);
            pending_move = timer.schedule(WaveformView.this::userMovedAnnotation, 500, TimeUnit.MILLISECONDS);
        }

        @Override
        public void changedTimerange()
        {
            // Update selected sample to assert that it's one of the visible ones.
            if (model_items != null)
                showSelectedSample();
        }
    };

    /** Selected model item in model, or <code>null</code> */
    private List<ModelItem> model_items = null;

    /** Waveform for the currently selected sample */
    private List<WaveformValueDataProvider> waveforms = null;

    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite parent)
    {
        // Arrange disposal
        parent.addDisposeListener((DisposeEvent e) ->
        {   // Ignore current model after this view is disposed.
            if (model != null)
            {
                model.removeListener(model_listener);
                removeAnnotation();
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

        final String[] names = getAvailableItems();
        pv_select = new MultiSelectCombo(parent, names, SWT.READ_ONLY);
        pv_select.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        ((GridData)pv_select.getLayoutData()).widthHint = 300;
        pv_select.addSelectionListener (new SelectionAdapter() {
            //@Override
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                widgetDefaultSelected(event);
            }
            //@Override
            @Override
            public void widgetDefaultSelected(final SelectionEvent event) {
                List<ModelItem> modelItems = getModelItems(getAvailableItems());
                selectPV(modelItems);
            }}
        );

        final Button refresh = new Button(parent, SWT.PUSH);
        refresh.setText(Messages.SampleView_Refresh);
        refresh.setToolTipText(Messages.SampleView_RefreshTT);
        refresh.setLayoutData(new GridData());
        refresh.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {   // First item is "--select PV name--"
                List<ModelItem> modelItems = getModelItems(getAvailableItems());
                selectPV(modelItems);
           }
        });

        // =====================
        // ======= Plot ========
        // =====================
        plot = new RTValuePlot(parent);
        plot.getXAxis().setName(Messages.WaveformIndex);
        plot.getYAxes().get(0).setAutoscale(true);
        plot.getYAxes().get(0).useAxisName(false);
        plot.showLegend(false);
        plot.requestUpdate();
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

        final MenuManager mm = new MenuManager();
        mm.setRemoveAllWhenShown(true);

        final Menu menu = mm.createContextMenu(plot.getPlotControl());
        plot.getPlotControl().setMenu(menu);
        getSite().registerContextMenu(mm, null);

        mm.addMenuListener(new IMenuListener(){
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                mm.add(plot.getToolbarAction());
                mm.add(plot.getLegendAction());
                mm.add(new Separator());
                mm.add(new ToggleYAxisAction<Double>(plot, true));
                mm.add(new Separator());
                mm.add(plot.getSnapshotAction());
                mm.add(new ToggleYAxisAutoscaleAction<Double>(plot, true));
            }
        });

    }

    /** Return List of selected ModelItems
     *  by finding Model Items from checked items in PV list
     *  @param names list of all PV names available in list
     *  @returns List of ModelItems
     */
    private List<ModelItem> getModelItems(final String [] names)
    {
        List<ModelItem> modelItems = new ArrayList<ModelItem>();
        List<Integer> selectedIndices = pv_select.getSelectedIndices();
        if (selectedIndices.size() == 0)
            modelItems = null;
        else {
            for(int n=0; n<selectedIndices.size(); n++) {
                int item = selectedIndices.get(n);
                modelItems.add(model.getItem(names[item]));
            }
        }
        return modelItems;
    }

    /**Returns List of the Names of available ModelItems
     * @returns String List of names
     */
     private String[] getAvailableItems()
     {
         List<String> curr_names_list = new ArrayList<>();
         if (model != null) {
             for (ModelItem item : model.getItems())
                 curr_names_list.add(item.getName());
         }
         return curr_names_list.toArray(new String[curr_names_list.size()]);
     }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        pv_select.setFocus();
    }

    /** {@inheritDoc} */
    @Override
    protected void updateModel(final Model old_model, final Model model)
    {
        if (this.model == model)
            return;
        removeAnnotation();
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
            if (pv_select.isDisposed())
            {
                return;
            }
            if (model == null)
            {   // Clear/disable GUI
                pv_select.setItems(new String[] {});
                pv_select.setEnabled(false);
                selectPV(null);
                return;
            }

            // Show PV names
            final List<String> names_list = new ArrayList<>();
            for (ModelItem item : model.getItems())
                names_list.add(item.getName());
            final String[] names = names_list.toArray(new String[names_list.size()]);

            // Show new items, clear rest
            pv_select.setItems(names);
            pv_select.setEnabled(true);
            List<ModelItem> modelItems = getModelItems(getAvailableItems());
            selectPV(modelItems);

        });
    }

    /** Select given PV item (or <code>null</code>). */
    private void selectPV(final List<ModelItem> new_item)
    {

        // Delete all existing traces
        for (Trace<Double> trace : plot.getTraces())
            plot.removeTrace(trace);

        model_items = new_item;

        if (model_items == null || model_items.size() == 0)
        {
            sample_index.setEnabled(false);
            removeAnnotation();
            return;
        }

        removeAnnotation();

        // Prepare to show waveforms of model item in plot

        // Create trace for waveform
        waveforms = new ArrayList<WaveformValueDataProvider>();
        for(int n=0; n<new_item.size(); n++) {
            waveforms.add(new WaveformValueDataProvider());
            plot.addTrace(new_item.get(n).getResolvedDisplayName(), new_item.get(n).getUnits(), waveforms.get(n), new_item.get(n).getColor(), TraceType.SINGLE_LINE, 1, PointType.NONE, 5, 0);
        // Enable waveform selection and update slider's range
        }
        sample_index.setEnabled(true);
        showSelectedSample();

    }

    /** Show the current sample of the current model item. */
    private void showSelectedSample()
    {
        final int numItems = model_items.size();

        final int idx = sample_index.getSelection();

        String timestampText = null;
        String statusText = null;

        Instant firstWaveformSampleTime = null;

        for(int n=0; n<numItems; n++) {
            ModelItem model_item = model_items.get(n);
            // Get selected sample (= one waveform)
            final PlotSamples samples = model_item.getSamples();
            PlotSample sample;
            samples.getLock().lock();
            try
            {
                if (n == 0) {
                    sample_index.setMaximum(samples.size());
                    sample = samples.get(idx);
                }
                else {
                    sample = samples.get(0);
                    for (int s = 1; s < samples.size(); s++) {
                        if (VTypeHelper.getTimestamp(samples.get(s).getVType()).isAfter(firstWaveformSampleTime)) {
                            sample = samples.get(s-1);
                            break;
                        }
                    }
                }

            }
            finally
            {
                samples.getLock().unlock();
            }
            // Setting the value can be delayed while the plot is being updated
            final VType value = sample.getVType();
            final int waveformIndex = n;
            Activator.getThreadPool().execute(() -> waveforms.get(waveformIndex).setValue(value));
            if (value == null)
                clearInfo();
            else
            {
                updateAnnotation(n, sample.getPosition(), sample.getValue());
                if (n == 0) {
                    timestampText = new String();
                    statusText = new String();
                    int size = value instanceof VNumberArray ? ((VNumberArray)value).getData().size() : 1;
                    plot.getXAxis().setValueRange(0.0, (double)size);
                    statusText += NLS.bind(Messages.SeverityStatusFmt, VTypeHelper.getSeverity(value).toString(), VTypeHelper.getMessage(value));
                    timestampText += TimestampHelper.format(VTypeHelper.getTimestamp(value));
                    firstWaveformSampleTime = VTypeHelper.getTimestamp(value);
                }
                else {
                    statusText += "; " + NLS.bind(Messages.SeverityStatusFmt, VTypeHelper.getSeverity(value).toString(), VTypeHelper.getMessage(value));
                    timestampText += "; " + TimestampHelper.format(VTypeHelper.getTimestamp(value));
                }
            }
        }

        timestamp.setText(timestampText);
        status.setText(statusText);
        plot.requestUpdate();
    }

    /** Clear all the info fields. */
    private void clearInfo()
    {
        timestamp.setText("");
        status.setText("");
        removeAnnotation();
    }

    private void userMovedAnnotation()
    {
        if (waveform_annotations == null)
            return;
        for (AnnotationInfo annotation : model.getAnnotations())
        {   // Locate the annotation for this waveform
            int n = 0;
            for (AnnotationInfo waveform_annotation : waveform_annotations) {
                if (annotation.isInternal()  &&
                    annotation.getItemIndex() == waveform_annotation.getItemIndex() &&
                    annotation.getText().equals(waveform_annotation.getText()))
                {   // Locate index of sample for annotation's time stamp
                    final PlotSamples samples = model_items.get(n).getSamples();
                    final TimeDataSearch search = new TimeDataSearch();
                    final int idx;
                    samples.getLock().lock();
                    try
                    {
                        idx = search.findClosestSample(samples, annotation.getTime());
                    }
                    finally
                    {
                        samples.getLock().unlock();
                    }
                    // Update waveform view for that sample on UI thread
                    sample_index.getDisplay().asyncExec(() ->
                    {
                        sample_index.setSelection(idx);
                        showSelectedSample();
                    });
                    return;
                }
                n++;
            }
        }
    }

    private void removeAnnotation()
    {
        if (model != null && waveform_annotations != null)
        {
            final List<AnnotationInfo> modelAnnotations = new ArrayList<AnnotationInfo>(model.getAnnotations());
            for (AnnotationInfo waveform_annotation : waveform_annotations) {
                if (modelAnnotations.remove(waveform_annotation))
                {
                    changing_annotations = true;
                    model.setAnnotations(modelAnnotations);
                    changing_annotations = false;
                }
            }
        }
        waveform_annotations = null;
    }

    private void updateAnnotation(final int annotation_index, final Instant time, final double value)
    {
    if (waveform_annotations == null)
            waveform_annotations = new ArrayList<AnnotationInfo>();

        final List<AnnotationInfo> annotations = new ArrayList<AnnotationInfo>(model.getAnnotations());
        // Initial annotation offset
        Point offset = new Point(20, -20);
        // If already in model, note its offset and remove
        for (AnnotationInfo annotation : annotations)
        {
            if (annotation.getText().equals(ANNOTATION_TEXT + " " + model_items.get(annotation_index).getDisplayName()))
            {   // Update offset to where user last placed it
                offset = annotation.getOffset();
                annotations.remove(annotation);
                break;

            }
        }

        int i = 0;
        int item_index = 0;
        for (ModelItem item : model.getItems())
        {
            if (item == model_items.get(annotation_index))
            {
                item_index = i;
                break;
            }
            i++;
        }
        waveform_annotations.add(annotation_index, new AnnotationInfo(true, item_index, time, value, offset, ANNOTATION_TEXT + " " + model_items.get(annotation_index).getDisplayName()));
        annotations.add(waveform_annotations.get(annotation_index));
        changing_annotations = true;
        model.setAnnotations(annotations);
        changing_annotations = false;
    }

    public class ToggleYAxisAction<XTYPE extends Comparable<XTYPE>> extends Action
    {
        final private RTPlot<XTYPE> plot;

        public ToggleYAxisAction(final RTPlot<XTYPE> plot, final boolean is_visible)
        {
            super(plot.getYAxes().get(0).isLogarithmic() ? "Linear Axis" : "Logarithmic Axis", null);
            this.plot = plot;
        }

        public void updateText()
        {
            setText(plot.getYAxes().get(0).isLogarithmic() ? "Linear Axis" : "Logarithmic Axis");
        }

        @Override
        public void run()
        {
            plot.getYAxes().get(0).setLogarithmic(!plot.getYAxes().get(0).isLogarithmic());
            plot.requestUpdate();
        }
    }

    public class ToggleYAxisAutoscaleAction<XTYPE extends Comparable<XTYPE>> extends Action
    {
        final private RTPlot<XTYPE> plot;

        public ToggleYAxisAutoscaleAction(final RTPlot<XTYPE> plot, final boolean is_visible)
        {
            super("Autoscaling", Action.AS_CHECK_BOX);
            this.plot = plot;
            this.setChecked(plot.getYAxes().get(0).isAutoscale());
        }

        @Override
        public void run()
        {
            plot.getYAxes().get(0).setAutoscale(!plot.getYAxes().get(0).isAutoscale());
            plot.requestUpdate();
            this.setChecked(plot.getYAxes().get(0).isAutoscale());
        }
    }
}
