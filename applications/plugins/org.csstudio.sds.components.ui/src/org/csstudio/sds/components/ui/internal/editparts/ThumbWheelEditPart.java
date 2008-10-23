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
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * Controller for the ThumbWheel.
 * 
 * @author Alen Vrecko
 * 
 */
public class ThumbWheelEditPart extends AbstractWidgetEditPart {

	private ThumbWheelLogic logic;
	private ThumbWheelModel model;
	private RefreshableThumbWheelFigure figure;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		model = (ThumbWheelModel) getWidgetModel();

		logic = new ThumbWheelLogic(model.getValue(), model
				.getWholePartDigits(), model.getDecimalPartDigits());

		logic.setMax(model.getMax());
		logic.setMin(model.getMin());

		figure = new RefreshableThumbWheelFigure(logic.getIntegerWheels(),
				logic.getDecimalWheels());
		model.setWholePartDigits(logic.getIntegerWheels());
		model.setDecimalPartDigits(logic.getDecimalWheels());
		figure.setWheelFonts(model.getFont());
		figure.setInternalBorderColor(model.getInternalBorderColor());
		figure.setInternalBorderThickness(model.getInternalBorderWidth());

		figure.addWheelListener(new WheelListener() {

			public void decrementDecimalPart(int index) {
				if (getExecutionMode() == ExecutionMode.RUN_MODE) {
					logic.decrementDecimalDigitAt(index);
					updateWheelValues();
					model.setManualValue(logic.getValue());
				}
			}

			public void incrementDecimalPart(int index) {
				if (getExecutionMode() == ExecutionMode.RUN_MODE) {
					logic.incrementDecimalDigitAt(index);
					updateWheelValues();
					model.setManualValue(logic.getValue());
				}
			}

			public void decrementIntegerPart(int index) {
				if (getExecutionMode() == ExecutionMode.RUN_MODE) {
					logic.decrementIntigerDigitAt(index);
					updateWheelValues();
					model.setManualValue(logic.getValue());
				}
			}

			public void incrementIntegerPart(int index) {
				if (getExecutionMode() == ExecutionMode.RUN_MODE) {
					logic.incrementIntigerWheel(index);
					updateWheelValues();
					model.setManualValue(logic.getValue());
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

		figure.revalidate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// decimal wheels
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure figure = (RefreshableThumbWheelFigure) refreshableFigure;

				logic.setDecimalWheels((Integer) newValue);
				figure.setDecimalDigitsPart(logic.getDecimalWheels());
				model.setDecimalPartDigits(logic.getDecimalWheels());
				updateWheelValues();
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_DECIMAL_DIGITS_PART,
				handler);

		// integer wheels
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure figure = (RefreshableThumbWheelFigure) refreshableFigure;

				logic.setIntegerWheels((Integer) newValue);
				figure.setWholeDigitsPart(logic.getIntegerWheels());
				model.setWholePartDigits(logic.getIntegerWheels());
				updateWheelValues();
				return true;
			}
		};

		setPropertyChangeHandler(ThumbWheelModel.PROP_WHOLE_DIGITS_PART,
				handler);

		// min
		handler = new IWidgetPropertyChangeHandler() {
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
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				logic.setMax((Double) newValue);
				updateWheelValues();

				return true;
			}
		};

		setPropertyChangeHandler(ThumbWheelModel.PROP_MAX, handler);

		// value
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				logic.setValue((Double) newValue);
				updateWheelValues();
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_VALUE, handler);

		// font
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure figure = (RefreshableThumbWheelFigure) refreshableFigure;
				figure.setWheelFonts((FontData) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_FONT, handler);

		// border color
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure figure = (RefreshableThumbWheelFigure) refreshableFigure;
				figure.setInternalBorderColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_INTERNAL_FRAME_COLOR,
				handler);

		// border width
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure figure = (RefreshableThumbWheelFigure) refreshableFigure;
				figure.setInternalBorderThickness((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_INTERNAL_FRAME_THICKNESS,
				handler);

	}
	
	/**
	 * Thumb wheel widgets are always disabled in edit mode.
	 */
	@Override
	protected boolean forceDisabledInEditMode() {
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

		public Double getMax() {
			return max.doubleValue();
		}

		public void setMax(Double max) {
			this.max = Double.isNaN(max) ? null : new BigDecimal(Double
					.toString(max), MathContext.UNLIMITED);
		}

		public Double getMin() {
			return min.doubleValue();
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