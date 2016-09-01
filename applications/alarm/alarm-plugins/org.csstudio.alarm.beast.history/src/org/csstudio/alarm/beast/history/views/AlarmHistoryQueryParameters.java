package org.csstudio.alarm.beast.history.views;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        private int size = 100;

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

    public List<ProcessVariable> getPvs() {
        return pvs;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public int getSize() {
        return size;
    }

    /**
     * build a elastic search query using
     * 1. list of all the pv's OR'ed
     * 2. with a time range if specified
     * 3. and the result limited to the size defined
     *
     * @return
     */
    public String getQueryString() {
        // TODO the ideal solution would be to use the elastic search client
        // instead of jersey and REST.
        // This would have better performance, along with a much nicer api to
        // build the queries.
        // Currently this effort has been limited by the inability to add and
        // use third party libraries easily. resulting in the rather ugly string
        // concatenated query


        /**example query
        {"size" : 100,
          "query" : {
            "bool" : {
              "must" : {
                "terms" : { "NAME" : ["XF:31IDA-OP{Tbl-Ax:X1}Mtr", "XF:31IDA-OP{Tbl-Ax:X2}Mtr"] }
              },
              "should" :{
                "range" : { "EVENTTIME":
                            {"gte":"2014-01-01 00:00:00.000",
                            "lte": "now"}
                          }
              }
            }
          }
        }**/
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"size\":"+String.valueOf(size)+", "
                + "\"sort\": { \"EVENTTIME\": { \"order\": \"desc\" }} , "
                + "\"query\" : { \"bool\" : ");
        // Add the pvNames
        if(pvs.isEmpty()){
            sb.append("{ \"must\" : { \"match_all\" : { } }");
        }else{
            sb.append("{ \"must\" : { \"terms\" : { \"NAME\" : [");
            sb.append(String.join(",",
                    pvs.stream().map(ProcessVariable::getName).map(e -> "\"" + e + "\"").collect(Collectors.toList())));
            sb.append("] } }");
        }
        // Add time constraint
        sb.append("} } }");
        return sb.toString();
    }

}
