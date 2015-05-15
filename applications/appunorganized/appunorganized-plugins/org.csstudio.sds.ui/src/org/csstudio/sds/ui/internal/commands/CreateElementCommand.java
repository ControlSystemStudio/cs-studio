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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.csstudio.sds.ui.internal.feedback.GraphicalFeedbackContributionsService;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.CreateRequest;

/**
 * A command, which creates a widget model and adds it to the display model.
 *
 * @author Sven Wende
 * @version $Revision: 1.13 $
 *
 */
public final class CreateElementCommand extends Command {

    /**
     * The model.
     */
    private ContainerModel _container;

    /**
     * The create request.
     */
    private CreateRequest _request;

    /**
     * Bounds, which define size and location of the new widget.
     */
    private Rectangle _bounds;

    /**
     * The internal {@link CompoundCommand}.
     */
    private Command _compoundCommand;

    private EditPartViewer _viewer;

    /**
     * Constructs the command.
     *
     * @param viewer
     *
     * @param container
     *            the display model to which the widgets should get added
     * @param request
     *            the create request
     * @param bounds
     *            bounds, which define size and location of the new widget
     */
    public CreateElementCommand(EditPartViewer viewer, final ContainerModel container, final CreateRequest request, final Rectangle bounds) {
        assert viewer != null;
        assert container != null;
        assert request != null;
        assert bounds != null;
        this.setLabel("Create widget");
        _viewer = viewer;
        _container = container;
        _request = request;
        _bounds = bounds;
    }

    private Command createCompoundCommands() {
        CompoundCommand comCmd = new CompoundCommand();

        Object model = _request.getNewObject();

        if (model != null && model instanceof AbstractWidgetModel) {
            AbstractWidgetModel widgetModel = (AbstractWidgetModel) model;
            IGraphicalFeedbackFactory feedbackFactory = GraphicalFeedbackContributionsService.getInstance().getGraphicalFeedbackFactory(widgetModel.getTypeID());
            if (feedbackFactory != null) {
                Command boundsCmd = feedbackFactory.createInitialBoundsCommand(widgetModel, _request, _bounds);
                comCmd.add(boundsCmd);
            }

            comCmd.add(new SetPropertyCommand(widgetModel, AbstractWidgetModel.PROP_LAYER, _container.getLayerSupport().getActiveLayer().getId()));
            comCmd.add(new AddWidgetCommand(_container, widgetModel));
            comCmd.add(new SetSelectionCommand(_viewer, widgetModel));
        }
        return comCmd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        _compoundCommand = this.createCompoundCommands();
        _compoundCommand.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _compoundCommand.undo();
    }
}
