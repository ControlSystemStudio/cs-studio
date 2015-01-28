/**
 * 
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.channel.widgets.TunerWidget;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * @author shroffk
 * 
 */
public class TunerEditPart extends
		AbstractChannelWidgetEditPart<TunerFigure, TunerModel> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.csstudio.channel.opiwidgets.AbstractChannelWidgetEditPart#doCreateFigure
	 * ()
	 */
	@Override
	protected TunerFigure doCreateFigure() {
		TunerFigure figure = new TunerFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	/**
	 * @param swtWidget
	 * @param widgetModel
	 * @param runMode
	 */
	private void configure(TunerWidget tunerWidget, TunerModel tunerModel,
			boolean runMode) {
		if (runMode) {
			tunerWidget.setChannelQuery(tunerModel.getChannelQuery());
		}
		tunerWidget.setConfigurable(tunerModel.isConfigurable());
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#
	 * registerPropertyChangeHandlers()
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				configure(getFigure().getSWTWidget(), getWidgetModel(),
						getFigure().isRunMode());
				return false;
			}

		};
		setPropertyChangeHandler(TunerModel.CHANNEL_QUERY, reconfigure);
		setPropertyChangeHandler(TunerModel.CONFIGURABLE, reconfigure);
	}

}
