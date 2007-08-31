package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;

import org.csstudio.sds.components.model.LabelModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;

/**
 * EditPart controller for the label widget.
 * 
 * @author jbercic
 * 
 */
public final class LabelEditPart extends AbstractWidgetEditPart {

	/**
	 * Returns the casted model. This is just for convenience.
	 * 
	 * @return the casted {@link LabelModel}
	 */
	protected LabelModel getCastedModel() {
		return (LabelModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		LabelModel model = getCastedModel();
		// create AND initialize the view properly
		final RefreshableLabelFigure figure = new RefreshableLabelFigure();
		
		figure.setFont(CustomMediaFactory.getInstance().getFont(model.getFont()));
		figure.setTextAlignment(model.getTextAlignment());
		figure.setTransparent(model.getTransparent());
		figure.setBorderWidth(model.getBorderWidth());
		figure.setRotation(model.getRotation());
		figure.setXOff(model.getXOff());
		figure.setYOff(model.getYOff());
		
		figure.setType(model.getType());
		figure.setTextValue(model.getTextValue());
		figure.setDecimalPlaces(model.getPrecision());
		
		return figure;
	}
	
	/**
	 * Registers handlers for changes of the different value properties.
	 */
	protected void registerValueChangeHandlers() {
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setType((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TYPE, handle);
		
		//text value
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setTextValue((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TEXTVALUE, handle);
		
		// precision
		IWidgetPropertyChangeHandler precisionHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) refreshableFigure;
				label.setDecimalPlaces((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_PRECISION, precisionHandler);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// changes to the font property
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				FontData fontData = (FontData) newValue;
				labelFigure.setFont(CustomMediaFactory.getInstance().getFont(
						fontData.getName(), fontData.getHeight(),
						fontData.getStyle()));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_FONT, handle);
		
		// changes to the text alignment property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setTextAlignment((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TEXT_ALIGN, handle);

		// changes to the transparency property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setTransparent((Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TRANSPARENT, handle);
		
		// changes to the border width property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setBorderWidth((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_BORDER_WIDTH, handle);
		
		// changes to the text rotation property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setRotation((Double)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_ROTATION, handle);
		
		// changes to the x offset property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setXOff((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_XOFF, handle);

		// changes to the y offset property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableLabelFigure labelFigure = (RefreshableLabelFigure) figure;
				labelFigure.setYOff((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_YOFF, handle);

		registerValueChangeHandlers();
	}
}
