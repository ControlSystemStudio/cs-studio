package org.csstudio.sds.components.ui.internal.editparts;

import java.math.BigDecimal;
import java.math.MathContext;

import org.csstudio.sds.components.model.ThumbWheelModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableThumbWheelFigure;
import org.csstudio.sds.components.ui.internal.figures.RefreshableThumbWheelFigure.WheelListener;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
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

		figure = new RefreshableThumbWheelFigure(model.getWholePartDigits(),
				model.getDecimalPartDigits());
		figure.setWheelFonts(model.getFont());
		figure.setInternalBorderColor(model.getInternalBorderColor());
		figure.setInternalBorderThickness(model.getInternalBorderWidth());
		logic = new ThumbWheelLogic(model.getValue(), model
				.getWholePartDigits(), model.getDecimalPartDigits());

		logic.setMax(model.getMax());
		logic.setMin(model.getMin());

		figure.addWheelListener(new WheelListener() {

			public void decrementDecimalPart(int index) {
				logic.decrementDecimalDigitAt(index);
				updateWheelValues();
				model.setValue(logic.getValue());
			}

			public void incrementDecimalPart(int index) {
				logic.incrementDecimalDigitAt(index);
				updateWheelValues();
				model.setValue(logic.getValue());
			}

			public void decrementIntegerPart(int index) {
				logic.decrementIntigerDigitAt(index);
				updateWheelValues();
				model.setValue(logic.getValue());
			}

			public void incrementIntegerPart(int index) {
				logic.incrementIntigerWheel(index);
				updateWheelValues();
				model.setValue(logic.getValue());

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
				RefreshableThumbWheelFigure rectangle = (RefreshableThumbWheelFigure) refreshableFigure;
				rectangle.setDecimalDigitsPart((Integer) newValue);
				logic.setDecimalWheels((Integer) newValue);
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
				RefreshableThumbWheelFigure rectangle = (RefreshableThumbWheelFigure) refreshableFigure;
				rectangle.setWholeDigitsPart((Integer) newValue);
				logic.setIntegerWheels((Integer) newValue);
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
				RefreshableThumbWheelFigure rectangle = (RefreshableThumbWheelFigure) refreshableFigure;
				logic.setValue(Double.toString((Double) newValue));
				updateWheelValues();
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_VALUE, handler);

		// value
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure rectangle = (RefreshableThumbWheelFigure) refreshableFigure;
				rectangle.setWheelFonts((FontData) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_FONT, handler);

		// border color
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure rectangle = (RefreshableThumbWheelFigure) refreshableFigure;
				rectangle.setInternalBorderColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_INTERNAL_FRAME_COLOR,
				handler);

		// border width
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure rectangle = (RefreshableThumbWheelFigure) refreshableFigure;
				rectangle.setInternalBorderThickness((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_INTERNAL_FRAME_THICKNESS,
				handler);

	}

	/**
	 * Represents the "brain" behind the ThumbWheel. It represents the wheel and
	 * its values. Integer wheels are indexed from right to left. Decimal wheels
	 * are indexed left to right from the decimal point. The implementation uses
	 * BigDecimal extensively it is not the most efficient way but the
	 * performance should still be sufficient.
	 * 
	 * @author Alen Vrecko
	 * 
	 */
	private static class ThumbWheelLogic {

		private static final char BEYOND_LIMIT_CHAR = 'X';

		private BigDecimal value;

		private int integerWheels;
		private int decimalWheels;
		private double max = Double.NaN;
		private double min = Double.NaN;
		private double wheelMax;
		private double wheelMin;

		public static final int WHEEL_INTEGER_LIMIT = 10;
		public static final int WHEEL_DECIMAL_LIMIT = 10;

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

		private void increment(int index, String numberGenerator) {
			// generate new number using the string ("1E" or "1E-" or similar)
			BigDecimal decrementor = new BigDecimal(numberGenerator + index,
					MathContext.UNLIMITED);
			BigDecimal newValue = value.add(decrementor);

			// handle over the zero handling
			if (newValue.signum() != 0 && (value.signum() != newValue.signum())) {
				newValue = value.negate().add(decrementor);
			}

			// if value is already beyond the upper limit or upper wheel limit
			// just ignore the request
			if ((!Double.isNaN(max) && value.doubleValue() > max)
					|| value.doubleValue() > wheelMax) {
				return;
			}

			// if we are below lower limit just drop to lower limit
			if (newValue.doubleValue() < min) {
				value = new BigDecimal(Double.toString(min),
						MathContext.UNLIMITED);
			} else if (newValue.doubleValue() < wheelMin) {
				value = new BigDecimal(Double.toString(wheelMin),
						MathContext.UNLIMITED);
			}
			// if we are incrementing below the wheel upper limit just set to
			// wheel upper limit
			else if (newValue.doubleValue() > wheelMax) {
				value = new BigDecimal(Double.toString(wheelMax),
						MathContext.UNLIMITED);
			}
			// if we are incrementing beyond the upper limit just set to upper
			// limit
			else if (!Double.isNaN(max) && newValue.doubleValue() > max) {
				value = new BigDecimal(Double.toString(max),
						MathContext.UNLIMITED);
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
			if (newValue.signum() != 0 && (value.signum() != newValue.signum())) {
				newValue = value.negate().add(decrementor);
			}

			// if value is already beyond the lower limit or lower wheel limit
			// just ignore the request
			if ((!Double.isNaN(min) && value.doubleValue() < min)
					|| value.doubleValue() < wheelMin) {
				return;
			}

			// if we are beyond upper limit just drop to upper limit
			if (newValue.doubleValue() > max) {
				value = new BigDecimal(Double.toString(max),
						MathContext.UNLIMITED);

			} else if (newValue.doubleValue() > wheelMax) {
				value = new BigDecimal(Double.toString(wheelMax),
						MathContext.UNLIMITED);
			}
			// if we are decrementing below the wheel lower limit just set to
			// wheel lower limit
			else if (newValue.doubleValue() < wheelMin) {
				value = new BigDecimal(Double.toString(wheelMin),
						MathContext.UNLIMITED);
			}
			// if we are decrementing below the lower limit just set to lower
			// limit
			else if (!Double.isNaN(min) && newValue.doubleValue() < min) {
				value = new BigDecimal(Double.toString(min),
						MathContext.UNLIMITED);
			} else {
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
				throw new IllegalStateException("Missing decimal part!");
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
			return value.doubleValue() > wheelMax
					|| value.doubleValue() < wheelMin;

		}

		public Double getMax() {
			return max;
		}

		public void setMax(Double max) {
			this.max = max;
		}

		public Double getMin() {
			return min;
		}

		public void setMin(Double min) {
			this.min = min;
		}

		public int getIntegerWheels() {
			return integerWheels;
		}

		public void setIntegerWheels(int integerWheels) {
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

			wheelMax = Double.parseDouble(nines);
			wheelMin = Double.parseDouble("-" + nines);
		}

		public int getDecimalWheels() {
			return decimalWheels;

		}

		public void setDecimalWheels(int decimalWheels) {
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

			wheelMax = Double.parseDouble(nines);
			wheelMin = Double.parseDouble("-" + nines);
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