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
 package org.csstudio.sds.model.commands;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.EventType;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * A Command to set a property value of a widget.
 * @author Kai Meyer
 *
 */
public class SetPropertyCommand extends Command {
    private CompoundCommand chain;
    private AbstractWidgetModel widget;
    private WidgetProperty property;
    private Object value;


    /**
     * Constructor.
     * @param widget
     *             The widget, which property value should be set
     * @param propertyName
     *             The name of the property
     * @param value
     *             The new value for the property
     */
    public SetPropertyCommand(final AbstractWidgetModel widget, final String propertyName, final Object value) {
        this.widget = widget;
        this.property = widget.getPropertyInternal(propertyName);
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        if(chain==null) {
            // .. create a command chain
            chain = new CompoundCommand();

            // .. change the property value
            chain.add(new ChangePropertyCommand(property, value));

            // .. add contributed commands
            SdsPlugin.getDefault().getWidgetPropertyPostProcessingService().applyForSingleProperty(widget, property, chain, EventType.ON_MANUAL_CHANGE);
        }

        chain.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        chain.undo();
    }
}
