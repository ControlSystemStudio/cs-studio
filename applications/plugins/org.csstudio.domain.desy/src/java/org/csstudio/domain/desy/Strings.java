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

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;



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
     * Trims the source string of the given trim char.
     *
     * @param source
     * @param char
     * @return
     */
    @Nonnull
    public static String trim(@Nonnull final String source, final char trim) {
        final String trimQuoted = Pattern.quote(String.valueOf(trim));
        final String sourceWOLeadingChars = source.replaceAll("^" + trimQuoted + "+", "");
        final String sourceWOLeadingAndTrailingChars = sourceWOLeadingChars.replaceAll(trimQuoted + "+$", "");
        return sourceWOLeadingAndTrailingChars;
    }

    @Nonnull
    public static Collection<String> splitIgnoreWithinQuotes(@Nonnull final String source,
                                                             @Nonnull final char sep) {
        return splitIgnore(source, sep, '\"');
    }

    /**
     * Splits a string into substring on a separating character. Ignores those separators in
     * within the ignore char (typically a quote '"') and those separators following on each other.
     *
     * Unfortunately, the {@link com.google.common.Splitter} doesn't provide the
     * 'ignore in whatever feature'.
     *
     * @param source
     * @param sep
     * @param ignore
     * @return
     */
    @Nonnull
    public static Collection<String> splitIgnore(@Nonnull final String source,
                                                 @Nonnull final char sep,
                                                 @Nonnull final char ignore) {
        String trimmedSource = trim(source, sep);

        final String sepQuoted = Pattern.quote(String.valueOf(sep));

        trimmedSource = trimmedSource.replaceAll("[" + sepQuoted + "]+" + ignorePairLookAheadRegex(String.valueOf(ignore)),
                                                 String.valueOf(sep));
        if ("".equals(trimmedSource)) {
            return Collections.emptyList();
        }
        final String[] split = trimmedSource.split("[" + sepQuoted + "]" + ignorePairLookAheadRegex(String.valueOf(ignore)));
        return Lists.newArrayList(split);
    }

    @Nonnull
    private static String ignorePairLookAheadRegex(@Nonnull final String ignExpr) {
        final String quotedIgnExpr = Pattern.quote(ignExpr);
        return "(?=([^" + quotedIgnExpr + "]*" + quotedIgnExpr + "[^" + quotedIgnExpr + "]*" + quotedIgnExpr + ")*[^" + quotedIgnExpr + "]*$)";
    }


    @Nonnull
    public static Collection<String> splitIgnoreWithinQuotesTrimmed(@Nonnull final String source,
                                                                    @Nonnull final char sep,
                                                                    @Nonnull final char trim) {

        return Collections2.transform(splitIgnoreWithinQuotes(source, sep),
                                      new Function<String, String>() {
                                            @Override
                                            @Nonnull
                                            public String apply(@Nonnull final String input) {
                                                return Strings.trim(input, trim);
                                            }
                                        });
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
