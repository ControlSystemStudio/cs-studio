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

import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.EngineModelException;

import com.google.common.base.Strings;

/**
 * Servlet that stops all channels in a group.
 *
 * @author bknerr
 * @since 04.10.2011
 */
public class StopGroupResponse extends AbstractGroupResponse {

    private static String URL_BASE_PAGE;
    private static String URL_STOP_GROUP_ACTION;
    static {
        URL_STOP_GROUP_ACTION = "stop";
        URL_BASE_PAGE = URL_GROUP_PAGE + "/" + URL_STOP_GROUP_ACTION;
    }
    private static final long serialVersionUID = 8061989658084863540L;


    /**
     * Constructor.
     */
    public StopGroupResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final String name = req.getParameter(PARAM_NAME);
        if (Strings.isNullOrEmpty(name)) {
            redirectToErrorPage(resp, "Required parameter '" + PARAM_NAME + "' is either null or empty!");
            return;
        }

        try {
            final ArchiveGroup group = getModel().getGroup(name);
            if (group == null) {
                redirectToErrorPage(resp, "Group '" + name + "' is unknown!");
                return;
            }
            if (!group.isStarted()) {
                redirectToWarnPage(resp, "Group '" + name + "' has already been stopped!");
                return;
            }

            group.stop("MANUAL GROUP STOP");

            resp.sendRedirect(ShowGroupResponse.urlTo(name));

        } catch (final EngineModelException e) {
            redirectToErrorPage(resp, "Group " + name + "could not be stopped:\n" + e.getMessage());
        }
    }

    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
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
