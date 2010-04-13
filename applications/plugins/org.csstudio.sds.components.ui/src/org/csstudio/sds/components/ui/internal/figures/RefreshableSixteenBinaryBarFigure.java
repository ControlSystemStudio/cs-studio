package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Sixteen Bit Binary Bar widget figure.
 * 
 * @author Alen Vrecko, Joerg Rathlev
 */
public class RefreshableSixteenBinaryBarFigure extends RectangleFigure implements ICrossedFigure {

	/**
	 * The orientation (horizontal==true | vertical==false).
	 */
	private boolean _orientationHorizontal = true;

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;

	private int value;

	private Color _onColor;

	private Color _offColor;

	private int _internalFrameThickness;

	private Color _internalFrameColor;

	private OnOffBox[] boxes;

	private GridLayout layout;

	private int bitRangeFrom = 0;

	private int bitRangeTo = 15;

	private boolean _showLabels;

	private Font _labelFont;

	private Color _labelColor;

    private CrossedPaintHelper _crossedPaintHelper;

	public RefreshableSixteenBinaryBarFigure() {
	    _crossedPaintHelper = new CrossedPaintHelper();
		createLayoutAndBoxes();
	}

	@Override
	public void paint(Graphics graphics) {
	    super.paint(graphics);
        Rectangle bound = getBounds().getCopy();
        _crossedPaintHelper.paintCross(graphics, bound);
	}
	
	/**
	 * Creates the layout and the boxes for this figure.
	 */
	private void createLayoutAndBoxes() {
		int column = 1;
		if (_orientationHorizontal) {
			column = numberOfBits();
		}
		layout = new GridLayout(column, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		setLayoutManager(layout);

		boxes = new OnOffBox[numberOfBits()];
		for (int i = 0; i < numberOfBits(); i++) {
			OnOffBox box = new OnOffBox(i + bitRangeFrom);
			box.setShowLabel(_showLabels);
			box.setLabelFont(_labelFont);
			box.setLabelColor(_labelColor);
			applyInternalBorderSettings(box);
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

	public void setLabelFont(Font font) {
		_labelFont = font;
		for (OnOffBox box : boxes) {
			box.setLabelFont(font);
		}
	}

	public void setOnColor(Color color) {
		if (color == null) {
			return;
		}
		_onColor = color;
	}

	public void setOffColor(Color color) {
		if (color == null) {
			return;
		}
		_offColor = color;
	}

	public void setInternalBorderThickness(int internalFrameThickness) {
		_internalFrameThickness = internalFrameThickness;
		for (OnOffBox box : boxes) {
			applyInternalBorderSettings(box);
		}
	}

	public void setInternalBorderColor(Color internalFrameColor) {
		_internalFrameColor = internalFrameColor;
		for (OnOffBox box : boxes) {
			applyInternalBorderSettings(box);
		}
	}

	/**
	 * Applies the current settings for the internal frame thickness and color
	 * to the given box.
	 * 
	 * @param box
	 *            the box.
	 */
	private void applyInternalBorderSettings(OnOffBox box) {
		if (_internalFrameColor != null) {
			box.setInternalFrame(_internalFrameThickness, _internalFrameColor);
		} else {
			box.setInternalFrame(_internalFrameThickness, null);
		}
	}

	public void setLabelColor(Color labelColor) {
		if (labelColor == null) {
			return;
		}
		_labelColor = labelColor;

		for (OnOffBox box : boxes) {
			box.setLabelColor(_labelColor);
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

	/**
	 * A box used to display the state of a single bit. A box is in either on or
	 * off state, and displays in a different color depending on that state. It
	 * can also optionally display a label with the index of the bit it
	 * displays.
	 */
	private class OnOffBox extends Figure {

		private Label _label;
		private final int _bitIndex;
		private boolean _isOn;

		public OnOffBox(int bitIndex) {
			this._bitIndex = bitIndex;

			BorderLayout layout = new BorderLayout();
			layout.setVerticalSpacing(0);

			setBorder(new LineBorder(0));
			setLayoutManager(layout);

			_label = new Label("");

			add(_label);
			setConstraint(_label, BorderLayout.CENTER);
		}

		public void setLabelFont(Font font) {
			_label.setFont(font);
		}

		@Override
		protected void paintFigure(Graphics graphics) {
			super.paintFigure(graphics);
			Rectangle figureBounds = getBounds().getCopy();
			setBackgroundColor(_isOn ? _onColor : _offColor);
			graphics.fillRectangle(getClientArea());
		}

		public void setLabelColor(Color color) {
			_label.setBackgroundColor(color);
			_label.setForegroundColor(color);
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

		/**
		 * Sets whether this box is on.
		 * 
		 * @param isOn
		 *            whether this box is on.
		 */
		public void setOn(boolean isOn) {
			this._isOn = isOn;
		}

		/**
		 * Sets whether this box displays a label.
		 * 
		 * @param show
		 *            whether this box displays a label.
		 */
		public void setShowLabel(boolean show) {
			if (show) {
				_label.setText(String.format("%02d", _bitIndex));
			} else {
				_label.setText("");
			}
		}

	}

    @Override
    public void setCrossedOut(boolean newValue) {
        _crossedPaintHelper.setCrossed(newValue);        
    }

}