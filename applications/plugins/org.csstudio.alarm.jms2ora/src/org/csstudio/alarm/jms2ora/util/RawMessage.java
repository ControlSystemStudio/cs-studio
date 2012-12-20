
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
 *
 */

package org.csstudio.alarm.jms2ora.util;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * TODO (mmoeller) :
 *
 * @author mmoeller
 * @version 1.0
 * @since 26.08.2011
 */
public class RawMessage {

    /** Hash table that contains the unprocessed value/key pairs from the JMS message */
    private final Hashtable<String, String> content;

    public RawMessage() {
        content = new Hashtable<String, String>();
    }

    public RawMessage(@Nonnull final MapMessage message) {

        content = new Hashtable<String, String>();

        String key;

        try {
            final Enumeration<?> keys = message.getMapNames();
            while (keys.hasMoreElements()) {
                key = (String) keys.nextElement();
                content.put(key, message.getString(key));
            }
        } catch (final JMSException jmse) {
            content.clear();
        }
    }

    public final boolean itemExists(@Nonnull final String key) {
        return content.containsKey(key);
    }

    @Nonnull
    public final Enumeration<String> getMapNames() {
        return content.keys();
    }

    /**
     * Return the value of the key.
     *
     * @param key
     * @return The value
     */
    @CheckForNull
    public final String getValue(@Nullable final String key) {

        if (key == null) {
            return null;
        }

        return content.get(key);
    }
}
