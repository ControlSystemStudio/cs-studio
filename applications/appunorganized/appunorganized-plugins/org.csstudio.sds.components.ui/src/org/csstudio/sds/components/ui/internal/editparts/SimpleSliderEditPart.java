/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.sds.components.model.SimpleSliderModel;
import org.csstudio.sds.components.ui.internal.figures.SimpleSliderFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EditPart controller for the Slider widget. The controller mediates between
 * {@link SimpleSliderModel} and {@link SimpleSliderFigure}.
 *
 * @author Sven Wende & Stefan Hofer
 *
 */
public final class SimpleSliderEditPart extends AbstractWidgetEditPart {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleSliderEditPart.class);

    /**
     * A UI job, which is used to reset the manual value of the slider figure
     * after a certain amount of time.
     */
    private UIJob _resetManualValueDisplayJob;

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        final SimpleSliderModel model = (SimpleSliderModel) getWidgetModel();

        final SimpleSliderFigure slider = new SimpleSliderFigure();
        slider.addSliderListener(new SimpleSliderFigure.ISliderListener() {
            @Override
            public void sliderValueChanged(final double newValue) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    model.setPropertyManualValue(SimpleSliderModel.PROP_VALUE, newValue);

                    slider.setManualValue(newValue);

                    if (_resetManualValueDisplayJob == null) {
                        _resetManualValueDisplayJob = new UIJob("reset") {
                            @Override
                            public IStatus runInUIThread(
                                    final IProgressMonitor monitor) {
                                slider.setManualValue(model.getValue());
                                slider.setValue(model.getValue());
                                return Status.OK_STATUS;
                            }
                        };

                    }
                    _resetManualValueDisplayJob.schedule(5000);
                } else {
                    LOG.info("Slider value changed");
                }
            }
        });

        slider.setPopulateEvents(false);
        slider.setMax(model.getMax());
        slider.setMin(model.getMin());
        slider.setIncrement(model.getIncrement());
        slider.setValue(model.getValue());
        slider.setShowValueAsText(model.isShowValueAsText());
        slider.setManualValue(model.getValue());
        slider.setOrientation(model.isHorizontal());
        slider.setDecimalPlaces(model.getPrecision());
        slider.setSliderWide(model.getSliderWidth());
        slider.setPopulateEvents(true);
        slider.setEnabled(getExecutionMode().equals(ExecutionMode.RUN_MODE)
                && model.isAccesible());
        return slider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // value
        IWidgetPropertyChangeHandler valHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                SimpleSliderFigure slider = (SimpleSliderFigure) refreshableFigure;
                slider.setPopulateEvents(false);
                slider.setValue((Double) newValue);
                slider.setManualValue((Double) newValue);
                slider.setPopulateEvents(true);
                return true;
            }
        };
        setPropertyChangeHandler(SimpleSliderModel.PROP_VALUE, valHandler);

        // min
        IWidgetPropertyChangeHandler minHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                SimpleSliderFigure slider = (SimpleSliderFigure) refreshableFigure;
                slider.setPopulateEvents(false);
                // slider.setMin((Integer) newValue);
                slider.setMin((Double) newValue);
                slider.setPopulateEvents(true);
                return true;
            }
        };
        setPropertyChangeHandler(SimpleSliderModel.PROP_MIN, minHandler);

        // max
        IWidgetPropertyChangeHandler maxHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                SimpleSliderFigure slider = (SimpleSliderFigure) refreshableFigure;
                slider.setPopulateEvents(false);
                slider.setMax((Double) newValue);
                slider.setPopulateEvents(true);
                return true;
            }
        };
        setPropertyChangeHandler(SimpleSliderModel.PROP_MAX, maxHandler);

        // increment
        IWidgetPropertyChangeHandler incrementHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                SimpleSliderFigure slider = (SimpleSliderFigure) refreshableFigure;
                slider.setPopulateEvents(false);
                slider.setIncrement((Double) newValue);
                slider.setPopulateEvents(true);
                return true;
            }
        };
        setPropertyChangeHandler(SimpleSliderModel.PROP_INCREMENT,
                incrementHandler);

        // precision
        IWidgetPropertyChangeHandler precisionHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                SimpleSliderFigure slider = (SimpleSliderFigure) refreshableFigure;
                slider.setPopulateEvents(false);
                slider.setDecimalPlaces((Integer) newValue);
                slider.setPopulateEvents(true);
                return true;
            }
        };
        setPropertyChangeHandler(SimpleSliderModel.PROP_PRECISION,
                precisionHandler);

        // show value as text
        IWidgetPropertyChangeHandler showValueAsTextHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                SimpleSliderFigure slider = (SimpleSliderFigure) refreshableFigure;
                slider.setShowValueAsText((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(SimpleSliderModel.PROP_SHOW_VALUE_AS_TEXT,
                showValueAsTextHandler);

        // minSliderWide
        IWidgetPropertyChangeHandler minSliderWideHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                SimpleSliderFigure slider = (SimpleSliderFigure) refreshableFigure;
                slider.setPopulateEvents(false);
                slider.setSliderWide((Integer) newValue);
                slider.setPopulateEvents(true);
                return true;
            }
        };
        setPropertyChangeHandler(SimpleSliderModel.PROP_SLIDER_WIDTH,
                minSliderWideHandler);

        // orientation
        IWidgetPropertyChangeHandler orientationHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                SimpleSliderFigure slider = (SimpleSliderFigure) refreshableFigure;
                slider.setPopulateEvents(false);
                slider.setOrientation((Boolean) newValue);
                slider.setPopulateEvents(true);
                return true;
            }
        };
        setPropertyChangeHandler(SimpleSliderModel.PROP_ORIENTATION,
                orientationHandler);

        // Color
        ColorChangeHandler<SimpleSliderFigure> colorHandler = new ColorChangeHandler<SimpleSliderFigure>() {

            @Override
            protected void doHandle(final SimpleSliderFigure figure, final Color color) {
//                figure.setPopulateEvents(false);
                figure.setForegroundColor(color);
//                figure.setPopulateEvents(true);
            }
        };
        setPropertyChangeHandler(SimpleSliderModel.PROP_COLOR_FOREGROUND,
                                 colorHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IValue getSample(final int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(index
                    + " is not a valid sample index");
        }

        SimpleSliderModel model = (SimpleSliderModel) getWidgetModel();
        double value = model.getValue();
        int precision = model.getPrecision();
        ITimestamp timestamp = TimestampFactory.now();

        // Note: the IValue implementations require a Severity, otherwise the
        // format() method will throw a NullPointerException. We don't really
        // have a severity here, so we fake one. This may cause problems for
        // clients who rely on getting a meaningful severity from the IValue.
        ISeverity severity = ValueFactory.createOKSeverity();

        // Fake some metadata because it is required for an IValue.
        INumericMetaData md = ValueFactory.createNumericMetaData(0, 0, 0, 0, 0,
                0, precision, "");

        IDoubleValue result = ValueFactory.createDoubleValue(timestamp,
                severity, null, md, Quality.Original, new double[] { value });

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        // always one sample
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean forceDisabledInEditMode() {
        return true;
    }
}
