/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.alarm.service.declaration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for parsing timestamps in alarm messages.
 *
 * @author Joerg Rathlev
 */
public final class EventtimeUtil {

    private static final Logger LOG = LoggerFactory.getLogger(EventtimeUtil.class);

    private static final SimpleDateFormat FORMAT =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Constructor.
     */
    private EventtimeUtil() {
        // Empty
    }

    /**
     * Parses am eventtime timestamp into a Date object. If the string cannot be
     * parsed as a timestamp, returns <code>null</code>.
     *
     * @param timestamp
     *            the timestamp.
     * @return the parsed date, or <code>null</code> if the string could not be
     *         parsed.
     */
    @CheckForNull
    public static Date parseTimestamp(@Nullable final String timestamp) {
        Date result = null;
        try {
            synchronized (FORMAT) {
                result = FORMAT.parse(timestamp);
            }
        } catch (final ParseException e) {
            // Already handled
            LOG.error("ParseException: " + timestamp, e);
        } catch (final NumberFormatException e) {
            // Already handled - ignore
            LOG.error("NumberFormatException: " + timestamp, e);
        } catch (final ArrayIndexOutOfBoundsException e) {
            LOG.error("ArrayIndexOutOfBoundsException: " + timestamp, e);
        }
        return result;
    }

}
