package org.csstudio.sds.components.ui.internal.editparts;

import java.math.BigDecimal;

import org.csstudio.sds.components.model.ThumbWheelModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableThumbWheelFigure;
import org.csstudio.sds.components.ui.internal.figures.RefreshableThumbWheelFigure.WheelListener;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

public class ThumbWheelEditPart extends AbstractWidgetEditPart {

	private ThumbWheelLogic wheelLogic;
	private ThumbWheelModel model;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		model = (ThumbWheelModel) getWidgetModel();
		final RefreshableThumbWheelFigure fig = new RefreshableThumbWheelFigure();

		wheelLogic = new ThumbWheelLogic(model.getValue());
		fig.setInternalBorderThickness(model.getInternalFrameThickness());
		fig.setInternalBorderColor(model.getInternalFrameColor());
		fig.setFontColor(model.getFontColor());
		fig.setDigitFont(model.getFont());
		fig.setWholeDigitsPart(model.getWholePartDigits());
		fig.setDecimalDigitsPart(model.getDecimalPartDigits());
		fig.addWheelListener(new WheelListener() {

			public void decrementDecimalPart(int index) {
				wheelLogic.decrementDecimalDigitAt(index);
				updateWheelValues(wheelLogic, fig, model);
				model.setValue(wheelLogic.getValue());
			}

			public void incrementDecimalPart(int index) {
				wheelLogic.incrementDecimalDigitAt(index);
				updateWheelValues(wheelLogic, fig, model);
				model.setValue(wheelLogic.getValue());
			}

			public void decrementIntegerPart(int index) {
				wheelLogic.decrementIntigerDigitAt(index);
				updateWheelValues(wheelLogic, fig, model);
				model.setValue(wheelLogic.getValue());
			}

			public void incrementIntegerPart(int index) {
				wheelLogic.incrementIntigerDigitAt(index);
				updateWheelValues(wheelLogic, fig, model);
				model.setValue(wheelLogic.getValue());

			}
		});

		updateWheelValues(wheelLogic, fig, model);
		return fig;

	}

	private static void updateWheelValues(ThumbWheelLogic logic,
			RefreshableThumbWheelFigure figure, ThumbWheelModel model) {

		int limit = model.getWholePartDigits();

		for (int i = 0; i < limit; i++) {
			figure.setIntegerWheel(i, logic.getIntegerDigitAt(i));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// labels
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure rectangle = (RefreshableThumbWheelFigure) refreshableFigure;
				rectangle.setDecimalDigitsPart((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_DECIMAL_DIGITS_PART,
				handler);

		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure rectangle = (RefreshableThumbWheelFigure) refreshableFigure;
				rectangle.setWholeDigitsPart((Integer) newValue);
				return true;
			}
		};

		setPropertyChangeHandler(ThumbWheelModel.PROP_WHOLE_DIGITS_PART,
				handler);

		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure rectangle = (RefreshableThumbWheelFigure) refreshableFigure;
				rectangle.setDecimalDigitsPart((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_DECIMAL_DIGITS_PART,
				handler);

		// labels
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableThumbWheelFigure rectangle = (RefreshableThumbWheelFigure) refreshableFigure;
				wheelLogic.setValue(Double.toString((Double) newValue));
				updateWheelValues(wheelLogic, rectangle, model);
				return true;
			}
		};
		setPropertyChangeHandler(ThumbWheelModel.PROP_VALUE, handler);

	}

	/**
	 * Knows which digit belongs to which position in an double.
	 * <p>
	 * A number 678.45
	 * </p>
	 * is represented as 678 in the whole part and 45 in the fraction part.
	 * <table>
	 * <tr>
	 * <td>index</td>
	 * <td>value</td>
	 * </tr>
	 * <tr>
	 * <td>0</td>
	 * <td>8</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>7</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>6</td>
	 * </tr>
	 * </table>
	 * </p>
	 * <p>
	 * Note that the index of wholepart digit goes from right to left of the
	 * dot. And for the decimal digits it goes from left to rigt.
	 * 
	 * @author Alen Vrecko
	 * 
	 */
	private static class ThumbWheelLogic {

		private BigDecimal value;

		private int integerWheels = 10;
		private int decimalWheels = 10;

		public int getIntegerWheels() {
			return integerWheels;
		}

		public void setIntegerWheels(int integerWheels) {
			this.integerWheels = integerWheels;
		}

		public int getDecimalWheels() {
			return decimalWheels;
		}

		public void setDecimalWheels(int decimalWheels) {
			this.decimalWheels = decimalWheels;
		}

		public ThumbWheelLogic(double value) {
			setValue(value);
		}

		public double getValue() {
			return value.doubleValue();
		}

		public void setValue(double value) {
			setValue(Double.toString(value));
		}

		public void setValue(String value) {
			this.value = new BigDecimal(value);
		}

		/**
		 * Increments the integer digit on a specific index. E.g. on 567.12
		 * calling increment for - 0 will set the value to 568.12, with index -
		 * 2 will result in 667.12.
		 * 
		 * @param index
		 * @param val
		 */
		public void incrementIntigerDigitAt(int index) {
			value = value.add(new BigDecimal("1E" + index));

		}

		/**
		 * Decrements the integer digit on a specific index. E.g. on 567.12
		 * calling increment for - 0 will set the value to 468.12, with index -
		 * 2 will result in 467.12.
		 * 
		 * @param index
		 * @param val
		 */
		public void decrementIntigerDigitAt(int index) {
			value = value.subtract(new BigDecimal(("-1E" + index)));

		}

		/**
		 * Increments the decimal digit on a specific index. E.g. on 567.12
		 * calling increment for - 0 will set the value to 567.22, with index -
		 * 1 will result in 567.11.
		 * 
		 * @param index
		 * @param val
		 */
		public void incrementDecimalDigitAt(int index) {
			value = value.add(new BigDecimal("0.1E-" + index));

		}

		/**
		 * Decrements the decimal digit on a specific index. E.g. on 567.12
		 * calling increment for - 0 will set the value to 568.02, with index -
		 * 1 will result in 567.11.
		 * 
		 * @param index
		 * @param val
		 */
		public void decrementDecimalDigitAt(int index) {
			value = value.subtract(new BigDecimal(("-0.1E-" + index)));
		}

		/**
		 * Returns a digit in the specified index. E.g. for 324.23 getting index
		 * 0,1,2 would return 4,2,3.
		 * 
		 * @param index
		 * @return
		 */
		public int getIntegerDigitAt(int index) {
			String plainString = value.toPlainString();
			int dot = plainString.indexOf('.');
			if (dot >= 0) {
				plainString = plainString.substring(0, dot);
			}

			if (index >= plainString.length()) {
				return 0;
			}

			return Integer.parseInt(""
					+ plainString.charAt(plainString.length() - 1 - index));

		}

		/**
		 * Returns a digit in the specified index. E.g. for 324.23 getting index
		 * 0,1 would return 2,3.
		 * 
		 * @param index
		 * @return
		 */
		public int getDecimalDigitAt(int index) {
			String plainString = value.toPlainString();
			plainString = plainString.substring(plainString.indexOf('.') + 1);
			if (index >= plainString.length()) {
				return 0;
			}
			return Integer.parseInt("" + plainString.charAt(index));

		}

	}

}