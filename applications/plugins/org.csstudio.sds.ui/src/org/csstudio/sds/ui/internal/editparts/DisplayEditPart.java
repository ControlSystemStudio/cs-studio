package org.csstudio.sds.ui.internal.editparts;

import org.csstudio.platform.util.PerformanceUtil;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.editparts.AbstractContainerEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.editparts.LayeredWidgetPane;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

/**
 * The EditPart implementation for synoptic display.
 * 
 * @author Sven Wende
 * 
 */
public final class DisplayEditPart extends AbstractContainerEditPart  {

	/**
	 * Constructor.
	 */
	public DisplayEditPart() {
		PerformanceUtil.getInstance().constructorCalled(this);
	}

	/**
	 * Returns the {@link DisplayModel} of this DisplayEditPart.
	 * @return DisplayModel
	 * 			The {@link DisplayModel} of this DisplayEditPart
	 */
	public DisplayModel getDisplayModel() {
		return (DisplayModel) getContainerModel();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		LayeredWidgetPane f = new LayeredWidgetPane(true);
		// mit setOpaque(true) wird der Hintergrund bunt aber die
		// Gitternetzlinien sind nicht mehr zu sehen
		// ist inzwischen im DisplayEditor integriert
		// f.setOpaque(true);
		f.setLayoutManager(new FreeformLayout());
		f.setBorder(new MarginBorder(5));
		
		f.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				getWidgetModel().getBackgroundColor()));
		
		Point p = new Point(getWidgetModel().getX(), getWidgetModel().getY());
		f.setLocation(p);
		f.setSize(getWidgetModel().getWidth(), getWidgetModel().getHeight());
		f.setBorderBounds(getWidgetModel().getWidth(), getWidgetModel().getHeight());
		f.setShowBorder(getExecutionMode().equals(ExecutionMode.EDIT_MODE));
		return f;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();

		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new RootComponentEditPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler bgColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				return false;
			}
		};
		setPropertyChangeHandler(DisplayModel.PROP_COLOR_FOREGROUND,
				bgColorHandler);
		IWidgetPropertyChangeHandler boundsHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				if (figure instanceof LayeredWidgetPane) {
					((LayeredWidgetPane)figure).setBorderBounds(getCastedModel().getWidth(), getCastedModel().getHeight());
				}
				return true;
			}
		};
		setPropertyChangeHandler(DisplayModel.PROP_WIDTH, boundsHandler);
		setPropertyChangeHandler(DisplayModel.PROP_HEIGHT, boundsHandler);
		
		IWidgetPropertyChangeHandler displayBorderHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				if (figure instanceof LayeredWidgetPane) {
					((LayeredWidgetPane)figure).setShowBorder((Boolean)newValue);
				}
				return true;
			}
		};
		setPropertyChangeHandler(DisplayModel.PROP_DISPLAY_BORDER_VISIBILITY, displayBorderHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void finalize() throws Throwable {
		PerformanceUtil.getInstance().finalizedCalled(this);
		super.finalize();
	}
}
