package org.csstudio.logging.es.model;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.logging.es.archivedjmslog.ArchiveModel;
import org.csstudio.logging.es.archivedjmslog.LiveModel;
import org.csstudio.logging.es.archivedjmslog.MergedModel;
import org.eclipse.swt.widgets.Shell;

public class LogArchiveModel extends MergedModel<EventLogMessage>
{
    final Shell shell;

    public LogArchiveModel(Shell shell, ArchiveModel<EventLogMessage> archive,
            LiveModel<EventLogMessage> live)
    {
        super(archive, live);
        this.shell = shell;
    }

    @Override
    public void newMessage(EventLogMessage msg)
    {
        // ignore the message if we are not in "NOW" mode.
        if (!RelativeTime.NOW.equals(this.endSpec))
        {
            return;
        }
        synchronized (this.messages)
        {
            if (!this.messages.isEmpty())
            {
                long lastTime = this.messages.last().getTime();
                long thisTime = msg.getTime();
                msg.setDelta(thisTime - lastTime);
            }
            this.messages.add(msg);
        }
        fireModelChanged();
    }
}
