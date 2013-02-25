/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
import org.eclipse.gef.Handle;
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
	 * @author Sven Wende (original author), Xihui Chen (since import from SDS 2009/9) 
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
			if(feedbackFigure != null){
				addFeedback(feedbackFigure);
				return feedbackFigure;
			}
			return super.createDragSourceFeedbackFigure();
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
		protected List<?> createSelectionHandles() {
			// get default handles
			List<Handle> handleList = super.createSelectionHandles();

			// add contributed handles

			GraphicalEditPart hostEP = (GraphicalEditPart) getHost();

			List<Handle> contributedHandles = feedbackFactory
					.createCustomHandles(hostEP);

			if (contributedHandles != null) {
				handleList.addAll(0, contributedHandles);
			}

			return handleList;

		}
	}