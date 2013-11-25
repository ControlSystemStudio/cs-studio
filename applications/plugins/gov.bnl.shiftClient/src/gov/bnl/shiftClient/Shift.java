package gov.bnl.shiftClient;

import java.util.*;

/**
 *@author: eschuhmacher
 */


public class Shift  {

    private Integer id;
    private Type type;
    private String owner;
    private Date startDate;
    private Date endDate;
    private String description;
    private String leadOperator;
    private String onShiftPersonal;
    private String report;
    private String closeShiftUser;
    private String status;

    public Shift() {
    }

    public Shift(final Integer id) {
        this.id = id;
    }

    Shift(final XmlShift shift) {
        this.id = shift.getId();
        this.owner = shift.getOwner();
        this.description = shift.getDescription();
        this.startDate = shift.getStartDate();
        this.endDate = shift.getEndDate();
        this.leadOperator = shift.getLeadOperator();
        this.onShiftPersonal = shift.getOnShiftPersonal();
        this.report = shift.getReport();
        this.closeShiftUser = shift.getCloseShiftUser();
        this.type = shift.getType();
        this.status = shift.getStatus();
    }


    /**
     * Getter for shift id.
     *
     * @return id shift id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter for shift id.
     *
     * @param id shift id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter for shift owner.
     *
     * @return owner shift owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Setter for shift owner.
     *
     * @param owner shift owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }
    /**
     * Getter for shift description.
     *
     * @return description shift description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for shift description.
     *
     * @param description shift description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for shift lead Operator.
     *
     * @return leadOperator shift lead operator
     */
    public String getLeadOperator() {
        return leadOperator;
    }

    /**
     * Setter for shift lead operator.
     *
     * @param leadOperator shift lead operator
     */
    public void setLeadOperator(String leadOperator) {
        this.leadOperator = leadOperator;
    }

    /**
     * Getter for shift onShiftPersonal.
     *
     * @return onShiftPersonal shift on shift personal
     */
    public String getOnShiftPersonal() {
        return onShiftPersonal;
    }

    /**
     * Setter for shift onShiftPersonal.
     *
     * @param onShiftPersonal shift on shift personal
     */
    public void setOnShiftPersonal(String onShiftPersonal) {
        this.onShiftPersonal = onShiftPersonal;
    }

    /**
     * Getter for shift report.
     *
     * @return report shift report
     */
    public String getReport() {
        return report;
    }

    /**
     * Setter for shift report.
     *
     * @param report shift report
     */
    public void setReport(String report) {
        this.report = report;
    }

    /**
     * Getter for shift type.
     *
     * @return type shift type
     */
    public Type getType() {
        return type;
    }

    /**
     * Setter for shift tyoe.
     *
     * @param type shift type
     */
    public void setType(Type type) {
        this.type = type;
    }
    /**
     * Getter for shift startDate.
     *
     * @return startDate shift start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Setter for shift startDate.
     *
     * @param startDate shift start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Getter for shift endDate.
     *
     * @return endDate shift end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Setter for shift endDate.
     *
     * @param endDate shift end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    /**
     * Getter for shift end shift user.
     *
     * @return closeShiftUser shift closeShiftUser
     */
    public String getCloseShiftUser() {
        return closeShiftUser;
    }

    /**
     * Setter for shift closeShiftUser.
     *
     * @param closeShiftUser shift type
     */
    public void setCloseShiftUser(String closeShiftUser) {
        this.closeShiftUser = closeShiftUser;
    }
    
    public String getStatus() {
    	return status;
    }
    
    public void setStatus(final String status) {
    	this.status = status;
    }


    public int compareTo(Shift num) {
        int x = startDate.compareTo(num.startDate);
        return x;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data Log to create the string representation for
     * @return string representation
     */
    public static String toLogger(Shift data) {
        return data.getId() + ", " + data.getOwner();
    }

    public XmlShift toXml() {
        XmlShift xmlShift = new XmlShift();
        xmlShift.setId(id);
        xmlShift.setOwner(owner);
        xmlShift.setDescription(description);
        xmlShift.setStartDate(startDate);
        xmlShift.setEndDate(endDate);
        xmlShift.setCloseShiftUser(closeShiftUser);
        xmlShift.setLeadOperator(leadOperator);
        xmlShift.setOnShiftPersonal(onShiftPersonal);
        xmlShift.setReport(report);
        xmlShift.setType(type);
        return xmlShift;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	result = prime * result + ((status == null) ? 0 : status.hashCode());
	return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Shift other = (Shift) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	if (status == null) {
	    if (other.status != null)
		return false;
	} else if (!status.equals(other.status))
	    return false;
	return true;
    }



}
