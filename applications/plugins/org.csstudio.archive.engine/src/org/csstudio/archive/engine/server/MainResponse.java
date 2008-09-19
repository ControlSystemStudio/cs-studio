package org.csstudio.archive.engine.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.archive.engine.Messages;
import org.csstudio.archive.engine.model.ArchiveGroup;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.eclipse.core.runtime.Platform;

/** Provide web page with engine overview.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class MainResponse extends AbstractResponse
{
    /** Avoid serialization errors */
    private static final long serialVersionUID = 1L;
    
    /** Bytes in a MegaByte */
    final static double MB = 1024.0*1024.0;

    MainResponse(final EngineModel model)
    {
        super(model);
    }
    
    @Override
    protected void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html = new HTMLWriter(resp, Messages.HTTP_MainTitle);
        html.openTable(2, new String[] { "Summary" });
        
        html.tableLine(new String[] { Messages.HTTP_Description, model.getName() });
        
        html.tableLine(new String[] { Messages.HTTP_State, model.getState().name() });
        final ITimestamp start = model.getStartTime();
        if (start != null)
        {
            html.tableLine(new String[]
            {
                Messages.HTTP_StartTime,
                start.format(ITimestamp.Format.DateTimeSeconds)
            });
            
            final double up_secs = 
                TimestampFactory.now().toDouble() - start.toDouble();
            html.tableLine(new String[]
            {
                Messages.HTTP_Uptime,
                PeriodFormat.formatSeconds(up_secs)
            });
        }
        
        html.tableLine(new String[]
        {
            Messages.HTTP_Workspace,
            Platform.getInstanceLocation().getURL().getFile().toString()
        });

        final int group_count = model.getGroupCount();
        int total_channel_count = 0;
        int connect_count = 0;
        for (int i=0; i<group_count; ++i)
        {
            final ArchiveGroup group = model.getGroup(i);
            final int channel_count = group.getChannelCount();
            for (int j=0; j<channel_count; ++j)
            {
                if (group.getChannel(j).isConnected())
                    ++connect_count;
            }
            total_channel_count += channel_count;
        }
        html.tableLine(new String[]
        { Messages.HTTP_GroupCount, Integer.toString(group_count) });
        html.tableLine(new String[]
        { Messages.HTTP_ChannelCount, Integer.toString(total_channel_count) });
        final int disconnect_count = total_channel_count - connect_count;
        if (disconnect_count > 0)
        {
            html.tableLine(new String[]
            {
                Messages.HTTP_Disconnected,
                HTMLWriter.makeRedText(Integer.toString(disconnect_count))
            });
        }
        html.tableLine(new String[]
        {
            Messages.HTTP_BatchSize,
            String.format("%d samples", model.getBatchSize())
        });
        html.tableLine(new String[]
        {
            Messages.HTTP_WritePeriod,
            String.format("%.1f sec", model.getWritePeriod())
        });
        final ITimestamp last_write_time = model.getLastWriteTime();
        html.tableLine(new String[]
        {
          Messages.HTTP_LastWriteTime,
          last_write_time == null
          ? Messages.HTTP_Never
          : last_write_time.format(ITimestamp.Format.DateTimeSeconds)
        });
        html.tableLine(new String[]
        {
            Messages.HTTP_WriteCount,
            (int)model.getWriteCount() + " samples"
        });
        html.tableLine(new String[]
        {
            Messages.HTTP_WriteDuration,
            String.format("%.1f sec", model.getWriteDuration())
        });
        html.tableLine(new String[]
        {
            Messages.HTTP_Idletime, 
            String.format("%.1f %%", model.getIdlePercentage())
        });
        
        final Runtime runtime = Runtime.getRuntime();
        final double used_mem = runtime.totalMemory() / MB;
        final double max_mem = runtime.maxMemory() / MB;
        final double perc_mem = max_mem > 0 ?
                     used_mem / max_mem * 100.0 : 0.0;
        html.tableLine(new String[]
        {
            "Memory",
            String.format("%.1f MB of %.1f MB used (%.1f %%)",
                          used_mem, max_mem, perc_mem)
        });
        
        html.tableLine(new String[] { Messages.HTTP_Version, EngineModel.VERSION });

        html.closeTable();
        
        html.close();
    }
}
