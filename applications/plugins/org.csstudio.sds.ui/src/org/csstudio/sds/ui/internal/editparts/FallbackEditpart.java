package org.csstudio.sds.ui.internal.editparts;

import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A simple default implementation that creates a sample figure. The figure
 * may be used as a placeholder.
 * 
 * @author Stefan Hofer
 */
final class FallbackEditpart extends AbstractWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		IFigure result = new DefaultFigure();
		result.setBackgroundColor(ColorConstants.gray);
		result.setBounds(new Rectangle(getWidgetModel().getX(),
				getWidgetModel().getY(), getWidgetModel().getWidth(),
				getWidgetModel().getHeight()));
		return result;
	}

	/**
	 * A default figure implementation.
	 * 
	 * @author Sven Wende
	 * 
	 */
	final class DefaultFigure extends RectangleFigure implements IAdaptable {

		/**
		 * This method is a tribute to unit tests, which need a way to test
		 * the performance of the figure implementation. Implementors should
		 * produce some random changes and refresh the figure, when this
		 * method is called.
		 * 
		 */
		public void randomNoiseRefresh() {

		}

		/**
		 * {@inheritDoc}
		 */
		public Object getAdapter(final Class adapter) {
			return null;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
	}

}