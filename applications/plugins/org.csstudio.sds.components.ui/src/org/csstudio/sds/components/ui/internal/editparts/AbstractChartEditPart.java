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

import org.csstudio.sds.components.model.AbstractChartModel;
import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.components.model.WaveformModel;
import org.csstudio.sds.components.ui.internal.figures.AbstractChartFigure;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart.ColorChangeHander;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Abstract base class for the edit parts of chart widgets (waveform, strip
 * chart).
 * 
 * @author Joerg Rathlev
 */
abstract class AbstractChartEditPart extends AbstractWidgetEditPart {

	/**
	 * Sets those properties on the figure that are defined in the
	 * {@code AbstractChartModel} base class. This method is provided for the
	 * convenience of subclasses, which can call this method in their
	 * implementation of {@link #doCreateFigure()}.
	 * 
	 * @param figure
	 *            the figure.
	 * @param model
	 *            the model.
	 */
	protected final void initializeCommonFigureProperties(
			final AbstractChartFigure figure, final AbstractChartModel model) {
		figure.setAliases(model.getAllInheritedAliases());
		for (int i = 0; i < model.numberOfDataSeries(); i++) {
			figure.setPlotColor(i, getModelColor(AbstractChartModel.plotColorPropertyId(i)));
		}
		figure.setMin(model.getMin());
		figure.setMax(model.getMax());
		figure.setAutoScale(model.getAutoscale());
		figure.setShowScale(model.getShowAxes());
		figure.setShowValues(model.isLabeledTicksEnabled());
		figure.setShowGridLines(model.getShowGridLines());
		figure.setGridLinesColor(getModelColor(AbstractChartModel.PROP_GRID_LINE_COLOR));
		figure.setLineChart(model.isLineChart());
		figure.setGraphLineWidth(model.getPlotLineWidth());
		figure.setBackgroundColor(getModelColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND));
		figure.setForegroundColor(getModelColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
		figure.setTransparent(model.isTransparent());
		figure.setYAxisScaling(model.getYAxisScaling());
		figure.setLabel(model.getLabel());
		figure.setXAxisLabel(model.getXAxisLabel());
		figure.setYAxisLabel(model.getYAxisLabel());
		figure.setDataPointDrawingStyle(model.getDataPointDrawingStyle());
	}
	
	/**
	 * Registers property change handlers for the properties defined in
	 * {@code AbstractChartModel}. This method is provided for the convenience
	 * of subclasses, which can call this method in their implementation of
	 * {@link #registerPropertyChangeHandlers()}.
	 */
	protected final void registerCommonPropertyChangeHandlers() {
		registerPlotColorChangeHandlers();
		registerDrawingStyleChangeHandlers();
		registerAxesChangeHandlers();
		registerLabelChangeHandler();
	}

	/**
	 * Registers a change handler for the label property. 
	 */
	private void registerLabelChangeHandler() {
		IWidgetPropertyChangeHandler labelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setLabel((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_LABEL, labelHandler);
	}
	
	/**
	 * Registers change handlers for the axes properties. 
	 */
	private void registerAxesChangeHandlers() {
		// max
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setMax((Double)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_MAX, handler);

		// min
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setMin((Double)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_MIN, handler);

		// autoscale
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setAutoScale((Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_AUTOSCALE, handler);
		
		// labeled ticks
		IWidgetPropertyChangeHandler showValuesHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setShowValues((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_LABELED_TICKS, showValuesHandler);
		
		// grid lines
		IWidgetPropertyChangeHandler ledgerLinesHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setShowGridLines((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_SHOW_GRID_LINES, ledgerLinesHandler);
		
		// show axes
		IWidgetPropertyChangeHandler scaleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setShowScale((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_SHOW_AXES, scaleHandler);
		
		// y-axis scaling
		IWidgetPropertyChangeHandler yAxisScalingHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setYAxisScaling((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_Y_AXIS_SCALING, yAxisScalingHandler);

		// x-axis label
		IWidgetPropertyChangeHandler xAxisLabelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setXAxisLabel((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_X_AXIS_LABEL, xAxisLabelHandler);

		// y-axis label
		IWidgetPropertyChangeHandler yAxisLabelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setYAxisLabel((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_Y_AXIS_LABEL, yAxisLabelHandler);
	}

	/**
	 * Registers change handlers for the drawing style properties.
	 */
	private void registerDrawingStyleChangeHandlers() {
		// line chart
		IWidgetPropertyChangeHandler connectionLinesHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setLineChart((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_LINE_CHART, connectionLinesHandler);

		// plot line width
		IWidgetPropertyChangeHandler lineWidthHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setGraphLineWidth((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_PLOT_LINE_WIDTH, lineWidthHandler);
		
		// transparent
		IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_TRANSPARENT, transparentHandler);
		
		// border width and style
		IWidgetPropertyChangeHandler borderHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.refreshConstraints();
				return true;
			}
		};
		setPropertyChangeHandler(BargraphModel.PROP_BORDER_WIDTH, borderHandler);
		setPropertyChangeHandler(BargraphModel.PROP_BORDER_STYLE, borderHandler);
		
		// data point drawing style
		IWidgetPropertyChangeHandler drawingStyleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure refreshableFigure) {
				AbstractChartFigure figure = (AbstractChartFigure) refreshableFigure;
				figure.setDataPointDrawingStyle((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_DATA_POINT_DRAWING_STYLE, drawingStyleHandler);
		
		// grid line color
		setPropertyChangeHandler(WaveformModel.PROP_GRID_LINE_COLOR, new ColorChangeHander<AbstractChartFigure>() {
			@Override
			protected void doHandle(AbstractChartFigure figure, Color color) {
				figure.setGridLinesColor(color);
			}
		});		
	}
	
	/**
	 * Registers change handlers for the plot color properties.
	 */
	private void registerPlotColorChangeHandlers() {
		AbstractChartModel model = ((AbstractChartModel) getModel());
		for (int i = 0; i < model.numberOfDataSeries(); i++) {
			final int nr = i;
			setPropertyChangeHandler(WaveformModel.plotColorPropertyId(i),new ColorChangeHander<AbstractChartFigure>() {
				@Override
				protected void doHandle(AbstractChartFigure figure, Color color) {
					figure.setPlotColor(nr, color);
				}
			});
		}
	}
}
