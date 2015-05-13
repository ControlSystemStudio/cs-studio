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
 * <p>
 * The result of executing a management command. A result consists of the result
 * type and the value returned by the command, if any.
 * </p>
 *
 * <p>
 * Note that the result type is not the Java class of the return value but a
 * logical type identifier which will be used to dispatch the return value to
 * an appropriate receiver.
 * </p>
 *
 * @author Joerg Rathlev
 */
public final class CommandResult implements Serializable {

    private static final long serialVersionUID = 5L;

    /**
     * Result type to indicate success without returning a value.
     */
    public static final String TYPE_VOID =
        "org.csstudio.platform.remotemanagement.resulttype.Void";

    /**
     * Result type for a message returned by a command that should be displayed
     * to the end user. This type can for example be used by commands that want
     * to return simple status information to the user in a generic way, without
     * the possibility of displaying the result in a type-specific viewer. The
     * Java type of the serialized object returned by the command must be
     * {@link String}.
     */
    public static final String TYPE_MESSAGE =
        "org.csstudio.platform.remotemanagement.resulttype.Message";

    /**
     * Result type which indicates that the command did not execute successfully
     * and no value was returned.
     */
    public static final String TYPE_ERROR =
        "org.csstudio.platform.remotemanagement.resulttype.Error";

    /**
     * Result type which indicates that the command did not execute successfully
     * and an exception was returned. The Java type of the serialized object
     * returned by the command must be {@link Throwable}.
     */
    public static final String TYPE_EXCEPTION =
        "org.csstudio.platform.remotemanagement.resulttype.Exception";

    /**
     * Result type which indicates that the command did not execute successfully
     * and an error message was returned. The Java type of the serialized object
     * returned by the command must be {@link String}. Receivers which handle
     * this result type should display the error message to the end user.
     */
    public static final String TYPE_ERROR_MESSAGE =
            "org.csstudio.platform.remotemanagement.resulttype.ErrorMessage";

    private final Object _value;
    private final String _type;

    /**
     * Creates a new command result.
     *
     * @param value
     *            the return value, or <code>null</code> if the command did not
     *            return any value.
     * @param type
     *            the type of this result.
     */
    private CommandResult(Object value, String type) {
        _value = value;
        _type = type;
    }

    /**
     * Creates a command result which indicates success and does not return any
     * value.
     *
     * @return the command result.
     */
    public static CommandResult createSuccessResult() {
        return new CommandResult(null, TYPE_VOID);
    }

    /**
     * Creates a command result which indicates success and returns a value.
     *
     * @param value
     *            the value that is returned by the command.
     * @param type
     *            the type of the result.
     * @return the command result.
     */
    public static CommandResult createSuccessResult(Serializable value,
            String type) {
        if (value == null || type == null) {
            throw new NullPointerException("value and type must not be null");
        }

        return new CommandResult(value, type);
    }

    /**
     * Creates a command result which transports a message for the end user who
     * executed the command.
     *
     * @param message
     *            the message.
     * @return the command result.
     */
    public static CommandResult createMessageResult(String message) {
        if (message == null) {
            throw new NullPointerException("message must not be null");
        }

        return new CommandResult(message, TYPE_MESSAGE);
    }

    /**
     * Creates a command result which indicates failure. The returned result
     * does not contain any information about the error. It is recommended to
     * use one of the methods {@link #createFailureResult(String)} or
     * {@link #createFailureResult(Throwable)} instead of this method so that
     * administration tools can display an informative error message to the
     * user.
     *
     * @return the command result.
     */
    public static CommandResult createFailureResult() {
        return new CommandResult(null, TYPE_ERROR);
    }

    /**
     * Creates a command result which indicates failure and contains an error
     * message.
     *
     * @param errorMessage
     *            the error message.
     * @return the command result.
     */
    public static CommandResult createFailureResult(String errorMessage) {
        if (errorMessage == null) {
            throw new NullPointerException("errorMessage must not be null");
        }

        return new CommandResult(errorMessage, TYPE_ERROR_MESSAGE);
    }

    /**
     * Creates a command result which indicates failure and contains the
     * exception which caused the failure.
     *
     * @param cause
     *            the exception which caused the command to fail.
     * @return the command result.
     */
    public static CommandResult createFailureResult(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause must not be null");
        }

        return new CommandResult(cause, TYPE_EXCEPTION);
    }

    /**
     * Returns the value returned by the command.
     *
     * @return the return value.
     */
    public Object getValue() {
        return _value;
    }

    /**
     * Returns the type of this result.
     *
     * @return the type of this result.
     */
    public String getType() {
        return _type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("CommandResult[");
        result.append("type=");
        result.append(_type);
        result.append("]");
        return result.toString();
    }
}
