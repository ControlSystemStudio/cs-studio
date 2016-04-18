/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.opiwidget;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.swt.rtplot.RTPlotListener;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.trends.databrowser2.model.TimeHelper;
import org.csstudio.trends.databrowser2.ui.Controller;
import org.csstudio.trends.databrowser2.ui.ModelBasedPlot;
import org.diirt.datasource.ExpressionLanguage;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVWriter;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ListDouble;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/** EditPart that interfaces between the {@link DataBrowserWidgetFigure} visible on the screen
 *  and the {@link DataBrowserWidgedModel} that stores the persistent configuration.
 *  <p>
 *  Life cycle:
 *  <ol>
 *  <li>doCreateFigure()
 *  <li>setExecutionMode(RUN/EDIT) - Ignored
 *  <li>activate()
 *  <li>deactivate()
 *  </ol>
 *  For now, it does NOT support execution mode changes while activated.
 *
 *  @author Jaka Bobnar - Original selection value PV support
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DataBrowserWidgedEditPart extends AbstractWidgetEditPart
{
    private DataBrowserWidgetFigure gui;

    /** Data Browser controller for D.B. Model and Plot, used in run mode */
    private Controller controller = null;

    /** PV for writing the selected values */
    private AtomicReference<PVWriter<Object>> pv = new AtomicReference<>();

    /** Listener to plot, writing cursor data to pv.
     *  Only listening to plot if PV is defined
     */
    final private RTPlotListener<Instant> plot_listener = new RTPlotListener<Instant>()
    {
        @Override
        public void changedCursors()
        {
            // Create VTable value from selected samples
            final List<String> names = new ArrayList<>();
            final List<String> times = new ArrayList<>();
            final List<Double> values = new ArrayList<>();
            for (Trace<Instant> trace : gui.getDataBrowserPlot().getPlot().getTraces())
            {
                names.add(trace.getName());
                final Optional<PlotDataItem<Instant>> sample = trace.getSelectedSample();
                if (sample.isPresent())
                {
                    times.add(TimeHelper.format(sample.get().getPosition()));
                    values.add(sample.get().getValue());
                }
                else
                {
                    times.add("-");
                    values.add(Double.NaN);
                }
            }
            final VType value = ValueFactory.newVTable(
                Arrays.asList(String.class, String.class, double.class),
                Arrays.asList("Trace", "Timestamp", "Value"),
                Arrays.<Object>asList(names,times, convert(values)));

            final PVWriter<Object> safe_pv = pv.get();
            if (safe_pv != null)
                safe_pv.write(value);
        }
    };

    /** @param values {@link List} of {@link Double}
     *  @return {@link ListDouble}
     */
    private static ListDouble convert(final List<Double> values)
    {
        final double[] array = new double[values.size()];
        for (int i=0; i<array.length; ++i)
            array[i] = values.get(i);
        return new ArrayDouble(array);
    }

    /** @return Casted widget model */
    @Override
    public DataBrowserWidgedModel getWidgetModel()
    {
        return (DataBrowserWidgedModel) getModel();
    }

    /** @return Casted widget figure */
    public DataBrowserWidgetFigure getWidgetFigure()
    {
        return (DataBrowserWidgetFigure) getFigure();
    }

    /** {@inheritDoc}} */
    @Override
    protected IFigure doCreateFigure()
    {
        final DataBrowserWidgedModel model = getWidgetModel();
        gui = new DataBrowserWidgetFigure(this, model.getSelectionValuePv(), model.isShowValueLabels());
        return gui;
    }

    /** {@inheritDoc}} */
    @Override
    protected void registerPropertyChangeHandlers()
    {
        // Show hover value labels
        setPropertyChangeHandler(DataBrowserWidgedModel.PROP_SHOW_VALUE_LABELS,
            (Object oldValue, Object newValue, IFigure figure) ->
            {
                getWidgetFigure().setShowValueLabels((boolean) newValue);
                return false;
            });

}

    /** {@inheritDoc}} */
    @Override
    public void activate()
    {
        // In run mode, start controller, which will start model
        if (getExecutionMode() == ExecutionMode.RUN_MODE)
        {
            try
            {
                // Connect plot to model (created by OPI/GEF)
                final ModelBasedPlot plot_widget = gui.getDataBrowserPlot();
                controller = new Controller(null, getWidgetModel().createDataBrowserModel(),
                        plot_widget);
                controller.start();

                // Have PV for cursor data?
                final String pv_name = getWidgetModel().getSelectionValuePv();
                if (! pv_name.isEmpty())
                {
                    pv.set(PVManager.write(ExpressionLanguage.channel(pv_name)).async());
                    plot_widget.getPlot().addListener(plot_listener);
                }

                final MenuManager mm = new MenuManager();
                mm.add(plot_widget.getPlot().getToolbarAction());
                mm.add(plot_widget.getPlot().getLegendAction());
                mm.add(new OpenDataBrowserAction(this));
                final Control control = plot_widget.getPlot().getPlotControl();
                final Menu menu = mm.createContextMenu(control);
                control.setMenu(menu);
            }
            catch (Exception ex)
            {
                Logger.getLogger(Activator.ID).log(Level.SEVERE, "Cannot start Data Browser Widget", ex);
            }
        }
        super.activate();
    }

    /** {@inheritDoc}} */
    @Override
    public void deactivate()
    {
        // In run mode, stop the controller, which will stop the model
        if (getExecutionMode() == ExecutionMode.RUN_MODE)
        {
            if (controller != null) {
                controller.stop();
            }

            final PVWriter<Object> safe_pv = pv.getAndSet(null);
            if (safe_pv != null)
            {
                gui.getDataBrowserPlot().getPlot().removeListener(plot_listener);
                safe_pv.close();
            }
        }
        super.deactivate();
    }
}
