package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.SixteenBinaryBarModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableSixteenBinaryBarFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * Edit Part for Sixteen Binary Bar.
 * 
 * @author Alen Vrecko
 * 
 */
public class SixteenBinaryBarEditPart extends AbstractWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		SixteenBinaryBarModel model = (SixteenBinaryBarModel) getWidgetModel();

		final RefreshableSixteenBinaryBarFigure bar = new RefreshableSixteenBinaryBarFigure();
		bar.setOnColor(model.getOnColor());
		bar.setOffColor(model.getOffColor());
		bar.setLabelFont(model.getLabelFont());
		bar.setHorizontal(model.getHorizontal());
		bar.setShowLabels(model.getShowLabels());
		bar.setValue(model.getValue());
		bar.setInternalBorderThickness(model.getInternalFrameThickness());
		bar.setInternalBorderColor(model.getInternalFrameColor());
		bar.setLabelColor(model.getLabelColor());
		return bar;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// orientation
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
				rectangle.setHorizontal((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SixteenBinaryBarModel.PROP_HORIZONTAL, handler);

		// labels
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
				rectangle.setShowLabels((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SixteenBinaryBarModel.PROP_SHOW_LABELS,
				handler);

		// value
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
				rectangle.setValue((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SixteenBinaryBarModel.PROP_VALUE, handler);

		// font
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
				rectangle.setLabelFont((FontData) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SixteenBinaryBarModel.PROP_LABEL_FONT, handler);

		// on color
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
				rectangle.setOnColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SixteenBinaryBarModel.PROP_ON_COLOR, handler);

		// off color
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
				rectangle.setOffColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SixteenBinaryBarModel.PROP_OFF_COLOR, handler);

		// i frame thickness
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
				rectangle.setInternalBorderThickness((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(
				SixteenBinaryBarModel.PROP_INTERNAL_FRAME_THICKNESS, handler);

		// label color
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
				rectangle.setLabelColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(SixteenBinaryBarModel.PROP_LABEL_COLOR,
				handler);

		// frame color
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
				rectangle.setInternalBorderColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(
				SixteenBinaryBarModel.PROP_INTERNAL_FRAME_COLOR, handler);
	}

}