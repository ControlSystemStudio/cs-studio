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

/**
 * <p>
 * Service through which management commands can be executed.
 * </p>
 *
 * <p>
 * All parameters and return values used by this service are serializable, so
 * this interface can be used for a remote service.
 * </p>
 *
 * @author Joerg Rathlev
 */
public interface IManagementCommandService {

    /**
     * Returns the commands supported by this service.
     *
     * @return the commands supported by this service.
     */
    public CommandDescription[] getSupportedCommands();

    /**
     * Executes the specified command.
     *
     * @param commandId
     *            the identifier of the command to execute.
     * @param parameters
     *            the parameters for the command. May be <code>null</code> to
     *            call the command without parameters.
     * @return the result of the command execution. If the specified command is
     *         not supported by this service, a command result indicating an
     *         error is returned. If the execution of the command fails with an
     *         exception, a command result of type
     *         {@link CommandResult#TYPE_EXCEPTION} is returned with the
     *         exception as its payload.
     */
    public CommandResult execute(String commandId, CommandParameters parameters);

    /**
     * Returns the dynamic enumeration values for the specified command and
     * parameter.
     *
     * @param commandId
     *            the identifier of the command.
     * @param parameterId
     *            the identifier of the parameter.
     * @return an array of enumeration values. If the specified command does not
     *         exist, or if the command does not have the specified parameter,
     *         or if the parameter is not a dynamic enumeration parameter,
     *         returns an empty array.
     */
    public CommandParameterEnumValue[] getDynamicEnumerationValues(
            String commandId, String parameterId);
}
