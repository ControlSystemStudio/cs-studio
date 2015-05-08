package org.csstudio.dct.metamodel;

import com.cosylab.vdct.dbd.DBDConstants;

public enum PromptGroup {
    ALL (-1, "", "All"),

    UNDEFINED(-1, "", "Undefined"),

    COMMON(DBDConstants.GUI_COMMON, "GUI_COMMON", "Common"),

    ALARMS(DBDConstants.GUI_ALARMS, "GUI_ALARMS", "Alarms"),

    BITS1(DBDConstants.GUI_BITS1, "GUI_BITS1", "Bits 1"),

    BITS2(DBDConstants.GUI_BITS2, "GUI_BITS2", "Bits 2"),

    CALC(DBDConstants.GUI_CALC, "GUI_CALC", "Calc"),

    CLOCK(DBDConstants.GUI_CLOCK, "GUI_CLOCK", "Clock"),

    COMPRESS(DBDConstants.GUI_COMPRESS, "GUI_COMPRESS", "Compress"),

    CONVERT(DBDConstants.GUI_CONVERT, "GUI_CONVERT", "Convert"),

    DISPLAY(DBDConstants.GUI_DISPLAY, "GUI_DISPLAY", "Display"),

    HIST(DBDConstants.GUI_HIST, "GUI_HIST", "Hist"),

    INPUTS(DBDConstants.GUI_INPUTS, "GUI_INPUTS", "Inputs"),

    LINKS(DBDConstants.GUI_LINKS, "GUI_LINKS", "Links"),

    MBB(DBDConstants.GUI_MBB, "GUI_MBB", "MBB"),

    MOTOR(DBDConstants.GUI_MOTOR, "GUI_MOTOR", "Motor"),

    OUTPUT(DBDConstants.GUI_OUTPUT, "GUI_OUTPUT", "Output"),

    PID(DBDConstants.GUI_PID, "GUI_PID", "PID"),

    PULSE(DBDConstants.GUI_PULSE, "GUI_PULSE", "Pulse"),

    SELECT(DBDConstants.GUI_SELECT, "GUI_SELECT", "Select"),

    SEQ1(DBDConstants.GUI_SEQ1, "GUI_SEQ1", "Seq 1"),

    SEQ2(DBDConstants.GUI_SEQ2, "GUI_SEQ2", "Seq 2"),

    SEQ3(DBDConstants.GUI_SEQ3, "GUI_SEQ3", "Seq 3"),

    SUB(DBDConstants.GUI_SUB, "GUI_SUB", "Sub"),

    TIMER(DBDConstants.GUI_TIMER, "GUI_TIMER", "Timer"),

    WAVE(DBDConstants.GUI_WAVE, "GUI_WAVE", "Wave"),

    SCAN(DBDConstants.GUI_SCAN, "GUI_SCAN", "Scan");

    private int vdctType;
    private String dbdString;
    private String description;

    private PromptGroup(int vdctType, String dbdString, String humanReadable) {
        this.vdctType = vdctType;
        this.dbdString = dbdString;
        this.description = humanReadable;
    }

    public int getVdctType() {
        return vdctType;
    }

    public String getDbdString() {
        return dbdString;
    }

    public String getDescription() {
        return description;
    }

    public static PromptGroup findByType(int type) {
        PromptGroup result = UNDEFINED;

        for(PromptGroup g : values()) {
            if(g.getVdctType() == type) {
                result = g;
            }
        }

        return result;
    }
}
