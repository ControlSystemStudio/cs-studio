package org.csstudio.opibuilder.editpolicies;

import org.csstudio.opibuilder.commands.AddWidgetCommand;
import org.csstudio.opibuilder.commands.ChangeGuideCommand;
import org.csstudio.opibuilder.commands.CloneCommand;
import org.csstudio.opibuilder.commands.SetBoundsCommand;
import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.commands.WidgetSetConstraintCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.feedback.IGraphicalFeedbackFactory;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.GuideModel;
import org.csstudio.opibuilder.util.GuideUtil;
import org.csstudio.opibuilder.util.WidgetsService;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
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
	protected EditPolicy createChildEditPolicy(EditPart child) {
		IGraphicalFeedbackFactory feedbackFactory = 
			WidgetsService.getInstance().getWidgetFeedbackFactory(
					((AbstractWidgetModel)child.getModel()).getTypeID());
		if(feedbackFactory != null && child instanceof AbstractBaseEditPart){
			return new GraphicalFeedbackChildEditPolicy(
					(AbstractBaseEditPart) child, feedbackFactory);
		}else
			return super.createChildEditPolicy(child);
	}
	
	@Override
	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {
		if(!(child instanceof AbstractBaseEditPart) || !(constraint instanceof Rectangle))
			return super.createChangeConstraintCommand(request, child, constraint);
		AbstractBaseEditPart part = (AbstractBaseEditPart) child;
		AbstractWidgetModel widgetModel = part.getCastedModel();

		IGraphicalFeedbackFactory feedbackFactory = 
			WidgetsService.getInstance().getWidgetFeedbackFactory(widgetModel.getTypeID());
		Command cmd;
		if(feedbackFactory != null){
			cmd = feedbackFactory.createChangeBoundsCommand(
					widgetModel, request, (Rectangle)constraint);
		}else		
			cmd = new WidgetSetConstraintCommand(
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
	protected Command createAddCommand(EditPart child, Object constraint) {
		if(!(child instanceof AbstractBaseEditPart) || !(constraint instanceof Rectangle))
			return super.createAddCommand(child, constraint);
		
		AbstractContainerModel container = (AbstractContainerModel)getHost().getModel();
		AbstractWidgetModel widget = (AbstractWidgetModel)child.getModel();
		CompoundCommand result = new CompoundCommand("Adding widgets to container");
		
		result.add(new AddWidgetCommand(container, widget));
		result.add(new SetBoundsCommand(widget, (Rectangle) constraint));
		return result;		
	}
	
	
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		String typeId = determineTypeIdFromRequest(request);

		IGraphicalFeedbackFactory feedbackFactory = 
			WidgetsService.getInstance().getWidgetFeedbackFactory(typeId);
		
		WidgetCreateCommand widgetCreateCommand = new WidgetCreateCommand((AbstractWidgetModel)request.getNewObject(), 
					(AbstractContainerModel)getHost().getModel(), 
					(Rectangle)getConstraintFor(request), false);
		if(feedbackFactory != null){
			CompoundCommand compoundCommand = new CompoundCommand();
			compoundCommand.add(widgetCreateCommand);
			compoundCommand.add(feedbackFactory.createInitialBoundsCommand(
					(AbstractWidgetModel)request.getNewObject(), request, (Rectangle)getConstraintFor(request)));
			return compoundCommand;
		}else
			return widgetCreateCommand;
	}
	
	
	/**
	 * Override to provide custom feedback figure for the given create request.
	 * 
	 * @param request
	 *            the create request
	 * @return custom feedback figure
	 */
	@Override
	protected IFigure createSizeOnDropFeedback(final CreateRequest request) {
		String typeId = determineTypeIdFromRequest(request);

		IGraphicalFeedbackFactory feedbackFactory = 
			WidgetsService.getInstance().getWidgetFeedbackFactory(typeId);

		if(feedbackFactory != null){
			Shape feedbackFigure = feedbackFactory
				.createSizeOnDropFeedback(request);
			addFeedback(feedbackFigure);
			return feedbackFigure;
		}else{
			return super.createSizeOnDropFeedback(request);
		}	
	}
	
	@Override
	protected void showSizeOnDropFeedback(CreateRequest request) {
		String typeId = determineTypeIdFromRequest(request);

		IGraphicalFeedbackFactory feedbackFactory =
			WidgetsService.getInstance().getWidgetFeedbackFactory(typeId);

		if(feedbackFactory != null){
			IFigure feedbackFigure = getSizeOnDropFeedback(request);

			feedbackFactory.showSizeOnDropFeedback(request, feedbackFigure,
				getCreationFeedbackOffset(request));

			feedbackFigure.repaint();
		}else{
			super.showSizeOnDropFeedback(request);
		}

		
	}
	
	/**
	 * Creates a prototype object to determine the type identification of the
	 * widget model, that is about to be created.
	 * 
	 * @param request
	 *            the create request
	 * @return the type identification
	 */
	@SuppressWarnings("unchecked")
	private String determineTypeIdFromRequest(final CreateRequest request) {
		Class newObject = (Class) request.getNewObjectType();
		AbstractWidgetModel instance;
		String typeId = ""; //$NON-NLS-1$
		try {
			instance = (AbstractWidgetModel) newObject.newInstance();
			typeId = instance.getTypeID();
		} catch (InstantiationException e) {
			CentralLogger.getInstance().error(this, e);
		} catch (IllegalAccessException e) {
			CentralLogger.getInstance().error(this, e);
		}

		return typeId;
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
			final AbstractBaseEditPart part, final Command cmd,
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
			final AbstractBaseEditPart part, final Command cmd,
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

	
	
	@Override
	protected Command getCloneCommand(ChangeBoundsRequest request) {
		CloneCommand clone = new CloneCommand((AbstractContainerModel)getHost().getModel());
		
		GraphicalEditPart currPart = null;
		for (Object part : request.getEditParts()) {
			currPart = (GraphicalEditPart)part;
			clone.addPart((AbstractWidgetModel)currPart.getModel(), (Rectangle)getConstraintForClone(currPart, request));
		}
		
		// Attach to horizontal guide, if one is given
		Integer guidePos = (Integer)request.getExtendedData()
				.get(SnapToGuides.KEY_HORIZONTAL_GUIDE);
		if (guidePos != null) {
			int hAlignment = ((Integer)request.getExtendedData()
					.get(SnapToGuides.KEY_HORIZONTAL_ANCHOR)).intValue();
			clone.setGuide(findGuideAt(guidePos.intValue(), true), hAlignment, true);
		}
		
		// Attach to vertical guide, if one is given
		guidePos = (Integer)request.getExtendedData()
				.get(SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos != null) {
			int vAlignment = ((Integer)request.getExtendedData()
					.get(SnapToGuides.KEY_VERTICAL_ANCHOR)).intValue();
			clone.setGuide(findGuideAt(guidePos.intValue(), false), vAlignment, false);
		}
		return clone;
	}

}
