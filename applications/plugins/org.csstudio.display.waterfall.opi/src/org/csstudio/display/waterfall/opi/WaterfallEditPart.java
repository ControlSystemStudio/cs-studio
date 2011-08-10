package org.csstudio.display.waterfall.opi;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.ComboFigure;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.utility.pvmanager.widgets.WaterfallWidget;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

public class WaterfallEditPart extends AbstractWidgetEditPart {
	
	/**
	 * Create and initialize figure.
	 */
	@Override
	protected IFigure doCreateFigure() {
		WaterfallFigure figure = new WaterfallFigure((Composite) getViewer().getControl(), getWidgetModel().getParent());
		figure.setRunMode(getExecutionMode() == ExecutionMode.RUN_MODE);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}
	
	/**Get the widget model.
	 * It is recommended that all widget controller should override this method.
	 *@return the widget model.
	 */
	@Override
	public WaterfallModel getWidgetModel() {
		return (WaterfallModel) super.getWidgetModel();
	}
	
	private static WaterfallWidget widgetOf(IFigure figure) {
		return ((WaterfallFigure) figure).getSWTWidget();
	}
	
	private static boolean runMode(IFigure figure) {
		return ((WaterfallFigure) figure).isRunMode();
	}
	
	private static void configure(WaterfallWidget widget, WaterfallModel model, boolean runMode) {
		if (runMode)
			widget.setInputText(model.getInputText());
		widget.setShowRange(model.isShowRange());
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// The handler when PV value changed.
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure figure) {
				configure(widgetOf(figure), getWidgetModel(), runMode(figure));
				return false;
			}
		};
		setPropertyChangeHandler(WaterfallModel.INPUT_TEXT, reconfigure);
		setPropertyChangeHandler(WaterfallModel.ADAPTIVE_RANGE, reconfigure);
		setPropertyChangeHandler(WaterfallModel.PIXEL_DURATION, reconfigure);
		setPropertyChangeHandler(WaterfallModel.SCROLL_DOWN, reconfigure);
		setPropertyChangeHandler(WaterfallModel.SHOW_RANGE, reconfigure);
		setPropertyChangeHandler(WaterfallModel.SORT_PROPERTY, reconfigure);
	}
	
}
