package org.csstudio.opibuilder.converter.writer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.opibuilder.converter.StringSplitter;
import org.apache.commons.lang.StringUtils;
import java.util.logging.Logger;

public class PVNameConversion {

    private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.PVNameConversion");

    private static Collection<String> functions = new ArrayList<String>();
    static {
        functions.add("sum");
    }

    private static Collection<String> operators = new ArrayList<String>();
    static {
        // Escaped for the purposes of regexing.
        operators.add("\\+");
        operators.add("-");
        operators.add("/");
        operators.add("\\*");
        operators.add("&&");
        operators.add("\\|\\|");
        operators.add("<");
        operators.add(">");
        operators.add("=");
    }

    private PVNameConversion() {
        // No instantiation.
    }

    /**
     * If pvName is a LOC or CALC EDM PV, attempt to convert it to a syntax
     * understood by CSS.
     *
     * If conversion fails or it is a regular PV, return the unchanged PV name.
     * @param pvName PV name to convert
     * @return converted PV name
     */
    public static String convertPVName(String pvName) {
        if (pvName.startsWith("LOC")) {
            log.config("EDM local PV: " + pvName);
            pvName = parseLocPV(pvName);
            log.config("Converted local PV: " + pvName);
        } else if (pvName.startsWith("CALC")) {
            log.config("EDM calc PV: " + pvName);
            pvName = parseCalcPV(pvName);
            log.config("Converted calc PV: " + pvName);
        }
        return pvName;
    }

    /**
     * Convert an EDM local PV into a CSS local PV.
     * @param pvName local EDM PV name
     * @return local CSS PV name
     */
    private static String parseLocPV(String pvName) {
        if(pvName.startsWith("LOC\\")){
            try {
                String newName = pvName.replace("$(!W)", "$(DID)");
                newName = newName.replaceAll("\\x24\\x28\\x21[A-Z]{1}\\x29", "\\$(DID)");
                String[] parts = StringSplitter.splitIgnoreInQuotes(newName, '=', true);
                StringBuilder sb = new StringBuilder("loc://");
                sb.append(parts[0].substring(5));
                if (parts.length > 1) {
                    String type = "";
                    String initValue = parts[1];
                    if (parts[1].startsWith("d:")) {
                        type = "<VDouble>";
                        initValue = parts[1].substring(2);
                    } else if (parts[1].startsWith("i:")) {
//                        type = "<VDouble>";
                        initValue = parts[1].substring(2);
                    } else if (parts[1].startsWith("s:")) {
//                          type = "<VString>";
                        initValue = "\""+parts[1].substring(2)+"\"";
                    } else if (parts[1].startsWith("e:")) { // Enumerated pv
                                                            // cannot be
                                                            // converted.
                        return pvName;
                    }
                    //doesn't append type yet to support utility pv.
                    sb.append("(").append(initValue).append(")");
                    }
                return sb.toString();
            } catch (Exception e) {
                    return pvName;
                }
            }
            return pvName;
    }


    /**
     * Parse the expression component of an EDM CALC PV; that is, the
     * part before the parentheses:
     * {A/B}(...)
     * {A*B}(...)
     * {1000 > x}(...)
     * The string must be tokens separated by the items in the static variable
     * operators.
     *
     * @param op the substring to parse
     * @return a List<String> of the operators
     */
    private static List<String> parseExpression(String expr) {
        // expression contained within {}
        if  (!(StringUtils.startsWith(expr, "\\{") &&
                StringUtils.endsWith(expr, "\\}"))) {
            throw new IllegalArgumentException("Failed to parse CALC expression");
        }
        expr = expr.substring(2, expr.length() - 2);
        List<String> parts = splitString(expr, operators, true);
        // The = in EDM expressions is == in CSS ones
        Collections.replaceAll(parts, "=", "==");
        return parts;
    }

