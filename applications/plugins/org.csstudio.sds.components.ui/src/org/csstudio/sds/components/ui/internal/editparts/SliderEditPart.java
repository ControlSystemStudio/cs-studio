package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.MeterElement;
import org.csstudio.sds.components.model.SliderElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableMeterFigure;
import org.csstudio.sds.components.ui.internal.figures.SliderFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.editparts.IElementPropertyChangeHandler;
import org.csstudio.sds.ui.figures.IRefreshableFigure;

/**
 * EditPart controller for <code>RectangleElement</code> elements.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class SliderEditPart extends AbstractElementEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		SliderElement model = (SliderElement) getCastedModel();

		SliderFigure slider = new SliderFigure();
		slider.addSliderListener(new SliderFigure.ISliderListener() {
			public void sliderValueChanged(int newValue) {
				getCastedModel().getProperty(SliderElement.PROP_VALUE)
						.setManualValue(newValue);
			}
		});

		slider.setMax(model.getMax());
		slider.setMin(model.getMin());
		slider.setIncrement(model.getIncrement());
		slider.setValue(model.getValue());

		return slider;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// value
		IElementPropertyChangeHandler valHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				SliderFigure slider = (SliderFigure) figure;
				slider.setValue((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SliderElement.PROP_VALUE, valHandler);

		// min
		IElementPropertyChangeHandler minHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				SliderFigure slider = (SliderFigure) figure;
				slider.setMin((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SliderElement.PROP_MIN, minHandler);

		// max
		IElementPropertyChangeHandler maxHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				SliderFigure slider = (SliderFigure) figure;
				slider.setMax((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SliderElement.PROP_MAX, maxHandler);

		// increment
		IElementPropertyChangeHandler incrementHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				SliderFigure slider = (SliderFigure) figure;
				slider.setIncrement((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SliderElement.PROP_INCREMENT, incrementHandler);

	}
}
