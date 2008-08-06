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

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.components.model.WaveformModel;
import org.csstudio.sds.components.ui.internal.figures.WaveformFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the Waveform widget. The controller mediates between
 * {@link WaveformModel} and {@link WaveformFigure}.
 * 
 * @author Sven Wende, Kai Meyer, Joerg Rathlev
 * 
 */
public final class WaveformEditPart extends AbstractWidgetEditPart {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		WaveformModel model = (WaveformModel) getWidgetModel();
		WaveformFigure figure = new WaveformFigure(WaveformModel.NUMBER_OF_ARRAYS);
		for (int i = 0; i < WaveformModel.NUMBER_OF_ARRAYS; i++) {
			figure.setData(i, model.getData(i));
			figure.setPlotColor(i, model.getPlotColor(i));
		}
		figure.setMin(model.getMin());
		figure.setMax(model.getMax());
		figure.setAutoScale(model.getAutoscale());
		figure.setShowScale(model.getShowScale());
		figure.setXSectionCount(model.getXSectionCount());
		figure.setShowValues(model.getShowValues());
		figure.setShowGridLines(model.getShowLedgerLines());
		figure.setGridLinesColor(model.getLedgerLineColor());
		figure.setShowConnectionLines(model.getShowConnectionLines());
		figure.setGraphLineWidth(model.getGraphLineWidth());
		figure.setBackgroundColor(CustomMediaFactory.getInstance().getColor(model.getBackgroundColor()));
		figure.setForegroundColor(CustomMediaFactory.getInstance().getColor(model.getForegroundColor()));
		figure.setTransparent(model.getTransparent());
		figure.setYAxisScaling(model.getYAxisScaling());
		figure.setLabel(model.getLabel());
		figure.setXAxisLabel(model.getXAxisLabel());
		figure.setYAxisLabel(model.getYAxisLabel());
		figure.setDataPointDrawingStyle(model.getDataPointDrawingStyle());
		return figure;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		registerDataPropertyChangeHandlers();
		
		// max
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
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
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
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
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setAutoScale((Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_AUTO_SCALE, handler);

		
		// show values
		IWidgetPropertyChangeHandler showValuesHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setShowValues((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_SHOW_VALUES, showValuesHandler);
		
		// show ledger lines
		IWidgetPropertyChangeHandler ledgerLinesHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setShowGridLines((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_SHOW_GRID_LINES, ledgerLinesHandler);
		
		// show scale
		IWidgetPropertyChangeHandler scaleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setShowScale((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_SHOW_SCALE, scaleHandler);
		
		// x-axis section count
		IWidgetPropertyChangeHandler xSectionHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setXSectionCount((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_X_AXIS_MAX_TICKMARKS, xSectionHandler);
		
		// show connection lines
		IWidgetPropertyChangeHandler connectionLinesHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setShowConnectionLines((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_SHOW_CONNECTION_LINES, connectionLinesHandler);
		
		// graph line width
		IWidgetPropertyChangeHandler lineWidthHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setGraphLineWidth((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_GRAPH_LINE_WIDTH, lineWidthHandler);
		
		// transparent
		IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
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
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
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
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setDataPointDrawingStyle((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_DATA_POINT_DRAWING_STYLE, drawingStyleHandler);
		
		// y-axis scaling
		IWidgetPropertyChangeHandler yAxisScalingHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setYAxisScaling((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_Y_AXIS_SCALING, yAxisScalingHandler);
		
		// label
		IWidgetPropertyChangeHandler labelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setLabel((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_LABEL, labelHandler);
		
		// x-axis label
		IWidgetPropertyChangeHandler xAxisLabelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setXAxisLabel((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_X_AXIS_LABEL, xAxisLabelHandler);

		// y-axis label
		IWidgetPropertyChangeHandler yAxisLabelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setYAxisLabel((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_Y_AXIS_LABEL, yAxisLabelHandler);
		
		// ledger line color
		IWidgetPropertyChangeHandler ledgerColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setGridLinesColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_GRID_LINE_COLOR, ledgerColorHandler);		
	}
	
	/**
	 * Registers the property change handlers for the properties that apply
	 * to each waveform array (the data and the plot color).
	 */
	private void registerDataPropertyChangeHandlers() {
		/**
		 * Change handler for the waveform data properties.
		 * 
		 * @author Joerg Rathlev
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
				figure.setData(_index, (double[]) newValue);
				return true;
			}
		}
		
		/**
		 * Change handler for the plot color properties.
		 * 
		 * @author Joerg Rathlev
		 */
		class PlotColorChangeHandler implements IWidgetPropertyChangeHandler {
			
			private final int _index;

			/**
			 * Constructor.
			 * @param index the index of the plot color.
			 */
			PlotColorChangeHandler(final int index) {
				_index = index;
			}
			
			/**
			 * {@inheritDoc}
			 */
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setPlotColor(_index, (RGB) newValue);
				return true;
			}
		}
		
		for (int i = 0; i < WaveformModel.NUMBER_OF_ARRAYS; i++) {
			setPropertyChangeHandler(WaveformModel.dataPropertyId(i),
					new DataChangeHandler(i));
			setPropertyChangeHandler(WaveformModel.plotColorPropertyId(i),
					new PlotColorChangeHandler(i));
		}
	}
}
