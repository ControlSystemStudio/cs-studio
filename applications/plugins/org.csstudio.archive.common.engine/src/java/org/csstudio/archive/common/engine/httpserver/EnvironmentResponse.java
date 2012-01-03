/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.EngineModel;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/** Provide web page with environment info.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class EnvironmentResponse extends AbstractResponse {

    private static final String URL_BASE_PAGE = "/environment";

    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;

    EnvironmentResponse(@Nonnull final EngineModel model) {
        super(model);
    }

    @Override
    protected void fillResponse(@Nonnull final HttpServletRequest req,
                                @Nonnull final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html = new HTMLWriter(resp, Messages.HTTP_MAIN_TITLE);

        html.openTable(1, new String[] {"Property", "Value"});

        createTableRows(html);

        html.closeTable();

        html.close();
    }

    private void createTableRows(@Nonnull final HTMLWriter html) {
        final Properties properties = System.getProperties();
        for (final Entry<Object, Object> entry : properties.entrySet()) {
            html.tableLine(new String[] {(String) entry.getKey(), splitIntoSanePieces((String) entry.getValue())});
        }
    }

    /**
     * Some property values are really long, and unless they contain
     * some spaces, the make the HTML table column explode.
     * This adds some spaces, so the table can wrap the text around.
     * @param value Original property
     * @return Property, maybe with some added spaces
     */
    @Nonnull
    private String splitIntoSanePieces(@Nonnull final String value) {

        final Iterable<String> splitted = Splitter.fixedLength(HTMLWriter.MAX_TABLE_ENTRY_WIDTH).split(value);
        return Joiner.on("...<br/>\n").join(splitted);
    }

    @Nonnull
    public static String baseUrl() {
        return URL_BASE_PAGE;
    }
}
