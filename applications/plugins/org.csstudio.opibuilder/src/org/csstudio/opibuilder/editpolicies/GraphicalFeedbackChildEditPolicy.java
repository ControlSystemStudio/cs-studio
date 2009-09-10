package org.csstudio.opibuilder.editpolicies;

import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.feedback.IGraphicalFeedbackFactory;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
	 * Provides support for selecting, positioning, and resizing an editpart. By
	 * default, selection is indicated via eight square handles along the
	 * editpart's figure, and a rectangular handle that outlines the editpart
	 * with a 1-pixel black line. The eight square handles will resize the
	 * current selection in the eight primary directions. The rectangular handle
	 * will drag the current selection using a {@link
	 * org.eclipse.gef.tools.DragEditPartsTracker}.
	 * <P>
	 * By default, during feedback, a rectangle filled using XOR and outlined
	 * with dashes is drawn. This feedback can be tailored by contributing a
	 * {@link IGraphicalFeedbackFactory} via the extension point
	 * org.csstudio.sds.graphicalFeedbackFactories.
	 * 
	 * @author Sven Wende, Xihui Chen
	 * 
*/
public final class GraphicalFeedbackChildEditPolicy extends ResizableEditPolicy {
		/**
		 * The edit part.
		 */
		private final AbstractBaseEditPart _child;
		
		private final IGraphicalFeedbackFactory feedbackFactory;

		/**
		 * Standard constructor.
		 * 
		 * @param child
		 *            An edit part.
		 */
		protected GraphicalFeedbackChildEditPolicy(final AbstractBaseEditPart child, IGraphicalFeedbackFactory feedbackFactory) {
			_child = child;
			this.feedbackFactory = feedbackFactory;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected IFigure createDragSourceFeedbackFigure() {
			
			IFigure feedbackFigure = feedbackFactory
					.createDragSourceFeedbackFigure(
							(AbstractWidgetModel) _child.getModel(),
							getInitialFeedbackBounds());

			addFeedback(feedbackFigure);

			return feedbackFigure;
		}

		/**
		 * Shows or updates feedback for a change bounds request.
		 * 
		 * @param request
		 *            the request
		 */
		@Override
		protected void showChangeBoundsFeedback(
				final ChangeBoundsRequest request) {


			IFigure feedbackFigure = getDragSourceFeedbackFigure();

			PrecisionRectangle rect = new PrecisionRectangle(
					getInitialFeedbackBounds().getCopy());
			getHostFigure().translateToAbsolute(rect);

			Point moveDelta = request.getMoveDelta();
			rect.translate(moveDelta);

			Dimension sizeDelta = request.getSizeDelta();
			rect.resize(sizeDelta);

			feedbackFactory.showChangeBoundsFeedback(
					(AbstractWidgetModel) getHost().getModel(), rect,
					feedbackFigure, request);

			feedbackFigure.repaint();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected List createSelectionHandles() {
			// get default handles
			List handleList = super.createSelectionHandles();

			// add contributed handles

			GraphicalEditPart hostEP = (GraphicalEditPart) getHost();

			List contributedHandles = feedbackFactory
					.createCustomHandles(hostEP);

			if (contributedHandles != null) {
				handleList.addAll(contributedHandles);
			}

			return handleList;

		}
	}