/**
 * 
 */
package org.csstudio.alarm.beast.history.views;

import java.util.List;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;

/**
 * @author Kunal Shroff
 *
 */
public class AlarmHistoryCommand extends AbstractAdaptedHandler<AlarmHistoryQueryParameters> {

    public AlarmHistoryCommand(Class<AlarmHistoryQueryParameters> dataType) {
        super(dataType);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void execute(List<AlarmHistoryQueryParameters> data, ExecutionEvent event) throws Exception {
        // TODO Auto-generated method stub

    }

}