    /**
     * Return variables provided in arguments to CALC pv as a List of Strings.
     * If an argument is a PV name, convert that PV name.
     *
     * @param argString
     *            - the arguments to a CALC PV, inside parentheses
     * @return List<String> of variables
     */
    private static List<String> parseArguments(String argString) {
        // expression contained within ()
        if (!(StringUtils.startsWith(argString, "(") &&
                StringUtils.endsWith(argString, ")"))) {
            throw new IllegalArgumentException("Failed to parse CALC ArrayList");
        }
        List<String> arguments = new ArrayList<String>();
        argString = argString.substring(1, argString.length() -1);
        List<String> pieces = splitString(argString, ",", false);
        for (String piece : pieces) {
            if (isPVString(piece)) {
                // recurse
                piece = convertPVName(piece);
                arguments.add("pv(\"" + piece + "\")");
            } else {
                arguments.add(piece);
            }
        }
        return arguments;
    }

    /**
     * Convert EDM CALC PV into an equivalent CSS formula
     * @param pvName EDM CALC PV name
     * @return CSS formula string
     */
    private static String parseCalcPV(String pvName) {
        try {
            // Remove CALC\\ from the start of the string.
            String calcString = pvName.substring(6);
            int firstParen = calcString.indexOf('(');
            String ops = calcString.substring(0, firstParen);
            String args = calcString.substring(firstParen, calcString.length());
            Deque<String> arguments = new ArrayDeque<String>(parseArguments(args));
            StringBuilder sb = new StringBuilder("=(");
            if (functions.contains(ops)) {
                // handle special functions
                if (ops.equals("sum")) {
                    // handle the sum function
                    sb.append(StringUtils.join(arguments, "+"));
                }
            } else {
                List<String> parts = parseExpression(ops);
                // placeholders is a map between tokens (often A, B), and the
                // arguments that they represent
                Map<String, String> placeholders = new HashMap<String, String>();
                for (String part : parts) {
                    if (StringUtils.isAlpha(part)) {
                        if (! placeholders.containsKey(part)) {
                            placeholders.put(part, arguments.removeFirst());
                        }
                    }
                }
                for (String part : parts) {
                    String next;
                    if (StringUtils.isAlpha(part)) {
                        // placeholder
                        next = placeholders.get(part);
                    } else {
                        // operator
                        next = part;
                    }
                    sb.append(next);
                }
            }
            sb.append(")");
            return sb.toString();
        } catch (IllegalArgumentException e) {
            log.info("Failed to parse CALC PV: " + e);
            return pvName;
        }
    }

    /**
     * Whether a string is a PV name
     * @param argument String to check
     * @return true if String is a PV name
     */
    private static boolean isPVString(String argument) {
        return !StringUtils.isNumeric(argument);
    }

    /**
     * Split a String into substrings using the specifed delimiter.
     * If includeDelimiters, include delimiters in the returned list.
     * @param toSplit the String to split
     * @param delimiter the delimiter to use
     * @param includeDelimiters whether to include the delimiters
     * @return a List<String> of components
     */
    private static List<String> splitString(String toSplit, String delimiter,
            boolean includeDelimiters) {
        ArrayList<String> delimiters = new ArrayList<String>();
        delimiters.add(delimiter);
        return splitString(toSplit, delimiters, includeDelimiters);
    }

    /**
     * Split a String into a substring using any of the Strings supplied in
     * delimiters.  If includeDelimiters, include the delimiters in the returned
     * list.
     * @param toSplit the String to split
     * @param delimiters the delimiters to use
     * @param includeDelimiters whether to include the delimiters
     * @return a List<String> of components
     */
    private static List<String> splitString(String toSplit,
            Collection<String> delimiters, boolean includeDelimiters) {
        // http://stackoverflow.com/questions/275768/is-there-a-way-to-split-strings-with-string-split-and-include-the-delimiters?lq=1
        String dels = StringUtils.join(delimiters, "|");
        List<String> matches = new ArrayList<String>();
        Pattern p = Pattern.compile(dels);
        Matcher m = p.matcher(toSplit);
        int start = 0;
        while (m.find()) {
            String part = toSplit.substring(start, m.start());
            // remove any whitespace
            matches.add(part.trim());
            if (includeDelimiters) {
                matches.add(m.group());
            }
            start = m.end();
        }
        String part = (start >= toSplit.length() ? "" : toSplit.substring(start));
        matches.add(part.trim());
        return matches;
    }
}
