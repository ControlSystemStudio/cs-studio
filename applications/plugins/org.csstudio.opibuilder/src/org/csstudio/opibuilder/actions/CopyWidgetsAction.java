package org.csstudio.opibuilder.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;

/**The action to copy selected widgets to clipboard.
 * @author Joerg Rathlev (class of same name in SDS)
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
		ISharedImages sharedImages = 
			part.getSite().getWorkbenchWindow().getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages
        .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		this.pasteWidgetsAction = pasteWidgetsAction;
	}

	@Override
	protected boolean calculateEnabled() {
		if(getSelectedObjects().size() <=0 || 
				((getSelectedObjects().size() == 1)&& 
						getSelectedObjects().get(0) instanceof DisplayEditpart))
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
	 * @return a list with all widget models that are currently selected. 
	 * The order of the selected widgets was kept. 
	 */
	@SuppressWarnings("unchecked")
	protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
		List selection = getSelectedObjects();
	
		List<AbstractWidgetModel> sameParentModels = new ArrayList<AbstractWidgetModel>();
		List<AbstractWidgetModel> differentParentModels = new ArrayList<AbstractWidgetModel>();
		List<AbstractWidgetModel> result = new ArrayList<AbstractWidgetModel>();
		AbstractContainerModel parent = null;
		for (Object o : selection) {
			if (o instanceof AbstractBaseEditPart && !(o instanceof DisplayEditpart)) {
				AbstractWidgetModel widgetModel = 
					((AbstractBaseEditPart) o).getWidgetModel();
				if(parent == null)
					parent = widgetModel.getParent();
				if(widgetModel.getParent() == parent)
					sameParentModels.add(widgetModel);
				else 
					differentParentModels.add(widgetModel);
			}
		}
		//sort widgets to its original order
		if(sameParentModels.size() > 1){
			AbstractWidgetModel[] modelArray = sameParentModels.toArray(new AbstractWidgetModel[0]);
		
			Arrays.sort(modelArray, new Comparator<AbstractWidgetModel>(){

				public int compare(AbstractWidgetModel o1,
						AbstractWidgetModel o2) {
					if(o1.getParent().getChildren().indexOf(o1) > 
						o2.getParent().getChildren().indexOf(o2))
						return 1;
					else
						return -1;					
				}
				
			});
			result.addAll(Arrays.asList(modelArray));
			if(differentParentModels.size() > 0)
				result.addAll(differentParentModels);
			return result;
		}
		if(differentParentModels.size() > 0)
			sameParentModels.addAll(differentParentModels);
		
		return sameParentModels;
	}

}
