package gov.bnl.shiftClient;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "type")

public class XmlType {
    private Integer id;
    private String name;

    public XmlType() {
    }

 /*   public XmlType(final Integer id) {
        this.id = id;
    }       */


    /**
     * @return the id
     */
    @XmlElement
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    @XmlElement
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    public static String toLogger(XmlType data) {
        return data.getId() + ", " + data.getName();
    }
}
