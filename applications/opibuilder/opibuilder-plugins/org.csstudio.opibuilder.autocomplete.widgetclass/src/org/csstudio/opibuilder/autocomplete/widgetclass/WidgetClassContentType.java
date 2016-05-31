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

import org.csstudio.autocomplete.parser.ContentType;

/**
 *
 * <code>WidgetClassContentType</code> is the identifier for the widget class auto complete content.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class WidgetClassContentType extends ContentType {

    static final WidgetClassContentType QUOTED = new WidgetClassContentType(true);
    static final WidgetClassContentType PLAIN = new WidgetClassContentType(false);

    private final boolean quoted;

    private WidgetClassContentType(boolean quoted) {
        super("WidgetClass");
        this.quoted = quoted;
    }

    boolean isQuoted() {
        return quoted;
    }
}
