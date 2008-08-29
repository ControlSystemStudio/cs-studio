package org.csstudio.sds.components.ui.internal.figures;

import java.util.ArrayList;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.CustomMediaFactory;
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

/**
 * The view for ThumbWheel.
 * 
 * @author avrecko
 * 
 */
public class RefreshableThumbWheelFigure extends RectangleFigure implements
		IAdaptable {

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;

	// need reference because of changing font and color
	private CharBox dot;
	private CharBox minus;

	private GridLayout layout = new GridLayout();

	// before the dot. 123,45 - we store 123
	private DigitBox[] wholePart;
	// part after the dot. 123,45 - we store 45
	private DigitBox[] decimalPart;

	private int wholePartDigits;

	private int decimalPartDigits;

	private boolean test;

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	/**
	 * Creates new widgets if needed to satisfy number of wheels specified.
	 */
	private void createWidgets() {
		removeAll();
		wholePart = null;
		decimalPart = null;
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
			DigitBox box = new DigitBox(i, false);
			add(box);
			wholePart[wholePartDigits - i - 1] = box;
			setConstraint(box, createGridData());

		}

		dot = new CharBox('.');
		add(dot);
		setConstraint(dot, createGridData());

		decimalPart = new DigitBox[decimalPartDigits];
		for (int i = 0; i < decimalPartDigits; i++) {
			DigitBox box = new DigitBox(i, true);
			add(box);
			decimalPart[i] = box;

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

	public RefreshableThumbWheelFigure(int integerWheels, int decimalDigits) {
		wholePartDigits = integerWheels;
		decimalPartDigits = decimalDigits;

		// we will be displaying the widget anyway so I don't see a point in
		// deferring this till later.
		createWidgets();
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

	public void setInternalBorderThickness(int thickness) {

		for (DigitBox box : wholePart) {
			box.setBorderThickness(thickness);
		}

		for (DigitBox box : decimalPart) {
			box.setBorderThickness(thickness);
		}

		dot.setBorderThickness(thickness);
		minus.setBorderThickness(thickness);
	}

	public void setInternalBorderColor(RGB color) {
		Color col = CustomMediaFactory.getInstance().getColor(color);

		for (DigitBox box : wholePart) {
			box.setBorderColor(col);
		}

		for (DigitBox box : decimalPart) {
			box.setBorderColor(col);
		}

		dot.setBorderColor(col);
		minus.setBorderColor(col);
	}

	/**
	 * Represents a box with a single char in it.
	 * 
	 */
	private static class CharBox extends Figure {

		private Label label;
		private Color color;
		private int thickness;

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

		public void setLabelFont(Font font) {
			label.setFont(font);
		}

		public void setBorderThickness(int thickness) {
			this.thickness = thickness;
			if (thickness == 0) {
				setBorder(null);
			} else {
				if (color != null) {
					setBorder(new LineBorder(color, thickness));
				} else {
					setBorder(new LineBorder(thickness));
				}
			}
		}

		public void setBorderColor(Color color) {
			this.color = color;
			if (color == null) {
				setBorder(null);
			} else {
				if (color != null) {
					setBorder(new LineBorder(color, thickness));
				} else {
					setBorder(new LineBorder(thickness));
				}
			}
		}

		public void setLabelFontColor(Color font) {
			label.setForegroundColor(color);
		}
	}

	/**
	 * Represents a box with a digit and up and down button. Calls
	 * increment/decrement listeners on button clicks.
	 * 
	 */
	private class DigitBox extends Figure {

		private Label label;
		private int thickness;
		private Color color;

		public DigitBox(final int positionIndex, boolean isDecimal) {

			BorderLayout layout = new BorderLayout();
			layout.setVerticalSpacing(0);

			setLayoutManager(layout);

			label = new Label("0");
			ArrowButton up = new ArrowButton(ArrowButton.NORTH);
			up.setPreferredSize(20, 20);
			if (isDecimal) {
				up.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						fireIncrementDecimalListeners(positionIndex);
					}

				});
			} else {
				up.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						fireIncrementIntegerListeners(wholePartDigits
								- positionIndex - 1);
					}
				});
			}

			add(up);
			setConstraint(up, BorderLayout.TOP);
			label.setPreferredSize(20, 10);
			add(label);
			setConstraint(label, BorderLayout.CENTER);
			ArrowButton down = new ArrowButton(ArrowButton.SOUTH);
			down.setPreferredSize(20, 20);
			if (isDecimal) {
				down.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						fireDecrementDecimalListeners(positionIndex);
					}
				});
			} else {
				down.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						fireDecrementIntegerListeners(wholePartDigits
								- positionIndex - 1);
					}
				});
			}

			add(down, BorderLayout.BOTTOM);

		}

		public void setLabelFont(Font font) {
			label.setFont(font);
		}

		public void setBorderThickness(int thickness) {
			this.thickness = thickness;
			if (thickness == 0) {
				setBorder(null);
			} else {
				if (color != null) {
					setBorder(new LineBorder(color, thickness));
				} else {
					setBorder(new LineBorder(thickness));
				}

			}
		}

		public void setBorderColor(Color color) {
			this.color = color;
			if (color == null) {
				setBorder(null);
			} else {
				if (color != null) {
					setBorder(new LineBorder(color, thickness));
				} else {
					setBorder(new LineBorder(thickness));
				}
			}
		}

		public void setValue(String value) {
			label.setText("" + value);
		}

		public void setLabelFontColor(Color color) {
			label.setForegroundColor(color);

		}

	}

	public void setWholeDigitsPart(int wholePartDigits2) {
		wholePartDigits = wholePartDigits2;
		createWidgets();

	}

	public void setDecimalDigitsPart(int decimalPartDigits2) {
		decimalPartDigits = decimalPartDigits2;
		createWidgets();

	}

	public void setIntegerWheel(int index, char value) {
		DigitBox box = wholePart[index];
		box.setValue("" + value);

	}

	public void setDecimalWheel(int index, char value) {
		DigitBox box = decimalPart[index];
		box.setValue("" + value);
	}

	public void showMinus(boolean b) {
		if (b) {
			minus.setChar('-');
		} else {
			minus.setChar(' ');
		}

	}

	// LISTENER PART

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

	private void fireIncrementDecimalListeners(int index) {
		for (WheelListener listener : listeners) {
			listener.incrementDecimalPart(index);
		}
	}

	private void fireDecrementDecimalListeners(int index) {
		for (WheelListener listener : listeners) {
			listener.decrementDecimalPart(index);
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

	public void setWheelFonts(FontData fontData) {
		Font font = CustomMediaFactory.getInstance().getFont(fontData);

		for (DigitBox box : wholePart) {
			box.setLabelFont(font);
		}

		for (DigitBox box : decimalPart) {
			box.setLabelFont(font);
		}

		dot.setLabelFont(font);
		minus.setLabelFont(font);
	}

}