/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.remote.management;

import java.io.Serializable;

/**
 * Describes a remote management command.
 *
 * @author Joerg Rathlev
 */
public final class CommandDescription implements Serializable {

    private static final long serialVersionUID = 2L;

    private final String _id;
    private final String _label;
    private final CommandParameterDefinition[] _parameters;

    /**
     * Creates a new command description.
     *
     * @param id
     *            the ID of the command.
     * @param label
     *            the label that will be used for the command in the user
     *            interface.
     * @param parameters
     *            the parameters expected by the command. May be null if the
     *            command does not require any parameters.
     */
    public CommandDescription(String id, String label,
            CommandParameterDefinition[] parameters) {
        if (id == null || label == null) {
            throw new NullPointerException("id and label must not be null");
        }

        _id = id;
        _label = label;
        if (parameters == null) {
            _parameters = new CommandParameterDefinition[0];
        } else {
            _parameters = new CommandParameterDefinition[parameters.length];
            System.arraycopy(parameters, 0, _parameters, 0, parameters.length);
        }
    }

    /**
     * Returns the identifier of the command.
     *
     * @return the identifier of the command.
     */
    public String getIdentifier() {
        return _id;
    }

    /**
     * Returns the label which is used for the command in the user interface.
     *
     * @return the label for the command.
     */
    public String getLabel() {
        return _label;
    }

    /**
     * Returns the parameters required by this command.
     *
     * @return the parameters required by this command. If this command does not
     *         require any parameters, returns an empty array (not
     *         <code>null</code>).
     */
    public CommandParameterDefinition[] getParameters() {
        CommandParameterDefinition[] result =
            new CommandParameterDefinition[_parameters.length];
        System.arraycopy(_parameters, 0, result, 0, _parameters.length);
        return result;
    }

    /**
     * Compares this description for equality with another object. Two command
     * descriptions are considered equal by this method if their command
     * identifiers are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof CommandDescription) {
            return ((CommandDescription) o)._id.equals(_id);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _label;
    }
}
