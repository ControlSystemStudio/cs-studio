/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.html.ChannelListResponse;
import org.csstudio.archive.engine.server.html.DebugResponse;
import org.csstudio.archive.engine.server.html.EnvironmentResponse;
import org.csstudio.archive.engine.server.html.HTMLChannelResponse;
import org.csstudio.archive.engine.server.html.HTMLDisconnectedResponse;
import org.csstudio.archive.engine.server.html.HTMLGroupResponse;
import org.csstudio.archive.engine.server.html.HTMLGroupsResponse;
import org.csstudio.archive.engine.server.html.HTMLMainResponse;
import org.csstudio.archive.engine.server.html.ResetResponse;
import org.csstudio.archive.engine.server.html.RestartResponse;
import org.csstudio.archive.engine.server.html.StopResponse;
import org.csstudio.archive.engine.server.json.JSONChannelResponse;
import org.csstudio.archive.engine.server.json.JSONDisconnectedResponse;
import org.csstudio.archive.engine.server.json.JSONGroupResponse;
import org.csstudio.archive.engine.server.json.JSONGroupsResponse;
import org.csstudio.archive.engine.server.json.JSONMainResponse;

/**
 * Factory for creating server responses based on the page and format requested.
 * @author Dominic Oram
 *
 */
public class ResponseFactory {
    final private Map<PageAndFormat, AbstractResponse> responses;

    /**
     * A helper class to easily create the hashmap based on a pair of values.
     * @author Dominic Oram
     *
     */
    class PageAndFormat {
        private final Page page;
        private final Format format;

        public PageAndFormat(Page p, Format f) {
            page = p;
            format = f;
        }

        @Override
        public int hashCode() {
            return (page.hashCode() << 16) + format.hashCode();
        }

        @Override
        public boolean equals (final Object O) {
            if (!(O instanceof PageAndFormat)) return false;
            if (((PageAndFormat) O).page != page) return false;
            if (((PageAndFormat) O).format != format) return false;
            return true;
          }
    }

    /**
     * Create the factory and a hash map with all possible responses.
     * @param model The model to base the responses on.
     */
    protected ResponseFactory(final EngineModel model)
    {
        responses = new HashMap<PageAndFormat, AbstractResponse>();
        responses.put(new PageAndFormat(Page.MAIN, Format.html), new HTMLMainResponse(model));
        responses.put(new PageAndFormat(Page.MAIN, Format.json), new JSONMainResponse(model));
        responses.put(new PageAndFormat(Page.CHANNEL, Format.html), new HTMLChannelResponse(model));
        responses.put(new PageAndFormat(Page.CHANNEL, Format.json), new JSONChannelResponse(model));
        responses.put(new PageAndFormat(Page.CHANNEL_LIST, Format.html), new ChannelListResponse(model));
        responses.put(new PageAndFormat(Page.DISCONNECTED, Format.html), new HTMLDisconnectedResponse(model));
        responses.put(new PageAndFormat(Page.DISCONNECTED, Format.json), new JSONDisconnectedResponse(model));
        responses.put(new PageAndFormat(Page.ENVIRONMENT, Format.html), new EnvironmentResponse(model));
        responses.put(new PageAndFormat(Page.GROUP, Format.html), new HTMLGroupResponse(model));
        responses.put(new PageAndFormat(Page.GROUP, Format.json), new JSONGroupResponse(model));
        responses.put(new PageAndFormat(Page.GROUPS, Format.html), new HTMLGroupsResponse(model));
        responses.put(new PageAndFormat(Page.GROUPS, Format.json), new JSONGroupsResponse(model));
        responses.put(new PageAndFormat(Page.DEBUG, Format.html), new DebugResponse(model));
        responses.put(new PageAndFormat(Page.RESET, Format.html), new ResetResponse(model));
        responses.put(new PageAndFormat(Page.RESTART, Format.html), new RestartResponse(model));
        responses.put(new PageAndFormat(Page.STOP, Format.html), new StopResponse(model));

    }

    /**
     * Get a response based on the page and the format requested.
     * @param page The page that has been requested.
     * @param format The format that the page should be in.
     * @return The response to send to the user.
     */
    public AbstractResponse getResponse(Page page, Format format) {
        return responses.get(new PageAndFormat(page, format));
    }
}
