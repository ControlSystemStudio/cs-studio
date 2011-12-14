package edu.msu.nscl.olog.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
/**
 * Level object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman
 * @Deprecated
 */
@XmlType(propOrder = {"name","xmlLogs"})
@XmlRootElement(name = "level")
@Deprecated public class XmlLevel {
    private String name = null;
    private XmlLogs logs = null;

    /**
     * Creates a new instance of XmlLevel.
     *
     */
    public XmlLevel() {
    }

    /**
     * Creates a new instance of XmlLevel.
     *
     * @param name
     */
    public XmlLevel(String name) {
        this.name = name;
    }

    /**
     * Getter for level name.
     *
     * @return level name
     */
    @XmlElement(name="name")
    public String getName() {
        return name;
    }

    /**
     * Setter for level name.
     *
     * @param name level name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for level's XmlLogs.
     *
     * @return XmlLogs object
     */
    @XmlElement(name = "logs")
    public XmlLogs getXmlLogs() {
        return logs;
    }

    /**
     * Setter for level's XmlLogs.
     *
     * @param logs XmlLogs object
     */
    public void setXmlLogs(XmlLogs logs) {
        this.logs = logs;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data the XmlLevel to log
     * @return string representation for log
     */
    public static String toLog(XmlLevel data) {
         if (data.logs == null) {
            return data.getName();
        } else {
            return data.getName() + XmlLogs.toLog(data.logs);
        }
    }
}
