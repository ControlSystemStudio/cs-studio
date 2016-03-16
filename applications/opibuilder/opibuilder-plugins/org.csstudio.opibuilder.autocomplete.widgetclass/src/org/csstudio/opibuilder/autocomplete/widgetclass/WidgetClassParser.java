/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.opibuilder.autocomplete.widgetclass;

import org.csstudio.autocomplete.AutoCompleteConstants;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.IContentParser;

/**
 * <code>WidgetClassParser</code> is the auto complete parser, which prepares the content for the widget class provider.
 * The parser identifies if the input string is a formula or a constant value trims the value accordingly.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class WidgetClassParser implements IContentParser {

    private static final char FORMULA_CHAR = AutoCompleteConstants.FORMULA_PREFIX.charAt(0);
    private static final char QUOTE = '"';

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.autocomplete.parser.IContentParser#accept(org.csstudio.autocomplete.parser.ContentDescriptor)
     */
    @Override
    public boolean accept(final ContentDescriptor desc) {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.autocomplete.parser.IContentParser#parse(org.csstudio.autocomplete.parser.ContentDescriptor)
     */
    @Override
    public ContentDescriptor parse(ContentDescriptor desc) {
        String originalValue = desc.getOriginalContent();

        // if the value is a formula, parse it, otherwise just return the same content
        if (originalValue.charAt(0) == FORMULA_CHAR) {
            String value;
            char[] orgValue = originalValue.toCharArray();
            int numQuotes = 0;
            for (char c : orgValue) {
                if (c == QUOTE) {
                    numQuotes++;
                }
            }
            // if number of quotes is odd, than the user is typing in a widget class constant
            // if the number of quotes is even, then we don't care.
            if (numQuotes % 2 == 1) {
                int idx = originalValue.lastIndexOf(QUOTE);
                if (idx < orgValue.length) {
                    value = originalValue.substring(idx + 1);
                } else {
                    value = "";
                }
                desc.setValue(value);
                desc.setStartIndex(idx + 1);
                desc.setEndIndex(orgValue.length);
                desc.setContentType(WidgetClassContentType.TYPE);
            }
        } else {
            desc.setContentType(WidgetClassContentType.TYPE);
        }

        return desc;
    }

}
