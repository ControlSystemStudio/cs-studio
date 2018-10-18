/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.waveformview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
import org.csstudio.trends.databrowser2.model.PlotSampleArray;
import org.csstudio.trends.databrowser2.model.PlotSamples;
import org.csstudio.ui.util.widgets.MultipleSelectionCombo;
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
    private MultipleSelectionCombo<ModelItem> pv_select;

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
    final private List<AnnotationInfo> waveform_annotations = new ArrayList<>();

    private boolean changing_annotations = false;

    final private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

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
            List<ModelItem> oldSelectedItems = pv_select.getSelection();
            List<ModelItem> items = new ArrayList<>();
            List<ModelItem> selectedItems = new ArrayList<>();
            for(ModelItem modelItem : model.getItems())
                items.add(modelItem);
            for(ModelItem modelItem : oldSelectedItems) {
                if (modelItem != item)
                    selectedItems.add(modelItem);
            }
            pv_select.setItems(items);
            pv_select.setSelection(selectedItems);
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
            if (waveforms.size() > 0) {
                Instant selectedInstant = Instant.MIN;
                int selectedIndex = sample_index.getSelection();
                if (selectedIndex < waveform_samples.size())
                    selectedInstant = waveform_samples.get(sample_index.getSelection()).getPosition();
                updateTimestamps();
                resetSlider();
                int newSelectedIndex = new TimeDataSearch().findClosestSample(waveform_samples, selectedInstant);
                // If the timestamp wasn't found, it should be outside of the new timerange.
                // Put the slider at the start or end as appropriate.
                if (newSelectedIndex == -1) {
                    if (waveform_samples.size() == 0 || waveform_samples.get(0).getPosition().compareTo(selectedInstant) > 0) {
                        newSelectedIndex = 0;
                    } else if (waveform_samples.get(waveform_samples.size() - 1).getPosition().compareTo(selectedInstant) < 0) {
                        newSelectedIndex = waveform_samples.size() - 1;
                    }
                }
                sample_index.setSelection(newSelectedIndex);
                showSelectedSample();
            }
        }
    };

    /** Selected model items in model. */
    private List<ModelItem> model_items = new ArrayList<>();

    /** Merged ordered list of all timestamps of all PlotSamples in all items. */
    final private PlotSampleArray waveform_samples = new PlotSampleArray();

    /**
     * Take all the samples from all model items that are within the plot range,
     * and sort them. The timestamps provide the index for the slider.
     */
    private void updateTimestamps() {
        List<PlotSample> samples = new ArrayList<>();
        Instant plot_start = model.getStartTime();
        Instant plot_end = model.getEndTime();
        if (model_items != null) {
            for (ModelItem item : model_items) {
                for (int i = 0; i < item.getSamples().size(); i++) {
                    PlotSample sample = item.getSamples().get(i);
                    if (sample.getPosition().isAfter(plot_start) && sample.getPosition().isBefore(plot_end)) {
                        samples.add(sample);
                    }
                }
            }
        }
        Collections.sort(samples, (s1, s2) -> {
            return s1.getPosition().compareTo(s2.getPosition());
        });
        waveform_samples.set(samples);
    }

    /** Waveforms for the currently selected samples */
    final private List<WaveformValueDataProvider> waveforms = new ArrayList<>();

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
                removeAnnotations();
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

        pv_select = new MultipleSelectionCombo<>(parent, 0);
        pv_select.setItems(getModelItems());
        pv_select.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns-2, 1));
        pv_select.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                selectPVs(pv_select.getSelection());
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
            {
                selectPVs(pv_select.getSelection());
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
        // It's important for indexing that the slider 'thumb' takes up only one data point.
        sample_index.setThumb(1);
        sample_index.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));
        sample_index.addSelectionListener(new SelectionAdapter()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                showSelectedSample();
            }
        });
        resetSlider();

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
                mm.add(new ToggleYAxisAutoscaleAction<Double>(plot, true));
                mm.add(new Separator());
                mm.add(plot.getSnapshotAction());
            }
        });

    }

    /**
     * Ensure the slider has the correct number of options.
     */
    private void resetSlider() {
        // Setting the maximum to 0 has no effect. Setting the maximum to 1 means
        // that there is one option in the slider - i.e. you can't move it.
        // If there are no samples we want to set the maximum to 1.
        sample_index.setMaximum(waveform_samples.size() == 0 ? 1 : waveform_samples.size());
    }

    /** Return List of selected ModelItems
     *  by finding Model Items from checked items in PV list
     *  @param names list of all PV names available in list
     *  @returns List of ModelItems
     */
    private List<ModelItem> getModelItems()
    {
        List<ModelItem> modelItemList = new ArrayList<>();
        if (model != null) {
            for (ModelItem item : model.getItems())
                modelItemList.add(item);
        }
        return modelItemList;
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
        removeAnnotations();
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
                return;

            final List<ModelItem> oldSelection = new ArrayList<>(pv_select.getSelection());
            pv_select.setItems(getModelItems());
            if (model == null) {
                pv_select.setEnabled(false);
                selectPVs(null);
                return;
            }

            // Show new items, clear rest
            pv_select.setEnabled(true);
            final List<ModelItem> newSelection = new ArrayList<>();
            for(ModelItem oldItem : oldSelection) {
                if (getModelItems().contains(oldItem))
                    newSelection.add(oldItem);
            }
            pv_select.setSelection(newSelection);
            selectPVs(oldSelection);

        });
    }

    /** Select given PV items. */
    private void selectPVs(final List<ModelItem> new_items)
    {

        // Delete all existing traces
        for (Trace<Double> trace : plot.getTraces())
            plot.removeTrace(trace);

        if (new_items == null) {
            model_items.clear();
        } else {
            model_items = new_items;
        }
        updateTimestamps();

        removeAnnotations();

        if (model_items.isEmpty())
        {
            sample_index.setEnabled(false);
            return;
        }

        // Prepare to show waveforms of model item in plot
        waveforms.clear();
        // Create trace for waveform
        for(int n=0; n<new_items.size(); n++) {
            waveforms.add(new WaveformValueDataProvider());
            plot.addTrace(new_items.get(n).getResolvedDisplayName(), new_items.get(n).getUnits(), waveforms.get(n), new_items.get(n).getColor(), TraceType.SINGLE_LINE, 1, PointType.NONE, 5, 0);
        // Enable waveform selection and update slider's range
        }
        sample_index.setEnabled(true);
        if (waveforms.size() > 0)
            showSelectedSample();

    }

    /** Show the current sample of the current model item. */
    private void showSelectedSample()
    {
        final int numItems = model_items.size();

        String timestampText = null;
        String statusText = null;
        resetSlider();

        if (sample_index.getSelection() >= waveform_samples.size()) {
            // Rapid update where there aren't enough timestamps for the current slider
            return;
        }
        Instant sliderTime = waveform_samples.get(sample_index.getSelection()).getPosition();

        for(int n=0; n<numItems; n++) {
            ModelItem model_item = model_items.get(n);
            // Get selected sample (= one waveform)
            final PlotSamples samples = model_item.getSamples();
            PlotSample sample = samples.get(0);
            samples.getLock().lock();
            try
            {
                // Choose the sample with either the matching timestamp or the one after.
                final int idx = new TimeDataSearch().findSampleGreaterOrEqual(samples, sliderTime);
                sample = samples.get(idx);
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
        removeAnnotations();
    }

    private void userMovedAnnotation()
    {
        if (waveform_annotations == null)
            return;
        for (AnnotationInfo annotation : model.getAnnotations())
        {   // Locate the annotation for this waveform
            for (AnnotationInfo waveform_annotation : waveform_annotations) {
                if (annotation.isInternal()  &&
                    annotation.getItemIndex() == waveform_annotation.getItemIndex() &&
                    annotation.getText().equals(waveform_annotation.getText()))
                {   // Locate index of sample for annotation's time stamp
                    // By first locating the relevant samples
                    if (waveform_annotation.getTime().compareTo(annotation.getTime()) == 0) {
                        // The annotation hasn't moved position, so we don't need to do anything.
                        continue;
                    }
                    for(ModelItem model_item : model_items) {
                        if (annotation.getText().contains(model_item.getDisplayName())) {
                            final int idx = new TimeDataSearch().findClosestSample(waveform_samples, annotation.getTime());
                            // Update waveform view for that sample on UI thread
                            sample_index.getDisplay().asyncExec(() ->
                            {
                                if (! (idx == sample_index.getSelection())) {
                                    sample_index.setSelection(idx);
                                    showSelectedSample();
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void removeAnnotations()
    {
        if (model != null)
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
        waveform_annotations.clear();
    }

    private void updateAnnotation(final int annotation_index, final Instant time, final double value)
    {

        final List<AnnotationInfo> annotations = new ArrayList<AnnotationInfo>(model.getAnnotations());
        // Initial annotation offset
        Point offset = new Point(20, -20);
        // If already in model, note its offset and remove
        for (AnnotationInfo annotation : annotations)
        {
            if (annotation.getText().equals(buildAnnotationText(annotation_index)))
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
        if (waveform_annotations.size() > annotation_index)
            waveform_annotations.remove(annotation_index);
        waveform_annotations.add(annotation_index, new AnnotationInfo(true, item_index, time, value, offset, buildAnnotationText(annotation_index)));
        annotations.add(waveform_annotations.get(annotation_index));
        changing_annotations = true;
        model.setAnnotations(annotations);
        changing_annotations = false;
    }

    private String buildAnnotationText(final Integer annotation_index) {
        return ANNOTATION_TEXT + " " + model_items.get(annotation_index).getDisplayName();
    }

    public class ToggleYAxisAction<XTYPE extends Comparable<XTYPE>> extends Action
    {
        final private RTPlot<XTYPE> plot;

        public ToggleYAxisAction(final RTPlot<XTYPE> plot, final boolean is_visible)
        {
            super("Logarithmic Axis", Action.AS_CHECK_BOX);
            this.plot = plot;
            this.setChecked(plot.getYAxes().get(0).isLogarithmic());
        }

        @Override
        public void run()
        {
            plot.getYAxes().get(0).setLogarithmic(!plot.getYAxes().get(0).isLogarithmic());
            plot.requestUpdate();
            this.setChecked(plot.getYAxes().get(0).isLogarithmic());
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
