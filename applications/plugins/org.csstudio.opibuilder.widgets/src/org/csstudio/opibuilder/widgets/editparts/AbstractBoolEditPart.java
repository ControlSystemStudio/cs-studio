package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.figures.AbstractBoolFigure;
import org.csstudio.opibuilder.widgets.model.AbstractBoolWidgetModel;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;

/**
 * Base editPart controller for a widget based on {@link AbstractBoolWidgetModel}.
 * 
 * @author Xihui Chen
 * 
 */
public abstract class AbstractBoolEditPart extends AbstractPVWidgetEditPart {

	/**
	 * Sets those properties on the figure that are defined in the
	 * {@link AbstractBoolFigure} base class. This method is provided for the
	 * convenience of subclasses, which can call this method in their
	 * implementation of {@link AbstractBaseEditPart#doCreateFigure()}.
	 * 
	 * @param figure
	 *            the figure.
	 * @param model
	 *            the model.
	 */
	protected void initializeCommonFigureProperties(
			final AbstractBoolFigure figure, final AbstractBoolWidgetModel model) {
		
		figure.setBit(model.getBit());
		figure.setShowBooleanLabel(model.isShowBoolLabel());
		figure.setOnLabel(model.getOnLabel());
		figure.setOffLabel(model.getOffLabel());
		figure.setOnColor(model.getOnColor());
		figure.setOffColor(model.getOffColor());
		figure.setFont(CustomMediaFactory.getInstance().getFont(
				model.getFont().getFontData()));
		
	}	
	
	/**
	 * Registers property change handlers for the properties defined in
	 * {@link AbstractBoolWidgetModel}. This method is provided for the convenience
	 * of subclasses, which can call this method in their implementation of
	 * {@link #registerPropertyChangeHandlers()}.
	 */
	protected void registerCommonPropertyChangeHandlers() {
		// value
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				if(newValue == null)
					return false;
				AbstractBoolFigure figure = (AbstractBoolFigure) refreshableFigure;
				figure.setValue(ValueUtil.getDouble((IValue)newValue));
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);
		
		// bit
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractBoolFigure figure = (AbstractBoolFigure) refreshableFigure;
				figure.setBit((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractBoolWidgetModel.PROP_BIT, handler);
		
		// show bool label
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractBoolFigure figure = (AbstractBoolFigure) refreshableFigure;
				figure.setShowBooleanLabel((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractBoolWidgetModel.PROP_SHOW_BOOL_LABEL, handler);
		
		// on label
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractBoolFigure figure = (AbstractBoolFigure) refreshableFigure;
				figure.setOnLabel((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractBoolWidgetModel.PROP_ON_LABEL, handler);
		
		// off label
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractBoolFigure figure = (AbstractBoolFigure) refreshableFigure;
				figure.setOffLabel((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractBoolWidgetModel.PROP_OFF_LABEL, handler);
		
		// on color
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractBoolFigure figure = (AbstractBoolFigure) refreshableFigure;
				figure.setOnColor(((OPIColor) newValue).getRGBValue());
				return true;
			}
		};
		setPropertyChangeHandler(AbstractBoolWidgetModel.PROP_ON_COLOR, handler);
		
		// off color
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractBoolFigure figure = (AbstractBoolFigure) refreshableFigure;
				figure.setOffColor(((OPIColor) newValue).getRGBValue());
				return true;
			}
		};
		setPropertyChangeHandler(AbstractBoolWidgetModel.PROP_OFF_COLOR, handler);
		
		
		// font
		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				AbstractBoolFigure figure = (AbstractBoolFigure) refreshableFigure;
				FontData fontData = ((OPIFont) newValue).getFontData();
				figure.setFont(CustomMediaFactory.getInstance().getFont(fontData));
				return true;
			}
		};
		setPropertyChangeHandler(AbstractBoolWidgetModel.PROP_FONT, fontHandler);
		
	}

}
