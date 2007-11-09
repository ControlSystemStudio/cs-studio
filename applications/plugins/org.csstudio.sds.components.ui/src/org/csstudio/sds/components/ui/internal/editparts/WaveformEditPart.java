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
import org.csstudio.sds.components.ui.internal.figures.RefreshableBargraphFigure;
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
 * @author Sven Wende, Kai Meyer
 * 
 */
public final class WaveformEditPart extends AbstractWidgetEditPart {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		WaveformModel model = (WaveformModel) getWidgetModel();
		WaveformFigure waveform = new WaveformFigure();
		waveform.setData(model.getData());
		waveform.setMin(model.getMin());
		waveform.setMax(model.getMax());
		waveform.setAutoScale(model.getAutoscale());
		waveform.setShowScale(model.getShowScale());
		waveform.setXSectionCount(model.getXSectionCount());
		waveform.setYSectionCount(model.getYSectionCount());
		waveform.setShowValues(model.getShowValues());
		waveform.setShowGridLines(model.getShowLedgerLines());
		waveform.setGridLinesColor(model.getLedgerLineColor());
		waveform.setShowConnectionLines(model.getShowConnectionLines());
		waveform.setConnectionLineColor(model.getConnectionLineColor());
		waveform.setGraphLineWidth(model.getGraphLineWidth());
		waveform.setBackgroundColor(CustomMediaFactory.getInstance().getColor(model.getBackgroundColor()));
		waveform.setForegroundColor(CustomMediaFactory.getInstance().getColor(model.getForegroundColor()));
		waveform.setGraphColor(model.getGraphColor());
		waveform.setTransparent(model.getTransparent());
		return waveform;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
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
		// data array
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setData((double[]) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_WAVE_FORM, handler);
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
		// y-axis section count
		IWidgetPropertyChangeHandler ySectionHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setYSectionCount((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_Y_AXIS_MAX_TICKMARKS, ySectionHandler);
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
		this.registerColorPropertyChangeHandlers();
	}
	
	/**
	 * Registers all Color-PropertyChangeHandler.
	 */
	protected void registerColorPropertyChangeHandlers() {
		// graph color
		IWidgetPropertyChangeHandler graphColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setGraphColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_GRAPH_COLOR, graphColorHandler);
		// connection line color
		IWidgetPropertyChangeHandler connectionColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setConnectionLineColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_CONNECTION_LINE_COLOR, connectionColorHandler);
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
}
