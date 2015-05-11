/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An abstract class to convert a parameterized String.
 * @author Kai Meyer
 *
 */
public abstract class AbstractToolTipConverter {
    /**
     * The sign, which is before a parameter.
     */
    public static final String START_SEPARATOR = "${";
    /**
     * The sign, which is after a parameter.
     */
    public static final String END_SEPARATOR = "}$";

    /**
     * Replaces all parameters (encapsulated by '${' and '}$').
     * @param toolTip The tooltip to convert
     * @return The converted tooltip
     */
    public final String convertToolTip(final String toolTip) {
        Pattern FIND_ALIAS_NAME_PATTERN = Pattern
        .compile("\\$\\{([^${}]+)\\}\\$");

        // Get a Matcher based on the target string.
        Matcher matcher = FIND_ALIAS_NAME_PATTERN.matcher(toolTip);

        String s = toolTip;

        // Find all the matches.
        while (matcher.find()) {
            String name = matcher.group(1);
            s = s.replaceFirst("${"+name+"}$", "aaaaa");
        }

        if (toolTip.contains(START_SEPARATOR) && toolTip.contains(END_SEPARATOR)) {
            int end = 0;
            int start = 0;
            StringBuffer buffer = new StringBuffer();
            while (start<toolTip.length() && end<toolTip.length()-2 && toolTip.indexOf(START_SEPARATOR, end)>-1) {
                start = toolTip.indexOf(START_SEPARATOR, end)+2;
                buffer.append(toolTip.substring(end, start));
                end = toolTip.indexOf(END_SEPARATOR, start);
                if (end > -1 && start > -1 && start<end) {
                    String parameter = toolTip.substring(start, end);
                    buffer.append(this.getReplacementForParameter(parameter));
                } else {
                    break;
                }
            }
            if (end>-1 && end <toolTip.length()) {
                buffer.append(toolTip.substring(end));
            }
            return buffer.toString();
        }
        return toolTip;
    }

    /**
     * Returns the replacement for a parameter.
     * @param parameter The parameter to replace
     * @return The replacement
     */
    protected abstract String getReplacementForParameter(final String parameter);

}
