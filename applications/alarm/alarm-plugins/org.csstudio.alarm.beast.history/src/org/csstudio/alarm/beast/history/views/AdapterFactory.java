package org.csstudio.alarm.beast.history.views;

import static org.csstudio.alarm.beast.history.views.AlarmHistoryQueryParameters.AlarmHistoryQueryBuilder.buildQuery;

import java.util.Arrays;
import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.csdata.TimestampedPV;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * An adapter factory to convert ProcessVariable, ProcessVariable[], TimestampedPV, TimestampedPV[] into a
 * single AlarmHistoryQueryParameters object
 *
 * @author Kunal Shroff
 *
 */
public class AdapterFactory implements IAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Class[] getAdapterList() {
        return new Class[] { AlarmHistoryQueryParameters.class };
    }

    @Override
    public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
        if (adaptableObject instanceof TimestampedPV[]){
            List<TimestampedPV> timestampedPVs = Arrays.asList((TimestampedPV[]) adaptableObject);
            AlarmHistoryQueryParameters parameter = buildQuery()
                    .forTimeStampedPVs(timestampedPVs).build();
            return (T) parameter;
        } else if (adaptableObject instanceof TimestampedPV) {
            List<TimestampedPV> timestampedPVs = Arrays.asList((TimestampedPV) adaptableObject);
            AlarmHistoryQueryParameters parameter = buildQuery()
                    .forTimeStampedPVs(timestampedPVs).build();
            return (T) parameter;
        } else if (adaptableObject instanceof ProcessVariable[]) {
            List<ProcessVariable> pvs = Arrays.asList((ProcessVariable[]) adaptableObject);
            AlarmHistoryQueryParameters parameter = buildQuery()
                    .forPVs(pvs).build();
            return (T) parameter;
        } else if (adaptableObject instanceof ProcessVariable) {
            List<ProcessVariable> pvs = Arrays.asList((ProcessVariable) adaptableObject);
            AlarmHistoryQueryParameters parameter = buildQuery()
                    .forPVs(pvs).build();
            return (T) parameter;
        } else {
            return null;
        }
    }

}
