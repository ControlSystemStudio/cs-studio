/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

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
 * @author Joerg Rathlev (similar class in SDS)
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
	 * @return a list with all widget models that are currently selected
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
