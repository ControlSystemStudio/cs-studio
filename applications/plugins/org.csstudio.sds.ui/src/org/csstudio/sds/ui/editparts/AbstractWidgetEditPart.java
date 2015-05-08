/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.ui.editparts;

import java.util.Arrays;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.ui.internal.commands.ChangeSettingsFromDroppedPvCommand;
import org.csstudio.sds.ui.internal.commands.DeleteWidgetsCommand;
import org.csstudio.sds.ui.internal.editor.dnd.DropPvRequest;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * This is the base class for all controllers of SDS widgets. In the GEF
 * model-view-controller architecture, subclasses of this class are the
 * controllers.
 *
 * @author Sven Wende & Stefan Hofer
 * @version $Revision: 1.28 $
 *
 */
public abstract class AbstractWidgetEditPart extends AbstractBaseEditPart implements
        NodeEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
            @Override
            protected Command createDeleteCommand(
                    final GroupRequest deleteRequest) {
                ContainerModel model = (ContainerModel) getHost().getParent()
                        .getModel();
                AbstractWidgetModel widgetModel = (AbstractWidgetModel) getHost()
                        .getModel();
                return new DeleteWidgetsCommand(getViewer(), model, Arrays.asList(widgetModel));
            }

            @Override
            public Command getCommand(final Request request) {
                if (DropPvRequest.REQ_DROP_PV.equals(request.getType())) {
                    return new ChangeSettingsFromDroppedPvCommand(
                            (DropPvRequest) request,
                            AbstractWidgetEditPart.this);
                }
                return super.getCommand(request);
            }

            @Override
            public EditPart getTargetEditPart(final Request request) {
                if (DropPvRequest.REQ_DROP_PV.equals(request.getType())) {
                    return getHost();
                }
                return super.getTargetEditPart(request);
            }
        });

        createEditPoliciesHook();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected synchronized void doRefreshVisuals(final IFigure figure) {
        super.doRefreshVisuals(figure);
        figure.setOpaque(true);
    }

    /**
     * Hook method for sub classes that want to define custom edit policies.
     */
    protected void createEditPoliciesHook() {
    }

}
