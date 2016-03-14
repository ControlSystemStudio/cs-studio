/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.actions;
import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.widgets.editparts.TabEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;


/**Duplicate the current tab.
 * @author Xihui Chen
 *
 */
public class ChangeTabIndexAction extends AbstractWidgetTargetAction {



    @Override
    public void run(IAction action) {
        int activeTabIndex = getSelectedTabWidget().getActiveTabIndex();
        InputDialog newIndexDialog = new InputDialog(
                null, "Change Tab Index", "New Index", "" + activeTabIndex,
                new IInputValidator() {

                    @Override
                    public String isValid(String newText) {
                        try {
                            int newIndex = Integer.parseInt(newText);
                            int itemCount = getSelectedTabWidget().getTabItemCount();
                            if(newIndex < 0 || newIndex >= itemCount)
                                return NLS.bind("Invalid Tab Index! It must be between [0, {0}]",  itemCount-1);
                        } catch (Exception e) {
                            return "It must be an integer!";
                        }
                        return null;
                    }
                });
        if(newIndexDialog.open() == Window.OK){
            int newIndex = Integer.parseInt(newIndexDialog.getValue());
            if( newIndex != activeTabIndex){
                Command command = new ChangeTabIndexCommand(getSelectedTabWidget(), newIndex);
                    execute(command);

            }
        }


    }


    /**
     * Gets the widget models of all currently selected EditParts.
     *
     * @return a list with all widget models that are currently selected
     */
    protected final TabEditPart getSelectedTabWidget() {
        return (TabEditPart)selection.getFirstElement();
    }


}
