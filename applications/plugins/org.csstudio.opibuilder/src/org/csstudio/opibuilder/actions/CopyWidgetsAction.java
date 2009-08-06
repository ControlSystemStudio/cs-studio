package org.csstudio.opibuilder.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.actions.ActionFactory;

/**The action to copy selected widgets to clipboard.
 * @author Xihui Chen
 *
 */
public class CopyWidgetsAction extends SelectionAction {


	public CopyWidgetsAction(OPIEditor part) {
		super(part);
		setText("Copy");
		setActionDefinitionId(ActionFactory.COPY.getId());
		setId(ActionFactory.COPY.getId());
	}

	@Override
	protected boolean calculateEnabled() {
		if(getSelectedObjects().size() == 0 || 
				(getSelectedObjects().size() == 1 && 
						getSelectedObjects().get(0) instanceof DisplayEditpart)
						|| getSelectedWidgetModels().size() <=0)
			return false;
		return true;
	}
	
	
	@Override
	public void run() {
		((OPIEditor)getWorkbenchPart()).getClipboard()
			.setContents(new Object[]{getSelectedWidgetModels()}, 
				new Transfer[]{OPIWidgetsTransfer.getInstance()});
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
						.getCastedModel());
			}
		}
		return selectedWidgetModels;
	}

}
