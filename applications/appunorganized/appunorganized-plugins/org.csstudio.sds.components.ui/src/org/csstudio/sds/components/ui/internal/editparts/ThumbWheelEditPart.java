/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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

import java.math.BigDecimal;
import java.math.MathContext;

import org.csstudio.sds.components.model.ThumbWheelModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableThumbWheelFigure;
import org.csstudio.sds.components.ui.internal.figures.RefreshableThumbWheelFigure.WheelListener;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Controller for the ThumbWheel.
 *
 * @author Alen Vrecko
 *
 */
public class ThumbWheelEditPart extends AbstractWidgetEditPart {

    private ThumbWheelLogic _logic;
    private ThumbWheelModel _model;
    private RefreshableThumbWheelFigure _figure;

    /**
     * {@inheritDoc}
     */
    @Override
    protected final IFigure doCreateFigure() {
        _model = (ThumbWheelModel) getWidgetModel();

        _logic = new ThumbWheelLogic(_model.getValue(), _model
                .getWholePartDigits(), _model.getDecimalPartDigits());

        _logic.setMax(_model.getMax());
        _logic.setMin(_model.getMin());

        _figure = new RefreshableThumbWheelFigure(_logic.getIntegerWheels(),
                _logic.getDecimalWheels());
        _model.setWholePartDigits(_logic.getIntegerWheels());
        _model.setDecimalPartDigits(_logic.getDecimalWheels());
        _figure.setWheelFonts(getModelFont(ThumbWheelModel.PROP_FONT));
        _figure.setInternalBorderColor(getModelColor(ThumbWheelModel.PROP_INTERNAL_FRAME_COLOR));
        _figure.setInternalBorderThickness(_model.getInternalBorderWidth());

        _figure.addWheelListener(new WheelListener() {

            public void decrementDecimalPart(int index) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    _logic.decrementDecimalDigitAt(index);
                    updateWheelValues();
                    _model.setManualValue(_logic.getValue());
                }
            }

            public void incrementDecimalPart(int index) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    _logic.incrementDecimalDigitAt(index);
                    updateWheelValues();
                    _model.setManualValue(_logic.getValue());
                }
            }

            public void decrementIntegerPart(int index) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    _logic.decrementIntigerDigitAt(index);
                    updateWheelValues();
                    _model.setManualValue(_logic.getValue());
                }
            }

            public void incrementIntegerPart(int index) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    _logic.incrementIntigerWheel(index);
                    updateWheelValues();
                    _model.setManualValue(_logic.getValue());
                }
            }
        });

        updateWheelValues();
        return _figure;
    }

    private void updateWheelValues() {

        // update all wheels
        int limit = _model.getWholePartDigits();

        for (int i = 0; i < limit; i++) {
            _figure.setIntegerWheel(i, _logic.getIntegerDigitAt(i));
        }

        limit = _model.getDecimalPartDigits();

        for (int i = 0; i < limit; i++) {
            _figure.setDecimalWheel(i, _logic.getDecimalDigitAt(i));
        }

        // update minus sign
        if (_logic.getValue() < 0) {
            _figure.showMinus(true);
        } else {
            _figure.showMinus(false);
        }

        _figure.revalidate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void registerPropertyChangeHandlers() {
        // decimal wheels
        setPropertyChangeHandler(ThumbWheelModel.PROP_DECIMAL_DIGITS_PART,
                                 getPropDecimalDigitdPart());
        // integer wheels
        setPropertyChangeHandler(ThumbWheelModel.PROP_WHOLE_DIGITS_PART,
                                 getPropWholeDigitsPart());
        // min
        setPropertyChangeHandler(ThumbWheelModel.PROP_MIN, getPropMax());
        // max
        setPropertyChangeHandler(ThumbWheelModel.PROP_MAX, getPropMin());
        // value
        setPropertyChangeHandler(ThumbWheelModel.PROP_VALUE, getPropValue());

        // font
        setPropertyChangeHandler(ThumbWheelModel.PROP_FONT, new FontChangeHandler<RefreshableThumbWheelFigure>(){

            @Override
            protected void doHandle(RefreshableThumbWheelFigure figure, Font font) {
                figure.setWheelFonts(font);
            }

        });

        // border color
        setPropertyChangeHandler(ThumbWheelModel.PROP_INTERNAL_FRAME_COLOR,
                new ColorChangeHandler<RefreshableThumbWheelFigure>(){
                    @Override
                    protected void doHandle(RefreshableThumbWheelFigure figure, Color color) {
                        figure.setInternalBorderColor(color);
                    }
        });

        // border width
        setPropertyChangeHandler(ThumbWheelModel.PROP_INTERNAL_FRAME_THICKNESS,
                                 getPropBorderWidth());

    }

    /**
     * @return
     */
    private IWidgetPropertyChangeHandler getPropBorderWidth() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                RefreshableThumbWheelFigure figure = (RefreshableThumbWheelFigure) refreshableFigure;
                figure.setInternalBorderThickness((Integer) newValue);
                return true;
            }
        };
        return handler;
    }

    /**
     * @return
     */
    private IWidgetPropertyChangeHandler getPropValue() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                _logic.setValue((Double) newValue);
                updateWheelValues();
                return true;
            }
        };
        return handler;
    }

    /**
     * @return
     */
    private IWidgetPropertyChangeHandler getPropMin() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                _logic.setMax((Double) newValue);
                updateWheelValues();

                return true;
            }
        };
        return handler;
    }

    /**
     * @return
     */
    private IWidgetPropertyChangeHandler getPropMax() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                _logic.setMin((Double) newValue);
                updateWheelValues();
                return true;
            }
        };
        return handler;
    }

    /**
     * @return
     */
    private IWidgetPropertyChangeHandler getPropWholeDigitsPart() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                RefreshableThumbWheelFigure figure = (RefreshableThumbWheelFigure) refreshableFigure;

                _logic.setIntegerWheels((Integer) newValue);
                figure.setWholeDigitsPart(_logic.getIntegerWheels());
                _model.setWholePartDigits(_logic.getIntegerWheels());
                updateWheelValues();
                return true;
            }
        };
        return handler;
    }

    /**
     * @return
     */
    private IWidgetPropertyChangeHandler getPropDecimalDigitdPart() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                RefreshableThumbWheelFigure figure = (RefreshableThumbWheelFigure) refreshableFigure;

                _logic.setDecimalWheels((Integer) newValue);
                figure.setDecimalDigitsPart(_logic.getDecimalWheels());
                _model.setDecimalPartDigits(_logic.getDecimalWheels());
                updateWheelValues();
                return true;
            }
        };
        return handler;
    }

    /**
     * Thumb wheel widgets are always disabled in edit mode.
     * @return true as always disabled.
     */
    @Override
    protected final boolean forceDisabledInEditMode() {
        return true;
    }

    /**
     * Represents the "brain" behind the ThumbWheel. It represents the wheel and
     * its values. Integer wheels are indexed from right to left. Decimal wheels
     * are indexed left to right from the decimal point.
     *
     * <p>
     * Note the inherent precision of value double is 15 decimal places
     * therefore you cannot have more than 15 wheels.
     * <p>
     *
     * @author Alen Vrecko
     *
     */
    private static class ThumbWheelLogic {

        private static final char BEYOND_LIMIT_CHAR = 'X';

        private BigDecimal value;

        private int integerWheels;
        private int decimalWheels;
        private BigDecimal _max = null;
        private BigDecimal _min = null;
        private BigDecimal wheelMax;
        private BigDecimal wheelMin;

        public static final int WHEEL_LIMIT = 15;

        public ThumbWheelLogic(double value, int integerWheels,
                int decimalWheels) {
            setValue(value);
            setIntegerWheels(integerWheels);
            setDecimalWheels(decimalWheels);
        }

        /**
         * Increments the integer digit on a specific index. E.g. on 567.12
         * calling increment for - 0 will set the value to 568.12, with index -
         * 2 will result in 667.12. Will not set beyond max value.
         *
         * @param index
         * @param val
         */
        public void incrementIntigerWheel(int index) {
            increment(index, "1E");
        }

        /**
         * Increments the decimal digit on a specific index. E.g. on 567.12
         * calling increment for - 0 will set the value to 567.22, with index -
         * 1 will result in 567.11. Will not go bellow max value.
         *
         * @param index
         * @param val
         */
        public void incrementDecimalDigitAt(int index) {
            increment(index, "0.1E-");
        }

        private boolean isZero(BigDecimal num) {
            return num.signum() == 0;
        }

        private boolean equalSign(BigDecimal a, BigDecimal b) {
            return a.signum() == b.signum();
        }

        private boolean greater(BigDecimal a, BigDecimal b) {
            if (b == null) {
                return false;
            }
            return a.compareTo(b) > 0;
        }

        private boolean less(BigDecimal a, BigDecimal b) {
            if (b == null) {
                return false;
            }
            return a.compareTo(b) < 0;
        }

        private void increment(int index, String numberGenerator) {
            // generate new number using the string ("1E" or "1E-" or similar)
            BigDecimal decrementor = new BigDecimal(numberGenerator + index,
                    MathContext.UNLIMITED);
            BigDecimal newValue = value.add(decrementor);

            // handle over the zero handling
            if (!isZero(newValue) && !equalSign(value, newValue)) {
                newValue = value.negate().add(decrementor);
            }

            // if value is already beyond the upper limit or upper wheel limit
            // just ignore the request
            if ((_max != null && greater(value, _max))
                    || greater(value, wheelMax)) {
                return;
            }

            // if we are below lower limit just drop to lower limit
            if (less(value, _min)) {
                value = _min;
            } else if (less(value, wheelMin)) {
                value = wheelMin;
            }

            // if we are incrementing above the wheel upper limit just set to
            // wheel upper limit
            else if (greater(newValue, wheelMax)) {
                value = wheelMax;
            }
            // if we are incrementing beyond the upper limit just set to upper
            // limit
            else if (_max != null && greater(newValue, _max)) {
                value = _max;
            } else {
                value = newValue;
            }
        }

        /**
         * Decrements the integer digit on a specific index. E.g. on 567.12
         * calling increment for - 0 will set the value to 468.12, with index -
         * 2 will result in 467.12. Will not go below min value.
         *
         * @param index
         * @param val
         */
        public void decrementIntigerDigitAt(int index) {
            decrement(index, "-1E");
        }

        /**
         * Decrements the decimal digit on a specific index. E.g. on 567.12
         * calling increment for - 0 will set the value to 568.02, with index -
         * 1 will result in 567.11. Will not go bellow min value.
         *
         * @param index
         * @param val
         */
        public void decrementDecimalDigitAt(int index) {
            decrement(index, "-0.1E-");
        }

        private void decrement(int index, String numberGenerator) {
            // generate new number using the string ("1E" or "1E-" or similar)
            BigDecimal decrementor = new BigDecimal(numberGenerator + index,
                    MathContext.UNLIMITED);
            BigDecimal newValue = value.add(decrementor);

            // handle over the zero handling
            if (!isZero(newValue) && !equalSign(value, newValue)) {
                newValue = value.negate().add(decrementor);
            }

            // if value is already beyond the lower limit or lower wheel limit
            // just ignore the request
            if ((_min != null && less(value, _min)) || less(value, wheelMin)) {
                return;
            }

            // if we are beyond upper limit just drop to upper limit
            if (greater(value, _max)) {
                value = _max;

            } else if (greater(value, wheelMax)) {
                value = wheelMax;
            }

            // if we are decrementing below the lower limit just set to lower
            // limit
            else if (_min != null && less(newValue, _min)) {
                value = _min;
            }

            // if we are decrementing below the wheel lower limit just set to
            // wheel lower limit
            else if (less(newValue, wheelMin)) {
                value = wheelMin;
            }

            else {
                value = newValue;
            }
        }

        /**
         * Returns a digit in the specified index. E.g. for 324.23 getting index
         * 0,1,2 would return 4,2,3. If the number is beyond max in will return
         * proper digit of max. Same goes for min.
         *
         * @param index
         * @return
         */
        public char getIntegerDigitAt(int index) {
            // check if number is beyond inherent wheel limit
            if (beyondDisplayLimit()) {
                return BEYOND_LIMIT_CHAR;
            }
            String plainString = value.toPlainString();
            // get rid of decimal part
            int dot = plainString.indexOf('.');
            if (dot >= 0) {
                plainString = plainString.substring(0, dot);
            }
            // get rid of leading minus
            if (plainString.startsWith("-")) {
                plainString = plainString.substring(1);
            }

            if (index >= plainString.length()) {
                return '0';
            }

            return plainString.charAt(plainString.length() - 1 - index);

        }

        /**
         * Returns a digit in the specified index. E.g. for 324.23 getting index
         * 0,1 would return 2,3.
         *
         * @param index
         * @return
         */
        public char getDecimalDigitAt(int index) {
            // check if number is beyond inherent wheel limit
            if (beyondDisplayLimit()) {
                return BEYOND_LIMIT_CHAR;
            }

            String plainString = value.toPlainString();

            if (plainString.indexOf('.') < 0) {
                return '0';
            }

            plainString = plainString.substring(plainString.indexOf('.') + 1);
            if (index >= plainString.length()) {
                return '0';
            }
            return plainString.charAt(index);

        }

        /**
         * Returns true if the value is bigger than the wheels can represent.
         *
         * @return
         */
        public boolean beyondDisplayLimit() {
            return greater(value, wheelMax) || less(value, wheelMin);

        }

        public void setMax(Double max) {
            this._max = Double.isNaN(max) ? null : new BigDecimal(Double
                    .toString(max), MathContext.UNLIMITED);
        }

        public void setMin(Double min) {
            this._min = Double.isNaN(min) ? null : new BigDecimal(Double
                    .toString(min), MathContext.UNLIMITED);
        }

        public int getIntegerWheels() {
            return integerWheels;
        }

        public void setIntegerWheels(int integerWheels) {
            if (integerWheels + decimalWheels > WHEEL_LIMIT) {
                this.integerWheels = WHEEL_LIMIT - decimalWheels;
                return;
            }

            this.integerWheels = integerWheels;

            String nines = "";
            for (int i = 0; i < integerWheels; i++) {
                nines += "9";
            }

            if (decimalWheels > 0) {
                nines += ".";
                for (int i = 0; i < decimalWheels; i++) {
                    nines += "9";
                }
            }

            wheelMax = new BigDecimal(nines, MathContext.UNLIMITED);
            wheelMin = new BigDecimal("-" + nines, MathContext.UNLIMITED);
        }

        public int getDecimalWheels() {
            return decimalWheels;

        }

        public void setDecimalWheels(int decimalWheels) {
            if (integerWheels + decimalWheels > WHEEL_LIMIT) {
                this.decimalWheels = WHEEL_LIMIT - integerWheels;
                return;
            }
            this.decimalWheels = decimalWheels;
            String nines = "";
            if (integerWheels > 0) {
                for (int i = 0; i < integerWheels; i++) {
                    nines += "9";
                }
            } else {
                nines += "0";
            }

            nines += ".";
            for (int i = 0; i < decimalWheels; i++) {
                nines += "9";
            }

            wheelMax = new BigDecimal(nines, MathContext.UNLIMITED);
            wheelMin = new BigDecimal("-" + nines, MathContext.UNLIMITED);
        }

        public double getValue() {
            return value.doubleValue();
        }

        public void setValue(double value) {
            setValue(Double.toString(value));
        }

        public void setValue(String value) {
            this.value = new BigDecimal(value, MathContext.UNLIMITED);
        }

    }

}