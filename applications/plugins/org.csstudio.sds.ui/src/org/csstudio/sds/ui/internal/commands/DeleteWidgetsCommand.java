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
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;

/**
 * A command, that deletes an widget model from the display model.
 *
 * @author Sven Wende & Stefan Hofer
 *
 */
public final class DeleteWidgetsCommand extends Command {
    /**
     * The display model.
     */
    private ContainerModel _container;

    /**
     * The element that gets deleted.
     */
    private List<AbstractWidgetModel> _deletedWidgets;

    /**
     * The original indices of the deleted widgets within the container.
     */
    private List<Integer> _indices;

    /**
     * The graphical viewer.
     */
    private EditPartViewer _viewer;

    /**
     * Constructor.
     *
     * @param container
     *            the display model
     * @param widgets
     *            the widgets, that should be deleted
     */
    public DeleteWidgetsCommand(final EditPartViewer viewer, final ContainerModel container, final List<AbstractWidgetModel> widgets) {
        assert container != null;
        assert widgets != null;
        _viewer = viewer;
        _container = container;
        _deletedWidgets = widgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // very important for performance (deselecting all widgets before a
        // delete operation reduces unnecessary refresh calls on the property
        // view)
        if (_viewer != null) {
            _viewer.deselectAll();
        }

        _indices = new ArrayList<Integer>(_deletedWidgets.size());
        for (AbstractWidgetModel w : _deletedWidgets) {
            _indices.add(_container.getIndexOf(w));
        }

        _container.removeWidgets(_deletedWidgets);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _container.addWidgets(_indices, _deletedWidgets);
        // _container.selectWidgets(_deletedWidgets);
    }

}
