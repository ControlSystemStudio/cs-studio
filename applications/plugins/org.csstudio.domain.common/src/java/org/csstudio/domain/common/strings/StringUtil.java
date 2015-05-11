package org.csstudio.domain.common.strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;


/**
 * Utility methods for handling strings.
 *
 * @author <code>splitIgnoreInQuotes</code> by Xihui Chen
 */
@SuppressWarnings("nls")
public final class StringUtil {

    /**
     * Constructor.
     */
    private StringUtil() {
        // Don't instantiate
    }

    public static String printArrays(final Object value) {
        String result = null;

        if (value == null) {
            result = "null";
        } else if (value instanceof double[]) {
            result = Arrays.toString((double[]) value);
        } else if (value instanceof long[]) {
            result = Arrays.toString((long[]) value);
        } else if (value instanceof String[]) {
            result = Arrays.toString((String[]) value);
        } else if (value instanceof Object[]) {
            result = Arrays.toString((Object[]) value);
        } else {
            result = value.toString();
        }

        return result;
    }

    public static String capitalize(final String s) {
        String result = s;
        if (hasLength(s)) {
            result = s.substring(0,1).toUpperCase() + s.substring(1);
        }
        return result;
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

    public static boolean hasLength(final String s) {
        return s != null && s.length() > 0;
    }

    /**
     * Returns the Substring of the String.
     *
     * @param str    String
     * @param end    the last char Position (not included in the Substring)
     * @return <code>null</code> if the String is <code>null</code>,
     *  the Substring from the beginning to the position <code>endIdx</code>,
     *  or the String if the position <code>endIdx</code>
     *  is larger than length of the String.
     */
    public static String checkedSubstring(final String str, final int end) {
        if (str == null) {
            return null;
        }
        if (str.length() < end) {
            return str;
        }
        return str.substring(0, end);
    }

    public static String toSeparatedString(final Collection<String> collection, final String separator) {
        final StringBuffer sb = new StringBuffer();

        if(!collection.isEmpty()) {
            final Iterator<String> it = collection.iterator();
            sb.append(it.next());

            while(it.hasNext()) {
                sb.append(separator);
                sb.append(it.next());
            }
        }

        return sb.toString();
    }

    public static String trimNull(final String s) {
        return hasLength(s) ? s : "";
    }

    private static final char SPACE = ' ';
    private static final char QUOTE = '"';

    /**
     * Split source string into an array of elements by the splitting character,
     * but the split characters between two quotes will be ignored.
     *
     * @param trimmedSource
     *            string to be split
     * @param splitChar
     *            the character used to split the source string
     * @param deleteHeadTailQuotes
     *            delete the quotes in the head and tail of individual elements
     *            if true
     * @return an array of individual elements
     * @throws Exception
     *             Exception on parse error (missing end of quoted string)
     */
    public static String[] splitIgnoreInQuotes(final String source,
                                               final char splitChar,
                                               final boolean deleteHeadTailQuotes) throws Exception {

        // Trim, replace tabs with spaces so we only need to handle
        // space in the following
        final String trimmedSource = source.replace('\t', SPACE).trim();
        final List<String> resultList = new ArrayList<String>();
        int pos = 0;
        int start = 0;
        while(pos < trimmedSource.length()) {

            start = pos;
            //skip multiple splitChars
            while(start < trimmedSource.length() && trimmedSource.charAt(start) == splitChar) {
                start++;
            }
            if(start >= trimmedSource.length()) {
                break;
            }
            pos = start;

            while(pos < trimmedSource.length() && trimmedSource.charAt(pos) !=splitChar) {
                //in case of quote, go to the end of next quote
                if(trimmedSource.charAt(pos) == QUOTE) {
                    // When locating the ending quote, ignore escaped quotes
                    int end = trimmedSource.indexOf(QUOTE, pos+1);
                    while (end > 0  &&  trimmedSource.charAt(end-1) == '\\')
                        end = trimmedSource.indexOf(QUOTE, end+1);
                    if(end < 0) {
                        throw new Exception("Missing end of quoted text in '" +
                                trimmedSource + "'");
                    }
                    pos = end + 1;
                } else {
                    pos++;
                }
            }

            String subString = trimmedSource.substring(start, pos);
            subString = subString.trim();
            if(deleteHeadTailQuotes) {
                //only delete quotes when both head and tail are quote
                if(subString.charAt(0) == QUOTE && subString.charAt(subString.length()-1) == QUOTE) {
                    subString = subString.substring(1, subString.length()-1);
                }
            }

            resultList.add(subString);
        }
        return resultList.toArray(new String[resultList.size()]);
    }

    /**If a String contains the regular expression.
     * @param source the source string.
     * @param regex the regular expression.
     * @return true if the source string contains the input regex. false other wise.
     */
    public static boolean containRegex(final String source, final String regex) {
        final Pattern p = Pattern.compile(regex);
        final Matcher m = p.matcher(source);
        return m.find();
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringUtils.join(null, *) = null
     * StringUtils.join([], *) = ""
     * StringUtils.join([null], *) = ""
     * StringUtils.join(["a", "b", "c"], "--") = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null) = "abc"
     * StringUtils.join(["a", "b", "c"], "") = "abc"
     * StringUtils.join([null, "", "a"], ',') = ",,a"
     * </pre>
     *
     * @param array the array of values to join together, may be null
     * @param separator the separator character to use, null treated as ""
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(final Object[] array,
                              final String sep) {
        if (array == null) {
            return null;
        }
        if (array.length <= 0) {
            return "";
        }
        final String separator = sep != null ? sep : "";

        final StringBuilder builder = new StringBuilder();
        for (final Object object : array) {
            if (object != null) {
                builder.append(object);
            }
            builder.append(separator);
        }
        // remove the last separator, if there is one
        if (!"".equals(separator)) {
            builder.delete(builder.length() - separator.length(),
                           builder.length());
        }
        return builder.toString();
    }

    /**
     * Joins the enumeration entries to a String object separated by the given String sep.
     *
     * @param values the string values in a enumeration
     * @param separator the separating string
     * @return the joined string with separated entries
     * @throws NamingException
     */
    public static String join(final NamingEnumeration<String> values,
                              final String separator) throws NamingException {

        final String sep = separator != null ? separator : "";

        if (values == null || !values.hasMoreElements()) {
            return null;
        }

        String resultString;
        boolean nonNullAttrValueFound = false;
        final StringBuilder builder = new StringBuilder();
        while (values.hasMore()) {
            final String next = values.next();
            if (next != null) { // null is permitted as value of a present attribute - not my idea!
                builder.append(next).append(sep);
                nonNullAttrValueFound = true;
            }
        }
        if (!nonNullAttrValueFound) {
            return null; // StringBuilder.toString would return an empty string in that case
        }
        if (!StringUtil.isBlank(sep)) {
            builder.delete(builder.length() - sep.length(), builder.length());
        }
        resultString = builder.toString();
        return resultString;
    }

}
