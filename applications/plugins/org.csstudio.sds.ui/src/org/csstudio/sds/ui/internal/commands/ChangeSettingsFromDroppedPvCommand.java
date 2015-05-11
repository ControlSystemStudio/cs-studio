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

import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.internal.editor.dnd.DropPvRequest;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.progress.UIJob;

/**
 * A command, which changes the settings from a Widget, caused by a Drag and
 * Drop.
 *
 * @author Sven Wende, Kai Meyer
 */
public final class ChangeSettingsFromDroppedPvCommand extends Command {
    /**
     * The Abstract EditPart.
     */
    private AbstractWidgetEditPart _editPart;
    /**
     * The Request.
     */
    private DropPvRequest _request;

    /**
     * The old alias value.
     */
    private String _oldValue;

    /**
     * Constructor.
     *
     * @param request
     *            The Request
     * @param editPart
     *            The EditPart for the Widget
     */
    public ChangeSettingsFromDroppedPvCommand(final DropPvRequest request, final AbstractWidgetEditPart editPart) {
        assert request != null;
        assert editPart != null;
        _request = request;
        _editPart = editPart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        _oldValue = _editPart.getWidgetModel().getAliases().get("channel");

        if (_request.getDroppedProcessVariables().size() > 0) {
            // FIXME: SW: Umgang, falls mehrere PVs gedroppt wurden
            _editPart.getWidgetModel().setAliasValue("channel", _request.getDroppedProcessVariables().get(0).getFullName());

            // remember the state
            _editPart.getWidgetModel().saveState();

            // connect
            _editPart.getWidgetModel().setLive(true);

            UIJob job = new UIJob("Temporary Connect") {
                @Override
                public IStatus runInUIThread(final IProgressMonitor monitor) {
                    _editPart.getWidgetModel().setLive(false);

                    // reset widget state
                    _editPart.getWidgetModel().restoreState();

                    return Status.OK_STATUS;
                }
            };
            job.schedule(3000);
        }
    }

    @Override
    public void undo() {
        _editPart.getWidgetModel().setLive(false);

        if (_oldValue == null) {
            _editPart.getWidgetModel().removeAlias("channel");
        } else {
            _editPart.getWidgetModel().setAliasValue("channel", _oldValue);
        }

        // reset widget state
        _editPart.getWidgetModel().restoreState();

    }
}
