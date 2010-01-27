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
package org.csstudio.opibuilder.widgets.figures;

import java.util.ArrayList;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.ArrowButton;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;

/**
 * The view for ThumbWheel.
 * 
 * @author Alen Vrecko, Jose Ortega, Xihui Chen
 * 
 */
public class ThumbWheelFigure extends Figure {

	/**
	 * A border adapter, which covers all border handlings.
	 */

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

	private RGB internalBorderColor;

	private int internalBorderWidth;
	
	private ArrayList<WheelListener> listeners = new ArrayList<WheelListener>();

	private Font wheelFont;

	@Override
	public boolean isOpaque() {
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	public void paintFigure(final Graphics graphics) {		
		graphics.setBackgroundColor(this.getBackgroundColor());
		Rectangle bounds = this.getBounds().getCopy();
		bounds.crop(this.getInsets());
		graphics.fillRectangle(bounds);
		super.paintFigure(graphics);
	}

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

		dot = new CharBox(' ');
		add(dot);
		setConstraint(dot, createGridData());

		decimalPart = new DigitBox[decimalPartDigits];
		for (int i = 0; i < decimalPartDigits; i++) {
			DigitBox box = new DigitBox(i, true);
			add(box);
			decimalPart[i] = box;

			setConstraint(box, createGridData());
		}

		setInternalBorderColor(internalBorderColor);
		setInternalBorderThickness(internalBorderWidth);
		setWheelFonts(wheelFont);

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

	public ThumbWheelFigure(int integerWheels, int decimalDigits) {
		wholePartDigits = integerWheels;
		decimalPartDigits = decimalDigits;

		// we will be displaying the widget anyway so I don't see a point in
		// deferring this till later.
		createWidgets();
	}

	public void setInternalBorderThickness(int thickness) {
		this.internalBorderWidth = thickness;
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
		this.internalBorderColor = color;
		if(color == null){
			return;
		}
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

		public void setLabelFontColor(Color color) {
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
			up.setFiringMethod(ArrowButton.REPEAT_FIRING);
			up.setPreferredSize(20, 20);
			if (isDecimal) {
				up.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						fireIncrementDecimalListeners(positionIndex);
					}

				});
			} else {
				up.addActionListener(new ActionListener() {
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
			down.setFiringMethod(ArrowButton.REPEAT_FIRING);
			down.setPreferredSize(20, 20);
			if (isDecimal) {
				down.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						fireDecrementDecimalListeners(positionIndex);
					}
				});
			} else {
				down.addActionListener(new ActionListener() {
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

	public void showDot(boolean b) {
		if (b) {
			dot.setChar('.');
		} else {
			dot.setChar(' ');
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

	public void setWheelFonts(Font font) {
		this.wheelFont = font;
		if(font == null){
			return;
		}

		for (DigitBox box : wholePart) {
			box.setFont(font);
		}

		for (DigitBox box : decimalPart) {
			box.setFont(font);
		}

		dot.setFont(font);
		minus.setFont(font);
	}
}