package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.EllipseElement;
import org.csstudio.sds.components.model.LabelElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableEllipseFigure;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.editparts.IElementPropertyChangeHandler;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.swt.graphics.FontData;

/**
 * EditPart controller for <code>LabelElement</code> elements.
 * 
 * @author Stefan Hofer & Sven Wende
 * 
 */
public final class LabelEditPart extends AbstractElementEditPart {
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		LabelElement model = (LabelElement) getCastedModel();
		
		RefreshableLabelFigure label = new RefreshableLabelFigure();
		label.setText(model.getLabel());
		label.setFont(CustomMediaFactory.getInstance().getFont(model.getFont()));
		
		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// label
		IElementPropertyChangeHandler labelHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) figure;
				label.setText(newValue.toString());
				return true;
			}
		};
		setPropertyChangeHandler(LabelElement.PROP_LABEL, labelHandler);
		// font
		IElementPropertyChangeHandler fontHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				RefreshableLabelFigure label = (RefreshableLabelFigure) figure;
				FontData fontData = (FontData) newValue;
				label.setFont(CustomMediaFactory.getInstance().getFont(
						fontData.getName(), fontData.getHeight(),
						fontData.getStyle()));
				return true;
			}
		};
		setPropertyChangeHandler(LabelElement.PROP_FONT, fontHandler);

	}
}
