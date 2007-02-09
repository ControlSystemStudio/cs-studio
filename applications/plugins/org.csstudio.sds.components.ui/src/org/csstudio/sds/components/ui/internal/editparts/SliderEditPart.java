package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.SliderElement;
import org.csstudio.sds.components.ui.internal.figures.SliderFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
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
		SliderFigure slider =  new SliderFigure();
		slider.addSliderListener(new SliderFigure.ISliderListener(){
			public void sliderValueChanged(int newValue) {
				getCastedModel().getProperty(SliderElement.PROP_VALUE).setManualValue(newValue);
			}
		});
		
		return slider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized boolean doRefreshFigure(final String propertyName,
			final Object newValue, final IRefreshableFigure figure) {
		return false;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// TODO Auto-generated method stub
		
	}
}
