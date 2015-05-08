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

import org.eclipse.gef.commands.Command;

/**
 * Command, which sets the value of a property.
 *
 * @author Sven Wende
 *
 */
final class SetValueCommand extends Command {

    /**
     * The new property value.
     */
    private Object _propertyValue;

    /**
     * The id of the property.
     */
    private Object _propertyId;

    /**
     * The old property value.
     */
    private Object _undoValue;

    /**
     * A flag indicating, whether reset is necessary on undo.
     */
    private boolean _resetOnUndo;

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
     * @param propValue
     *            the new property value
     * @param propertySource
     *            the property source
     */
    public SetValueCommand(final String propLabel, final Object propId,
            final Object propValue, final IPropertySource propertySource) {
        _propertyId = propId;
        _propertyValue = propValue;
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
        /*
         * Fix for Bug# 54250 IPropertySource.isPropertySet(String) returns
         * false both when there is no default value, and when there is a
         * default value and the property is set to that value. To correctly
         * determine if a reset should be done during undo, we compare the
         * return value of isPropertySet(String) before and after
         * setPropertyValue(...) is invoked. If they are different (it must have
         * been false before and true after -- it cannot be the other way
         * around), then that means we need to reset.
         */
        boolean wasPropertySet = _propertySource.isPropertySet(_propertyId);
        _undoValue = _propertySource.getPropertyValue(_propertyId);
        if (_undoValue instanceof IPropertySource) {
            _undoValue = ((IPropertySource) _undoValue).getEditableValue();
        }
        if (_propertyValue instanceof IPropertySource) {
            _propertyValue = ((IPropertySource) _propertyValue)
                    .getEditableValue();
        }

        _propertySource.setPropertyValue(_propertyId, _propertyValue);

        if (_propertySource instanceof IPropertySource2) {
            _resetOnUndo = !wasPropertySet
                    && ((IPropertySource2) _propertySource)
                            .isPropertyResettable(_propertyId);
        } else {
            _resetOnUndo = !wasPropertySet
                    && _propertySource.isPropertySet(_propertyId);
        }

        if (_resetOnUndo) {
            _undoValue = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redo() {
        execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        if (_resetOnUndo) {
            _propertySource.resetPropertyValue(_propertyId);
        } else {
            _propertySource.setPropertyValue(_propertyId, _undoValue);
        }
    }

}
