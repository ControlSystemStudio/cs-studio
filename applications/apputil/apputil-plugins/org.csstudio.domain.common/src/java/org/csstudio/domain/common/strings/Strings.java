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
package org.csstudio.domain.common.strings;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.google.common.base.Function;
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
     * Splits a source string on a comma, ignoring commas within quotes.
     *
     * Note, uneven numbers of quotes break the regex such that the first separator before the first
     * quote is not considered.
     *
     * @param source a string of comma separated entries
     * @return an iterable of strings, and an empty list if the string is blank
     */
    public static String[] splitOnCommaIgnoreInQuotes(final String source) {
        return source.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    public static Collection<String> splitIgnoreWithinQuotes(final String source,
                                                             final char sep) {
        return splitIgnore(source, sep, '\"');
    }

    /**
     * Splits a string into substring on a separating character. Ignores those separators in
     * within the ignore char (typically a quote '"') and those separators following on each other.
     * Empty strings are filtered (any output string !isEmpty).
     *
     * @param source
     * @param sep
     * @param ignore
     * @return
     */
    public static Collection<String> splitIgnore(final String source,
                                                 final char sep,
                                                 final char ign) {

        final List<String> result = Lists.newArrayList();
        final Matcher matcher = Pattern.compile(createRegEx(sep, ign)).matcher(source);
        while (matcher.find()) {
            final String cand = matcher.group();
            if (!cand.isEmpty()) {
                result.add(cand);
            }
        }
        return result;
    }

    /**
     * Splits a string into substring on a separating character. Ignores those separators in
     * within the ignore char (typically a quote '"') and those separators following on each other.
     * Result strings are trimmed, which may result in empty strings.
     *
     * @param source
     * @param sep
     * @param trim
     * @return
     */
    public static Collection<String> splitIgnoreWithinQuotesTrimmed(final String source,
                                                                    final char sep,
                                                                    final char trim) {

        return Collections2.transform(splitIgnoreWithinQuotes(source, sep),
                                      new Function<String, String>() {
                                            @Override
                                                                                    public String apply(final String input) {
                                                return Strings.trim(input, trim);
                                            }
                                        });
    }

    /**
     * Trims the source string of the given trim char.
     *
     * @param source
     * @param char
     * @return
     */
    public static String trim(final String source, final char trim) {
        final String trimQuoted = Pattern.quote(String.valueOf(trim));
        final String sourceWOLeadingChars = source.replaceAll("^" + trimQuoted + "+", "");
        final String sourceWOLeadingAndTrailingChars = sourceWOLeadingChars.replaceAll(trimQuoted + "+$", "");
        return sourceWOLeadingAndTrailingChars;
    }


    /**
     * Returns the size of the string measured in bytes.
     * Computes the number of unicode 'code points', which is equivalent to
     * {@link String#getBytes()}.length, but without having to copy the string to a byte array.
     *
     * @param s the string
     * @return the number of bytes of the unicode characters
     */
    public static int getSizeInBytes(final String s) {
        if (com.google.common.base.Strings.isNullOrEmpty(s)) {
            return 0;
        }
        return s.codePointCount(0, s.length());
    }

    /**
     * Creates the magic regex that finds the fields of the source string.
     * Note that their order matters!
     * sep=, ign=X : ([^X,]*X[^X]+X[^X,]*) | ([^,X]*X[^,X]*)(?=[^X]*) | ([^X,]+)
     */
    private static String createRegEx(final char sep,
                                      final char ign) {
        final String qSep = Pattern.quote(String.valueOf(sep));
        final String qIgn = Pattern.quote(String.valueOf(ign));
        return createRegExDoubleIgnore(qSep, qIgn) + "|" +
               createRegExSingleIgnoreWithLookAhead(qSep, qIgn) + "|" +
               createRegExWithoutSepsOrIgnore(qSep, qIgn);
    }
    /**
     * Regex matching anything between qSeps(,) with two qIgn(X): ,(abcXab,c,Xabc), OR ,(Xa,X), OR ,(abcXmX),
     */
    private static String createRegExDoubleIgnore(final String qSep,
                                                  final String qIgn) {
        return "([^" + qSep + qIgn + "]*" + qIgn + "[^" + qIgn + "]+" + qIgn + "[^" + qSep + qIgn + "]*)";
    }
    /**
     * Regex matching anything between qSeps(,) with ONE qIgn(X) when there isn't any other qIgn later on: ,(abcXab),abaa,aa
     */
    private static String createRegExSingleIgnoreWithLookAhead(final String qSep,
                                                               final String qIgn) {
        return "([^" + qSep + qIgn + "]*" + qIgn + "[^" + qSep + qIgn + "]*)(?=[^" + qIgn + "]*)";
    }
    /**
     * Regex matching anything between qSeps and qIgns: ,(abc), OR x(foo)x OR X(aa), OR ,(aa)X
     */
    private static String createRegExWithoutSepsOrIgnore(final String qSep,
                                                         final String qIgn) {
        return "([^" + qSep + qIgn + "]+)";
    }

    /**
     * Checks if a String is whitespace, empty ("") or null.
       <code>
         StringUtils.isBlank(null)      = true
         StringUtils.isBlank("")        = true
         StringUtils.isBlank(" ")       = true
         StringUtils.isBlank("bob")     = false
         StringUtils.isBlank("  bob  ") = false
       </code>
     * @param s
     * @return true if the string is null, empty, or does only contain whitespaces
     */
    public static boolean isBlank(final String s) {
        return s == null || s.trim().equals("");
    }

}
