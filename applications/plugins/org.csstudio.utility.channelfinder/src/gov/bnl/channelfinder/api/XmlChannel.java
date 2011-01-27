/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

package gov.bnl.channelfinder.api;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Channel object that can be represented as XML/JSON in payload data.
 *
 * @author Ralph Lange <Ralph.Lange@bessy.de>
 */

@XmlRootElement(name = "channel")
public class XmlChannel {
    private String name;
    private String owner;
    private XmlProperties properties = new XmlProperties();
    private XmlTags tags = new XmlTags();
  
    /** Creates a new instance of XmlChannel */
    public XmlChannel() {
    }

    /**
     * Creates a new instance of XmlChannel.
     *
     * @param name channel name
     */
    public XmlChannel(String name) {
        this.name = name;
    }

    /**
     * Creates a new instance of XmlChannel.
     *
     * @param name channel name
     * @param owner owner name
     */
    public XmlChannel(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    /**
     * Getter for channel name.
     *
     * @return name
     */
    @XmlAttribute
    public String getName() {
        return name;
    }

    /**
     * Setter for channel name.
     *
     * @param name the value to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for channel owner.
     *
     * @return owner
     */
    @XmlAttribute
    public String getOwner() {
        return owner;
    }

    /**
     * Setter for channel owner.
     *
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Getter for channel's XmlProperties.
     *
     * @return XmlProperties
     */
    @XmlElement(name = "properties")
    public XmlProperties getXmlProperties() {
        return properties;
    }

    /**
     * Setter for channel's XmlProperties.
     *
     * @param properties XmlProperties
     */
    public void setXmlProperties(XmlProperties properties) {
        this.properties = properties;
    }

    /**
     * Adds an XmlProperty to the channel.
     *
     * @param property single XmlProperty
     */
    public void addXmlProperty(XmlProperty property) {
        this.properties.addXmlProperty(property);
    }

    /**
     * Getter for the channel's XmlTags.
     *
     * @return XmlTags for this channel
     */
    @XmlElement(name = "tags")
    public XmlTags getXmlTags() {
        return tags;
    }

    /**
     * Setter for the channel's XmlTags.
     *
     * @param tags XmlTags
     */
    public void setXmlTags(XmlTags tags) {
        this.tags = tags;
    }

    /**
     * Adds an XmlTag to the collection.
     *
     * @param tag
     */
    public void addXmlTag(XmlTag tag) {
        this.tags.addXmlTag(tag);
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data XmlChannel to create the string representation for
     * @return string representation
     */
    public static String toLog(XmlChannel data) {
        return data.getName() + "(" + data.getOwner() + "):["
                + XmlProperties.toLog(data.properties)
                + XmlTags.toLog(data.tags)
                + "]";
    }
}
