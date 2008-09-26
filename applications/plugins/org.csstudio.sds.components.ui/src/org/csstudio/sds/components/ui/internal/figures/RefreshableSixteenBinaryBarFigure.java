package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
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
 * Sixteen Bit Binary Bar widget figure.
 * 
 * @author Alen Vrecko, Joerg Rathlev
 */
public class RefreshableSixteenBinaryBarFigure extends RectangleFigure
		implements IAdaptable {

	/**
	 * The orientation (horizontal==true | vertical==false).
	 */
	private boolean _orientationHorizontal = true;

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;

	private int value;

	private Color onColor;

	private Color offColor;

	private int internalFrameThickness;

	private RGB internalFrameColor;

	private OnOffBox[] boxes;

	private GridLayout layout;
	
	private int bitRangeFrom = 0;
	
	private int bitRangeTo = 15;

	private boolean _showLabels;

	public RefreshableSixteenBinaryBarFigure() {
		createLayoutAndBoxes();
	}

	/**
	 * Creates the layout and the boxes for this figure.
	 */
	private void createLayoutAndBoxes() {
		layout = new GridLayout(numberOfBits(), false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		setLayoutManager(layout);

		boxes = new OnOffBox[numberOfBits()];
		for (int i = 0; i < numberOfBits(); i++) {
			OnOffBox box = new OnOffBox(i + bitRangeFrom);
			box.setShowLabel(_showLabels);
			add(box);

			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessVerticalSpace = true;
			gridData.grabExcessHorizontalSpace = true;

			setConstraint(box, gridData);
			boxes[i] = box;

		}
	}
	
	/**
	 * Removes the boxes from this figure, then recreates them and refreshes the
	 * layout.
	 */
	private void recreateLayoutAndBoxes() {
		removeBoxes();
		createLayoutAndBoxes();
		updateBoxes();
	}
	
	/**
	 * Removes the boxes from this figure.
	 */
	private void removeBoxes() {
		removeAll();
	}

	/**
	 * Updates the on/off state of the boxes based on the current value.
	 */
	private void updateBoxes() {
		for (int i = 0; i < numberOfBits(); i++) {
			boxes[i].setOn(((1 << (i + bitRangeFrom)) & value) != 0);
		}
	}

	/**
	 * Sets the orientation (horizontal==true | vertical==false).
	 * 
	 * @param horizontal
	 *            The orientation.
	 */
	public void setHorizontal(final boolean horizontal) {
		_orientationHorizontal = horizontal;

		if (_orientationHorizontal) {
			layout.numColumns = numberOfBits();
		} else {
			layout.numColumns = 1;
		}

		revalidate();

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

	public void setShowLabels(Boolean newValue) {
		_showLabels = newValue;
		for (OnOffBox box : boxes) {
			box.setShowLabel(newValue);
		}
	}

	public void setValue(Integer newValue) {
		this.value = newValue;
		updateBoxes();
	}

	public void setLabelFont(FontData newValue) {
		Font font = CustomMediaFactory.getInstance().getFont(newValue);
		for (OnOffBox box : boxes) {
			box.setLabelFont(font);
		}
	}

	public void setOnColor(RGB newValue) {
		if (newValue == null) {
			return;
		}
		this.onColor = CustomMediaFactory.getInstance().getColor(newValue);
		updateBoxes();
	}

	public void setOffColor(RGB newValue) {
		if (newValue == null) {
			return;
		}
		this.offColor = CustomMediaFactory.getInstance().getColor(newValue);
		updateBoxes();
	}

	public void setInternalBorderThickness(int internalFrameThickness) {
		this.internalFrameThickness = internalFrameThickness;

		for (OnOffBox box : boxes) {
			if (internalFrameColor != null) {
				Color color = CustomMediaFactory.getInstance().getColor(
						internalFrameColor);
				box.setInternalFrame(internalFrameThickness, color);
			} else {
				box.setInternalFrame(internalFrameThickness, null);
			}

		}

	}

	public void setInternalBorderColor(RGB internalFrameColor) {
		this.internalFrameColor = internalFrameColor;

		for (OnOffBox box : boxes) {
			if (internalFrameColor != null) {
				Color color = CustomMediaFactory.getInstance().getColor(
						internalFrameColor);
				box.setInternalFrame(internalFrameThickness, color);
			} else {
				box.setInternalFrame(internalFrameThickness, null);
			}

		}

	}

	public void setLabelColor(RGB labelColor) {
		if (labelColor == null) {
			return;
		}
		Color color = CustomMediaFactory.getInstance().getColor(labelColor);

		for (OnOffBox box : boxes) {
			box.setLabelColor(color);
		}
	}
	
	/**
	 * Sets the range of bits that are displayed by this figure.
	 * 
	 * @param from
	 *            the index of the lowest bit to display.
	 * @see #setBitRangeTo(int)
	 */
	public void setBitRangeFrom(final int from) {
		this.bitRangeFrom = from;
		recreateLayoutAndBoxes();
	}
	
	/**
	 * Sets the range of bits that are displayed by this figure.
	 * 
	 * @param to
	 *            the index of the highest bit to display.
	 * @see #setBitRangeFrom(int)
	 */
	public void setBitRangeTo(final int to) {
		this.bitRangeTo = to;
		recreateLayoutAndBoxes();
	}
	
	/**
	 * Returns the number of bits displayed in this figure.
	 * 
	 * @return the number of bits displayed in this figure.
	 */
	private int numberOfBits() {
		return bitRangeTo - bitRangeFrom + 1;
	}

	private class OnOffBox extends Figure {

		private Label label;
		private final int i;
		private boolean isOn;

		public OnOffBox(int i) {
			this.i = i;

			BorderLayout layout = new BorderLayout();
			layout.setVerticalSpacing(0);

			setBorder(new LineBorder(0));
			setLayoutManager(layout);

			label = new Label("");

			add(label);
			setConstraint(label, BorderLayout.CENTER);

		}

		public void setLabelFont(Font font) {

			label.setFont(font);

		}

		@Override
		protected void paintFigure(Graphics graphics) {
			super.paintFigure(graphics);

			if (isOn) {

				setBackgroundColor(onColor);

			} else {
				setBackgroundColor(offColor);

			}

			graphics.fillRectangle(getClientArea());
		}

		public boolean isOn() {
			return isOn;
		}

		public void setLabelColor(Color color) {
			label.setBackgroundColor(color);
			label.setForegroundColor(color);
		}

		public void setInternalFrame(int thickness, Color color) {
			if (color != null) {
				if (thickness > 0) {

					if (color != null) {
						setBorder(new LineBorder(color, thickness));
					} else {
						setBorder(new LineBorder(thickness));
					}
				} else {
					setBorder(null);
				}

				revalidate();

			}

		}

		public void setOn(boolean isOn) {
			this.isOn = isOn;

			// needed only to prevent null pointer while initializing the widget
			// with initial values
			if (offColor == null || onColor == null) {
				return;
			}

		}

		public void setShowLabel(boolean show) {
			if (show) {
				if (i < 10) {
					label.setText("0" + i);
				} else {
					label.setText("" + i);
				}
			} else {
				label.setText("");
			}
		}

	}

}