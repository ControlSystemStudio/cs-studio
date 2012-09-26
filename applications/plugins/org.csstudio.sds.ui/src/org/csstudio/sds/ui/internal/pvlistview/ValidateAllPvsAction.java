package org.csstudio.sds.ui.internal.pvlistview;

import java.util.List;

import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ValidateAllPvsAction extends Action implements IMenuCreator {

	private Menu actionMenu;

	private final List<ValidatePvsAction> validateActions;

	public ValidateAllPvsAction(List<ValidatePvsAction> validateActions) {
		this.validateActions = validateActions;
		setMenuCreator(this);
	}

	@Override
	public void run() {
		for (ValidatePvsAction validateAction : validateActions) {
			validateAction.run();
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public String getText() {
		return "Validate";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return AbstractUIPlugin.imageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
				"icons/validationButtonImage.png");
	}
	
	@Override
	public Menu getMenu(Control parent) {
		if (actionMenu == null) {
			actionMenu = new Menu(parent);
			for (ValidatePvsAction validateAction : validateActions) {
				ActionContributionItem item = new ActionContributionItem(
						validateAction);
				item.fill(actionMenu, -1);
			}
		}
		
		return actionMenu;
	}

	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}

}
