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

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 21.10.2011
 */
public class HelpResponse extends AbstractResponse {

    private static final long serialVersionUID = -154850528871142413L;
    private static final String URL_BASE_PAGE = "/help";
    private static final String URL_BASE_DESC = Messages.HTTP_HELP;
    private final String _adminParamKey;

    /**
     * Constructor.
     * @param adminParamKey
     */
    public HelpResponse(@Nonnull final EngineModel model,
                        @Nonnull final String adminParamKey) {
        super(model);
        _adminParamKey = adminParamKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception {
        final HTMLWriter html =
            new HTMLWriter(resp, "Archive Engine Help");

        createCommandsTable(html);

        html.close();

    }

    private void createCommandsTable(@Nonnull final HTMLWriter html) {
        html.openTable(4, new String[] {Messages.HTTP_URL_COMMANDS});

        html.tableLine(new String[] {
                Messages.HTTP_URL,
                Messages.HTTP_PARAMETERS,
                Messages.HTTP_REQUIRED,
                Messages.HTTP_DESCRIPTION,
        });
        html.tableLine(new String[] {
                HTMLWriter.makeRedText("*"),
                HTMLWriter.makeRedText(_adminParamKey),
                HTMLWriter.makeRedText(Messages.HTTP_YES),
                "Required key parameter for admin access to following " + HTMLWriter.makeRedText("red") + " urls.",
        });

        insertAddChannelCommandTableLines(html);
        insertChannelListCommandTableLines(html);
        insertPermanentDisableCommandTableLines(html);
        insertRemoveChannelCommandTableLines(html);
        insertShowChannelCommandTableLines(html);
        insertStartChannelCommandTableLines(html);
        insertStopChannelCommandTableLines(html);
        insertDisconnectedChannelsCommandTableLines(html);

        insertAddGroupCommandTableLines(html);
        insertShowGroupCommandTableLines(html);
        insertStartGroupCommandTableLines(html);
        insertStopGroupCommandTableLines(html);

        insertEnvironmentCommandTableLines(html);
        insertGroupsCommandTableLines(html);
        insertMainCommandTableLines(html);
        insertResetCommandTableLines(html);
        insertRestartCommandTableLines(html);
        insertShutdownCommandTableLines(html);

        html.closeTable();
    }

    private void insertShutdownCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                HTMLWriter.makeRedText(ShutdownResponse.baseUrl()),
                "",
                "",
                "Gracefully shuts down the engine, writes all queued samples and status information of channels and the engine.",
        });
    }

    private void insertRestartCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                HTMLWriter.makeRedText(RestartResponse.baseUrl()),
                "",
                "",
                "Clears all channels, closes PVs, retrieves new configuration from DB and restarts the engine.",
        });
    }

    private void insertResetCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                ResetResponse.baseUrl(),
                "",
                "",
                "Resets the archiver's statistics.",
        });
    }

    private void insertMainCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                MainResponse.baseUrl(),
                "",
                "",
                "Start page with archiver's statistics and overview.",
        });
    }

    private void insertGroupsCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                GroupsResponse.baseUrl(),
                "",
                "",
                "Lists the archiver's groups and group statistics.",
        });
    }

    private void insertEnvironmentCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                EnvironmentResponse.baseUrl(),
                "",
                "",
                "Lists the archiver's runtime environment as key value pairs.",
        });
    }

    @SuppressWarnings("static-access")
    private void insertStopGroupCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                StopGroupResponse.baseUrl(),
                StopGroupResponse.PARAM_NAME,
                Messages.HTTP_YES,
                "Stops all channels in a group with the given name that have been started.",
        });
    }

    @SuppressWarnings("static-access")
    private void insertStartGroupCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                StartGroupResponse.baseUrl(),
                StartGroupResponse.PARAM_NAME,
                Messages.HTTP_YES,
                "Starts all channels in a group with the given name that are stopped or disabled.",
        });
    }

    @SuppressWarnings("static-access")
    private void insertShowGroupCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                ShowGroupResponse.baseUrl(),
                ShowGroupResponse.PARAM_NAME,
                Messages.HTTP_YES,
                "Shows the group with the given name.",
        });
    }

    private void insertDisconnectedChannelsCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                DisconnectedResponse.baseUrl(),
                "",
                "",
                "Lists all disconnected channels from all groups.",
        });
    }

    @SuppressWarnings("static-access")
    private void insertAddGroupCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                AddGroupResponse.baseUrl(),
                AddGroupResponse.PARAM_NAME,
                Messages.HTTP_YES,
                "Adds the group with the given name, if not yet existing.",
        });
        html.tableLine(new String[] {
                "",
                AddGroupResponse.PARAM_DESC,
                Messages.HTTP_NO,
                "Description of the group. If provided description has to be non-empty.",
        });
    }

    @SuppressWarnings("static-access")
    private void insertStopChannelCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                HTMLWriter.makeRedText(StopChannelResponse.baseUrl()),
                StopChannelResponse.PARAM_NAME,
                Messages.HTTP_YES,
                "Stops the channel with the given name. On archiver restart the channel is started again (see permanent disabling).",
        });
    }

    @SuppressWarnings("static-access")
    private void insertStartChannelCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                StartChannelResponse.baseUrl(),
                StartChannelResponse.PARAM_NAME,
                Messages.HTTP_YES,
                "Start the the channel with the given name. Entails implicit enabling of the channel.",
        });
    }

    @SuppressWarnings("static-access")
    private void insertShowChannelCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                ShowChannelResponse.baseUrl(),
                ShowChannelResponse.PARAM_NAME,
                Messages.HTTP_YES,
                "Shows the the channel with the given name.",
        });
    }

    @SuppressWarnings("static-access")
    private void insertRemoveChannelCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                HTMLWriter.makeRedText(RemoveChannelResponse.baseUrl()),
                RemoveChannelResponse.PARAM_NAME,
                Messages.HTTP_YES,
                "Removes the the channel with the given name. Only works for those channels that do not already have archived samples.",
        });
    }

    @SuppressWarnings("static-access")
    private void insertPermanentDisableCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                HTMLWriter.makeRedText(PermanentDisableChannelResponse.baseUrl()),
                PermanentDisableChannelResponse.PARAM_NAME,
                Messages.HTTP_YES,
                "Permanently disables the channel with the given name (stopped immediately and not started again on archiver restart)",
        });
    }

    @SuppressWarnings("static-access")
    private void insertAddChannelCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                AddChannelResponse.baseUrl(),
                AddChannelResponse.PARAM_NAME,
                Messages.HTTP_YES,
                "Adds the channel with the given name and parameters.",
        });
        html.tableLine(new String[] {
                "",
                AddChannelResponse.PARAM_CHANNEL_GROUP,
                Messages.HTTP_YES,
                "Name of the group to which this channel will belong (has to exist)",
        });
        html.tableLine(new String[] {
                "",
                AddChannelResponse.PARAM_DATATYPE,
                Messages.HTTP_NO,
                // TODO (2012-10-26 jp adapted to new pvmanager)
                // "Java datatype of the channel, as such or as ArrayList<?> out of:\n" + DesyTypeFactoryProvider.getInstalledTargetTypes(),
                "Java datatype of the channel, as such or as ArrayList<?> out of: NOT IMPLEMENTED\n",
        });
        html.tableLine(new String[] {
                "",
                AddChannelResponse.PARAM_LOPR,
                Messages.HTTP_NO,
                "Epics LOPR value, i.e. low operation(display) range, has to be appropriate for the given datatype.",
        });
        html.tableLine(new String[] {
                "",
                AddChannelResponse.PARAM_HOPR,
                Messages.HTTP_NO,
                "Epics HOPR value, i.e. high operation/display range, has to be appropriate for the given datatype.",
        });
    }

    private void insertChannelListCommandTableLines(@Nonnull final HTMLWriter html) {
        html.tableLine(new String[] {
                ChannelListResponse.baseUrl(),
                ChannelListResponse.PARAM_PATTERN,
                Messages.HTTP_NO,
                "Lists all channels according to the given regular expression pattern (default '.*', i.e. match all).",
        });
    }

    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
    }

    @Nonnull
    public static String linkTo() {
        return new Url(baseUrl()).link(URL_BASE_DESC);
    }
}
