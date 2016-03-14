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
package org.csstudio.opibuilder.widgets.editparts;

import java.math.BigDecimal;
import java.math.MathContext;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.model.ThumbWheelModel;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.simplepv.VTypeHelper;
import org.csstudio.swt.widgets.figures.ThumbWheelFigure;
import org.csstudio.swt.widgets.figures.ThumbWheelFigure.WheelListener;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;

/**
 * Controller for the ThumbWheel.
 *
 * @author Alen Vrecko, Jose Ortega, Xihui Chen, Takashi Nakamoto
 *
 */
public class ThumbWheelEditPart extends AbstractPVWidgetEditPart {

    private ThumbWheelLogic logic;
    private ThumbWheelModel model;
    private ThumbWheelFigure figure;
    private IPVListener pvLoadLimitsListener;
    private Display meta = null;


    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        model = (ThumbWheelModel) getWidgetModel();

        logic = new ThumbWheelLogic(0, model
                .getWholePartDigits(), model.getDecimalPartDigits());

        logic.setMax(model.getMaximum());
        logic.setMin(model.getMinimum());

        figure = new ThumbWheelFigure(logic.getIntegerWheels(),
                logic.getDecimalWheels(), this.getExecutionMode() == ExecutionMode.RUN_MODE);
        model.setWholePartDigits(logic.getIntegerWheels());
        model.setDecimalPartDigits(logic.getDecimalWheels());
        FontData fontData = model.getFont().getFontData();
        figure.setWheelFont(CustomMediaFactory.getInstance().getFont(
                fontData.getName(), fontData.getHeight(),
                fontData.getStyle()));
        figure.setInternalBorderColor(model.getInternalBorderColor());
        figure.setInternalFocusedBorderColor(model.getInternalFocusedBorderColor());
        figure.setInternalBorderThickness(model.getInternalBorderWidth());
        figure.setButtonVisibility(model.isButtonVisible());

        markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);

        figure.addWheelListener(new WheelListener() {

            @Override
            public void decrementDecimalPart(int index) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    logic.decrementDecimalDigitAt(index);
                    updateWheelValues();
                    //write value to pv if pv name is not empty
                    if(getWidgetModel().getPVName().trim().length() > 0){
                        double doubleValue = logic.getValue();
                        setPVValue(AbstractPVWidgetModel.PROP_PVNAME, doubleValue);
                    }
                }
            }

            @Override
            public void incrementDecimalPart(int index) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    logic.incrementDecimalDigitAt(index);
                    updateWheelValues();
                    //write value to pv if pv name is not empty
                    if(getWidgetModel().getPVName().trim().length() > 0){
                        double doubleValue = logic.getValue();
                        setPVValue(AbstractPVWidgetModel.PROP_PVNAME, doubleValue);
                    }
                }
            }

            @Override
            public void decrementIntegerPart(int index) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    logic.decrementIntigerDigitAt(index);
                    updateWheelValues();
                    //write value to pv if pv name is not empty
                    if(getWidgetModel().getPVName().trim().length() > 0){
                        double doubleValue = logic.getValue();
                        setPVValue(AbstractPVWidgetModel.PROP_PVNAME, doubleValue);
                    }
                }
            }

            @Override
            public void incrementIntegerPart(int index) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    logic.incrementIntigerWheel(index);
                    updateWheelValues();
                    //write value to pv if pv name is not empty
                    if(getWidgetModel().getPVName().trim().length() > 0){
                        double doubleValue = logic.getValue();
                        setPVValue(AbstractPVWidgetModel.PROP_PVNAME, doubleValue);
                    }
                }
            }
        });

        updateWheelValues();
        return figure;
    }

    private void updateWheelValues() {

        // update all wheels
        int limit = model.getWholePartDigits();

        for (int i = 0; i < limit; i++) {
            figure.setIntegerWheel(i, logic.getIntegerDigitAt(i));
        }

        limit = model.getDecimalPartDigits();

        for (int i = 0; i < limit; i++) {
            figure.setDecimalWheel(i, logic.getDecimalDigitAt(i));
        }

        // update minus sign
        if (logic.getValue() < 0) {
            figure.showMinus(true);
        } else {
            figure.showMinus(false);
        }

        // update minus sign
        if (model.getDecimalPartDigits() > 0) {
            figure.showDot(true);
        } else {
            figure.showDot(false);
        }

        figure.revalidate();
    }

    @Override
    public ThumbWheelModel getWidgetModel() {
        return (ThumbWheelModel) super.getWidgetModel();
    }

    @Override
    protected void doActivate() {
        super.doActivate();
        registerLoadLimitsListener();
    }

    /**
     *
     */
    private void registerLoadLimitsListener() {
        if (getExecutionMode() == ExecutionMode.RUN_MODE) {
            final ThumbWheelModel model = getWidgetModel();
            if (model.isLimitsFromPV()) {
                IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
                if (pv != null) {
                    if (pvLoadLimitsListener == null)
                        pvLoadLimitsListener = new IPVListener.Stub() {
                            @Override
                            public void valueChanged(IPV pv) {
                                VType value = pv.getValue();
                                Display displayInfo = VTypeHelper.getDisplayInfo(value);
                                if (value != null && displayInfo != null) {
                                    Display new_meta = displayInfo;
                                    if (meta == null || !meta.equals(new_meta)) {
                                        meta = new_meta;
                                        model.setPropertyValue(ThumbWheelModel.PROP_MAX,
                                                meta.getUpperDisplayLimit());
                                        model.setPropertyValue(ThumbWheelModel.PROP_MIN,
                                                meta.getLowerDisplayLimit());
                                    }
                                }
                            }


                        };
                    pv.addListener(pvLoadLimitsListener);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {

        // PV value
        IWidgetPropertyChangeHandler pvhandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                if(newValue != null){
                    double doubleValue = VTypeHelper.getDouble((VType)newValue);
                    logic.setValue(doubleValue);
                    updateWheelValues();
                }
                return true;
            }
        };
        setPropertyChangeHandler(ThumbWheelModel.PROP_PVVALUE, pvhandler);

        IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                registerLoadLimitsListener();
                return false;
            }
        };
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME,
                pvNameHandler);

        // decimal wheels
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ThumbWheelFigure figure = (ThumbWheelFigure) refreshableFigure;

                logic.setDecimalWheels((Integer) newValue);
                figure.setDecimalDigits(logic.getDecimalWheels());
                model.setDecimalPartDigits(logic.getDecimalWheels());
                updateWheelValues();
                return true;
            }
        };
        setPropertyChangeHandler(ThumbWheelModel.PROP_DECIMAL_DIGITS_PART,
                handler);

        // integer wheels
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ThumbWheelFigure figure = (ThumbWheelFigure) refreshableFigure;

                logic.setIntegerWheels((Integer) newValue);
                figure.setIntegerDigits(logic.getIntegerWheels());
                model.setWholePartDigits(logic.getIntegerWheels());
                updateWheelValues();
                return true;
            }
        };

        setPropertyChangeHandler(ThumbWheelModel.PROP_INTEGER_DIGITS_PART,
                handler);

        // min
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                logic.setMin((Double) newValue);
                updateWheelValues();
                return true;
            }
        };
        setPropertyChangeHandler(ThumbWheelModel.PROP_MIN, handler);

        // max
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                logic.setMax((Double) newValue);
                updateWheelValues();

                return true;
            }
        };

        setPropertyChangeHandler(ThumbWheelModel.PROP_MAX, handler);

        // value
