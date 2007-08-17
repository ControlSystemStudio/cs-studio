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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.sds.components.model.ClockModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;

/**
 * EditPart controller for the Clock widget. The controller mediates between
 * {@link ClockModel} and {@link RefreshableClockFigure}.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class ClockEditPart extends AbstractWidgetEditPart {
	private SimpleDateFormat _dateFormat;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ClockModel model = (ClockModel) getWidgetModel();

		_dateFormat = new SimpleDateFormat(model.getDatePattern());
		RefreshableLabelFigure label = new RefreshableLabelFigure();

		updateLabel(label, model.getTime().doubleValue());

		label
				.setFont(CustomMediaFactory.getInstance().getFont(
						model.getFont()));
		label.setTextAlignment(model.getTextAlignment());

		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// label
		IWidgetPropertyChangeHandler timeHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;

				updateLabel(label, Math
						.round(((Double) newValue).doubleValue()));
				return true;
			}
		};
		setPropertyChangeHandler(ClockModel.PROP_TIME, timeHandler);
		// font
		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
				FontData fontData = (FontData) newValue;
				label.setFont(CustomMediaFactory.getInstance().getFont(
						fontData.getName(), fontData.getHeight(),
						fontData.getStyle()));
				return true;
			}
		};
		setPropertyChangeHandler(ClockModel.PROP_FONT, fontHandler);
		// text alignment
		IWidgetPropertyChangeHandler alignmentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
				label.setTextAlignment((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ClockModel.PROP_TEXT_ALIGNMENT,
				alignmentHandler);

		// format pattern
		IWidgetPropertyChangeHandler patternHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {

				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
				ClockModel model = (ClockModel) getWidgetModel();

				_dateFormat = new SimpleDateFormat(newValue.toString());
				updateLabel(label, model.getTime().doubleValue());

				return true;
			}
		};
		setPropertyChangeHandler(ClockModel.PROP_PATTERN, patternHandler);
	}

	/**
	 * Update the given label with the given system time.
	 * 
	 * @param label
	 *            The label to update.
	 * @param time
	 *            The system time to update the label with.
	 */
	protected void updateLabel(final RefreshableLabelFigure label,
			final double time) {
		label.setText(_dateFormat.format(new Date(Math.round(time))));
	}
}
