package org.csstudio.alarm.treeView.views;

import java.util.Date;

import org.csstudio.alarm.treeView.model.Severity;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
public class PendingUpdateTest {

    @Test
    public void testAcknowledgementUpdate() {
        final AlarmTreeUpdater updater = Mockito.mock(AlarmTreeUpdater.class);

        final AbstractPendingUpdate out = AbstractPendingUpdate.createAcknowledgementUpdate("testchannel");
        out.apply(updater);

        Mockito.verify(updater).applyAcknowledgement("testchannel");
        Mockito.verifyNoMoreInteractions(updater);
    }

    @Test
    public void testAlarmUpdate() {
        final AlarmTreeUpdater updater = Mockito.mock(AlarmTreeUpdater.class);
        final Date t = new Date();

        final AbstractPendingUpdate out = AbstractPendingUpdate.createAlarmUpdate("testchannel", Severity.MAJOR, t);
        out.apply(updater);

        Mockito.verify(updater).applyAlarm("testchannel", Severity.MAJOR, t);
        Mockito.verifyNoMoreInteractions(updater);
    }

}
