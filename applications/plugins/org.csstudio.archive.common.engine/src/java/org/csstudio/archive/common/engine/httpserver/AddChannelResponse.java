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

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;

import com.google.common.base.Strings;

/**
 * Simple http request/response for removing a channel from the archiver configuration.
 *
 * @author bknerr
 * @since 22.09.2011
 */
public class AddChannelResponse extends AbstractChannelResponse {

    static final String PARAM_CHANNEL_GROUP = "group";
    static final String PARAM_DATATYPE = "datatype";
//    private static final String PARAM_CONTROLSYSTEM = "controlsystem";
//    private static final String PARAM_DESCRIPTION = "desc";
    static final String PARAM_LOPR = "lopr";
    static final String PARAM_HOPR = "hopr";

    private static String URL_ADD_CHANNEL_ACTION;
    private static String URL_ADD_CHANNEL_PAGE;
    static {
        URL_ADD_CHANNEL_ACTION = "add";
        URL_ADD_CHANNEL_PAGE = URL_CHANNEL_PAGE + "/" + URL_ADD_CHANNEL_ACTION;
    }

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    AddChannelResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {

        final EpicsChannelName name = parseEpicsNameOrConfigureRedirectResponse(req, resp);
        if (name == null) {
            return;
        }

        final String group = req.getParameter(PARAM_CHANNEL_GROUP);
        final String type = req.getParameter(PARAM_DATATYPE);
        if (Strings.isNullOrEmpty(group) || Strings.isNullOrEmpty(type)) {
            redirectToErrorPage(resp, "At least one out of the required parameters '" +
                                      PARAM_CHANNEL_GROUP + "' & '" +
                                      PARAM_DATATYPE + "' is either null or empty!");
            return;
        }

//        final String controlsystem = req.getParameter(PARAM_CONTROLSYSTEM);
//        final String desc = req.getParameter(PARAM_DESCRIPTION);
        final String lopr = req.getParameter(PARAM_LOPR);
        final String hopr = req.getParameter(PARAM_HOPR);

        try {
            getModel().configureNewChannel(name, group, type, lopr, hopr);
            resp.sendRedirect(ShowChannelResponse.urlTo(name.toString()));
        } catch (final EngineModelException e) {
            redirectToErrorPage(resp, "Channel could not be configured:\n" + e.getMessage());
        }
    }

    @Nonnull
    public static String baseUrl() {
        return URL_ADD_CHANNEL_PAGE;
    }
    @Nonnull
    public static String linkTo(@Nonnull final String name) {
        return new Url(baseUrl()).with(PARAM_NAME, name).link(name);
    }
    @Nonnull
    public static String urlTo(@Nonnull final String name) {
        return new Url(baseUrl()).with(PARAM_NAME, name).url();
    }
}
