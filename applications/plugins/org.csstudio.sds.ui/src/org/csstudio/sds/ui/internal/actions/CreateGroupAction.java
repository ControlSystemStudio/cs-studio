package org.csstudio.sds.ui.internal.actions;

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.GroupingContainerModel;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.commands.AddWidgetCommand;
import org.csstudio.sds.ui.internal.editor.WidgetCreationFactory;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * An Action to create a group surrounding the selected Widgets.
 * @author Kai Meyer
 *
 */
public final class CreateGroupAction extends AbstractGroupingAction {
	
	/**
	 * The image for this action.
	 */
	private static final ImageDescriptor IMAGE_DESCRIPTOR = CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID, "/icons/addgroup.gif");

	/**
	 * The selected widgets.
	 */
	private List<AbstractWidgetModel> _widgets;
	
	/**
	 * The common ancestor for all widgets.
	 */
	private ContainerModel _ancestor;
	
	/**
	 * The offset for the surrounding {@link GroupingContainerModel}.
	 */
	private static final int OFFSET = 5;
	
	/**
	 * Constructor.
	 * @param widgets The widgets which should be added to the new container 
	 * @param cmdStack the {@link CommandStack} for the internal {@link Command}
	 */
	public CreateGroupAction(final CommandStack cmdStack, final List<AbstractWidgetModel> widgets) {
		super("Create a Group", IMAGE_DESCRIPTOR, cmdStack);
		_widgets = widgets;
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected Command createCommand() {
		CompoundCommand cmd = new CompoundCommand();
		cmd.setLabel("Create Group");
		//ContainerModel parent = _widgets.get(0).getParent();
		_ancestor = this.getCommonAncestor(_widgets);
		
		this.removeWidgets(cmd, _widgets);
		
		// Create a new GroupingContainer
		Rectangle bounds = this.getFittingBounds(_widgets);
		WidgetCreationFactory factory = new WidgetCreationFactory(GroupingContainerModel.ID);
		// create a widget
		ContainerModel containerModel = (ContainerModel) factory.getNewObject();
		// initialize widget
		containerModel.setLocation(bounds.x,bounds.y);
		containerModel.setWidth(bounds.width);
		containerModel.setHeight(bounds.height);
		containerModel.setLayer(_ancestor.getLayerSupport().getActiveLayer().getId());
		this.setContainerModel(containerModel);
		AddWidgetCommand addCommand = new AddWidgetCommand(_ancestor, this.getContainerModel(), true);
		cmd.add(addCommand);
		
		this.addWidgets(cmd, this.getContainerModel(), _widgets);
		return cmd;
	}
	
	/**
	 * Determines the common ancestor for all given {@link AbstractWidgetModel}s.
	 * @param widgets The {@link AbstractWidgetModel}s
	 * @return The {@link ContainerModel}, which is the ancestor for all {@link AbstractWidgetModel}s
	 */
	private ContainerModel getCommonAncestor(final List<AbstractWidgetModel> widgets) {
		if (widgets.size()>0) {
			ContainerModel ancestor = widgets.get(0).getParent();
			while (ancestor!=null) {
				boolean isForAllReachable = true;
				for (AbstractWidgetModel widget : widgets) {
					if (!isAncestorReachable(ancestor, widget)) {
						isForAllReachable = false;
						break;
					}
				}
				if (isForAllReachable) {
					return ancestor; 
				}
				ancestor = ancestor.getParent();
			}
		}
		return null;
	}
	
	/**
	 * Determines if the given {@link ContainerModel} is an ancestor of this model.
	 * @param ancestor The probably ancestor
	 * @param widget The {@link AbstractWidgetModel} which ancestor should be find 
	 * @return true, if the given {@link ContainerModel} is an ancestor of this model, false otherwise
	 */
	private boolean isAncestorReachable(final ContainerModel ancestor, final AbstractWidgetModel widget) {
		ContainerModel parent = widget.getParent();
		while (parent!=null) {
			if (parent.equals(ancestor)) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}
	
	/**
	 * Determines the bounds for the container, which surrounds for all selected widgets.
	 * @param widgets The widgets, which should be added to a container
	 * @return The bounds for the new container
	 */
	private Rectangle getFittingBounds(final List<AbstractWidgetModel> widgets) {
		int x = widgets.get(0).getXForAncestor(_ancestor);
		int y = widgets.get(0).getYForAncestor(_ancestor);
		int absoluteWidth = widgets.get(0).getWidth() + widgets.get(0).getXForAncestor(_ancestor);
		int absoluteHeight = widgets.get(0).getHeight() + widgets.get(0).getYForAncestor(_ancestor);
		for (int i=1;i<widgets.size();i++) {			
			AbstractWidgetModel model = widgets.get(i);
			x = Math.min(x, model.getXForAncestor(_ancestor));
			y = Math.min(y, model.getYForAncestor(_ancestor));
			absoluteWidth = Math.max(absoluteWidth, model.getXForAncestor(_ancestor) + model.getWidth());
			absoluteHeight = Math.max(absoluteHeight, model.getYForAncestor(_ancestor) + model.getHeight());
		}
		return new Rectangle(x-OFFSET, y-OFFSET, absoluteWidth-x+2*OFFSET, absoluteHeight-y+2*OFFSET);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point adaptWidgetPosition(final AbstractWidgetModel widgetModel) {
		int x = widgetModel.getXForAncestor(_ancestor) - this.getContainerModel().getX();
		int y = widgetModel.getYForAncestor(_ancestor) - this.getContainerModel().getY();
//		int x = widgetModel.getX() - this.getContainerModel().getX();
//		int y = widgetModel.getY() - this.getContainerModel().getY();
		return new Point(x,y);
	}

}
