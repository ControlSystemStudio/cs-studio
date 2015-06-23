package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.Edm_activeLogMeterClass;

public class Opi_activeLogMeterClass extends Opi_activeMeterClass {

    public Opi_activeLogMeterClass(Context con, Edm_activeLogMeterClass r) {
        super(con, r);
        if (r.getScaleFormat().equals("Exponential")) {
            System.out.println("Adding an exp");
            new OpiBoolean(widgetContext, "log_scale", true);
        }
        new OpiBoolean(widgetContext, "show_value_label", false);
        new OpiBoolean(widgetContext, "show_markers", false);
        new OpiBoolean(widgetContext, "show_minor_ticks", false);
    }

}
