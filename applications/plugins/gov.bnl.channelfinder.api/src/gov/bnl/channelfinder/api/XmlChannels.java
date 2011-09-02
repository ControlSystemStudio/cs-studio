/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
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

@XmlRootElement(name = "channels")
public class XmlChannels {
    private Collection<XmlChannel> channels = new ArrayList<XmlChannel>();
  
    /** Creates a new instance of XmlChannels. */
    public XmlChannels() {
    }

    /** Creates a new instance of XmlChannels with one initial channel.
     * @param c initial element
     */
    public XmlChannels(XmlChannel c) {
        channels.add(c);
    }

    /**
     * Returns a collection of XmlChannel.
     *
     * @return a collection of XmlChannel
     */
    @XmlElement(name = "channel")
    public Collection<XmlChannel> getChannels() {
        return channels;
    }

    /**
     * Sets the collection of channels.
     *
     * @param items new channel collection
     */
    public void setChannels(Collection<XmlChannel> items) {
        this.channels = items;
    }

    /**
     * Adds a channel to the channel collection.
     *
     * @param item the XmlChannel to add
     */
    public void addXmlChannel(XmlChannel item) {
        this.channels.add(item);
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data XmlChannel to create the string representation for
     * @return string representation
     */
    public static String toLog(XmlChannels data) {
        if (data.getChannels().size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (XmlChannel c : data.getChannels()) {
                s.append(XmlChannel.toLog(c) + ",");
            }
            s.delete(s.length()-1, s.length());
            s.append("]");
            return s.toString();
        }
    }
}
