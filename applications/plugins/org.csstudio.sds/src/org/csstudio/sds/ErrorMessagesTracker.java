/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds;

import java.util.ArrayList;
import java.util.List;

/**
 * A tracker for error messages. Error messages are kept internally and can be
 * requested as one joined <code>String</code>.
 *
 * @author Alexander Will
 * @version $Revision: 1.3 $
 *
 */
public class ErrorMessagesTracker {
    /**
     * Error messages that have been tracked.
     */
    private List<String> _errorMessages;

    /**
     * Standard constructor.
     */
    public ErrorMessagesTracker() {
        resetErrorMessages();
    }

    /**
     * Reset the internal error messages log.
     */
    protected final void resetErrorMessages() {
        _errorMessages = new ArrayList<String>();
    }

    /**
     * Add an error message to the internal error messages log.
     *
     * @param errorMessage
     *            An error message.
     */
    protected final void trackErrorMessage(final String errorMessage) {
        _errorMessages.add(errorMessage);
    }

    /**
     * Return whether an error occurred during the last operation of this
     * service.
     *
     * @return True, if an error occurred during the last operation of this
     *         service.
     */
    public final boolean isErrorOccurred() {
        return (_errorMessages.size() > 0);
    }

    /**
     * Return the internal error messages protocol of the last operation of this
     * service.
     *
     * @return The internal error messages protocol of the last operation of
     *         this service.
     */
    public final List<String> getErrorMessages() {
        return new ArrayList<String>(_errorMessages);
    }
}
