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
import org.csstudio.archive.engine.server.html.DisconnectedResponse;
import org.csstudio.archive.engine.server.html.EnvironmentResponse;
import org.csstudio.archive.engine.server.html.GroupResponse;
import org.csstudio.archive.engine.server.html.GroupsResponse;
import org.csstudio.archive.engine.server.html.HTMLChannelResponse;
import org.csstudio.archive.engine.server.html.HTMLMainResponse;
import org.csstudio.archive.engine.server.html.ResetResponse;
import org.csstudio.archive.engine.server.html.RestartResponse;
import org.csstudio.archive.engine.server.html.StopResponse;
import org.csstudio.archive.engine.server.json.JSONChannelResponse;
import org.csstudio.archive.engine.server.json.JSONMainResponse;

public class ResponseFactory {
    /** Model from which to serve info */
    final protected EngineModel model;

    final private Map<PageAndFormat, AbstractResponse> responses;

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


    protected ResponseFactory(final EngineModel model)
    {
        this.model = model;

        responses = new HashMap<PageAndFormat, AbstractResponse>();
        responses.put(new PageAndFormat(Page.MAIN, Format.html), new HTMLMainResponse(model));
        responses.put(new PageAndFormat(Page.MAIN, Format.json), new JSONMainResponse(model));
        responses.put(new PageAndFormat(Page.CHANNEL, Format.html), new HTMLChannelResponse(model));
        responses.put(new PageAndFormat(Page.CHANNEL, Format.json), new JSONChannelResponse(model));
        responses.put(new PageAndFormat(Page.CHANNEL_LIST, Format.html), new ChannelListResponse(model));
        responses.put(new PageAndFormat(Page.DISCONNECTED, Format.html), new DisconnectedResponse(model));
        responses.put(new PageAndFormat(Page.ENVIRONMENT, Format.html), new EnvironmentResponse(model));
        responses.put(new PageAndFormat(Page.GROUP, Format.html), new GroupResponse(model));
        responses.put(new PageAndFormat(Page.GROUPS, Format.html), new GroupsResponse(model));
        responses.put(new PageAndFormat(Page.DEBUG, Format.html), new DebugResponse(model));
        responses.put(new PageAndFormat(Page.RESET, Format.html), new ResetResponse(model));
        responses.put(new PageAndFormat(Page.RESTART, Format.html), new RestartResponse(model));
        responses.put(new PageAndFormat(Page.STOP, Format.html), new StopResponse(model));

    }

    public AbstractResponse getResponse(Page p, Format f) {
        return responses.get(new PageAndFormat(p, f));
    }
}
