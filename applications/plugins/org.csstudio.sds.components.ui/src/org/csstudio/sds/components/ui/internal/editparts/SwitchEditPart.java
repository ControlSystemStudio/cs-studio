package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

import org.csstudio.sds.components.common.SwitchPlugins;
import org.csstudio.sds.components.model.SwitchModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableSwitchFigure;

/**
 * EditPart controller for the switch widget.
 * 
 * @author jbercic
 * 
 */
public final class SwitchEditPart extends AbstractWidgetEditPart {
	/**
	 * Returns the casted model. This is just for convenience.
	 * 
	 * @return the casted {@link SwitchModel}
	 */
	protected SwitchModel getCastedModel() {
		return (SwitchModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		SwitchModel model = getCastedModel();
		// create AND initialize the view properly
		final RefreshableSwitchFigure figure = new RefreshableSwitchFigure();
		
		figure.setFill(!model.getTransparent());
		figure.setLineWidth(model.getLineWidth());
		figure.setType(model.getType());
		figure.setState(model.getState());
		figure.setRotation(model.getRotation());
		
		return figure;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// background transparency
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
				switchFigure.setFill(!((Boolean) newValue));
				return true;
			}
		};
		setPropertyChangeHandler(SwitchModel.PROP_TRANSPARENT, handle);
		
		// switch type
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
				switchFigure.setType((Integer) newValue);
				return true;
			}
		};
		if (SwitchPlugins.names.length>0) {
			setPropertyChangeHandler(SwitchModel.PROP_TYPE, handle);
		}
		
		// switch state
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
				switchFigure.setState((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SwitchModel.PROP_STATE, handle);
		
		// rotation
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
				switchFigure.setRotation((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SwitchModel.PROP_ROTATE, handle);
		
		// line width
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
				switchFigure.setLineWidth((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SwitchModel.PROP_LINEWIDTH, handle);
		
		// widget width and height
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
				switchFigure.resize();
				return true;
			}
		};
		setPropertyChangeHandler(SwitchModel.PROP_HEIGHT, handle);
		setPropertyChangeHandler(SwitchModel.PROP_WIDTH, handle);
	}
}
