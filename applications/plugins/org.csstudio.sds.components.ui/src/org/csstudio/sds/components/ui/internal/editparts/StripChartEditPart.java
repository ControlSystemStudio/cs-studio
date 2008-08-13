/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

import java.util.Arrays;

import org.csstudio.sds.components.model.StripChartModel;
import org.csstudio.sds.components.ui.internal.figures.StripChartFigure;
import org.csstudio.sds.components.ui.internal.figures.WaveformFigure;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * Edit part for the strip chart widget.
 * 
 * @author Joerg Rathlev
 */
public final class StripChartEditPart extends AbstractChartEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		StripChartModel model = (StripChartModel) getWidgetModel();
		int valuesPerDataSeries = numberOfValuesPerSeries(model);
		StripChartFigure figure = new StripChartFigure(
				model.numberOfDataSeries(), valuesPerDataSeries);
		initializeCommonFigureProperties(figure, model);
		initializeXAxisFigureProperties(figure, model);
		// Note: the value properties are not initialized. This would cause
		// the initial value that is drawn for each data series to be the static
		// value of the property, which is probably not what the user wants.
		
		return figure;
	}
	
	/**
	 * Returns the number of data values that are recorded for each data series.
	 * 
	 * @param model
	 *            the model.
	 * @return the number of data values that are recorded.
	 */
	public int numberOfValuesPerSeries(final StripChartModel model) {
		// TODO: constrain to a sensible maximum
		return (int) Math.floor(model.getXAxisTimespan() / model.getUpdateInterval());
	}

	/**
	 * Initializes the x-axis properties of the figure.
	 * 
	 * @param figure the figure.
	 * @param model the model.
	 */
	private void initializeXAxisFigureProperties(final StripChartFigure figure,
			final StripChartModel model) {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		registerDataPropertyChangeHandlers();
	}

	/**
	 * Registers the property change handlers for the data properties.
	 */
	private void registerDataPropertyChangeHandlers() {
		/**
		 * Change handler for the waveform data properties.
		 */
		class DataChangeHandler implements IWidgetPropertyChangeHandler {
			
			private final int _index;

			/**
			 * Constructor.
			 * @param index the index of the data array.
			 */
			DataChangeHandler(final int index) {
				_index = index;
			}
			
			/**
			 * {@inheritDoc}
			 */
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				StripChartFigure figure = (StripChartFigure) refreshableFigure;
				double value = (Double) newValue;
				figure.setCurrentValue(_index, value);
				return true;
			}
		}
		
		/**
		 * Change handler for the plot enablement properties.
		 */
		class EnablePlotChangeHandler implements IWidgetPropertyChangeHandler {
			
			private final int _index;

			/**
			 * Constructor.
			 * @param index the index of the data array.
			 */
			EnablePlotChangeHandler(final int index) {
				_index = index;
			}
			
			/**
			 * {@inheritDoc}
			 */
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				StripChartFigure figure = (StripChartFigure) refreshableFigure;
				boolean enabled = (Boolean) newValue;
				// TODO
				return true;
			}
		}
		
		StripChartModel model = (StripChartModel) getWidgetModel();
		for (int i = 0; i < model.numberOfDataSeries(); i++) {
			setPropertyChangeHandler(StripChartModel.valuePropertyId(i),
					new DataChangeHandler(i));
			setPropertyChangeHandler(StripChartModel.valuePropertyId(i),
					new EnablePlotChangeHandler(i));
		}
	}

}
