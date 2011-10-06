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

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 29.09.2011
 */
public class StartChannelResponse extends AbstractChannelResponse {

    private static String URL_START_CHANNEL_ACTION;
    private static String URL_START_CHANNEL_PAGE;
    static {
        URL_START_CHANNEL_ACTION = "start";
        URL_START_CHANNEL_PAGE = URL_CHANNEL_PAGE + "/" + URL_START_CHANNEL_ACTION;
    }

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public StartChannelResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final EpicsChannelName epicsName = parseEpicsNameOrConfigureRedirectResponse(req, resp);
        if (epicsName == null) {
            return;
        }

        try {
            final String name = epicsName.toString();
            final ArchiveChannelBuffer<?, ?> buffer = getModel().getChannel(name);
            if (buffer == null) {
                redirectToErrorPage(resp, "Channel '" + name + "' is unknown!");
                return;
            }
            if (buffer.isStarted()) {
                redirectToWarnPage(resp, "Channel '" + name + "' has already been started!");
                return;
            }

            buffer.start("MANUAL START");

            resp.sendRedirect(ShowChannelResponse.urlTo(name));

        } catch (final IllegalArgumentException e) {
            redirectToErrorPage(resp, "Channel could not be started:\n" + e.getMessage());
        } catch (final EngineModelException e) {
            redirectToErrorPage(resp, "Channel could not be started:\n" + e.getMessage());
        }
    }

    @Nonnull
    public static String baseUrl() {
        return URL_START_CHANNEL_PAGE;
    }
    @Nonnull
    public static String linkTo(@Nonnull final String name) {
        return new Url(baseUrl()).with(PARAM_NAME, name).link(Messages.HTTP_START);
    }
    @Nonnull
    public static String urlTo(@Nonnull final String name) {
        return new Url(baseUrl()).with(PARAM_NAME, name).url();
    }
}
