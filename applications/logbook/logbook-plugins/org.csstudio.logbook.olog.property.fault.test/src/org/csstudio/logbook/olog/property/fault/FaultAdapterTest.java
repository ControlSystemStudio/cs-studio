package org.csstudio.logbook.olog.property.fault;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FaultAdapterTest {

    @Test
    public void checkSimpleFaultTextParsin() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fault Description");
        sb.append("\n\n");
        sb.append(FaultAdapter.FAULT_COMMENT_CAUSE + "\n");
        sb.append("Fault root cause description");
        sb.append("\n\n");
        sb.append(FaultAdapter.FAULT_COMMENT_REPAIR + "\n");
        sb.append("Fault repair action description");
        sb.append("\n\n");
        sb.append(FaultAdapter.FAULT_COMMENT_CORRECTION + "\n");
        sb.append("Fault future corrective actions description");
        Map<String, String> map = FaultAdapter.extractFaultText(sb.toString());
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("Description", "Fault Description");
        expectedMap.put(FaultAdapter.FAULT_COMMENT_CAUSE, "Fault root cause description");
        expectedMap.put(FaultAdapter.FAULT_COMMENT_REPAIR, "Fault repair action description");
        expectedMap.put(FaultAdapter.FAULT_COMMENT_CORRECTION, "Fault future corrective actions description");
        assertEquals(expectedMap, map);
    }

    @Test
    public void checkOnlyDescFaultTextParsin() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fault Description");
        sb.append("\n\n");
        Map<String, String> map = FaultAdapter.extractFaultText(sb.toString());
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("Description", "Fault Description");
        assertEquals(expectedMap, map);
    }

    @Test
    public void checkDescRepairFaultTextParsin() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fault Description");
        sb.append("\n\n");
        sb.append(FaultAdapter.FAULT_COMMENT_REPAIR + "\n");
        sb.append("Fault repair action description");
        sb.append("\n\n");
        Map<String, String> map = FaultAdapter.extractFaultText(sb.toString());
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("Description", "Fault Description");
        expectedMap.put(FaultAdapter.FAULT_COMMENT_REPAIR, "Fault repair action description");
        assertEquals(expectedMap, map);
    }
    
    @Test
    public void checkCauseCorrectionFaultTextParsin() {
        StringBuilder sb = new StringBuilder();
        sb.append(FaultAdapter.FAULT_COMMENT_CAUSE + "\n");
        sb.append("Fault root cause description");
        sb.append("\n\n");
        sb.append(FaultAdapter.FAULT_COMMENT_CORRECTION + "\n");
        sb.append("Fault future corrective actions description");
        Map<String, String> map = FaultAdapter.extractFaultText(sb.toString());
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(FaultAdapter.FAULT_COMMENT_CAUSE, "Fault root cause description");
        expectedMap.put(FaultAdapter.FAULT_COMMENT_CORRECTION, "Fault future corrective actions description");
        assertEquals(expectedMap, map);
    }

}
