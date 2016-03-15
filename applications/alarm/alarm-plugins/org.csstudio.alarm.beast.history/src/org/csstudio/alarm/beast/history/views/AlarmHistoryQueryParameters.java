package org.csstudio.alarm.beast.history.views;

import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.diirt.util.time.TimeInterval;

/**
 * List of query parameters to be used to Query the Alarm message history
 * service.
 * 
 * @author Kunal Shroff
 *
 */
public class AlarmHistoryQueryParameters {

    private final List<ProcessVariable> pvs;
    private final TimeInterval interval;

    public AlarmHistoryQueryParameters(List<ProcessVariable> pvs, TimeInterval interval) {
        super();
        this.pvs = pvs;
        this.interval = interval;
    }

    public String getQueryString() {
        return "{\"query\" : " + 
                    "{\"term\" : " + "{ \"NAME\" : \"XF:31IDA-OP{Tbl-Ax:X1}Mtr\" }" + "}" + "}";
    }

}
