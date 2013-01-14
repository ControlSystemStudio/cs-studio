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
package org.csstudio.alarm.treeview.model;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Helper for the validation of strings which should represent URLs
 * 
 * @author jpenning
 * @since 16.02.2011
 */
public final class UrlValidator {
    
    private static final String NO_PROTOCOL = "no protocol";
    
    /**
     * Result of the URL check
     */
    public enum Result {
        URL_IS_OK, URL_HAS_NO_PROTOCOL, URL_INVALID
    }
    
    private UrlValidator() {
        // Do not instantiate
    }
    
    /**
     * Checks the given string. If a valid protocol is present, URL_IS_OK is returned.
     * If no protocol is present, URL_HAS_NO_PROTOCOL is returned.
     * If neither valid nor no protocol is present, URL_INVALID is returned.
     * 
     * @param urlAsString
     * @return result
     */
    public static Result checkUrl(@CheckForNull final String urlAsString) {
        Result result = Result.URL_INVALID;
        
        if (urlAsString != null) {
            if (urlAsString.isEmpty()) {
                result = Result.URL_IS_OK;
            } else {
                result = tryToFormUrl(urlAsString);
            }
        }
        
        return result;
    }

    private static Result tryToFormUrl(@Nonnull final String urlAsString) {
        Result result = Result.URL_INVALID;
        try {
            @SuppressWarnings("unused")
            final URL url = new URL(urlAsString);
            result = Result.URL_IS_OK;
        } catch (final MalformedURLException e) {
            if (e.getMessage().startsWith(NO_PROTOCOL)) {
                result = Result.URL_HAS_NO_PROTOCOL;
            }
        }
        return result;
    }
    
}
