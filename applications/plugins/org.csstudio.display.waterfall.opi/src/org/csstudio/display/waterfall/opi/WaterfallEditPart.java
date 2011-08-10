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
		if (figure.isRunMode())
			figure.getSWTWidget().setInputText(getWidgetModel().getPvName());
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

	@Override
	protected void registerPropertyChangeHandlers() {
		// The handler when PV value changed.
		IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure figure) {
				if (runMode(figure)) {
					widgetOf(figure).setInputText((String) newValue);
				}
				return false;
			}
		};
		setPropertyChangeHandler(WaterfallModel.PV_NAME, valueHandler);
//		
//		//The handler when max property value changed.
//		IWidgetPropertyChangeHandler maxHandler = new IWidgetPropertyChangeHandler() {
//			
//			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
//				((SimpleBarGraphFigure) figure).setMax((Double)newValue);
//				return false;
//			}
//		};
//		setPropertyChangeHandler(SimpleBarGraphModel.PROP_MAX, maxHandler);
//		
//		//The handler when min property value changed.
//		IWidgetPropertyChangeHandler minHandler = new IWidgetPropertyChangeHandler() {
//			
//			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
//				((SimpleBarGraphFigure) figure).setMin((Double)newValue);
//				return false;
//			}
//		};
//		setPropertyChangeHandler(SimpleBarGraphModel.PROP_MIN, minHandler);
//		
	}
	
//	@Override
//	public Object getValue() {
//		return ((SimpleBarGraphFigure)getFigure()).getValue();
//	}
//
//	@Override
//	public void setValue(Object value) {
//		if(value instanceof Double)
//			((SimpleBarGraphFigure)getFigure()).setValue((Double)value);
//	}
}
