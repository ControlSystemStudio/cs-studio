package gov.bnl.shiftClient;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

/**
 *@author :eschuhmacher
 */
@XmlRootElement(name = "shifts")
public class XmlShifts extends LinkedList<XmlShift> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6279865221993808192L;

	public XmlShifts() {
    }

    public XmlShifts(XmlShift shift) {
        this.add(shift);
    }

    public XmlShifts(List<XmlShift> shifts) {
        this.addAll(shifts);
    }

    @XmlElement(name = "shift")
    public List<XmlShift> getShifts() {
        return this;
    }

    public void setShifts(List<XmlShift> shifts) {
        this.addAll(shifts);
    }

    public void addShift(XmlShift shift) {
        this.add(shift);
    }


    /**
     * Creates a compact string representation for the shift.
     *
     * @param data Shift to create the string representation for
     * @return string representation
     */
    public static String toLogger(XmlShifts data) {
        if (data.getShifts().size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (XmlShift c : data.getShifts()) {
                s.append(XmlShift.toLogger(c) + ",");
            }
            s.delete(s.length() - 1, s.length());
            s.append("]");
            return s.toString();
        }
    }

    public static String toLogger(List<Shift> data) {
        if (data.size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (Shift c : data) {
                s.append(Shift.toLogger(c) + ",");
            }
            s.delete(s.length() - 1, s.length());
            s.append("]");
            return s.toString();
        }
    }
}
