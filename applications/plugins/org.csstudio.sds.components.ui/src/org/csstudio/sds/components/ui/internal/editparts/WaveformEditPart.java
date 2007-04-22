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

import org.csstudio.sds.components.model.WaveformModel;
import org.csstudio.sds.components.ui.internal.figures.WaveformFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.util.CustomMediaFactory;
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
	protected IRefreshableFigure doCreateFigure() {
		WaveformModel model = (WaveformModel) getCastedModel();
		WaveformFigure waveform = new WaveformFigure();
		waveform.setData(model.getData());
		waveform.setMin(model.getMin());
		waveform.setMax(model.getMax());
		waveform.setAutoScale(model.getAutoscale());
		waveform.setShowScale(model.getShowScale());
		waveform.setShowScale(model.getShowScale());
		waveform.setShowLedgerlLines(model.getShowLedgerLines());
		waveform.setShowConnectionLines(model.getShowConnectionLines());
		waveform.setBackgroundColor(CustomMediaFactory.getInstance().getColor(model.getBackgroundColor()));
		waveform.setForegroundColor(CustomMediaFactory.getInstance().getColor(model.getForegroundColor()));
		waveform.setGraphColor(model.getGraphColor());
		waveform.setConnectionLineColor(model.getConnectionLineColor());
		waveform.setLedgerLineColor(model.getLedgerLineColor());
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
					final IRefreshableFigure refreshableFigure) {
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
					final IRefreshableFigure refreshableFigure) {
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
					final IRefreshableFigure refreshableFigure) {
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
					final IRefreshableFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setData((double[]) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_WAVE_FORM, handler);
		// show ledger lines
		IWidgetPropertyChangeHandler ledgerLinesHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setShowLedgerlLines((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_SHOW_LEDGER_LINES, ledgerLinesHandler);
		// show scale
		IWidgetPropertyChangeHandler scaleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setShowScale((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_SHOW_SCALE, scaleHandler);
		// show connection lines
		IWidgetPropertyChangeHandler connectionLinesHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setShowConnectionLines((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_SHOW_CONNECTION_LINES, connectionLinesHandler);
		// graph color
		IWidgetPropertyChangeHandler graphColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
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
					final IRefreshableFigure refreshableFigure) {
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
					final IRefreshableFigure refreshableFigure) {
				WaveformFigure figure = (WaveformFigure) refreshableFigure;
				figure.setLedgerLineColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(WaveformModel.PROP_LEDGER_LINE_COLOR, ledgerColorHandler);
	}
}
