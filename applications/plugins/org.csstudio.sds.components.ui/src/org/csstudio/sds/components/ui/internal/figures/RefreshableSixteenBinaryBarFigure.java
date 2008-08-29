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

	private Boolean showLabels;

	private int value;

	private FontData labelFont;

	private Color onColor;

	private Color offColor;

	private int internalFrameThickness;

	private RGB internalFrameColor;

	private RGB labelColor;

	private OnOffBox[] boxes;

	private GridLayout layout;

	public RefreshableSixteenBinaryBarFigure() {
		layout = new GridLayout(16, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		setLayoutManager(layout);

		boxes = new OnOffBox[16];
		for (int i = 0; i < 16; i++) {
			OnOffBox box = new OnOffBox(i);
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

	private void updateBoxes() {
		for (int i = 0; i < 16; i++) {
			if (((1 << i) & value) == 0) {
				boxes[i].setOn(false);
			} else {
				boxes[i].setOn(true);
			}
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
			layout.numColumns = 16;
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
		this.showLabels = newValue;

		for (OnOffBox box : boxes) {
			box.setShowLabel(newValue);
		}

	}

	public void setValue(Integer newValue) {
		this.value = newValue;
		updateBoxes();
	}

	public void setLabelFont(FontData newValue) {
		this.labelFont = newValue;
		Font font = CustomMediaFactory.getInstance().getFont(newValue);
		for (OnOffBox box : boxes) {
			box.setLabelFont(font);
		}
	}

	public void setOnColor(RGB newValue) {
		this.onColor = CustomMediaFactory.getInstance().getColor(newValue);
		updateBoxes();
	}

	public void setOffColor(RGB newValue) {
		this.offColor = CustomMediaFactory.getInstance().getColor(newValue);
		updateBoxes();
	}

	public void setInternalBorderThickness(int internalFrameThickness) {
		this.internalFrameThickness = internalFrameThickness;
		Color color = CustomMediaFactory.getInstance().getColor(internalFrameColor);
		for (OnOffBox box : boxes) {
			box.setInternalFrame(internalFrameThickness, color);
		}

	}

	public void setInternalBorderColor(RGB internalFrameColor) {
		this.internalFrameColor = internalFrameColor;
		Color color = CustomMediaFactory.getInstance().getColor(internalFrameColor);
		for (OnOffBox box : boxes) {
			box.setInternalFrame(internalFrameThickness, color);
		}

	}

	public void setLabelColor(RGB labelColor) {
		this.labelColor = labelColor;

		Color color = CustomMediaFactory.getInstance().getColor(labelColor);

		for (OnOffBox box : boxes) {
			box.setLabelColor(color);
		}
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
					setBorder(new LineBorder(color, thickness));
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