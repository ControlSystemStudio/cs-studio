package org.csstudio.sds.ui.internal.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.editparts.GroupingContainerEditPart;
import org.csstudio.sds.ui.internal.commands.DeleteElementCommand;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * An Action to remove the selected widgets from their surrounding container.
 * @author Kai Meyer
 *
 */
public final class RemoveGroupAction extends AbstractGroupingAction {
	
	/**
	 * The image for this action.
	 */
	private static final ImageDescriptor IMAGE_DESCRIPTOR = CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID, "/icons/removegroup.gif");
	
	/**
	 * The selected container.
	 */
	private GroupingContainerEditPart _container; 
	
	/**
	 * Constructor.
	 * @param container The selected container
	 * @param cmdStack the {@link CommandStack} for the internal {@link Command}
	 */
	public RemoveGroupAction(final CommandStack cmdStack, final GroupingContainerEditPart container) {
		super("Remove Group", IMAGE_DESCRIPTOR, cmdStack);
		_container = container;
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected Command createCommand() {
		CompoundCommand cmd = new CompoundCommand();
		cmd.setLabel("Remove Group");
		this.setContainerModel(_container.getContainerModel());
		
		List<AbstractWidgetModel> widgets = new ArrayList<AbstractWidgetModel>();
		for (Object object : _container.getChildren()) {
			if (object instanceof AbstractBaseEditPart) {
				AbstractWidgetModel widgetModel = ((AbstractBaseEditPart)object).getWidgetModel();
				widgets.add(widgetModel);
			}
		}
	
		this.removeWidgets(cmd, widgets);
		
		// delete the container
		DeleteElementCommand deleteCommand = new DeleteElementCommand(this.getContainerModel().getParent(), this.getContainerModel());
		cmd.add(deleteCommand);
		
		this.addWidgets(cmd, this.getContainerModel().getParent(), widgets);
		
		return cmd;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point adaptWidgetPosition(final AbstractWidgetModel widgetModel) {
		int x = widgetModel.getX() + this.getContainerModel().getX();
		int y = widgetModel.getY() + this.getContainerModel().getY();
		return new Point(x,y);
	}

}
