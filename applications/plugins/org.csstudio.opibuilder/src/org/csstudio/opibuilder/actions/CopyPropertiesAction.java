package org.csstudio.opibuilder.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.dnd.Transfer;

/**The action that only copy properties from a widget.
 * @author Xihui Chen
 *
 */
public class CopyPropertiesAction extends SelectionAction {


	public static final String ID = "org.csstudio.opibuilder.actions.copyproperties";
	private PastePropertiesAction pastePropertiesAction;
	
	/**
	 * @param part the OPI Editor
	 * @param pasteWidgetsAction pass the paste action will 
	 * help to update the enable state of the paste action
	 * after copy action invoked.
	 */
	public CopyPropertiesAction(OPIEditor part, PastePropertiesAction pasteWidgetsAction) {
		super(part);
		setText("Copy Properties");
		setId(ID);
		this.pastePropertiesAction = pasteWidgetsAction;
	}

	@Override
	protected boolean calculateEnabled() {
		if(getSelectedObjects().size() == 1)
			return true;
		return false;
	}
	
	
	@Override
	public void run() {
		((OPIEditor)getWorkbenchPart()).getClipboard()
			.setContents(new Object[]{getSelectedWidgetModels()}, 
				new Transfer[]{OPIWidgetsTransfer.getInstance()});
		pastePropertiesAction.update();
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
