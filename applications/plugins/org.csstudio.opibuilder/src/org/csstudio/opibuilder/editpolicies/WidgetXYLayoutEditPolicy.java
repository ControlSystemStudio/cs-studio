package org.csstudio.opibuilder.editpolicies;

import org.csstudio.opibuilder.commands.ChangeGuideCommand;
import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.commands.WidgetSetConstraintCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditpart;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.GuideModel;
import org.csstudio.opibuilder.util.GuideUtil;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.rulers.RulerProvider;

/**
 * The EditPolicy for create/move/resize a widget.
 *
 * @author Xihui Chen
 *
 */
public class WidgetXYLayoutEditPolicy extends XYLayoutEditPolicy {

	
	@Override
	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {
		if(!(child instanceof AbstractBaseEditpart) || !(constraint instanceof Rectangle))
			return super.createChangeConstraintCommand(request, child, constraint);
		AbstractBaseEditpart part = (AbstractBaseEditpart) child;
		AbstractWidgetModel widgetModel = part.getCastedModel();
		Command cmd = new WidgetSetConstraintCommand(
					widgetModel, request, (Rectangle)constraint);
		
		// for guide support
			
			if ((request.getResizeDirection() & PositionConstants.NORTH_SOUTH) != 0) {
				Integer guidePos = (Integer) request.getExtendedData().get(
						SnapToGuides.KEY_HORIZONTAL_GUIDE);
				if (guidePos != null) {
					cmd = chainGuideAttachmentCommand(request, part, cmd,
							true);
				} else if (GuideUtil.getInstance().getGuide(
						widgetModel, true) != null) {
					// SnapToGuides didn't provide a horizontal guide, but
					// this part is attached
					// to a horizontal guide. Now we check to see if the
					// part is attached to
					// the guide along the edge being resized. If that is
					// the case, we need to
					// detach the part from the guide; otherwise, we leave
					// it alone.
					int alignment = GuideUtil.getInstance().getGuide(
							widgetModel, true).getAlignment(widgetModel);
					int edgeBeingResized = 0;
					if ((request.getResizeDirection() & PositionConstants.NORTH) != 0) {
						edgeBeingResized = -1;
					} else {
						edgeBeingResized = 1;
					}
					if (alignment == edgeBeingResized) {
						cmd = cmd.chain(new ChangeGuideCommand(widgetModel, true));
					}
				}
			}

			if ((request.getResizeDirection() & PositionConstants.EAST_WEST) != 0) {
				Integer guidePos = (Integer) request.getExtendedData().get(
						SnapToGuides.KEY_VERTICAL_GUIDE);
				if (guidePos != null) {
					cmd = chainGuideAttachmentCommand(request, part, cmd,
							false);
				} else if (GuideUtil.getInstance().getGuide(
						widgetModel, false) != null) {
					int alignment = GuideUtil.getInstance().getGuide(
							widgetModel, false).getAlignment(widgetModel);
					int edgeBeingResized = 0;
					if ((request.getResizeDirection() & PositionConstants.WEST) != 0) {
						edgeBeingResized = -1;
					} else {
						edgeBeingResized = 1;
					}
					if (alignment == edgeBeingResized) {
						cmd = cmd.chain(new ChangeGuideCommand(widgetModel, false));
					}
				}
			}

			if (request.getType().equals(REQ_MOVE_CHILDREN)
					|| request.getType().equals(REQ_ALIGN_CHILDREN)) {
				cmd = chainGuideAttachmentCommand(request, part, cmd, true);
				cmd = chainGuideAttachmentCommand(request, part, cmd, false);
				cmd = chainGuideDetachmentCommand(request, part, cmd, true);
				cmd = chainGuideDetachmentCommand(request, part, cmd, false);
			}
		
	return cmd;
	
	}
	
	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		return null;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		return new WidgetCreateCommand((AbstractWidgetModel)request.getNewObject(), 
					(AbstractContainerModel)getHost().getModel(), 
					(Rectangle)getConstraintFor(request));
	}
	
	/**
	 * Adds a ChangeGuideCommand to the given Command.
	 * 
	 * @param request
	 *            The Request
	 * @param part
	 *            The AbstractWidgetEditPart, which model should be detached
	 *            from a guide
	 * @param cmd
	 *            The Command
	 * @param horizontal
	 *            A boolean, true if the guide is horizontal, false otherwise
	 * @return Command The given command
	 */
	private Command chainGuideAttachmentCommand(final Request request,
			final AbstractBaseEditpart part, final Command cmd,
			final boolean horizontal) {
		Command result = cmd;

		// Attach to guide, if one is given
		Integer guidePos = (Integer) request.getExtendedData().get(
				horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
						: SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos != null) {
			int alignment = ((Integer) request.getExtendedData().get(
					horizontal ? SnapToGuides.KEY_HORIZONTAL_ANCHOR
							: SnapToGuides.KEY_VERTICAL_ANCHOR)).intValue();
			ChangeGuideCommand cgm = new ChangeGuideCommand(
					part.getCastedModel(), horizontal);
			cgm.setNewGuide(findGuideAt(guidePos.intValue(), horizontal),
					alignment);
			result = result.chain(cgm);
		}

		return result;
	}
	
	/**
	 * Adds a ChangeGuideCommand to the given Command.
	 * 
	 * @param request
	 *            The request
	 * @param part
	 *            The AbstractWidgetEditPart, which model should be detached
	 *            from a guide
	 * @param cmd
	 *            The Command
	 * @param horizontal
	 *            A boolean, true if the guide is horizontal, false otherwise
	 * @return Command The given command
	 */
	private Command chainGuideDetachmentCommand(final Request request,
			final AbstractBaseEditpart part, final Command cmd,
			final boolean horizontal) {
		Command result = cmd;

		// Detach from guide, if none is given
		Integer guidePos = (Integer) request.getExtendedData().get(
				horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
						: SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos == null) {
			result = result.chain(new ChangeGuideCommand(part.getCastedModel(),
					horizontal));
		}

		return result;
	}
	
	/**
	 * Returns the guide at the given position and with the given orientation.
	 * 
	 * @param pos
	 *            The Position of the guide
	 * @param horizontal
	 *            The orientation of the guide
	 * @return GuideModel The GuideModel
	 */
	private GuideModel findGuideAt(final int pos, final boolean horizontal) {
		RulerProvider provider = ((RulerProvider) getHost().getViewer()
				.getProperty(
						horizontal ? RulerProvider.PROPERTY_VERTICAL_RULER
								: RulerProvider.PROPERTY_HORIZONTAL_RULER));
		return (GuideModel) provider.getGuideAt(pos);
	}


}
