/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, Member of the Helmholtz Association,
 * (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.ui.internal.figures;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Orientable;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.ScrollBar;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * A slider figure.
 *
 * @author Sven Wende
 * @version $Revision: 1.30 $
 *
 */
public final class SimpleSliderFigure extends Panel implements IAdaptable {
    /**
     * Listeners that react on slider events.
     */
    private final List<ISliderListener> _sliderListeners;

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;

    /**
     * The scroll bar figure.
     */
    private final MyScrollBar _scrollBar;

    /**
     * A label, which display the current value.
     */
    private final Label _valueLabel;

    /**
     * Lower border of the displayed value.
     */
    private int _min = 0;

    /**
     * Upper border of the displayed value.
     */
    private int _max = 100;

    /**
     * The current value.
     */
    private int _currentValue = 30;

    /**
     * The precision of the scrollbar.
     */
    private int _scrollbarPrecision = 100;

    /**
     * The potenz for the precision.
     */
    private int _decimalPlaces = 2;

    /**
     * The minimum wide for the slider.
     */
    private int _sliderWide = 5;

    /**
     * The "show value as text" flag.
     */
    private boolean _showValueAsText = false;

    /**
     * The original minimum value.
     */
    private double _originalMin;

    /**
     * The original maximum value.
     */
    private double _originalMax;

    /**
     * The original manual value.
     */
    private double _originalManVal;

    /**
     * The original value.
     */
    private double _originalVal;

    /**
     * Flag which is used to disable slider events. When the current value is set on the scrollbar,
     * eventing must be turned off.
     */
    private boolean _populateEvents = true;

    private boolean _enable;

    private CrossedOutAdapter _crossedOutAdapter;

    private RhombusAdapter _rhombusAdapter;

    /**
     * Standard constructor.
     */
    public SimpleSliderFigure() {
        _sliderListeners = new ArrayList<ISliderListener>();

        BorderLayout layout = new BorderLayout();
        layout.setVerticalSpacing(2);
        layout.setHorizontalSpacing(2);

        setLayoutManager(layout);

        _valueLabel = new Label();
        add(_valueLabel, BorderLayout.TOP);
        _valueLabel.setVisible(_showValueAsText);

        _scrollBar = createScrollbarFigure();
        add(_scrollBar, BorderLayout.CENTER);

    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);

    }

    /**
     * Creates the scrollbar.
     *
     * @return the scrollbar figure
     */
    private MyScrollBar createScrollbarFigure() {
        MyScrollBar bar = new MyScrollBar();
        bar.setExtent(5);
        bar.setMaximum(1000);
        bar.setMinimum(1);
        bar.setValue(400);
        bar.setStepIncrement(1);
        bar.setOrientation(Orientable.VERTICAL);
        bar.setBackgroundColor(ColorConstants.blue);
        Ellipse thumb = new Ellipse();
        thumb.setSize(new Dimension(40, 40));
        thumb.setFont(CustomMediaFactory.getInstance().getDefaultFont(SWT.BOLD));
        thumb.setBackgroundColor(ColorConstants.red);

        thumb.setBorder(new SchemeBorder(SchemeBorder.SCHEMES.RIDGED));
        bar.validate();

        // add listener
        bar.addPropertyChangeListener(RangeModel.PROPERTY_VALUE, new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent event) {
                fireManualValueChange((Integer) event.getNewValue());
            }
        });

        return bar;
    }

    /**
     * Inform all slider listeners, that the manual value has changed.
     *
     * @param newManualValue
     *            the new manual value
     */
    private void fireManualValueChange(final int newManualValue) {
        int tmp = newManualValue;

        if (tmp < _min) {
            tmp = _min;
        }
        if (tmp > _max) {
            tmp = _max;
        }

        if (_populateEvents) {
            for (ISliderListener l : _sliderListeners) {
                l.sliderValueChanged( ((double) tmp) / _scrollbarPrecision);
            }
        }
    }

    /**
     * Set the "populate events" flag.
     *
     * @param populateEvents
     *            the "populate events" flag.
     */
    public void setPopulateEvents(final boolean populateEvents) {
        _populateEvents = populateEvents;
    }

    /**
     * Sets if the current value should also be shown as text.
     *
     * @param showValueAsText
     *            True if the value should be shown as text, false otherwise
     */
    public void setShowValueAsText(final boolean showValueAsText) {
        _showValueAsText = showValueAsText;
        _valueLabel.setVisible(_showValueAsText);
    }

    /**
     * Set the minimum value.
     *
     * @param min
     *            The minimum value.
     */
    public void setMin(final double min) {
        _originalMin = min;
        updateScrollbar();
    }

    /**
     * Set the maximum value.
     *
     * @param max
     *            The maximum value.
     */
    public void setMax(final double max) {
        _originalMax = max;
        updateScrollbar();
    }

    /**
     * Refreshes the scroolbar.
     */
    private void updateScrollbar() {
//        if (isEnabled()) {
            _min = (int) (_originalMin * _scrollbarPrecision);
            _max = (int) (_originalMax * _scrollbarPrecision);
            _scrollBar.setMinimum(_min);
            _scrollBar.setMaximum(_max + _sliderWide);

            _currentValue = (int) Math.round(_originalVal * _scrollbarPrecision);

            int settedValue = _currentValue;
            // update scrollbar
            if (_currentValue < _min) {
                // current value is out of the sliders range -> disable the
                // slider
                // 07.01.08 the slider shouldn't be disabled any more
                // _scrollBar.setEnabled(false);
                // _scrollBar.setValue(_currentValue);
                settedValue = _min;
            } else if (_currentValue > _max) {
                settedValue = _max;
            }
            _scrollBar.setValue(settedValue);
            _scrollBar.invalidate();
//        }
    }

    /**
     * Sets the real max value for the scrollbar based on the given max value.
     *
     * @param max
     *            The base for the real max value
     */
    private void setScrollbarMax(final int max) {

        _scrollBar.setMaximum(max + _sliderWide);

    }

    /**
     * Sets the count of decimal places for this slider.
     *
     * @param decimalPlaces
     *            The precision
     */
    public void setDecimalPlaces(final int decimalPlaces) {
        _decimalPlaces = decimalPlaces;
        this.updateValueText();
    }

    /**
     * Gets the precision of this slider.
     *
     * @return The precision
     */
    public int getDecimalPlaces() {
        return _decimalPlaces;
    }

    /**
     * Sets the value for the precision of this slider.
     *
     * @param precision
     *            The precision
     */
    private void setScrollbarPrecision(final int precision) {


        // double min = this.getDoubleFor(_min);
        // double max = this.getDoubleFor(_max);
        int minWide = _sliderWide / _scrollbarPrecision;
        // double value = this.getDoubleFor(_currentValue);
        // double manualValue = this.getDoubleFor(_manualValue);
        _scrollbarPrecision = precision;
        // this.setMin(min);
        // this.setMax(max);
        this.setSliderWide(minWide);
        // this.setValue(value);
        // this.setManualValue(manualValue);

        updateScrollbar();
    }

    /**
     * Sets the wide for the slider.
     *
     * @param wide
     *            The wide
     */
    public void setSliderWide(final int wide) {

        _sliderWide = wide * _scrollbarPrecision;
        this.setScrollbarMax(_max);
        _scrollBar.setExtent(_sliderWide);

    }

    /**
     * Gets the slider wide.
     *
     * @return int The slider wide
     */
    public int getSliderWide() {
        return (_sliderWide / _scrollbarPrecision);
    }

    /**
     * Set the increment value.
     *
     * @param increment
     *            The increment value.
     */
    public void setIncrement(final double increment) {

        this.setScrollbarPrecision((int) Math.pow(10, this.getCountOfDecimals(increment)));
        int inc = (int) (increment * _scrollbarPrecision);
        if (inc == 0) {
            inc = 1;
        }

        _scrollBar.setStepIncrement(inc);
        _scrollBar.setPageIncrement(inc);

    }

    /**
     * Gets the count of numbers after the comma.
     *
     * @param value
     *            The double value
     * @return int The count of numbers after the comma
     */
    private int getCountOfDecimals(final double value) {
        // This is a bit of a hack: because the double value may not precisely
        // represent the value that was entered by the user (for example, 0.1
        // cannot be represented exactly by any finite binary fraction), the
        // scale may be much bigger than expected if the BigDecimal is created
        // directly from the double value (for example, with 0.1, the scale
        // would is 55). So we convert the double value to a string first, in
        // the hope that it will be reasonably rounded, and then convert that
        // string to a BigDecimal in order to get its scale.
        BigDecimal bd = new BigDecimal(Double.toString(value));
        int scale = bd.scale();

        // The value is constrained to 0..3 to prevent precisions that get too
        // large.
        return Math.max(0, Math.min(scale, 3));
    }

    /**
     * Sets the orientation.
     *
     * @param horizontal
     *            true for horizontal and false for vertical layout
     */
    public void setOrientation(final boolean horizontal) {
        _scrollBar.setOrientation(horizontal ? Orientable.HORIZONTAL : Orientable.VERTICAL);
    }

    @Override
    public void setForegroundColor(final Color fg) {
        _scrollBar.setAlarmColor(fg);
        // super.setForegroundColor(fg);
    }

    @Override
    public void setBackgroundColor(final Color bg) {
        _scrollBar.setBackgroundColor(bg);
        // super.setForegroundColor(fg);
    }

    /**
     * Set the current slider value.
     *
     * <b>Important:</b> This method should only get called by the Controller and not by the figure
     * itself!
     *
     * @param value
     *            the current slider value
     */
    public void setValue(final double value) {
        _originalVal = value;

        // CentralLogger.getInstance().debug(this, "setValue("+value+")");
        // // store current value
        // _currentValue = (int) (value * _scrollbarPrecision);
        //
        // // update scrollbar
        // if (_currentValue < _min || _currentValue > _max) {
        // // current value is out of the sliders range -> disable the slider
        // _scrollBar.setEnabled(false);
        // _scrollBar.setValue(_currentValue);
        // } else {
        // _scrollBar.setEnabled(true);
        // _scrollBar.setValue(_currentValue);
        // _scrollBar.invalidate();
        // }

        updateScrollbar();

        updateValueText();

    }

    /**
     * Set the current manual value.
     *
     * <b>Important:</b> This method should only get called by the Controller and not by the figure
     * itself!
     *
     * @param value
     *            the current slider value
     */
    public void setManualValue(final double value) {
        _originalManVal = value;
        updateValueText();
    }

    /**
     * Updates the value labels text.
     */
    private void updateValueText() {
        // if (isEnabled()) {
        // update the value label text
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(_decimalPlaces);
        _valueLabel
                .setText("" + format.format(_originalVal) + " [MAN: " + format.format(_originalManVal) + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // }
    }

    /**
     * Add a slider listener.
     *
     * @param listener
     *            The slider listener to add.
     */
    public void addSliderListener(final ISliderListener listener) {
        _sliderListeners.add(listener);
    }

    /**
     * Remove a slider listener.
     *
     * @param listener
     *            The slider listener that is to be removed.
     */
    public void removeSliderListener(final ISliderListener listener) {
        _sliderListeners.remove(listener);
    }

    /**
     * This method is a tribute to unit tests, which need a way to test the performance of the
     * figure implementation. Implementors should produce some random changes and refresh the
     * figure, when this method is called.
     *
     */
    public void randomNoiseRefresh() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean value) {
        _enable = value;
        _scrollBar.enableButtons(_enable);
        _scrollBar.setEnabled(true);
        super.setEnabled(value);
    }

    @Override
    protected void paintFigure(final Graphics graphics) {
        graphics.setBackgroundColor(getBackgroundColor());
        bounds.crop(this.getInsets());
        graphics.fillRectangle(bounds);
        graphics.setBackgroundColor(getBackgroundColor());
        graphics.setForegroundColor(getForegroundColor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if (_borderAdapter == null) {
                _borderAdapter = new BorderAdapter(this);
            }
            return _borderAdapter;
        } else if (adapter == ICrossedFigure.class) {
            if (_crossedOutAdapter == null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        } else if (adapter == IRhombusEquippedWidget.class) {
            if (_rhombusAdapter == null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }

        return null;
    }

    /**
     * Definition of listeners that react on slider events.
     *
     * @author Sven Wende
     * @version $Revision: 1.30 $
     *
     */
    public interface ISliderListener {
        /**
         * React on a slider event.
         *
         * @param newValue
         *            The new slider value.
         */
        void sliderValueChanged(double newValue);
    }

    /**
     *
     * A Scrollbar how can disable separately the Buttons
     *
     * @author hrickens
     * @author $Author: jhatje $
     * @version $Revision: 1.30 $
     * @since 16.04.2010
     */
    private class MyScrollBar extends ScrollBar{

        public void enableButtons(final boolean enable) {
            getButtonUp().setEnabled(enable);
            if(enable) {
                getThumb().setBackgroundColor(new Color(null, 25,25,25));
            } else {
                getThumb().setBackgroundColor(new Color(null, 155,155,155));
            }
            getButtonDown().setEnabled(enable);
        }

        public void setAlarmColor(final Color color) {
            getButtonDown().setBackgroundColor(color);
            getButtonUp().setBackgroundColor(color);
        }
    }
}
