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

import java.text.MessageFormat;

import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.ui.internal.properties.view.IPropertySource;
import org.eclipse.gef.commands.Command;

/**
 * Command, which sets the dynamics descriptor of a property.
 *
 * @author Sven Wende
 */
public final class SetDynamicsDescriptorCommand extends Command {

    /**
     * The new property value.
     */
    private DynamicsDescriptor _dynamicsDescriptor;

    /**
     * The id of the property.
     */
    private Object _propertyId;

    /**
     * The old property value.
     */
    private DynamicsDescriptor _undoValue;

    /**
     * The property source.
     */
    private IPropertySource _propertySource;

    /**
     * Constructor.
     *
     * @param propLabel
     *            a label for the property, that is beeing set
     * @param propId
     *            the id of the property
     * @param dynamicsDescriptor
     *            the new dynamics descriptor
     * @param propertySource
     *            the property source
     */
    public SetDynamicsDescriptorCommand(final String propLabel,
            final Object propId, final DynamicsDescriptor dynamicsDescriptor,
            final IPropertySource propertySource) {
        super(MessageFormat.format("Set {0} Property",
                new Object[] { propLabel }).trim());
        _propertyId = propId;
        _dynamicsDescriptor = dynamicsDescriptor;
        _propertySource = propertySource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        _undoValue = _propertySource.getDynamicsDescriptor(_propertyId);

        _propertySource.setDynamicsDescriptor(_propertyId, _dynamicsDescriptor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _propertySource.setDynamicsDescriptor(_propertyId, _undoValue);
    }

}
