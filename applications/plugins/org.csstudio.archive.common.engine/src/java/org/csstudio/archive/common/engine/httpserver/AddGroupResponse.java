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

import com.google.common.base.Strings;

/**
 * Adds a new group to the engine.
 *
 * @author bknerr
 * @since 04.10.2011
 */
public class AddGroupResponse extends AbstractGroupResponse {

    private static String URL_ADD_GROUP_ACTION;
    private static String URL_ADD_GROUP_PAGE;
    static {
        URL_ADD_GROUP_ACTION = "add";
        URL_ADD_GROUP_PAGE = URL_GROUP_PAGE + "/" + URL_ADD_GROUP_ACTION;
    }

    private static final long serialVersionUID = 5010342398243858729L;

    private static final String PARAM_DESC = "desc";

    /**
     * Constructor.
     */
    protected AddGroupResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("static-access")
    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final String name = req.getParameter(PARAM_NAME);
        if (Strings.isNullOrEmpty(name)) {
            redirectToErrorPage(resp, "Required parameter '" + PARAM_NAME + "' is either null or empty!");
            return;
        }
        final String desc = req.getParameter(PARAM_DESC);
        if (desc != null && "".equals(desc)) {
            redirectToErrorPage(resp, " parameter '" + PARAM_NAME + "' mustn't be empty!");
            return;
        }

        try {
            getModel().configureNewGroup(name, desc);
            resp.sendRedirect(ShowGroupResponse.getUrl() + "?" + ShowGroupResponse.PARAM_NAME + "=" + name);

        } catch (final EngineModelException e) {
            redirectToErrorPage(resp, "Group " + name + " could not be created:\n" + e.getMessage());
        }

    }

    @Nonnull
    public static String getUrl() {
        return URL_ADD_GROUP_PAGE;
    }
}
