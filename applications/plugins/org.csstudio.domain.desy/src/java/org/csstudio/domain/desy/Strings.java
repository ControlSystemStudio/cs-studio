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
package org.csstudio.domain.desy;

import java.util.Collections;

import javax.annotation.Nonnull;

import com.google.common.base.Splitter;



/**
 * String utilities (that are not present in the com.google.common collections).
 *
 * @author bknerr
 * @since 17.05.2011
 */
public final class Strings {
    /**
     * Don't instantiate.
     */
    private Strings() {
        // Empty
    }

    /**
     * Creates a list of string from the comma separated entries in the input string
     * Each list entry is trimmed of whitespaces, so <code>"", "  "</code> entries are not
     * added!
     *
     * @param commaSeparatedString a string of comma separated entries
     * @return an iterable of strings, and an empty list if the string is blank
     */
    @Nonnull
    public static Iterable<String> createListFrom(@Nonnull final String commaSeparatedString) {
        if (com.google.common.base.Strings.isNullOrEmpty(commaSeparatedString)) {
            return Collections.emptyList();
        }
        return Splitter.on(",").trimResults().omitEmptyStrings().split(commaSeparatedString);
    }

    /**
     * Returns the size of the string measured in bytes.
     * Computes the number of unicode 'code points', which is equivalent to
     * {@link String#getBytes()}.length, but without having to copy the string to a byte array.
     *
     * @param s the string
     * @return the number of bytes of the unicode characters
     */
    public static int getSizeInBytes(@Nonnull final String s) {
        if (com.google.common.base.Strings.isNullOrEmpty(s)) {
            return 0;
        }
        return s.codePointCount(0, s.length());
    }
}
