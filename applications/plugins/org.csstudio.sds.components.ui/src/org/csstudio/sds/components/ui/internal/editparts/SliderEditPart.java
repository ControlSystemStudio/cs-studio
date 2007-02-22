/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.SliderElement;
import org.csstudio.sds.components.ui.internal.figures.SliderFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.editparts.IElementPropertyChangeHandler;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

/**
 * EditPart controller for <code>RectangleElement</code> elements.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class SliderEditPart extends AbstractElementEditPart {

	/**
	 * A UI job, which is used to reset the manual value of the slider figure
	 * after a certain amount of time.
	 */
	private UIJob _resetManualValueDisplayJob;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		final SliderElement model = (SliderElement) getCastedModel();

		final SliderFigure slider = new SliderFigure();
		slider.addSliderListener(new SliderFigure.ISliderListener() {
			public void sliderValueChanged(final int newValue) {
				model.getProperty(SliderElement.PROP_VALUE).setManualValue(
						newValue);

				slider.setManualValue((Integer) newValue);

				if (_resetManualValueDisplayJob == null) {
					_resetManualValueDisplayJob = new UIJob("reset") {
						@Override
						public IStatus runInUIThread(final IProgressMonitor monitor) {
							slider.setManualValue(model.getValue());
							return Status.OK_STATUS;
						}
					};

				}
				_resetManualValueDisplayJob.schedule(5000);

			}
		});

		slider.setMax(model.getMax());
		slider.setMin(model.getMin());
		slider.setIncrement(model.getIncrement());
		slider.setValue(model.getValue());
		slider.setManualValue(model.getValue());
		slider.setOrientation(model.isHorizontal());

		return slider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// value
		IElementPropertyChangeHandler valHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				SliderFigure slider = (SliderFigure) refreshableFigure;
				slider.setValue((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SliderElement.PROP_VALUE, valHandler);

		// min
		IElementPropertyChangeHandler minHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				SliderFigure slider = (SliderFigure) refreshableFigure;
				slider.setMin((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SliderElement.PROP_MIN, minHandler);

		// max
		IElementPropertyChangeHandler maxHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				SliderFigure slider = (SliderFigure) refreshableFigure;
				slider.setMax((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SliderElement.PROP_MAX, maxHandler);

		// increment
		IElementPropertyChangeHandler incrementHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				SliderFigure slider = (SliderFigure) refreshableFigure;
				slider.setIncrement((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SliderElement.PROP_INCREMENT, incrementHandler);

		// increment
		IElementPropertyChangeHandler orientationHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				SliderFigure slider = (SliderFigure) refreshableFigure;

				int orientation = (Integer) newValue;
				slider.setOrientation(orientation == 0);

				SliderElement element = (SliderElement) getModel();

				// invert the size of the element
				element.setSize(element.getHeight(), element.getWidth());

				return true;
			}
		};
		setPropertyChangeHandler(SliderElement.PROP_ORIENTATION,
				orientationHandler);

	}
}
