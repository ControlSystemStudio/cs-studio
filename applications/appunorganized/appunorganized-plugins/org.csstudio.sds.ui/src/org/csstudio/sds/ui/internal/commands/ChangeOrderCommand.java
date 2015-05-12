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
import org.eclipse.gef.commands.Command;

/**
 * An undoable command that can be used to change the order of a widget in the model.
 *
 * @author Kai Meyer
 *
 */
public final class ChangeOrderCommand extends Command {
    /**
     * The new index for the widget model.
     */
    private int _newIndex;
    /**
     * The old index of the widget model.
     */
    private int _oldIndex;
    /**
     * The widget model, which index has to be changed.
     */
    private AbstractWidgetModel _widgetModel;
    /**
     * The parent display model of the widget model.
     */
    private ContainerModel _container;

    /**
     * Constructor.
     * @param containerModel
     *             The parent display model
     * @param widgetModel
     *             The widget model
     * @param index
     *             The new index
     */
    public ChangeOrderCommand(final ContainerModel containerModel, final AbstractWidgetModel widgetModel, final int index) {
        assert containerModel != null;
        assert widgetModel != null;
        _newIndex = index;
        _widgetModel = widgetModel;
        _container = containerModel;
        _oldIndex = _container.getIndexOf(_widgetModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        _container.changeOrder(_widgetModel, _newIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _container.changeOrder(_widgetModel, _oldIndex);
    }

}
