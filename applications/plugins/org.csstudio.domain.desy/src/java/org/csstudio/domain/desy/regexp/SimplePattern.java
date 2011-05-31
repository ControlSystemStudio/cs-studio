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
package org.csstudio.domain.desy.regexp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since Mar 17, 2011
 */
public final class SimplePattern {
    /**
     * Constructor.
     */
    private SimplePattern() {
        // Don't instantiate
    }

    private static final List<String> ESCAPE_SYMBOLS =
        Lists.newArrayList("^",
                           "$",
                           "(",
                           ")",
                           "[",
                           "]",
                           ".",
                           "+");
    private static final String BACKSLASH_PATTERN = ".*(\\\\).*";

    /**
     * Transforms the given 'global' pattern recognizing only '*' and '?' as special wildcard
     * characters. All others are treated literally. <br/>
     * The returned String is a regular expression.
     * Note that '\' are not permitted at all!
     *
     * '*' translates into '.*' = match any number of arbitrary symbol
     * '?' translates into '.?' = match exactly one arbitrary symbol
     *
     * '^' translates into '\^'
     * '$' translates into '\$'
     * '(' translates into '\('
     *
     * Any other backslashed
     *
     * @param globPattern the simple pattern with '*' and '?' as wildcards
     * @return a regular expression pattern
     */
    @Nonnull
    public static String toRegExp(@Nonnull final String globPattern) {


        final Pattern backSlashPattern = Pattern.compile(BACKSLASH_PATTERN);
        final Matcher m = backSlashPattern.matcher(globPattern);
        if (m.matches()) {
            throw new PatternSyntaxException("No backslashes permitted for simple pattern.", BACKSLASH_PATTERN, m.regionStart());
        }
        String regExp = globPattern;
        for (final String symbol : ESCAPE_SYMBOLS) {
            regExp = regExp.replace(symbol, "\\" + symbol);
        }

        // wildcards
        regExp = regExp.replace("*", ".*");
        regExp = regExp.replace("?", ".{1}?");

        return regExp;
    }
}
