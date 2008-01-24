package org.csstudio.sds.ui.editparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.GuideModel;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.csstudio.sds.ui.internal.commands.AddWidgetCommand;
import org.csstudio.sds.ui.internal.commands.ChangeGuideCommand;
import org.csstudio.sds.ui.internal.commands.CloneCommand;
import org.csstudio.sds.ui.internal.commands.CreateElementCommand;
import org.csstudio.sds.ui.internal.commands.SetBoundsCommand;
import org.csstudio.sds.ui.internal.feedback.GraphicalFeedbackContributionsService;
import org.csstudio.sds.util.GuideUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.rulers.RulerProvider;

/**
 * The EditPolicy for {@link DisplayModel}. It can be used with
 * <code>Figures</code> in {@link XYLayout}. The constraint for XYLayout is a
 * {@link org.eclipse.draw2d.geometry.Rectangle}.
 * 
 * This policy is optimized for the runmode.
 * 
 * @author Sven Wende
 */
final class RunModeXYLayoutEditPolicy extends XYLayoutEditPolicy {

	/**
	 * Overriden, to provide a generic EditPolicy for children, which is aware
	 * of different feedback and selection handles. {@inheritDoc}
	 */
	@Override
	protected EditPolicy createChildEditPolicy(final EditPart child) {
		return new GenericChildEditPolicy(child);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		// Not supported in Run Mode
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		// Not supported in Run Mode
		return null;
	}

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
	 * @author Sven Wende
	 * 
	 */
	protected final class GenericChildEditPolicy extends ResizableEditPolicy {
		/**
		 * The edit part.
		 */
		private final EditPart _child;

		/**
		 * Standard constructor.
		 * 
		 * @param child
		 *            An edit part.
		 */
		protected GenericChildEditPolicy(final EditPart child) {
			_child = child;
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
			assert _child.getModel() instanceof AbstractWidgetModel : "widget models must be derived from AbstractWidgetModel"; //$NON-NLS-1$"

			return new ArrayList();

		}
	}

}
