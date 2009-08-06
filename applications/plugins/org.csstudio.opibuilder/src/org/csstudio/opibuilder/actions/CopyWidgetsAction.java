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


	private PasteWidgetsAction pasteWidgetsAction;
	
	/**
	 * @param part the OPI Editor
	 * @param pasteWidgetsAction pass the paste action will 
	 * help to update the enable state of the paste action
	 * after copy action invoked.
	 */
	public CopyWidgetsAction(OPIEditor part, PasteWidgetsAction pasteWidgetsAction) {
		super(part);
		setText("Copy");
		setActionDefinitionId("org.eclipse.ui.edit.copy"); //$NON-NLS-1$
		setId(ActionFactory.COPY.getId());
		this.pasteWidgetsAction = pasteWidgetsAction;
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
		pasteWidgetsAction.update();
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
