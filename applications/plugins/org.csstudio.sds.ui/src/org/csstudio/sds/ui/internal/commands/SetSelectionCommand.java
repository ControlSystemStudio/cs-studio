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
package org.csstudio.sds.ui.internal.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.internal.editparts.DisplayEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;

/**
 * An undoable command that can be used remove the current selection of a
 * graphical viewer.
 *
 * @author swende
 *
 */
public final class SetSelectionCommand extends Command {

    private List<AbstractWidgetModel> _oldSelection;

    private EditPartViewer _viewer;

    private DisplayModel _displayModel;

    private List<AbstractWidgetModel> _newSelection;

    /**
     * Constructor.
     * @param viewer the graphical viewer
     */
    public SetSelectionCommand(EditPartViewer viewer, List<AbstractWidgetModel> selectedWidgets) {
        assert viewer != null;
        assert selectedWidgets!=null;
        _viewer = viewer;
        _displayModel = (DisplayModel)((DisplayEditPart) _viewer.getRootEditPart().getChildren().get(0)).getModel();
        _newSelection = selectedWidgets;
    }

    public SetSelectionCommand(EditPartViewer viewer, AbstractWidgetModel widget) {
        this(viewer, Arrays.asList(widget));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // memorize old selection
        _oldSelection = new ArrayList<AbstractWidgetModel>();

        for(EditPart ep : (List<EditPart>)_viewer.getSelectedEditParts()) {
            if(ep instanceof AbstractBaseEditPart) {
                _oldSelection.add(((AbstractBaseEditPart)ep).getWidgetModel());
            }
        }

        // select new widgets
        _displayModel.selectWidgets(_newSelection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _displayModel.selectWidgets(_oldSelection);
    }

}
