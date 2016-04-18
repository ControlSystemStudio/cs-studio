/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;

/**The action to copy selected widgets to clipboard.
 * @author Joerg Rathlev (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class CopyWidgetsAction extends SelectionAction {



    /**
     * @param part the OPI Editor
     * @param pasteWidgetsAction pass the paste action will
     * help to update the enable state of the paste action
     * after copy action invoked.
     */
    public CopyWidgetsAction(OPIEditor part) {
        super(part);
        setText("Copy");
        setActionDefinitionId("org.eclipse.ui.edit.copy"); //$NON-NLS-1$
        setId(ActionFactory.COPY.getId());
        ISharedImages sharedImages =
            part.getSite().getWorkbenchWindow().getWorkbench().getSharedImages();
        setImageDescriptor(sharedImages
        .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
    }

    @Override
    protected boolean calculateEnabled() {
        if(getSelectedObjects().size() == 0 ||
                getSelectedObjects().size() == 1 && getSelectedObjects().get(0) instanceof EditPart
                && ((EditPart)getSelectedObjects().get(0)).getModel() instanceof DisplayModel)
            return false;
        for(Object o : getSelectedObjects()){
            if(o instanceof AbstractBaseEditPart)
                return true;
        }
        return false;
    }


    @Override
    public void run() {

        DisplayModel tempModel = new DisplayModel();
        List<AbstractWidgetModel> widgetModels = getSelectedWidgetModels();
        for(AbstractWidgetModel widget : widgetModels){
            tempModel.addChild(widget, false);
        }

        String xml = XMLUtil.widgetToXMLString(tempModel, false);

        ((OPIEditor)getWorkbenchPart()).getClipboard()
            .setContents(new Object[]{xml},
                new Transfer[]{OPIWidgetsTransfer.getInstance()});
        Display.getCurrent().asyncExec(new Runnable() {

            @Override
            public void run() {
                IAction pasteAction = ((ActionRegistry)((OPIEditor)getWorkbenchPart()).getAdapter(ActionRegistry.class)).
                getAction(ActionFactory.PASTE.getId());
                if(pasteAction != null){
                    ((PasteWidgetsAction)pasteAction).refreshEnable();
                }

            }
        });
    }

    /**
     * Gets the widget models of all currently selected EditParts.
     *
     * @return a list with all widget models that are currently selected.
     * The order of the selected widgets was kept.
     */
    protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
        List<?> selection = getSelectedObjects();

        List<AbstractWidgetModel> sameParentModels = new ArrayList<AbstractWidgetModel>();
        List<AbstractWidgetModel> differentParentModels = new ArrayList<AbstractWidgetModel>();
        List<AbstractWidgetModel> result = new ArrayList<AbstractWidgetModel>();
        AbstractContainerModel parent = null;
        for (Object o : selection) {
            if (o instanceof AbstractBaseEditPart && !(o instanceof DisplayEditpart)) {
                AbstractWidgetModel widgetModel =
                    (AbstractWidgetModel) ((EditPart) o).getModel();
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

                @Override
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
