package org.csstudio.logbook.olog.property.fault;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.StringJoiner;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.PropertyBuilder;
import org.csstudio.logbook.olog.property.fault.Fault.BeamLossState;
import org.eclipse.core.runtime.IAdapterFactory;


/**
 *
 * @author Kunal Shroff
 *
 */
public class FaultAdapter implements IAdapterFactory{

    public static final String FAULT_PROPERTY_NAME = "fault";

    public static final String FAULT_PROPERTY_ATTR_AREA = "Area";
    public static final String FAULT_PROPERTY_ATTR_SYSTEM = "System";
    public static final String FAULT_PROPERTY_ATTR_DEVICE = "Device";

    public static final String FAULT_PROPERTY_ATTR_ASSIGNED = "Assign";
    public static final String FAULT_PROPERTY_ATTR_CONTACT = "Contact";

    private static final String FAULT_PROPERTY_ATTR_TOCCOURED = "TimeOccoured";
    private static final String FAULT_PROPERTY_ATTR_TCLEARED = "TimeCleared";
    private static final String FAULT_PROPERTY_ATTR_LOST_STATE = "BeamState";
    private static final String FAULT_PROPERTY_ATTR_TLOST = "TimeBeamLost";
    private static final String FAULT_PROPERTY_ATTR_TRESTORED = "TimeBeamRestored";

    public static final String FAULT_PROPERTY_ATTR_LOGIDS = "LogIds";

    public static final String FAULT_COMMENT_CAUSE = "Cause:";
    public static final String FAULT_COMMENT_REPAIR = "Repair:";
    public static final String FAULT_COMMENT_CORRECTION = "Corrective:";

    /**
     * A utility method that helps convert the input from the the
     * faultEditorWidget into a logbook property
     *
     * @param fault to be converted to the {@link PropertyBuilder}
     * @param list of log entries to be added to this fault
     * @return PropertyBuilder representing this fault
     */
    public static PropertyBuilder createFaultProperty(Fault fault, List<String> list) {
        PropertyBuilder pb = PropertyBuilder.property(FAULT_PROPERTY_NAME);
        if (fault.getArea() != null && !fault.getArea().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_AREA, fault.getArea());
        if (fault.getSubsystem() != null && !fault.getSubsystem().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_SYSTEM, fault.getSubsystem());
        if (fault.getDevice() != null && !fault.getDevice().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_DEVICE, fault.getDevice());
        if (fault.getAssigned() != null && !fault.getAssigned().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_ASSIGNED, fault.getAssigned());
        if (fault.getContact() != null && !fault.getContact().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_CONTACT, fault.getContact());
        if (fault.getFaultOccuredTime() != null)
            pb.attribute(FAULT_PROPERTY_ATTR_TOCCOURED, fault.getFaultOccuredTime().toString());
        if (fault.getFaultClearedTime() != null)
            pb.attribute(FAULT_PROPERTY_ATTR_TCLEARED, fault.getFaultClearedTime().toString());
        if (fault.getBeamLossState() != null)
            pb.attribute(FAULT_PROPERTY_ATTR_LOST_STATE, fault.getBeamLossState().toString());
        if (fault.getBeamlostTime() != null)
            pb.attribute(FAULT_PROPERTY_ATTR_TLOST, fault.getBeamlostTime().toString());
        if (fault.getBeamRestoredTime() != null)
            pb.attribute(FAULT_PROPERTY_ATTR_TRESTORED, fault.getBeamRestoredTime().toString());
        if (!list.isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_LOGIDS, String.join(";", list));
        return pb;
    }

    /**
     * A utility method that helps convert the input from the the
     * faultEditorWidget into a logbook property
     *
     * @param fault to be converted to the {@link PropertyBuilder}
     * @return PropertyBuilder for the given fault
     */
    public static PropertyBuilder createFaultProperty(Fault fault) {
        PropertyBuilder pb = PropertyBuilder.property(FAULT_PROPERTY_NAME);
        if (fault.getArea() != null && !fault.getArea().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_AREA, fault.getArea());
        if (fault.getSubsystem() != null && !fault.getSubsystem().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_SYSTEM, fault.getSubsystem());
        if (fault.getDevice() != null && !fault.getDevice().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_DEVICE, fault.getDevice());
        if (fault.getAssigned() != null && !fault.getAssigned().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_ASSIGNED, fault.getAssigned());
        if (fault.getContact() != null && !fault.getContact().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_CONTACT, fault.getContact());
        if (fault.getFaultOccuredTime() != null)
            pb.attribute(FAULT_PROPERTY_ATTR_TOCCOURED, fault.getFaultOccuredTime().toString());
        if (fault.getFaultClearedTime() != null)
            pb.attribute(FAULT_PROPERTY_ATTR_TCLEARED, fault.getFaultClearedTime().toString());
        if (fault.getBeamLossState() != null)
            pb.attribute(FAULT_PROPERTY_ATTR_LOST_STATE, fault.getBeamLossState().toString());
        if (fault.getBeamlostTime() != null)
            pb.attribute(FAULT_PROPERTY_ATTR_TLOST, fault.getBeamlostTime().toString());
        if (fault.getBeamRestoredTime() != null)
            pb.attribute(FAULT_PROPERTY_ATTR_TRESTORED, fault.getBeamRestoredTime().toString());
        if (fault.getLogIds().isEmpty())
            pb.attribute(FAULT_PROPERTY_ATTR_LOGIDS, String.join(";",
                    fault.getLogIds().stream().sorted().map(String::valueOf).collect(Collectors.toList())));
        return pb;
    }


