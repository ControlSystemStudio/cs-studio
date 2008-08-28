package org.csstudio.sds.components.ui.internal.figures;

import java.util.ArrayList;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.ArrowButton;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class RefreshableThumbWheelFigure extends RectangleFigure implements
		IAdaptable {

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;

	private FontData digitFont;

	private RGB fontColor;

	private int internalFrameThickness;

	private RGB internalFrameColor;

	private CharBox sign;

	// need reference because of changing font and color
	private CharBox dot;
	private CharBox minus;

	private GridLayout layout;

	// before the dot. 123,45 - we store 123
	private DigitBox[] wholePart;
	// part after the dot. 123,45 - we store 45
	private DigitBox[] decimalPart;

	private int wholePartDigits;

	private int decimalPartDigits;

	private void recreateWidgets() {

		removeAll();
		listeners.clear();
		layout = new GridLayout(2 + wholePartDigits + decimalPartDigits, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayoutManager(layout);

		minus = new CharBox(' ');
		add(minus);
		setConstraint(minus, createGridData());
		wholePart = new DigitBox[wholePartDigits];
		for (int i = 0; i < wholePartDigits; i++) {
			DigitBox box = new DigitBox(i);
			add(box);
			wholePart[wholePartDigits - i - 1] = box;
			setConstraint(box, createGridData());

		}

		dot = new CharBox('.');
		add(dot);
		setConstraint(dot, createGridData());

		decimalPart = new DigitBox[decimalPartDigits];
		for (int i = 0; i < decimalPartDigits; i++) {
			DigitBox box = new DigitBox(i);
			add(box);
			decimalPart[decimalPartDigits - i - 1] = box;

			setConstraint(box, createGridData());
		}
		revalidate();
	}

	public GridData createGridData() {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		return data;
	}

	public RefreshableThumbWheelFigure() {

	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if (_borderAdapter == null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}

	public void setInternalBorderThickness(int internalFrameThickness) {
		this.internalFrameThickness = internalFrameThickness;
		Color color = ColorFontUtil.get(internalFrameColor);

	}

	public void setInternalBorderColor(RGB internalFrameColor) {
		this.internalFrameColor = internalFrameColor;
		Color color = ColorFontUtil.get(internalFrameColor);

	}

	private static class CharBox extends Figure {

		private Label label;

		public CharBox(char ch) {

			BorderLayout layout = new BorderLayout();
			layout.setVerticalSpacing(0);

			setLayoutManager(layout);

			label = new Label("" + ch);
			add(label);
			setConstraint(label, BorderLayout.CENTER);

		}

		public void setChar(char c) {
			label.setText("" + c);
		}

	}

	private class DigitBox extends Figure {

		private Label label;
		private final int i;

		public DigitBox(final int i) {
			this.i = i;

			BorderLayout layout = new BorderLayout();
			layout.setVerticalSpacing(0);

			setLayoutManager(layout);

			label = new Label("0");
			ArrowButton up = new ArrowButton(ArrowButton.NORTH);
			up.setPreferredSize(20, 20);
			up.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					fireIncrementIntegerListeners(wholePartDigits - i - 1);
				}
			});
			add(up);
			setConstraint(up, BorderLayout.TOP);
			label.setPreferredSize(20, 10);
			add(label);
			setConstraint(label, BorderLayout.CENTER);
			ArrowButton down = new ArrowButton(ArrowButton.SOUTH);
			down.setPreferredSize(20, 20);
			down.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					fireDecrementIntegerListeners(wholePartDigits - i - 1);
				}
			});
			add(down, BorderLayout.BOTTOM);

		}

		public void setLabelFont(Font font) {

			label.setFont(font);

		}

		public void setColor(Color color) {
			label.setBackgroundColor(color);
			label.setForegroundColor(color);
		}

		public void setInternalFrame(int thickness, Color color) {
			if (color != null) {
				if (thickness > 0) {
					setBorder(new LineBorder(color, thickness));
				} else {
					setBorder(null);
				}

				revalidate();

			}

		}

		public void setValue(String value) {
			label.setText("" + value);
		}

	}

	public void setFontColor(RGB fontColor) {
		this.fontColor = fontColor;

	}

	public void setDigitFont(FontData font) {
		digitFont = font;

	}

	public void setWholeDigitsPart(int wholePartDigits2) {
		wholePartDigits = wholePartDigits2;
		recreateWidgets();

	}

	public void setDecimalDigitsPart(int decimalPartDigits2) {
		decimalPartDigits = decimalPartDigits2;
		recreateWidgets();
	}

	public void addWheelListener(WheelListener listener) {
		listeners.add(listener);
	}

	public void removeWheelListener(WheelListener listener) {
		listeners.remove(listener);
	}

	private void fireDecrementIntegerListeners(int index) {
		for (WheelListener listener : listeners) {
			listener.decrementIntegerPart(index);
		}
	}

	private void fireIncrementIntegerListeners(int index) {
		for (WheelListener listener : listeners) {
			listener.incrementIntegerPart(index);
		}
	}

	private ArrayList<WheelListener> listeners = new ArrayList<WheelListener>();

	public static interface WheelListener {
		/**
		 * Signals increment on a wheel of the integer part.
		 * 
		 * @param index
		 */
		void incrementIntegerPart(int index);

		/**
		 * Signals increment on a wheel of the decimal part.
		 * 
		 * @param index
		 */
		void incrementDecimalPart(int index);

		/**
		 * Signals decrement on a wheel of the integer part.
		 * 
		 * @param index
		 */
		void decrementIntegerPart(int index);

		/**
		 * Signals decrement on a wheel of the integer part.
		 * 
		 * @param index
		 */
		void decrementDecimalPart(int index);

	}

	public void setIncrementDecimalDigit(int index) {
		// TODO Auto-generated method stub

	}

	public void setIntegerWheel(int index, int value) {
		DigitBox box = wholePart[index];
		box.setValue("" + value);

	}

	public void setDecimalWheel(int index, int value) {
		// TODO Auto-generated method stub

	}

}