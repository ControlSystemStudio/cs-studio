package org.csstudio.sds.ui.internal.actions;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.internal.commands.SetPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IWorkbenchPart;

/**
 * The Action to move the selected Widgets to the specified layer.
 * @author Kai Meyer
 *
 */
public final class MoveToLayerAction extends AbstractOrderAction {
	
	/**
	 * Action ID of this action.
	 */
	public static final String ID = "org.csstudio.sds.ui.internal.actions.MoveToLayerAction";
	/**
	 * The name of the Layer.
	 */
	private final String _layerName;

	/**
	 * Constructor.
	 * @param workbenchPart
	 * 			The {@link IWorkbenchPart} for this Action
	 * @param layerName
	 * 			The name of the new layer for the widgets
	 */
	public MoveToLayerAction(final IWorkbenchPart workbenchPart, final String layerName) {
		super(workbenchPart);
		_layerName = layerName;
		setId(ID);
		setText("Move to Layer '"+_layerName+"'");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createCommand(final ContainerModel container,
			final AbstractWidgetModel widget) {
		return new SetPropertyCommand(widget, AbstractWidgetModel.PROP_LAYER, _layerName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update() {
		super.update();
		this.setChecked(false);
		for (Object selObject : getSelectedObjects()) {
			if (selObject instanceof AbstractWidgetEditPart) {
				AbstractWidgetModel model = ((AbstractWidgetEditPart)selObject).getWidgetModel();
				if (model.getLayer().equals(_layerName)) {
					this.setChecked(true);
				}
			}
		}
	}

}
