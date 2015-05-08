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
package org.csstudio.sds.ui.internal.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.ui.internal.commands.DeleteWidgetsCommand;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action class that copies all currently selected widgets to the Clipboard.
 *
 * @author Sven Wende
 *
 */
public final class DeleteWidgetsAction extends DeleteAction {

    public DeleteWidgetsAction(IWorkbenchPart part) {
        super(part);
    }

    @SuppressWarnings({ "unchecked", "restriction" })
    @Override
    public Command createDeleteCommand(List objects) {
        if (objects.isEmpty() || !(objects.get(0) instanceof EditPart)) {
            return null;
        }

        GroupRequest deleteReq = new GroupRequest(RequestConstants.REQ_DELETE);
        deleteReq.setEditParts(objects);

        CompoundCommand compoundCmd = new CompoundCommand(GEFMessages.DeleteAction_ActionDeleteCommandName);

        Map<ContainerModel, List<AbstractWidgetModel>> deleteCandidates = new HashMap<ContainerModel, List<AbstractWidgetModel>>();

        for (int i = 0; i < objects.size(); i++) {
            EditPart ep = (EditPart) objects.get(i);

            AbstractWidgetModel w = (AbstractWidgetModel) ep.getModel();
            ContainerModel c = w.getParent();

            if (c != null) {
                if (!deleteCandidates.containsKey(c)) {
                    deleteCandidates.put(c, new ArrayList<AbstractWidgetModel>());
                }
                deleteCandidates.get(c).add(w);
            }
        }

        DisplayEditor editor = (DisplayEditor) getWorkbenchPart();

        for (ContainerModel c : deleteCandidates.keySet()) {
            compoundCmd.add(new DeleteWidgetsCommand(editor.getGraphicalViewer(), c, deleteCandidates.get(c)));
        }

        return compoundCmd;
    }
}