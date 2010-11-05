/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.bnl.channelfinder.api;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author rlange
 */
@XmlType(name = "property", propOrder = {"name","value","owner"})
class XmlProperty {
    private String name = null;
    private String value = null;
    private String owner = null;

    /**
     * Creates a new instance of XmlProperty.
     *
     */
    public XmlProperty() {
    }

    /**
     * Creates a new instance of XmlProperty.
     *
     * @param name
     * @param owner
     * @param value
     */
    public XmlProperty(String name, String owner, String value) {
        this.value = value;
        this.owner = owner;
        this.name = name;
    }

    /**
     * Getter for property name.
     *
     * @return property name
     */
    @XmlAttribute
    public String getName() {
        return name;
    }

    /**
     * Setter for property name.
     *
     * @param name property name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for property value.
     *
     * @return property value
     */
    @XmlAttribute
    public String getValue() {
        return value;
    }

    /**
     * Setter for property value.
     *
     * @param value property value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Getter for property owner.
     *
     * @return property owner
     */
    @XmlAttribute
    public String getOwner() {
        return owner;
    }

    /**
     * Setter for property owner.
     *
     * @param owner property owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

}