//        handler = new IWidgetPropertyChangeHandler() {
//            public boolean handleChange(final Object oldValue,
//                    final Object newValue, final IFigure refreshableFigure) {
//                logic.setValue((Double) newValue);
//                updateWheelValues();
//                return true;
//            }
//        };
//        setPropertyChangeHandler(ThumbWheelModel.PROP_VALUE, handler);

        // font
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ThumbWheelFigure figure = (ThumbWheelFigure) refreshableFigure;
                FontData fontData = ((OPIFont)newValue).getFontData();
                figure.setWheelFont(CustomMediaFactory.getInstance().getFont(
                        fontData.getName(), fontData.getHeight(),
                        fontData.getStyle()));
                return true;
            }
        };
        setPropertyChangeHandler(ThumbWheelModel.PROP_FONT, handler);

        // border color
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ThumbWheelFigure figure = (ThumbWheelFigure) refreshableFigure;
                figure.setInternalBorderColor(((OPIColor)newValue).getSWTColor());
                return true;
            }
        };
        setPropertyChangeHandler(ThumbWheelModel.PROP_INTERNAL_FRAME_COLOR,
                handler);

        // focused border color
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ThumbWheelFigure figure = (ThumbWheelFigure) refreshableFigure;
                figure.setInternalFocusedBorderColor(((OPIColor)newValue).getSWTColor());
                return true;
            }
        };
        setPropertyChangeHandler(ThumbWheelModel.PROP_INTERNAL_FOCUSED_FRAME_COLOR,
                handler);

        // border width
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ThumbWheelFigure figure = (ThumbWheelFigure) refreshableFigure;
                figure.setInternalBorderThickness((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ThumbWheelModel.PROP_INTERNAL_FRAME_THICKNESS,
                handler);

        // show button
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ThumbWheelFigure figure = (ThumbWheelFigure) refreshableFigure;
                figure.setButtonVisibility((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ThumbWheelModel.PROP_SHOW_BUTTONS,
                handler);
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
        private BigDecimal max = null;
        private BigDecimal min = null;
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
            if ((max != null && greater(value, max))
                    || greater(value, wheelMax)) {
                return;
            }

            // if we are below lower limit just drop to lower limit
            if (less(value, min)) {
                value = min;
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
            else if (max != null && greater(newValue, max)) {
                value = max;
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
            if ((min != null && less(value, min)) || less(value, wheelMin)) {
                return;
            }

            // if we are beyond upper limit just drop to upper limit
            if (greater(value, max)) {
                value = max;

            } else if (greater(value, wheelMax)) {
                value = wheelMax;
            }

            // if we are decrementing below the lower limit just set to lower
            // limit
            else if (min != null && less(newValue, min)) {
                value = min;
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
            this.max = Double.isNaN(max) ? null : new BigDecimal(Double
                    .toString(max), MathContext.UNLIMITED);
        }

        public void setMin(Double min) {
            this.min = Double.isNaN(min) ? null : new BigDecimal(Double
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

            StringBuilder nines = new StringBuilder();
            for (int i = 0; i < integerWheels; i++) {
                nines.append("9");
            }

            if (decimalWheels > 0) {
                nines.append(".");
                for (int i = 0; i < decimalWheels; i++) {
                    nines.append("9");
                }
            }

            wheelMax = new BigDecimal(nines.toString(), MathContext.UNLIMITED);
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
            StringBuilder nines = new StringBuilder();
            if (integerWheels > 0) {
                for (int i = 0; i < integerWheels; i++) {
                    nines.append("9");
                }
            } else {
                nines.append("0");
            }

            nines.append(".");
            for (int i = 0; i < decimalWheels; i++) {
                nines.append("9");
            }

            wheelMax = new BigDecimal(nines.toString(), MathContext.UNLIMITED);
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

    @Override
    public Object getValue() {
        Double doubleObject = new Double(logic.getValue());
        return doubleObject;
    }

    @Override
    public void setValue(Object value) {
        if(value instanceof Number) {
            double doubleValue = ((Number) value).doubleValue();
            logic.setValue(doubleValue);
        }
        else if(value instanceof String) {
            double doubleValue = Double.parseDouble((String) value);
            logic.setValue(doubleValue);
        }else
            super.setValue(value);
    }
}
