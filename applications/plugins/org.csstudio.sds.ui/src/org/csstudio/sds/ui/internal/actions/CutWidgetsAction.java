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

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.ui.internal.commands.DeleteWidgetsCommand;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action which cuts the selected widgets into the clipboard.
 *
 * @author Joerg Rathlev
 */
public final class CutWidgetsAction extends AbstractCutCopyWidgetsAction {

    /**
     * Action ID of this action.
     */
    public static final String ID = "org.csstudio.sds.ui.internal.actions.CutWidgetsAction";

    /**
     * Creates a new cut action.
     *
     * @param workbenchPart
     *            the workbench part.
     */
    public CutWidgetsAction(final IWorkbenchPart workbenchPart) {
        super(workbenchPart);
        setId(ID);
        setText("Cut");
        setActionDefinitionId("org.eclipse.ui.edit.cut");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        List<AbstractWidgetModel> widgets = getSelectedWidgetModels();

        if (widgets.size() > 0) {
            copyToClipboard(widgets);
            deleteWidgets(widgets);
        }
    }

    /**
     * Deletes the widgets from the editor. The modification is labeled as a cut
     * operation and can be undone by the user.
     *
     * @param widgets
     *            the widgets.
     */
    private void deleteWidgets(final List<AbstractWidgetModel> widgets) {
        ContainerModel container = getDisplayEditor().getDisplayModel();
        CompoundCommand cmd = new CompoundCommand("Cut " + widgets.size() + " Widget" + ((widgets.size() > 1) ? "s" : ""));
        cmd.add(new DeleteWidgetsCommand(getDisplayEditor().getGraphicalViewer(), container, widgets));
        execute(cmd);
    }

    /**
     * Returns the currently open display editor.
     *
     * @return the currently open display editor
     */
    private DisplayEditor getDisplayEditor() {
        if (getWorkbenchPart() instanceof DisplayEditor) {
            return (DisplayEditor) getWorkbenchPart();
        }
        return null;
    }
}
