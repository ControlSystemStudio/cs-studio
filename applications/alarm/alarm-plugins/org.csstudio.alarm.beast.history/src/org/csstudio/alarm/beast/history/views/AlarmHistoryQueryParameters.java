package org.csstudio.alarm.beast.history.views;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.csdata.TimestampedPV;
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
    // TODO replace this with TimeInterval once diirt time is released
    private final Instant start;
    private final Instant end;
    private final int size;

    // Helper to build the alarm history query
    public static class AlarmHistoryQueryBuilder {

        private List<ProcessVariable> pvs = new ArrayList<ProcessVariable>();
        private Instant start;
        private Instant end;
        private int size;

        public static AlarmHistoryQueryBuilder buildQuery(){
            return new AlarmHistoryQueryBuilder();
        }

        private AlarmHistoryQueryBuilder() {
        }
        
        public AlarmHistoryQueryBuilder forPVs(List<ProcessVariable> pvs){
            this.pvs = pvs;
            return this;
        }
        
        public AlarmHistoryQueryBuilder forTimeStampedPVs(List<TimestampedPV> timestampedPVs){
            // Add received items, tracking their start..end time
            long start_ms = Long.MAX_VALUE,  end_ms = 0;
            this.pvs = new ArrayList<ProcessVariable>();
            for (TimestampedPV timestampedPV : timestampedPVs)
            {
                this.pvs.add(timestampedPV);
                final long time = timestampedPV.getTime();
                if (time < start_ms)
                    start_ms = time;
                if (time > end_ms)
                    end_ms = time;
            }
            this.start = Instant.ofEpochMilli(start_ms);
            this.end = Instant.ofEpochMilli(end_ms);
            return this;
        }
        
        public AlarmHistoryQueryBuilder forTimeInterval(TimeInterval interval){
            this.start = Instant.ofEpochSecond(interval.getStart().getSec(), interval.getStart().getNanoSec());
            this.end = Instant.ofEpochSecond(interval.getEnd().getSec(), interval.getEnd().getNanoSec());
            return this;
        }
        
        public AlarmHistoryQueryBuilder size(int size){
            this.size = size;
            return this;
        }
        
        public AlarmHistoryQueryParameters build(){
            return new AlarmHistoryQueryParameters(pvs, start, end, size);
        }
    }

    private AlarmHistoryQueryParameters(List<ProcessVariable> pvs, Instant start, Instant end, int size) {
        this.pvs = Collections.unmodifiableList(pvs);
        this.start = start;
        this.end = end;
        this.size = size;
    }

    public String getQueryString() {
        return "{\"query\" : " + 
                    "{\"term\" : " + "{ \"NAME\" : \"XF:31IDA-OP{Tbl-Ax:X1}Mtr\" }" + "}" + "}";
    }

}
