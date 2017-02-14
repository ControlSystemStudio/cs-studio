package org.csstudio.logbook.olog.property.fault;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class Fault {

    private int id;
    private String area;
    private String subsystem;
    private String device;

    private String description;

    private String assigned;

    private String contact;

    private Instant faultOccuredTime;
    private Instant faultClearedTime;

    public enum BeamLossState {
        True, False, Studies, Commissioning, Interlocks
    };

    private BeamLossState beamLossState;

    private Instant beamlostTime;
    private Instant beamRestoredTime;

    private String rootCause;
    private String repairAction;
    private String correctiveAction;
    
    // list of associated logs
    private List<Integer> logIds = Collections.emptyList();

    public Fault(String description) {
        this.description = description;
    }

    public Fault(int id, String area, String subsystem, String device, String description, String assigned,
            String contact, Instant faultOccuredTime, Instant faultClearedTime, BeamLossState beamLossState,
            Instant beamlostTime, Instant beamRestoredTime, String rootCause, String repairAction,
            String correctiveAction, List<Integer> logIds) {
        super();
        this.id = id;
        this.area = area;
        this.subsystem = subsystem;
        this.device = device;
        this.description = description;
        this.assigned = assigned;
        this.contact = contact;
        this.faultOccuredTime = faultOccuredTime;
        this.faultClearedTime = faultClearedTime;
        this.beamLossState = beamLossState;
        this.beamlostTime = beamlostTime;
        this.beamRestoredTime = beamRestoredTime;
        this.rootCause = rootCause;
        this.repairAction = repairAction;
        this.correctiveAction = correctiveAction;
        this.logIds = logIds;
    }

    public Fault() {
    }

    public int getId() {
        return id;
    }

    public String getArea() {
        return area;
    }

    public String getSubsystem() {
        return subsystem;
    }

    public String getDevice() {
        return device;
    }

    public String getDescription() {
        return description;
    }

    public String getAssigned() {
        return assigned;
    }

    public String getContact() {
        return contact;
    }

    public Instant getFaultOccuredTime() {
        return faultOccuredTime;
    }

    public Instant getFaultClearedTime() {
        return faultClearedTime;
    }

    public BeamLossState getBeamLossState() {
        return beamLossState;
    }

    public Instant getBeamlostTime() {
        return beamlostTime;
    }

    public Instant getBeamRestoredTime() {
        return beamRestoredTime;
    }

    public String getRootCause() {
        return rootCause;
    }

    public String getRepairAction() {
        return repairAction;
    }

    public String getCorrectiveAction() {
        return correctiveAction;
    }
    
    public List<Integer> getLogIds() {
        return Collections.unmodifiableList(logIds);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setFaultOccuredTime(Instant faultOccuredTime) {
        this.faultOccuredTime = faultOccuredTime;
    }

    public void setFaultClearedTime(Instant faultClearedTime) {
        this.faultClearedTime = faultClearedTime;
    }

    public void setBeamLossState(BeamLossState beamLossState) {
        this.beamLossState = beamLossState;
    }

    public void setBeamlostTime(Instant beamlostTime) {
        this.beamlostTime = beamlostTime;
    }

    public void setBeamRestoredTime(Instant beamRestoredTime) {
        this.beamRestoredTime = beamRestoredTime;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public void setRepairAction(String repairAction) {
        this.repairAction = repairAction;
    }

    public void setCorrectiveAction(String correctiveAction) {
        this.correctiveAction = correctiveAction;
    }
    
    public void setLogIds(List<Integer> logIds){
        this.logIds = Collections.unmodifiableList(logIds);
    }

    public boolean validate(){
        if(getArea() == null || getArea().isEmpty())
            return false;
        if(getSubsystem() == null || getSubsystem().isEmpty())
            return false;
        if(getDevice() == null || getDevice().isEmpty())
            return false;
        if(getDescription()== null || getDescription().isEmpty())
            return false;
        if(getFaultOccuredTime() == null)
            return false;
        if(getBeamLossState()== null)
            return false;
        return true;
    }
}