    /**
     * A utility method for creating a single log entry text from the various
     * fields of the faultEditorWidget
     *
     * @param fault
     * @return
     */
    public static String createFaultText(Fault fault) {
        StringBuilder sb = new StringBuilder();
        sb.append(fault.getDescription());
        sb.append("\n\n");
        sb.append(FAULT_COMMENT_CAUSE + "\n");
        sb.append(fault.getRootCause());
        sb.append("\n\n");
        sb.append(FAULT_COMMENT_REPAIR + "\n");
        sb.append(fault.getRepairAction());
        sb.append("\n\n");
        sb.append(FAULT_COMMENT_CORRECTION + "\n");
        sb.append(fault.getCorrectiveAction());
        return sb.toString();
    }

    /**
     * A helper method to create string representation of fault for this calender view
     * @return
     */
    public static String faultString(Fault fault) {
        StringBuffer sb = new StringBuffer();
        sb.append(fault.getArea() != null ? fault.getArea():"None");
        sb.append(":");
        sb.append(fault.getSubsystem() != null ? fault.getSubsystem():"None");
        sb.append(":");
        sb.append(fault.getDevice() != null ? fault.getDevice():"None");
        sb.append(System.lineSeparator());

        sb.append(fault.getAssigned() != null ? fault.getAssigned() : "no owner");
        sb.append(System.lineSeparator());

        sb.append(fault.getDescription());
        sb.append(System.lineSeparator());

        sb.append(fault.getRootCause());
        sb.append(System.lineSeparator());

        sb.append(fault.getRepairAction());
        sb.append(System.lineSeparator());

        sb.append(fault.getCorrectiveAction());
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * A helper method to create string representation of fault for this calender view
     * @return
     */
    public static String faultSummaryString(Fault fault) {
        StringBuffer sb = new StringBuffer();
        sb.append(fault.getArea() != null ? fault.getArea():"None");
        sb.append(":");
        sb.append(fault.getSubsystem() != null ? fault.getSubsystem():"None");
        sb.append(":");
        sb.append(fault.getDevice() != null ? fault.getDevice():"None");
        sb.append(System.lineSeparator());

        sb.append(fault.getAssigned() != null ? fault.getAssigned() : "no owner");
        sb.append(System.lineSeparator());

        sb.append(fault.getDescription());
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    public static String createFaultExportText(Fault fault, String delimiter) {
        StringJoiner sj = new StringJoiner(delimiter);
        sj.add(fault.getArea());
        sj.add(fault.getSubsystem());
        sj.add(fault.getDevice());
        sj.add(fault.getAssigned());
        sj.add(fault.getContact());

        sj.add(fault.getFaultOccuredTime() != null ? fault.getFaultOccuredTime().toString() : null);
        sj.add(fault.getFaultClearedTime() != null ? fault.getFaultClearedTime().toString() : null);

        sj.add(fault.getBeamLossState() != null ? fault.getBeamLossState().toString() : null);

        sj.add(fault.getBeamlostTime() != null ? fault.getBeamlostTime().toString() : null);
        sj.add(fault.getBeamRestoredTime() != null ? fault.getBeamRestoredTime().toString() : null);

        sj.add(fault.getDescription());
        sj.add(fault.getRootCause());
        sj.add(fault.getRepairAction());
        sj.add(fault.getCorrectiveAction());

        sj.add(!fault.getLogIds().isEmpty() ? String.join(":",
                fault.getLogIds().stream().sorted().map(String::valueOf).collect(Collectors.toList())) : null);

        return sj.toString();
    }



    /**
     * A utility method that takes a single string and using the special fault
     * comment key words parses out the description, cause, repair and
     * corrective actions comments from the string
     *
     * @param text
     * @return
     */
    public static Map<String, String> extractFaultText(String text) {
        Map<String, String> map = new HashMap<String, String>();
        int lastIndex = text.length();
        int indexCause = text.indexOf(FAULT_COMMENT_CAUSE);
        int indexRepair = text.indexOf(FAULT_COMMENT_REPAIR);
        int indexCorrection = text.indexOf(FAULT_COMMENT_CORRECTION);

        if (indexCorrection != -1) {
            String correction = text.substring(indexCorrection + FAULT_COMMENT_CORRECTION.length(), lastIndex).trim();
            lastIndex = indexCorrection;
            map.put(FAULT_COMMENT_CORRECTION, correction);
        }

        if (indexRepair != -1) {
            String repair = text.substring(indexRepair + FAULT_COMMENT_REPAIR.length(), lastIndex).trim();
            lastIndex = indexRepair;
            map.put(FAULT_COMMENT_REPAIR, repair);
        }
        if (indexCause != -1) {
            String cause = text.substring(indexCause + FAULT_COMMENT_CAUSE.length(), lastIndex).trim();
            lastIndex = indexCause;
            map.put(FAULT_COMMENT_CAUSE, cause);
        }
        if (lastIndex > 0) {
            String description = text.substring(0, lastIndex).trim();
            map.put("Description", description);
        }

        return map;
    }

    /**
     *
     * @param logEntry
     * @return
     */
    public static Fault extractFaultFromLogEntry(LogEntry logEntry) {

        Optional<Property> property = logEntry.getProperties().stream().filter((prop) -> {
            return prop.getName().equals(FAULT_PROPERTY_NAME);
        }).findFirst();
        if (property.isPresent()) {
            Fault fault = new Fault();
            fault.setId(Integer.valueOf(logEntry.getId().toString()));
            Map<String, String> faultText = extractFaultText(logEntry.getText());
            for (Entry<String, String> text : faultText.entrySet()) {
                switch (text.getKey()) {
                case "Description":
                    fault.setDescription(text.getValue());
                    break;
                case FAULT_COMMENT_CAUSE:
                    fault.setRootCause(text.getValue());
                    break;
                case FAULT_COMMENT_REPAIR:
                    fault.setRepairAction(text.getValue());
                    break;
                case FAULT_COMMENT_CORRECTION:
                    fault.setCorrectiveAction(text.getValue());
                    break;
                default:
                    break;
                }
            }

            for (Entry<String, String> attribute : property.get().getAttributes()) {
                switch (attribute.getKey()) {
                case FAULT_PROPERTY_ATTR_AREA:
                    fault.setArea(attribute.getValue());
                    break;
                case FAULT_PROPERTY_ATTR_SYSTEM:
                    fault.setSubsystem(attribute.getValue());
                    break;
                case FAULT_PROPERTY_ATTR_DEVICE:
                    fault.setDevice(attribute.getValue());
                    break;
                case FAULT_PROPERTY_ATTR_ASSIGNED:
                    fault.setAssigned(attribute.getValue());
                    break;
                case FAULT_PROPERTY_ATTR_CONTACT:
                    fault.setContact(attribute.getValue());
                    break;
                case FAULT_PROPERTY_ATTR_TOCCOURED:
                    fault.setFaultOccuredTime(ZonedDateTime.parse(attribute.getValue()).toInstant());
                    break;
                case FAULT_PROPERTY_ATTR_TCLEARED:
                    fault.setFaultClearedTime(ZonedDateTime.parse(attribute.getValue()).toInstant());
                    break;
                case FAULT_PROPERTY_ATTR_LOST_STATE:
                    fault.setBeamLossState(BeamLossState.valueOf(attribute.getValue()));
                    break;
                case FAULT_PROPERTY_ATTR_TLOST:
                    fault.setBeamlostTime(ZonedDateTime.parse(attribute.getValue()).toInstant());
                    break;
                case FAULT_PROPERTY_ATTR_TRESTORED:
                    fault.setBeamRestoredTime(ZonedDateTime.parse(attribute.getValue()).toInstant());
                    break;
                case FAULT_PROPERTY_ATTR_LOGIDS:
                    fault.setLogIds(Arrays.asList(attribute.getValue().split(";")).stream().map(Integer::valueOf)
                            .collect(Collectors.toList()));
                    break;
                default:
                    break;
                }
            }
            return fault;
        } else {
            throw new IllegalArgumentException(
                    "The given log entry does not have a fault property and cannot be converted.");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if(adapterType == Fault.class){
            LogEntry logEntry = (LogEntry)adaptableObject;
            Fault fault = FaultAdapter.extractFaultFromLogEntry(logEntry);
            return fault;
        }
        return null;
    }

    @Override
    public Class<?>[] getAdapterList() {
        return new Class[] { Fault.class };
    }
}
