/**
 * Copyright (C) 2010-2012 Brookhaven National Laboratory
 * Copyright (C) 2010-2012 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * All rights reserved. Use is subject to license terms.
 */
package gov.bnl.channelfinder.api;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

/**
 * Channels (collection) object that can be represented as XML/JSON in payload data.
 *
 * @author Ralph Lange <Ralph.Lange@bessy.de>
 */

@XmlRootElement(name = "tags")
public class XmlTags {
    private Collection<XmlTag> tags = new ArrayList<XmlTag>();
  
    /** Creates a new instance of XmlTags. */
    public XmlTags() {
    }

    /** Creates a new instance of XmlTags with one initial tag.
     * @param t initial element
     */
    public XmlTags(XmlTag t) {
        tags.add(t);
    }

    /**
     * Returns a collection of XmlTag.
     *
     * @return a collection of XmlTag
     */
    @XmlElement(name = "tag")
    public Collection<XmlTag> getTags() {
        return tags;
    }

    /**
     * Sets the collection of tags.
     *
     * @param items new tag collection
     */
    public void setTags(Collection<XmlTag> items) {
        this.tags = items;
    }

    /**
     * Adds a tag to the tag collection.
     *
     * @param item the XmlTag to add
     */
    public void addXmlTag(XmlTag item) {
        this.tags.add(item);
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data XmlTags to create the string representation for
     * @return string representation
     */
    public static String toLog(XmlTags data) {
        if (data.getTags().size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (XmlTag t : data.getTags()) {
                s.append(XmlTag.toLog(t) + ",");
            }
            s.delete(s.length()-1, s.length());
            s.append("]");
            return s.toString();
        }
    }
}
