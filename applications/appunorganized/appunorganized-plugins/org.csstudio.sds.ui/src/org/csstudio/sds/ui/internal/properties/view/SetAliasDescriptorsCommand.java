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
 package org.csstudio.sds.ui.internal.properties.view;

import java.text.MessageFormat;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.gef.commands.Command;

/**
 * Command, which sets the alias descriptors of a widget model.
 *
 * @author Alexander Will
 *
 */
final class SetAliasDescriptorsCommand extends Command {

    /**
     * The new property value.
     */
    private Map<String, String> _aliases;

    /**
     * The old property value.
     */
    private Map<String, String> _undoValue;

    /**
     * The property source.
     */
    private IPropertySource _propertySource;

    /**
     * Constructor.
     *
     * @param propLabel
     *            a label for the property, that is beeing set
     * @param aliases
     *            the new aliases
     * @param propertySource
     *            the property source
     */
    public SetAliasDescriptorsCommand(final String propLabel,
            final Map<String, String> aliases,
            final IPropertySource propertySource) {
        super(MessageFormat.format("Set {0} Property",
                new Object[] { propLabel }).trim());
        _aliases = aliases;
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
    @SuppressWarnings("unchecked")
    @Override
    public void execute() {
        _undoValue = (Map<String, String>) _propertySource.getPropertyValue(AbstractWidgetModel.PROP_ALIASES);
        _propertySource.setPropertyValue(AbstractWidgetModel.PROP_ALIASES, _aliases);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _propertySource.setPropertyValue(AbstractWidgetModel.PROP_ALIASES, _undoValue);
    }

}
