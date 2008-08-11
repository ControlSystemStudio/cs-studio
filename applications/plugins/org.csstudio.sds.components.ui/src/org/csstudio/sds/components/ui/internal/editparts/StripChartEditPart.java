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
		// The strip chart currently uses a waveform figure as its figure
		// because a strip chart figure has not yet been written.
		StripChartModel model = (StripChartModel) getWidgetModel();
		WaveformFigure figure = new WaveformFigure(model.numberOfDataSeries());
		initializeCommonFigureProperties(figure, model);
		initializeDataProperties(figure, model);
		return figure;
	}

	/**
	 * Initializes the data properties of the figure.
	 * 
	 * @param figure
	 *            the figure.
	 * @param model
	 *            the model.
	 */
	private void initializeDataProperties(final WaveformFigure figure,
			final StripChartModel model) {
		for (int i = 0; i < model.numberOfDataSeries(); i++) {
			if (model.isPlotEnabled(i)) {
				double[] data = new double[10];
				Arrays.fill(data, model.getCurrentValue(i));
				figure.setData(i, data);
			}
		}
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
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				double value = (Double) newValue;
				double[] data = new double[10];
				Arrays.fill(data, value);
				if (((StripChartModel) getWidgetModel()).isPlotEnabled(_index)) {
					figure.setData(_index, data);
				} else {
					figure.setData(_index, new double[0]);
				}
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
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				boolean enabled = (Boolean) newValue;
				if (enabled) {
					 double value = ((StripChartModel) getWidgetModel()).getCurrentValue(_index);
					 double[] data = new double[10];
					 Arrays.fill(data, value);
					 figure.setData(_index, data);
				} else {
					figure.setData(_index, new double[0]);
				}
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
