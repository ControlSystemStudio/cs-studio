/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.httpserver;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.domain.common.collection.CollectionsUtil;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/** Helper for creating web pages with consistent look (header, footer, ...)
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract class AbstractResponse extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractResponse.class);

    /** Required by Serializable */
    private static final long serialVersionUID = 1L;

    /** Model from which to serve info */
    private final EngineModel _model;

    /** String to compare admin parameter against for pages secured for erroneous modification */
    private final String _adminParamKey;
    private final String _adminParamValue;

    /** Construct <code>HttpServlet</code>
     *  @param title Page title
     */
    protected AbstractResponse(@Nonnull final EngineModel model) {
        this._model = model;
        _adminParamValue = null;
        _adminParamKey = null;
    }
    /** Construct <code>HttpServlet</code>
     *  @param title Page title
     */
    protected AbstractResponse(@Nonnull final EngineModel model,
                               @Nonnull final String adminParamKey,
                               @Nonnull final String admingParamValue) {
        _model = model;
        _adminParamKey = adminParamKey;
        _adminParamValue = admingParamValue;
    }
    @Nonnull
    public String getAdminParamKey() {
        return _adminParamKey;
    }
    @Nonnull
    public String getAdminParamValue() {
        return _adminParamValue;
    }

    @Nonnull
    protected static String numOf(@Nonnull final String org) {
        return "#" + org;
    }

    @Nonnull
    protected EngineModel getModel() {
        return _model;
    }

    /** {@inheritDoc} */
    @Override
    protected void doGet(@Nonnull final HttpServletRequest req,
                         @Nonnull final HttpServletResponse resp)
                         throws ServletException, IOException {
        try {
            if (!Strings.isNullOrEmpty(_adminParamKey)) {
                final String parameter = req.getParameter(_adminParamKey);
                if (!_adminParamValue.equals(parameter)) {
                    redirectToErrorPage(resp, "This command or URL is secured by an admin key=value pair for " + _adminParamKey + "=?" +
                                              "\nPlease ensure to add the correct admin key=value pair.");
                    return;
                }
            }
            fillResponse(req, resp);
        } catch (final Exception ex) {
            ex.printStackTrace();
            if (resp.isCommitted()) {
                LOG.warn("HTTP Server exception", ex);
                return;
            }
            resp.sendError(400, "HTTP Server exception" + ex.getMessage());
        }
    }

    /** Derived class must implement this to provide page content.
     *  <p>
     *  Call <code>startHTML</code> once, then other print methods.
     *  @param req The request
     *  @param resp The response
     */
    protected abstract void fillResponse(@Nonnull final HttpServletRequest req,
                                         @Nonnull final HttpServletResponse resp)
       throws Exception;


    @Nonnull
    protected String getValueAsString(@CheckForNull final ISystemVariable<?> var) {
        if (var == null) {
            return "null";
        }
        if (Collection.class.isAssignableFrom(var.getData().getClass())) {
            return CollectionsUtil.toLimitLengthString((Collection<?>) var.getData(), 10);
        }
        return var.getData().toString();
    }

    @Nonnull
    protected String limitLength(@Nonnull final String valueAsString, final int maxValueDisplay) {
        return valueAsString.substring(0, Math.min(valueAsString.length(), maxValueDisplay));
    }


    protected void redirectToErrorPage(@Nonnull final HttpServletResponse resp,
                                       @Nonnull final String msg) throws Exception {
        final HTMLWriter html = new HTMLWriter(resp, "Request error");
        html.text("Error on processing request:\n" + msg);
        MainResponse.linkTo(Messages.HTTP_MAIN);
        html.close();
    }
    protected void redirectToWarnPage(@Nonnull final HttpServletResponse resp,
                                      @Nonnull final String msg) throws Exception {
        final HTMLWriter html = new HTMLWriter(resp, "Request warning");
        html.text("Warning on processing request:\n" + msg);
        MainResponse.linkTo(Messages.HTTP_MAIN);
        html.close();
    }
    protected void redirectToSuccessPage(@Nonnull final HttpServletResponse resp,
                                         @Nonnull final String msg) throws Exception {
        final HTMLWriter html = new HTMLWriter(resp, "Request success");
        html.text("Request successful:\n" + msg);
        MainResponse.linkTo(Messages.HTTP_MAIN);
        html.close();
    }

    /**
     * Url builder.
     *
     * @author bknerr
     * @since 05.10.2011
     */
    public static final class Url {
        private String _url;
        private boolean _hasParams;
        /**
         * Constructor.
         */
        public Url(@Nonnull final String url) {
            _url = url;
        }
        @Nonnull
        public Url with(@Nonnull final String key, @Nonnull final String value) {
            if (!_hasParams) {
                _url += "?" + key + "=" + value;
            } else {
                _url += "&" + key + "=" + value;
            }
            _hasParams = true;
            return this;
        }
        @Nonnull
        public String link(@CheckForNull final String text) {
            if (Strings.isNullOrEmpty(text)) {
                return "<a href=\"" + _url + "\">" + _url + "</a>";
            }
            return "<a href=\"" + _url + "\">" + text + "</a>";
        }
        @Nonnull
        public String url() {
            return _url;
        }
    }
}
