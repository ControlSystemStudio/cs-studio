package gov.bnl.shiftClient;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
*@author: eschuhmacher
 */

@XmlRootElement(name = "shift")
public class XmlShift {

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


    /** Creates a new instance of XmlShift */
    public XmlShift() {
    }

    /**
     * Creates a new instance of XmlShift.
     *
     * @param logId log id
     */
    public XmlShift(Integer logId) {
        this.id = logId;
    }

    /**
     * Getter for shift id.
     *
     * @return id shift id
     */
    @XmlElement
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
    @XmlAttribute
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
    @XmlAttribute
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
    @XmlAttribute
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
    @XmlAttribute
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
    @XmlAttribute
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
    @XmlAttribute
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
    @XmlAttribute
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
    @XmlAttribute
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

    /**
     * Setter for shift status.
     *
     * @param status shift type
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    @XmlAttribute
    public String getStatus() {
        return status;
    }


    /**
     * Creates a compact string representation for the log.
     *
     * @param data Log to create the string representation for
     * @return string representation
     */
    public static String toLogger(XmlShift data) {
        return data.getId() + ", " + data.getOwner();
    }

}
