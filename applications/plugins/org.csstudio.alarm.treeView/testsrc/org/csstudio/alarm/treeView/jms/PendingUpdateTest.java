package org.csstudio.alarm.treeView.jms;

import java.util.Date;

import org.csstudio.alarm.treeView.model.Severity;
import org.junit.Test;
import org.mockito.Mockito;

public class PendingUpdateTest {

    @Test
    public void testAcknowledgementUpdate() {
        AlarmTreeUpdater updater = Mockito.mock(AlarmTreeUpdater.class);

        PendingUpdate out = PendingUpdate.createAcknowledgementUpdate("testchannel");
        out.apply(updater);
        
        Mockito.verify(updater).applyAcknowledgement("testchannel");
        Mockito.verifyNoMoreInteractions(updater);
    }

    @Test
    public void testAlarmUpdate() {
        AlarmTreeUpdater updater = Mockito.mock(AlarmTreeUpdater.class);
        Date t = new Date();

        PendingUpdate out = PendingUpdate.createAlarmUpdate("testchannel", Severity.MAJOR, t);
        out.apply(updater);
        
        Mockito.verify(updater).applyAlarm("testchannel", Severity.MAJOR, t);
        Mockito.verifyNoMoreInteractions(updater);
    }

}
