package gov.bnl.shiftClient;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

/**
 *@author :eschuhmacher
 */
@XmlRootElement(name = "types")
public class XmlTypes extends LinkedList<XmlType> {

    public XmlTypes() {
    }


    public XmlTypes(XmlType xmlType) {
        this.add(xmlType);
    }

    public XmlTypes(List<XmlType> types) {
        this.addAll(types);
    }

    @XmlElement(name = "type")
    public List<XmlType> getTypes() {
        return this;
    }

    public void addType(XmlType type) {
        this.add(type);
    }


    /**
     * Creates a compact string representation for the types.
     *
     * @param data Shift to create the string representation for
     * @return string representation
     */
    public static String toLogger(XmlTypes data) {
        if (data.getTypes().size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (XmlType c : data.getTypes()) {
                s.append(XmlType.toLogger(c) + ",");
            }
            s.delete(s.length() - 1, s.length());
            s.append("]");
            return s.toString();
        }
    }

    public static String toLogger(List<Type> data) {
        if (data.size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (Type c : data) {
                s.append(Type.toLogger(c) + ",");
            }
            s.delete(s.length() - 1, s.length());
            s.append("]");
            return s.toString();
        }
    }
}
