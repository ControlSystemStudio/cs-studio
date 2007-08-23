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

import org.csstudio.sds.components.model.AdvancedSliderModel;
import org.csstudio.sds.components.ui.internal.figures.AdvancedSliderFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.IFigure;
import org.eclipse.ui.progress.UIJob;

/**
 * EditPart controller for the Slider widget. The controller mediates between
 * {@link AdvancedSliderModel} and {@link AdvancedSliderFigure}.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class AdvancedSliderEditPart extends AbstractWidgetEditPart {

	/**
	 * A UI job, which is used to reset the manual value of the slider figure
	 * after a certain amount of time.
	 */
	private UIJob _resetManualValueDisplayJob;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final AdvancedSliderModel model = (AdvancedSliderModel) getWidgetModel();

		final AdvancedSliderFigure slider = new AdvancedSliderFigure();
		slider.addSliderListener(new AdvancedSliderFigure.ISliderListener() {
			public void sliderValueChanged(final int newValue) {
				model.getProperty(AdvancedSliderModel.PROP_VALUE).setManualValue(
						newValue);

				slider.setManualValue((Integer) newValue);

				if (_resetManualValueDisplayJob == null) {
					_resetManualValueDisplayJob = new UIJob("reset") {
						@Override
						public IStatus runInUIThread(
								final IProgressMonitor monitor) {
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
		IWidgetPropertyChangeHandler valHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AdvancedSliderFigure slider = (AdvancedSliderFigure) refreshableFigure;
				slider.setValue((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AdvancedSliderModel.PROP_VALUE, valHandler);

		// min
		IWidgetPropertyChangeHandler minHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AdvancedSliderFigure slider = (AdvancedSliderFigure) refreshableFigure;
				slider.setMin((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AdvancedSliderModel.PROP_MIN, minHandler);

		// max
		IWidgetPropertyChangeHandler maxHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AdvancedSliderFigure slider = (AdvancedSliderFigure) refreshableFigure;
				slider.setMax((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AdvancedSliderModel.PROP_MAX, maxHandler);

		// increment
		IWidgetPropertyChangeHandler incrementHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AdvancedSliderFigure slider = (AdvancedSliderFigure) refreshableFigure;
				slider.setIncrement((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AdvancedSliderModel.PROP_INCREMENT, incrementHandler);

		// increment
		IWidgetPropertyChangeHandler orientationHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AdvancedSliderFigure slider = (AdvancedSliderFigure) refreshableFigure;

				int orientation = (Integer) newValue;
				slider.setOrientation(orientation == 0);

				AdvancedSliderModel model = (AdvancedSliderModel) getModel();

				// invert the size of the element
				model.setSize(model.getHeight(), model.getWidth());

				return true;
			}
		};
		setPropertyChangeHandler(AdvancedSliderModel.PROP_ORIENTATION,
				orientationHandler);

	}
}
