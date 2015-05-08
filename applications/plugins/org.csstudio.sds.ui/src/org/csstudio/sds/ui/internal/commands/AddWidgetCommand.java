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

import java.util.Arrays;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.eclipse.gef.commands.Command;

/**
 * An undoable command that can be used to add a widget model to a display
 * model.
 *
 * @author swende
 *
 */
public final class AddWidgetCommand extends Command {
    /**
     * The display model.
     */
    private ContainerModel _container;

    /**
     * The display model that should be added.
     */
    private List<AbstractWidgetModel> _widgets;

    /**
     * Constructor.
     *
     * @param container
     *            the display model
     * @param widgets
     *            the widgets to add
     * @param select
     *            specifies if the added widgets should be selected
     */
    public AddWidgetCommand(final ContainerModel container, final List<AbstractWidgetModel> widgets) {
        assert container != null;
        assert widgets != null;
        _container = container;
        _widgets = widgets;
    }

    /**
     * Constructor.
     *
     * @param container
     *            the display model
     * @param widget
     *            the widget to add
     * @param select
     *            specifies if the added widget should be selected
     */
    public AddWidgetCommand(final ContainerModel container, final AbstractWidgetModel widget) {
        this(container, Arrays.asList(widget));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // adjust layer
        for (AbstractWidgetModel w : _widgets) {
            w.setLayer(_container.getLayerSupport().getActiveLayer().getId());
        }

        // add widgets
        _container.addWidgets(_widgets);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _container.removeWidgets(_widgets);
    }

}
