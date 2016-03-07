/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.sds.components.ui.internal.editparts;

import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.sds.components.model.StripChartModel;
import org.csstudio.sds.components.ui.internal.figures.StripChartFigure;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Display;

/**
 * Edit part for the strip chart widget.
 *
 * @author Joerg Rathlev
 */
public final class StripChartEditPart extends AbstractChartEditPart {

    /**
     * The delay before the first value is sent to the figure, in milliseconds.
     */
    private static final long FIRST_UPDATE_DELAY = 2000;

    /**
     * The current value of each channel.
     */
    private double[] _currentValue;

    /**
     * The figure that is managed by this edit part.
     */
    private StripChartFigure _figure;

    /**
     * The timer which runs the update at the specified interval.
     */
    private Timer _updateTimer;

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        StripChartModel model = (StripChartModel) getWidgetModel();
        int valuesPerDataSeries = numberOfValuesPerSeries(model);
        double lastValueTime = model.getUpdateInterval() * (valuesPerDataSeries - 1);
        _figure = new StripChartFigure(model.numberOfDataSeries(),
                valuesPerDataSeries, lastValueTime);
        initializeCommonFigureProperties(_figure, model);
        initializeXAxisFigureProperties(model);
        initializeValueProperties(model);
        initializeUpdateTask(model);
        return _figure;
    }

    /**
     * Returns the number of data values that are recorded for each data series.
     *
     * @param model
     *            the model.
     * @return the number of data values that are recorded.
     */
    public int numberOfValuesPerSeries(final StripChartModel model) {
        return ((int) Math.ceil(model.getXAxisTimespan() / model.getUpdateInterval())) + 1;
    }

    /**
     * Initializes the x-axis properties of the figure.
     *
     * @param model
     *            the model.
     */
    private void initializeXAxisFigureProperties(final StripChartModel model) {
        _figure.setXAxisTimespan(model.getXAxisTimespan());
    }

    /**
     * Initializes the current values.
     *
     * @param model
     *            the model.
     */
    private void initializeValueProperties(final StripChartModel model) {
        _currentValue = new double[model.numberOfDataSeries()];
        for (int i = 0; i < model.numberOfDataSeries(); i++) {
            // Note: we don't forward the current value to the figure here. This
            // would cause the initial (default) value to be plotted as a data
            // point, which is not what we want.
            _currentValue[i] = model.getCurrentValue(i);
            _figure.setPlotEnabled(i, model.isPlotEnabled(i));
        }
    }

    /**
     * Creates the update task if the display is in run mode.
     *
     * @param model
     *            the model.
     */
    private void initializeUpdateTask(final StripChartModel model) {
        if (getExecutionMode() == ExecutionMode.RUN_MODE) {
            _updateTimer = new Timer(this + "-UpdateTimer", true);
            long updateInterval = Math.round(model.getUpdateInterval() * 1000);
            _updateTimer.scheduleAtFixedRate(new UpdateTask(),
                    FIRST_UPDATE_DELAY, updateInterval);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();
        registerDataPropertyChangeHandlers();
    }

    /**
     * Sets the current value of the specified data series.
     *
     * @param index
     *            the index of the data series.
     * @param value
     *            the current value.
     */
    private synchronized void setCurrentValue(final int index,
            final double value) {
        _currentValue[index] = value;
    }

    /**
     * Registers the property change handlers for the data properties.
     */
    private void registerDataPropertyChangeHandlers() {
        /**
         * Change handler for the waveform data properties.
         */
        class DataChangeHandler implements IWidgetPropertyChangeHandler {

            private final int _index;

            /**
             * Constructor.
             *
             * @param index
             *            the index of the data array.
             */
            DataChangeHandler(final int index) {
                _index = index;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                double value = (Double) newValue;
                StripChartEditPart.this.setCurrentValue(_index, value);
                return false;
            }
        }

        /**
         * Change handler for the plot enablement properties.
         */
        class EnablePlotChangeHandler implements IWidgetPropertyChangeHandler {

            private final int _index;

            /**
             * Constructor.
             *
             * @param index
             *            the index of the data array.
             */
            EnablePlotChangeHandler(final int index) {
                _index = index;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                StripChartFigure figure = (StripChartFigure) refreshableFigure;
                boolean enabled = (Boolean) newValue;
                figure.setPlotEnabled(_index, enabled);
                return true;
            }
        }

        StripChartModel model = (StripChartModel) getWidgetModel();
        for (int i = 0; i < model.numberOfDataSeries(); i++) {
            setPropertyChangeHandler(StripChartModel.valuePropertyId(i),
                    new DataChangeHandler(i));
            setPropertyChangeHandler(StripChartModel.enablePlotPropertyId(i),
                    new EnablePlotChangeHandler(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate() {
        if (_updateTimer != null) {
            _updateTimer.cancel();
        }
        super.deactivate();
    }

    /**
     * Task that forwards the current values of all channels to the figure.
     */
    private class UpdateTask extends TimerTask {

        /**
         * Forwards the current values of all channels to the figure.
         */
        @Override
        public void run() {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    // Note: this is safe from deadlocks only under the
                    // assumption that no other thread ever holds the monitor
                    // lock of the enclosing StripChartEditorPart instance
                    // while waiting for the main thread (display thread). The
                    // display thread can wait for the StripChartEditorPart
                    // monitor lock both here as well is when calling the
                    // setCurrentValue method via the property change handler.
                    synchronized (StripChartEditPart.this) {
                        for (int i = 0; i < _currentValue.length; i++) {
                            _figure.addValue(i, _currentValue[i]);
                        }
                    }
                    _figure.repaint();
                }
            });
        }
    }

}
