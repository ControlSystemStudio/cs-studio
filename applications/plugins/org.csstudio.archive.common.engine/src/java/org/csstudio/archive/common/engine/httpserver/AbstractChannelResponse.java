/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.engine.httpserver;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;

import com.google.common.base.Strings;



/**
 * Super type for channel related servlets.
 *
 * @author bknerr
 * @since 29.09.2011
 */
abstract class AbstractChannelResponse extends AbstractResponse {

    protected static final String URL_CHANNEL_PAGE = "/channel";
    static final String PARAM_NAME = "name";

    private static final long serialVersionUID = -8281575454479593469L;

    /**
     * Constructor.
     */
    protected AbstractChannelResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    /**
     * Checks for the validity of the name parameter and creates an epics compatible name object.
     * Or if invalid, configures the response to contain the error message.
     * @param req the request
     * @param resp the response
     * @return the valid epics name or <code>null</code> if an error response has been configured.
     * @throws Exception
     */
    @CheckForNull
    protected EpicsChannelName parseEpicsNameOrConfigureRedirectResponse(@Nonnull final HttpServletRequest req,
                                                                         @Nonnull final HttpServletResponse resp) throws Exception {
        final String name = req.getParameter(PARAM_NAME);
        if (Strings.isNullOrEmpty(name)) {
            redirectToErrorPage(resp, "Required parameter '" + PARAM_NAME + "' is either null or empty!");
            return null;
        }
        try {
            // Note, that once far in the bright future when we support several control system
            // types, we'd need channel name support/service for any of them and validated 'name' classes with a
            // a common supertype (the abstract channel identifier/name) which are created either
            // directly here or via engine model.
            return new EpicsChannelName(name);

        } catch (final IllegalArgumentException e) {
            redirectToErrorPage(resp, "Channel name is not EPICS compatible:\n" + e.getMessage());
        }
        return null;
    }
}
