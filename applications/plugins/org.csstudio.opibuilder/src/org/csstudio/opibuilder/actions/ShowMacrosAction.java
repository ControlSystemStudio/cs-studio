package org.csstudio.opibuilder.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPart;

/**Show the predefined macros of the selected widget in console and message dialog.
 * @author Xihui Chen
 *
 */
public class ShowMacrosAction extends SelectionAction {


	public static final String ID = "org.csstudio.opibuilder.actions.showmacors";
	
	/**
	 * @param part the OPI Editor
	 * @param pasteWidgetsAction pass the paste action will 
	 * help to update the enable state of the paste action
	 * after copy action invoked.
	 */
	public ShowMacrosAction(IWorkbenchPart part) {
		super(part);
		setText("Show Predefined Macros");
		setId(ID);
	}

	@Override
	protected boolean calculateEnabled() {
		if(getSelectedWidgetModels().size() == 1)
			return true;
		return false;
	}
	
	
	@Override
	public void run() {
		AbstractWidgetModel widget = getSelectedWidgetModels().get(0);
		String message = NLS.bind("The predefined macros of {0}:\n", widget.getName());
		StringBuilder sb = new StringBuilder(message);
		Map<String, String> macroMap = OPIBuilderMacroUtil.getWidgetMacroMap(widget);
		for(String key : macroMap.keySet()){
			sb.append(key + "=" + macroMap.get(key) + "\n");
		}
		ConsoleService.getInstance().writeInfo(sb.toString());
		MessageDialog.openInformation(null, "Predefined Macros", sb.toString());
	}
	
	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	@SuppressWarnings("unchecked")
	protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
		List selection = getSelectedObjects();
	
		List<AbstractWidgetModel> selectedWidgetModels = new ArrayList<AbstractWidgetModel>();
	
		for (Object o : selection) {
			if (o instanceof AbstractBaseEditPart) {
				selectedWidgetModels.add(((AbstractBaseEditPart) o)
						.getWidgetModel());
			}
		}
		return selectedWidgetModels;
	}

}
