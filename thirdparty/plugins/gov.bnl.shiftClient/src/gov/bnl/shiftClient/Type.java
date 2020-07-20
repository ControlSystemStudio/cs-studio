package gov.bnl.shiftClient;


import java.util.Collection;

public class Type  {


    private int id;
    private String name;

    public Type(XmlType xmlType) {
        this.id = xmlType.getId();
        this.name = xmlType.getName();
    }
    
    public Type(String name) {
        this.name = name;
    }

    public Type() {
    }
    /**
     * @return the id
     */
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
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    public static String toLogger(Type data) {
        return data.getId() + ", " + data.getName();
    }

    public XmlType toXml() {
        XmlType xmlType = new XmlType();
        xmlType.setId(id);
        xmlType.setName(name);
        return xmlType;
    }
}