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

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.ArchiveGroup;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.EngineModelException;

import com.google.common.base.Strings;

/**
 * Simple http request/response for removing a channel from the archiver configuration.
 *
 * @author bknerr
 * @since 22.09.2011
 */
public class AddChannelResponse extends AbstractResponse {

    static final String URL_ADD_CHANNEL_PAGE = "addChannel";

    private static final long serialVersionUID = -5977457225438178049L;

    private static final String PARAM_CHANNEL_NAME = "name";
    private static final String PARAM_CHANNEL_GROUP = "group";
    private static final String PARAM_DATATYPE = "datatype";
    private static final String PARAM_CONTROLSYSTEM = "controlsystem";
    private static final String PARAM_DESCRIPTION = "desc";
    private static final String PARAM_LOPR = "lopr";
    private static final String PARAM_HOPR = "hopr";

    private static final String VALID = "OK";

    /**
     * Constructor.
     */
    protected AddChannelResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {

        final String name = req.getParameter(PARAM_CHANNEL_NAME);
        final String group = req.getParameter(PARAM_CHANNEL_GROUP);

        final String type = req.getParameter(PARAM_DATATYPE);
        final String controlsystem = req.getParameter(PARAM_CONTROLSYSTEM);
        final String desc = req.getParameter(PARAM_DESCRIPTION);
        final String lopr = req.getParameter(PARAM_LOPR);
        final String hopr = req.getParameter(PARAM_HOPR);

        final String msg = nameAndGroupInvalid(name, group, getModel());
        if (!VALID.equals(msg)) {
            createErrorPage(resp, msg);
            return;
        }

        try {
            getModel().configureNewChannel("MANUAL SETUP", name, group, type, controlsystem, desc, lopr, hopr);
            resp.sendRedirect(ChannelResponse.URL_CHANNEL_PAGE + "?" + ChannelResponse.PARAM_NAME + "="+name);
        } catch (final EngineModelException e) {
            createErrorPage(resp, "Channel could not be configured:\n" + e.getMessage());
        }
        return;
    }

    private void createErrorPage(@Nonnull final HttpServletResponse resp,
                                     @Nonnull final String msg) throws Exception {
        final HTMLWriter html = new HTMLWriter(resp, "Request error");
        html.text("Error on processing request:\n" + msg);
        HTMLWriter.makeLink("main", "Back to main");
        html.close();
    }

    @Nonnull
    private String nameAndGroupInvalid(@CheckForNull final String channelName,
                                       @CheckForNull final String groupName,
                                       @Nonnull final EngineModel engineModel) {
        if (Strings.isNullOrEmpty(channelName) || Strings.isNullOrEmpty(groupName)) {
            return "parameters '" + PARAM_CHANNEL_NAME + "' and/or '" +  PARAM_CHANNEL_GROUP + "' are null or empty!";
        }
        final ArchiveChannelBuffer<?, ?> channel = engineModel.getChannel(channelName);
        if (channel != null) {
            return "Channel with name: '" + channelName + "' does already exist.";
        }
        final ArchiveGroup group = engineModel.getGroup(groupName);
        if (group == null) {
            return "Group with name: '" + groupName + "' does not exist.";
        }
        // Channel does not yet exist, group does exist - great
        return VALID;
    }

}
