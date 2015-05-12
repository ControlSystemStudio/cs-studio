package org.csstudio.sds.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;

public class TooltipResolver {
    private static Pattern FIND_ALIAS_NAME_PATTERN = Pattern.compile("\\$\\{([^${}]+)\\}");

    /**
     * The sign, which is before a parameter.
     */
    public static final String START_SEPARATOR = "${";
    /**
     * The sign, which is after a parameter.
     */
    public static final String END_SEPARATOR = "}";

    public static String resolveToValue(String tooltipPattern, AbstractWidgetModel widget) {
        return resolve(tooltipPattern, widget.getProperties());
    }

    private static String resolve(String tooltipPattern, List<WidgetProperty> properties) {

        // Get a Matcher based on the target string.
        Matcher matcher = FIND_ALIAS_NAME_PATTERN.matcher(tooltipPattern);

        String s = tooltipPattern;

        // Find all the matches.
        while (matcher.find()) {
            String name = matcher.group(1);

            WidgetProperty property = findWidgetProperty(properties, name);

            String replacement = "--";

            if (property != null) {
                replacement = property.getTextForTooltip();
            }

            // we have to escape all $ chars to prevent exceptions during the
            // following replacments
            replacement = replacement.replace("$", "\\$");

            try {
                s = s.replaceAll("\\$\\{" + name + "\\}", replacement);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        return s;
    }

    private static WidgetProperty findWidgetProperty(List<WidgetProperty> properties, String propertyNameOrDescription) {
        WidgetProperty result = null;

        for (WidgetProperty p : properties) {
            if (propertyNameOrDescription.equals(p.getId()) || propertyNameOrDescription.equals(p.getDescription())) {
                result = p;
            }
        }

        return result;
    }

}
