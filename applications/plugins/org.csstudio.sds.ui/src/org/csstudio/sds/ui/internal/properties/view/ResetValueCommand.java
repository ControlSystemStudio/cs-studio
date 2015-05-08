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
 * A Command used to restore the default value of a property.
 *
 * @author Sven Wende
 */
final class ResetValueCommand extends Command {

    /**
     * The property that has to be reset.
     */
    private Object _propertyName;

    /**
     * The current non-default value of the property.
     */
    private Object _undoValue;

    /**
     * The property source whose property has to be reset.
     */
    private IPropertySource _target;

    /**
     * Default Constructor: Sets the label for the Command.
     *
     * @since 3.1
     */
    public ResetValueCommand() {
        super("Restore Default Value");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExecute() {
        boolean answer = false;
        if (_target != null && _propertyName != null) {
            answer = _target.isPropertySet(_propertyName);
            if (_target instanceof IPropertySource2) {
                answer = answer
                        && (((IPropertySource2) _target)
                                .isPropertyResettable(_propertyName));
            }
        }
        return answer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        _undoValue = _target.getPropertyValue(_propertyName);
        if (_undoValue instanceof IPropertySource) {
            _undoValue = ((IPropertySource) _undoValue).getEditableValue();
        }
        redo();
    }

    /**
     * Sets the IPropertySource.
     *
     * @param propSource
     *            the IPropertySource whose property has to be reset
     */
    public void setTarget(final IPropertySource propSource) {
        _target = propSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redo() {
        _target.resetPropertyValue(_propertyName);
    }

    /**
     * Sets the property that is to be reset.
     *
     * @param pName
     *            the property to be reset
     */
    public void setPropertyId(final Object pName) {
        _propertyName = pName;
    }

    /**
     * Restores the non-default value that was reset.
     *
     * @see org.eclipse.gef.commands.Command#undo()
     */
    @Override
    public void undo() {
        _target.setPropertyValue(_propertyName, _undoValue);
    }

}
