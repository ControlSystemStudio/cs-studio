package org.csstudio.sds.ui.internal.commands;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.csstudio.sds.ui.internal.feedback.GraphicalFeedbackContributionsService;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.CreateRequest;

/**
 * A command, which creates a widget model and adds it to the display model.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class CreateElementCommand extends Command {

	/**
	 * The model.
	 */
	private ContainerModel _container;

	/**
	 * The create request.
	 */
	private CreateRequest _request;

	/**
	 * Bounds, which define size and location of the new widget.
	 */
	private Rectangle _bounds;
	
	/**
	 * The internal {@link CompoundCommand}. 
	 */
	private Command _compoundCommand;

	/**
	 * Constructs the command.
	 * 
	 * @param container
	 *            the display model to which the widgets should get added
	 * @param request
	 *            the create request
	 * @param bounds
	 *            bounds, which define size and location of the new widget
	 */
	public CreateElementCommand(final ContainerModel container,
			final CreateRequest request, final Rectangle bounds) {
		assert container != null;
		assert request != null;
		assert bounds != null;
		this.setLabel("Create widget");
		_container = container;
		_request = request;
		_bounds = bounds;
	}
	
	private Command createCompoundCommands() {
		CompoundCommand comCmd = new CompoundCommand();
		// create widget model
		AbstractWidgetModel model = (AbstractWidgetModel) _request
				.getNewObject();
		assert model != null;
		// set constraints
		IGraphicalFeedbackFactory feedbackFactory = GraphicalFeedbackContributionsService
		.getInstance().getGraphicalFeedbackFactory(model.getTypeID());				
		if(feedbackFactory!=null) {
			Command boundsCmd = feedbackFactory.createInitialBoundsCommand(model, _request,	_bounds);
			comCmd.add(boundsCmd);
		}
		
		comCmd.add(new SetPropertyCommand(model, AbstractWidgetModel.PROP_LAYER, _container.getLayerSupport().getActiveLayer().getId()));	
		comCmd.add(new AddWidgetCommand(_container, model));
		
		return comCmd;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		_compoundCommand = this.createCompoundCommands();
		_compoundCommand.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_compoundCommand.undo();
	}
}
